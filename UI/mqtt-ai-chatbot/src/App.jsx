import { useEffect, useState } from "react";

import "./App.css";
import ChatDisplay from "./components/ChatDisplay";
import ChatInput from "./components/ChatInput";
import connectMqtt from "./mqtt-client/mqttClient";

function App() {
  const [messages, setMessages] = useState([]);
  const [client, setClient] = useState(null);
  const [loading, setLoading] = useState(false); // Loading state for AI responses
  const [sessionId, setSessionId] = useState(null);
  const [isStartingSession, setIsStartingSession] = useState(false);

  // const [sessionMode, setSessionMode] = useState("MQTT"); // or "HTTP"
  const sessionMode = "MQTT"; // or "HTTP"
  const userId = "user123"; // Replace with actual user logic

  // Create new chat session (API call)
  const startNewChat = async (firstMessage) => {
    const res = await fetch(`http://localhost:8080/api/test/start/${userId}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ userId, message: firstMessage }),
    });
    const data = await res.json();
    setSessionId(data.id);
    return data.id;
  };
  // mqtt start new chat
  const startNewChatMqtt = (firstMessage) => {
    return new Promise((resolve) => {
      const responseTopic = `chat/user/${userId}/new/out`;
      if (isStartingSession) return; // 🚫 prevent double start
      setIsStartingSession(true);
      const handleNewSession = (topic, messageBuf) => {
        if (topic !== responseTopic) return; // ignore other topics

        try {
          const data = JSON.parse(messageBuf.toString());
          if (data && data.sessionId) {
            setSessionId(data.sessionId);

            if (data.message) {
              setMessages((prev) => [
                ...prev,
                { sender: "ai", text: data.message },
              ]);
            }

            resolve(data.sessionId);
          }
        } catch (err) {
          console.error(
            "Failed to parse new-session payload:",
            err,
            messageBuf.toString()
          );
        }
      };

      // listen once for session creation
      client.once("message", handleNewSession);

      // subscribe to the response topic- listener
      client.subscribe(responseTopic, (err) => {
        if (err) console.error("subscribe error:", err);
      });

      // publish new chat request (includes first message)
      const payload = JSON.stringify({ userId, message: firstMessage });
      console.log("📨 Calling startNewChatMqtt with:",payload);
      client.publish(`chat/user/${userId}/new/in`, payload, (err) => {
        if (err) console.error("publish new chat error:", err);
      });
    });
  };
  // client connection--listeners

  useEffect(() => {
    const mqttClient = connectMqtt(userId, (topic, msg) => {
      if (topic.endsWith("/new/out")) {
        // new session
        const data = JSON.parse(msg);
        setSessionId(data.sessionId);
        if (data.reply) {
          setMessages((prev) => [...prev, { sender: "ai", text: data.reply }]);
        }
      } else if (topic.endsWith("/out")) {
        // follow-up messages
        handleIncomingMessage(msg);
      }
    });

    setClient(mqttClient);

    return () => mqttClient.end(); // cleanup
  }, []);

  // subscribe to session topic when sessionId changes
  useEffect(() => {
    if (client && sessionId) {
      client.subscribe(`chat/user/${userId}/${sessionId}/out`);
    }
  }, [client, sessionId]);
  // Incoming AI message from backend
  const handleIncomingMessage = (msg) => {
    const data = JSON.parse(msg);
    setMessages((prev) => [
      ...prev,
      { sender: data.sender || "ai", text: data.message },
    ]);

    setLoading(false); // Reset loading state after receiving message
  };

  // User sends message
  const sendMessage = async (text) => {
    if (!client) return;

    let activeSessionId = sessionId;
    setMessages((prev) => [...prev, { sender: "user", text }]);
    // If no session yet, create one on-demand
    let topic, payload;
    if (!activeSessionId) {
      if (sessionMode === "HTTP") {
        activeSessionId = await startNewChat(text); // HTTP API
      } else if (sessionMode === "MQTT") {
        activeSessionId = await startNewChatMqtt(text); // MQTT
      } else {
        throw new Error("Unknown session mode: " + sessionMode);
      }

      //    topic = `chat/user/${userId}/new/in`;
      // payload = JSON.stringify({
      //   userId,
      //   sessionId: activeSessionId,
      //   message: text,
      //   sender: "user",
      // });
    } else {
      // Continue chat
      topic = `chat/user/${userId}/${activeSessionId}/in`;
      payload = JSON.stringify({
        userId,
        sessionId: activeSessionId,
        message: text,
        sender: "user",
      });
      client.publish(topic, payload, (err) => {
        if (err) console.error("MQTT publish error:", err);
      });
      console.log(payload);
    }

    setLoading(true); // Set loading state when user sends a message
  };

  return (
    <div>
      <h2 style={{ textAlign: "center" }}>🧠 AI MQTT Chat</h2>
      <ChatInput onSend={sendMessage} />
      <ChatDisplay messages={messages} loading={loading} />
    </div>
  );
}

export default App;

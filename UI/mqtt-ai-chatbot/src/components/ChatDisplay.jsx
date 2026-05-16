import React, { useEffect, useRef } from "react";


const ChatDisplay = ({ messages, loading }) => {
  const messagesEndRef = useRef(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  return (
    <div
      style={{
        display: "flex",
        flexDirection: "column",
        border: "2px solid #ccc",
        borderRadius: "8px",
        height: "80vh",
        overflow: "auto",
        backgroundColor: "#fafafa",
      }}

     
    >
      {messages.map((msg, idx) => (
        <div
          key={idx}
          style={{
            display: "flex",

            justifyContent: msg.sender === "user" ? "flex-start" : "flex-end",
            marginBottom: "0.5rem",
          }}
        >
          <div
            style={{
              maxWidth: "60%",
              padding: "0.5rem 1rem",
              borderRadius: "12px",
              backgroundColor: msg.sender === "user" ? "#e0f7fa" : "#c8e6c9",
            }}
          >
            <strong>{msg.sender === "user" ? "You" : "AI"}</strong>: {msg.text}
          </div>
          {/* {console.log(msg)} */}
        </div>
      ))}
      {loading && (
        <div style={{ textAlign: "right", fontStyle: "italic", color: "#666" }}>
          AI is typing...
        </div>
      )}
      <div ref={messagesEndRef} />
    </div>
  );
};

export default ChatDisplay;

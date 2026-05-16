import React, { useState } from "react";

const ChatInput = ({ onSend }) => {
  const [text, setText] = useState("");

  const handleSend = () => {
    if (!text.trim()) return;
    onSend(text);
    setText("");
  };
  const handleEndChat = () => {
    setText("__END_SESSION__");
    onSend(text);
    setText("");
  };

  return (
    <div style={{ display: "flex", padding: "1rem" }}>
      <input
        value={text}
        onChange={(e) => setText(e.target.value)}
        onKeyDown={(e) => e.key === "Enter" && handleSend()}
        style={{ flexGrow: 1, padding: "0.5rem" }}
      />
      <button onClick={handleSend} style={{ marginLeft: "0.5rem" }}>
        Send
      </button>
      <button onClick={handleEndChat} className="end-chat-btn">
        End Chat
      </button>
    </div>
  );
};

export default ChatInput;

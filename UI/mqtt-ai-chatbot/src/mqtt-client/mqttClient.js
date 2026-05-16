import mqtt from 'mqtt';

const brokerUrl = 'ws://localhost:9001'; // Make sure Mosquitto is configured for WebSocket

 const connectMqtt = (userId,onMessageCallback) => {
  const client = mqtt.connect(brokerUrl, {
    clientId: `webclient_${userId}_${Date.now()}`,
    clean: true,
  });

  client.on('connect', () => {
    console.log('✅ MQTT connected');
   
  });

  client.on('message', (topic, message) => {
    const payload = message.toString();
    console.log("📩 Global listener fired:", topic);
    console.log(`📨 MQTT message received [${topic}]: ${payload}`);
    // onMessageCallback(payload);
     onMessageCallback(topic, payload);
  });

  return client;
};
export default connectMqtt;
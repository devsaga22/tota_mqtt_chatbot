package com.chatbot.tota_backend.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {
	 private static final String MQTT_BROKER_URL = "tcp://localhost:1883";
//	  
	    @Bean
	    public MqttConnectOptions mqttConnectOptions() {
	        MqttConnectOptions options = new MqttConnectOptions();
	        options.setCleanSession(true);
	        options.setAutomaticReconnect(true);
	  
	        // You can add more config if needed later (e.g., username/password)
	        return options;
	    }
//	    spring Dependency Injection automatically scans the container for the beans and resolves internally
//	    used for Bean DI for configuratons
	    @Bean(name="backend_publisher")
	    public MqttClient mqttPublisherClient(MqttConnectOptions options) throws Exception {
	        MqttClient client = new MqttClient(MQTT_BROKER_URL, "backend_publisher", new MemoryPersistence());
	        client.connect(options);
// u r doing connection in PostConstruct MqttListener
	        return client;
	    }
	    @Bean(name="backend_subscriber")
	    public MqttClient mqttListenerClient(MqttConnectOptions options) throws Exception {
	        return new MqttClient(MQTT_BROKER_URL, "backend_subscriber", new MemoryPersistence());
	    }
}

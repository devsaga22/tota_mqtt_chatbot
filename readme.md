# MQTT-Based Intelligent Chatbot Platform

A modular, real-time chatbot application leveraging MQTT architecture for lightweight, asynchronous, bi-directional messaging between a dynamic User Interface and a scalable Spring Boot backend engine.

---

## 🏗️ System Architecture

The codebase is strictly decoupled into distinct service boundaries to ensure that message ingestion, database persistence, and LLM processing do not bottleneck each other:

1. **`ui/`**: Client interface built to handle real-time UI states and message rendering.
2. **`backend/`**: Spring Boot orchestration core managing the state machine, MongoDB Atlas pipelines, and LLM client routing.
3. **`mqtt_conf/`**: Houses the Docker Compose configurations and `mosquitto.conf` files to quickly spin up an isolated local network broker.

---

## 🛠️ Tech Stack
*   **Core Orchestration:** Java 17 / Spring Boot 3.x
*   **Messaging Protocol:** MQTT via Eclipse Paho Client
*   **Database Persistent Layer:** MongoDB Atlas (Cloud Managed Cluster)
*   **Broker Infrastructure:** Docker / Eclipse Mosquitto container from Docker hub
*   **Frontend UI:** react

---

## 🚀 Open for Contributions: LLM & API Extensibility

The backend engine utilizes a highly modular client layer to decouple the core chat state logic from external LLM providers. 

> ⚠️ **Note on LLM Integration:** External API schemas (such as OpenRouter) change over time. If you are deploying this repository and notice an API drift, we encourage you to refactor the LLM service layer!

### How to Add/Update LLM Providers:
1. Navigate to `backend/src/main/java/com/chatbot/tota_backend/service/`
2. Locate the LLM orchestration logic interface.
3. Update the HTTP payload mapping or plug in a completely new provider client (e.g., direct OpenAI, Anthropic, or Ollama for local offline execution).
4. Pull requests extending support for newer models or fixing API schema drifts are highly welcome!

---

## 💻 Getting Started Locally

### 1. Initialize the MQTT Broker
Navigate into the configuration directory and spin up the Docker network:
```bash
cd mqtt_conf
docker compose up -d

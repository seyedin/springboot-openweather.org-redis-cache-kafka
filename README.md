# 📦 springboot-openweather.org-redis-kafka

A **standalone Spring Boot application** that fetches live weather data from the **OpenWeather API**, caches the response using **Redis** with a 5-minute TTL, and publishes weather logs via **Apache Kafka** using the **publish/subscribe model**.

---

### 🚀 Features

- 🔍 Fetch real-time weather data by city name
- 🧠 Caches weather data in Redis for 5 minutes to reduce API calls
- 📬 Logs each weather fetch event to Kafka
- 🌐 Exposes REST endpoint: `GET /api/weather?city=CityName`

---

### 🛠️ Technologies Used

- **Spring Boot**
- **Redis** (with Spring Data Redis)
- **Apache Kafka**
- **RestTemplate** (for HTTP calls)
- **OpenWeather API**

---

### 📁 Project Structure

```
src/
├── config/                 # Redis & OpenWeather configuration
├── controller/             # REST API controller
├── dto/                    # Data transfer objects
├── kafka/                  # Kafka producer & consumer classes
├── service/                # Core business logic
└── WeatherAppApplication.java
```

---

### ⚙️ Configuration

You can configure Redis, Kafka, and the OpenWeather API in your `application.yml`:

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379

  kafka:
    bootstrap-servers: localhost:9092
    topic:
      weather-log: weather-logs
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer

openweather:
  api:
    key: YOUR_API_KEY_HERE
    url: https://api.openweathermap.org/data/2.5/weather
```

---

### 🌤️ How to Get OpenWeather API Key

1. Go to [OpenWeatherMap](https://openweathermap.org/api)
2. Create an account and log in
3. Navigate to **API Keys**
4. Copy your key and place it in `application.yml` under `openweather.api.key`

> ⚠️ After verifying your email, it may take a while for the API key to become active.

---

### 🧪 How to Run the App

#### 1. Start Redis & Kafka (via Docker)

```bash
# Redis
docker run -d -p 6379:6379 redis

# Kafka + Zookeeper (single-node)
docker run -d --name zookeeper -p 2181:2181 confluentinc/cp-zookeeper
docker run -d --name kafka -p 9092:9092 --env KAFKA_ZOOKEEPER_CONNECT=host.docker.internal:2181 --env KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 --env KAFKA_BROKER_ID=1 --env KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 confluentinc/cp-kafka
```

#### 2. Build & Run Spring Boot App

```bash
./mvnw spring-boot:run
```

---

### 🔗 Endpoint Example

```http
GET http://localhost:8080/api/weather?city=Tehran
```

Example response:

```json
{
  "city": "Tehran",
  "temperature": 31.0,
  "weather": "Clear",
  "description": "clear sky",
  "humidity": 20,
  "windSpeed": 3.5,
  "sunrise": "05:12",
  "sunset": "19:45"
}
```

---

### 📩 Kafka Integration

- Each successful weather data retrieval is logged via Kafka to the topic: `weather-logs`.
- The Kafka consumer logs the message to the console.

> 🧠 Note: Kafka here uses the **Publish/Subscribe model**, not a traditional message queue.

---

### ✅ Sample Output (Kafka Consumer)

```
Received Kafka message: Weather data for Tehran: 31.0°C, Clear
```

---

### 📌 Commit Info

> **Initial and final commit:** Spring Boot weather app with Redis caching & Kafka logging  
> _(Kafka uses pub/sub model instead of traditional message queue)_

---

### 🧑‍💻 Author

- Developed by: **Sanaz Seyedin**
- Contact: `sanazseyedin@example.com` *(replace with your real email)*

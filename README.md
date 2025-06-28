# springboot-openweather.org-redis-kafka

A **standalone Spring Boot application** that fetches live weather data from the **OpenWeather API**, caches the response using **Redis** with a 5-minute TTL, and publishes weather logs via **Apache Kafka** using the **publish/subscribe model**.

---

### Features

- Fetch real-time weather data by city name
- Caches weather data in Redis for 5 minutes to reduce API calls
- Logs each weather fetch event to Kafka
- Exposes REST endpoint:

  `GET /api/weather?city=CityName`

---

### Technologies Used

- **Spring Boot**
- **Redis** (with Spring Data Redis)
- **Apache Kafka**
- **RestTemplate** (for HTTP calls)
- **OpenWeather API**
---
### How It Works  
1- A GET request to /weather?city={name}:  
- Sends a request to OpenWeather API
- Maps response to a simplified DTO
- Caches the result in Redis
- Publishes a Kafka log message with summary info

2- Kafka consumer logs received weather summaries in the console.

---
### Prerequisites
You must have **Redis** and **Kafka + Zookeeper** running either:

- As **Docker containers** (recommended)
- Or **installed locally**

The configuration is explained below.

---

### Project Structure

```
src/
└── main/
    ├── java/
    │   └── ir/
    │       ├── config/                  # RedisCacheConfig, OpenWeatherConfig
    │       ├── controller/              # WeatherController: exposes the REST endpoint
    │       ├── dto/                     # Data Transfer Objects: Clouds, Wind, Sys, WeatherResponse, etc.
    │       ├── kafka/                   # Kafka integration: WeatherKafkaProducer, WeatherKafkaConsumer
    │       ├── service/                 # Business logic: WeatherService
    │       └── WeatherAppApplication.java   # Main Spring Boot application entry point
    └── resources/
        └── application.yml              # Configuration: Redis, Kafka, OpenWeather API key

```

---

### Configuration

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

### How to Get OpenWeather API Key

1. Go to [OpenWeatherMap](https://openweathermap.org/api)
2. Create an account and log in
3. Navigate to **API Keys**
4. Copy your key and place it in `application.yml` under `openweather.api.key`
```yaml
openweather:
  api:
    key: YOUR_API_KEY
    url: https://api.openweathermap.org/data/2.5/weather
```
> After verifying your email, it may take a while for the API key to become active.

---
### Kafka Setup & Docker Compose Integration

1. Kafka Configuration in Spring Boot

Kafka is configured in the `application.yml` file as follows:

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    topic:
      weather-log: weather-logs
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
```
- bootstrap-servers: Specifies the Kafka broker location.
- weather-logs: The topic used for publishing weather log messages.
- Key and value serializers are set to handle plain strings.
  
---

2. Kafka Producer & Consumer

- Kafka Producer
  The WeatherKafkaProducer class is used to publish weather log messages:
  
```java
kafkaProducer.sendWeatherLog(
    "Weather data for " + city + ": " + custom.getTemperature() + "°C, " + custom.getWeather()
);
```

This is called from within the WeatherService class after fetching and mapping weather data.  

- Kafka Producer
  The WeatherKafkaConsumer class listens to the same topic:

```java
@KafkaListener(topics = "weather-logs", groupId = "weather-group")
public void listen(ConsumerRecord<String, String> record) {
    System.out.println("Received Kafka message: " + record.value());
}
```
- Kafka messages are consumed in a pub-sub model using a consumer group (weather-group). Messages are not pulled from a queue but distributed to consumers in the group.
- Producer sends a weather log to the topic weather-logs
- Consumer (with groupId weather-group) listens and logs the message
  
### Kafka Integration

- Each successful weather data retrieval is logged via Kafka to the topic: `weather-logs`.
- The Kafka consumer logs the message to the console.

> Note: Kafka here uses the **Publish/Subscribe model**, not a traditional message queue.
---

## Docker Setup
You can run Redis, Kafka, and Zookeeper using Docker Compose.

1- Redis Setup (Standalone)  
docker-compose.redis.yml

```yaml
version: '3.8'

services:
  redis:
    image: redis:6.2
    container_name: redis
    ports:
      - "6379:6379"
```
2- Kafka + Zookeeper Setup
docker-compose.kafka.yml 
```yaml
version: '3.8'

services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.0.1
    container_name: zookeeper
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000

  kafka:
    image: confluentinc/cp-kafka:7.0.1
    container_name: kafka
    ports:
      - "9092:9092"
    depends_on:
      - zookeeper
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
```
### Run with Docker Compose  

```bash
# Step 1: Start Redis
docker-compose -f docker-compose.redis.yml up -d

# Step 2: Start Kafka & Zookeeper
docker-compose -f docker-compose.kafka.yml up -d

# Step 3: Check logs if needed
docker logs kafka
docker logs zookeeper

# Step 4: Run your Spring Boot app
./mvnw spring-boot:run
```
---

### How to Run the App

#### 1. Start Redis & Kafka (via Docker)

```bash
# Redis
docker run -d -p 6379:6379 redis

# Kafka + Zookeeper (single-node setup)
docker run -d --name zookeeper -p 2181:2181 confluentinc/cp-zookeeper

docker run -d --name kafka -p 9092:9092 \
  --env KAFKA_ZOOKEEPER_CONNECT=host.docker.internal:2181 \
  --env KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  --env KAFKA_BROKER_ID=1 \
  --env KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  confluentinc/cp-kafka
```
#### 2. Build & Run Spring Boot App

```bash
# If using Maven wrapper:
./mvnw clean install
./mvnw spring-boot:run

# Or with system Maven:
mvn clean install
mvn spring-boot:run
```

### 3. Endpoint Example (Test the API)

```http
GET http://localhost:8080/api/weather?city=Tehran
```

You should receive a JSON response like:

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
At the same time, the weather log message will be:  
- Published to Kafka topic weather-logs by the producer
- Consumed by the listener and printed in the console:
```text
Received Kafka message: Weather fetched for Tehran at 2025-06-28 19:20:42
```
---

### Sample Output (Kafka Consumer)

```
Received Kafka message: Weather data for Tehran: 31.0°C, Clear
```

---

### Commit Info

> **Initial and final commit:** Spring Boot weather app with Redis caching & Kafka logging  
> _(Kafka uses pub/sub model instead of traditional message queue)_

---

### Author

- Developed by: **Sanaz Seyedin**
- Contact: `s.s.seyedein@outlook.com`

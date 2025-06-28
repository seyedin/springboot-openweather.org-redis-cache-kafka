package ir.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class WeatherKafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(WeatherKafkaProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic.weather-log}")
    private String topic;

    public WeatherKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendWeatherLog(String message) {
        kafkaTemplate.send(topic, message);
        log.info("Message sent to topic: {} , Message: {} ", topic, message);
    }
}

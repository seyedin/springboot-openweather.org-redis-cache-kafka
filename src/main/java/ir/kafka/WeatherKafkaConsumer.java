package ir.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class WeatherKafkaConsumer {

    @KafkaListener(topics = "weather-logs", groupId = "weather-group")
    public void listen(ConsumerRecord<String, String> record) {
        System.out.println("Received Kafka message: " + record.value());
    }
}

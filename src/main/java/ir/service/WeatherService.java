package ir.service;

import ir.config.OpenWeatherConfig;
import ir.dto.CustomWeatherResponse;
import ir.dto.WeatherResponse;
import ir.kafka.WeatherKafkaProducer;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Service
public class WeatherService {

    private final OpenWeatherConfig config;
    private final RestTemplate restTemplate;
    private final WeatherKafkaProducer weatherKafkaProducer;

    public WeatherService(OpenWeatherConfig config, RestTemplate restTemplate, WeatherKafkaProducer weatherKafkaProducer) {
        this.config = config;
        this.restTemplate = restTemplate;
        this.weatherKafkaProducer = weatherKafkaProducer;
    }

    @Cacheable(value = "weatherCache", key = "#city.toLowerCase()")
    public CustomWeatherResponse getWeatherByCity(String city) {
        String url = config.getUrl() + "?q=" + city + "&appid=" + config.getKey() + "&units=metric";
        WeatherResponse fullResponse = restTemplate.getForObject(url, WeatherResponse.class);

        CustomWeatherResponse custom = new CustomWeatherResponse();
        custom.setCity(fullResponse.getName());
        custom.setTemperature(fullResponse.getMain().getTemp());

        if (fullResponse.getWeather() != null && !fullResponse.getWeather().isEmpty()) {
            custom.setWeather(fullResponse.getWeather().get(0).getMain());
            custom.setDescription(fullResponse.getWeather().get(0).getDescription());
        }

        custom.setHumidity(fullResponse.getMain().getHumidity());
        custom.setWindSpeed(fullResponse.getWind().getSpeed());
        custom.setSunrise(formatUnixTime(fullResponse.getSys().getSunrise()));
        custom.setSunset(formatUnixTime(fullResponse.getSys().getSunset()));

        // ارسال لاگ به Kafka
        weatherKafkaProducer.sendWeatherLog("Weather data for " + city + ": " +
                custom.getTemperature() + "°C, " +
                custom.getWeather() + ", " +
                custom.getDescription());

        return custom;
    }

    private String formatUnixTime(long unixSeconds) {
        Date date = new Date(unixSeconds * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Tehran"));
        return sdf.format(date);
    }
}

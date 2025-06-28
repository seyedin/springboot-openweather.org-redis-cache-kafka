package ir.controller;

import ir.service.WeatherService;
import ir.dto.CustomWeatherResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/weather")
    public CustomWeatherResponse getWeather(@RequestParam String city) {
        return weatherService.getWeatherByCity(city);
    }
}

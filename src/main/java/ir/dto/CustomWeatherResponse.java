package ir.dto;

import java.io.Serializable;

public class CustomWeatherResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String city;
    private double temperature;
    private String weather;
    private String description;
    private int humidity;
    private double windSpeed;
    private String sunrise;
    private String sunset;


    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public String getWeather() { return weather; }
    public void setWeather(String weather) { this.weather = weather; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getHumidity() { return humidity; }
    public void setHumidity(int humidity) { this.humidity = humidity; }

    public double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }

    public String getSunrise() { return sunrise; }
    public void setSunrise(String sunrise) { this.sunrise = sunrise; }

    public String getSunset() { return sunset; }
    public void setSunset(String sunset) { this.sunset = sunset; }
}

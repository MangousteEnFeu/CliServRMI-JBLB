package ch.hearc.heg.scl.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Représente les données météorologiques à un instant donné.
 * Correspond aux données retournées par l'API OpenWeatherMap.
 */
public class WeatherData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer id;                    // ID en base
    private Integer stationId;             // FK vers WEATHER_STATION
    private double temperature;            // Température en °C
    private double feelsLike;              // Température ressentie en °C
    private int humidity;                  // Humidité en %
    private int pressure;                  // Pression atmosphérique en hPa
    private String description;            // Description (ex: "ciel dégagé", "nuageux")
    private String icon;                   // Code icône OpenWeatherMap (ex: "01d")
    private double windSpeed;              // Vitesse du vent en m/s
    private LocalDateTime timestamp;       // Date/heure de la mesure
    
    // Constructeur vide
    public WeatherData() {
    }
    
    // Constructeur pour nouvelle mesure (sans ID, venant de l'API)
    public WeatherData(Integer stationId, double temperature, double feelsLike, 
                       int humidity, int pressure, String description, 
                       String icon, double windSpeed) {
        this.stationId = stationId;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.humidity = humidity;
        this.pressure = pressure;
        this.description = description;
        this.icon = icon;
        this.windSpeed = windSpeed;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters et Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getStationId() {
        return stationId;
    }
    
    public void setStationId(Integer stationId) {
        this.stationId = stationId;
    }
    
    public double getTemperature() {
        return temperature;
    }
    
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
    
    public double getFeelsLike() {
        return feelsLike;
    }
    
    public void setFeelsLike(double feelsLike) {
        this.feelsLike = feelsLike;
    }
    
    public int getHumidity() {
        return humidity;
    }
    
    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }
    
    public int getPressure() {
        return pressure;
    }
    
    public void setPressure(int pressure) {
        this.pressure = pressure;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public double getWindSpeed() {
        return windSpeed;
    }
    
    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "WeatherData{" +
                "temperature=" + temperature + "°C" +
                ", feelsLike=" + feelsLike + "°C" +
                ", humidity=" + humidity + "%" +
                ", pressure=" + pressure + "hPa" +
                ", description='" + description + '\'' +
                ", windSpeed=" + windSpeed + "m/s" +
                ", timestamp=" + timestamp +
                '}';
    }
}
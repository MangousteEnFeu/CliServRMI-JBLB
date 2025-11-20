package ch.hearc.heg.scl.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Représente une station météo identifiée par ses coordonnées géographiques.
 * Serializable car transmise via RMI.
 */
public class WeatherStation implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer id;                    // ID en base (null si pas encore persisté)
    private String name;                   // Nom de la ville/localité
    private double latitude;               // Latitude (ex: 46.9917)
    private double longitude;              // Longitude (ex: 6.9307)
    private LocalDateTime lastUpdated;     // Date/heure dernière mise à jour
    
    // Données météo actuelles (null si on ne les charge pas)
    private WeatherData currentWeather;
    
    // Constructeur vide (utile pour JDBC)
    public WeatherStation() {
    }
    
    // Constructeur avec coordonnées (création nouvelle station)
    public WeatherStation(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.lastUpdated = LocalDateTime.now();
    }
    
    // Constructeur complet (chargement depuis DB)
    public WeatherStation(Integer id, String name, double latitude, double longitude, LocalDateTime lastUpdated) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.lastUpdated = lastUpdated;
    }
    
    // Getters et Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public WeatherData getCurrentWeather() {
        return currentWeather;
    }
    
    public void setCurrentWeather(WeatherData currentWeather) {
        this.currentWeather = currentWeather;
    }
    
    @Override
    public String toString() {
        return "WeatherStation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeatherStation that = (WeatherStation) o;
        return Double.compare(that.latitude, latitude) == 0 &&
               Double.compare(that.longitude, longitude) == 0;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
}
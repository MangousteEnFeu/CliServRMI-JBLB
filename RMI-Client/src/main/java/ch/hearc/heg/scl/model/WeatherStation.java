package ch.hearc.heg.scl.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Représente une station météo identifiée par son ID OpenWeatherMap.
 * Serializable car transmise via RMI.
 */
public class WeatherStation implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;                    // ID en base (null si pas encore persisté)
    private Long openWeatherMapId;         // ID unique de l'API OpenWeatherMap
    private String name;                   // Nom de la ville/localité
    private String country;                // Code pays (ex: CH, FR)
    private double latitude;               // Latitude (ex: 46.9917)
    private double longitude;              // Longitude (ex: 6.9307)
    private LocalDateTime lastUpdated;     // Date/heure dernière mise à jour

    // Données météo actuelles (null si on ne les charge pas)
    private WeatherData currentWeather;

    // Constructeur vide (utile pour JDBC)
    public WeatherStation() {
    }

    // Constructeur avec coordonnées (création nouvelle station) - ANCIEN, gardé pour compatibilité
    public WeatherStation(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.lastUpdated = LocalDateTime.now();
    }

    //Constructeur avec ID OpenWeatherMap (création depuis l'API)
    public WeatherStation(Long openWeatherMapId, String name, String country, double latitude, double longitude) {
        this.openWeatherMapId = openWeatherMapId;
        this.name = name;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.lastUpdated = LocalDateTime.now();
    }

    // Constructeur complet (chargement depuis DB) - MODIFIÉ
    public WeatherStation(Integer id, Long openWeatherMapId, String name, String country,
                          double latitude, double longitude, LocalDateTime lastUpdated) {
        this.id = id;
        this.openWeatherMapId = openWeatherMapId;
        this.name = name;
        this.country = country;
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

    public Long getOpenWeatherMapId() {
        return openWeatherMapId;
    }

    public void setOpenWeatherMapId(Long openWeatherMapId) {
        this.openWeatherMapId = openWeatherMapId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Retourne le nom complet de la station avec le code pays.
     *
     * @return Le nom avec le pays (ex: "Neuchâtel, CH") ou juste le nom si pays absent
     */
    public String getFullName() {
        if (country != null && !country.isEmpty()) {
            return name + ", " + country;
        }
        return name;
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
                ", openWeatherMapId=" + openWeatherMapId +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", lastUpdated=" + lastUpdated +
                '}';
    }

    // MODIFIÉ : Égalité basée sur l'ID OpenWeatherMap (évite les doublons)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeatherStation that = (WeatherStation) o;
        return Objects.equals(openWeatherMapId, that.openWeatherMapId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(openWeatherMapId);
    }
}
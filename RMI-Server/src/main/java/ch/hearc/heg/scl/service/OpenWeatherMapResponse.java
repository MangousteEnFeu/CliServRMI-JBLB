package ch.hearc.heg.scl.service;

import java.util.List;

/**
 * Classe principale pour désérialiser la réponse JSON de OpenWeatherMap.
 * Correspond à la structure de l'API Current Weather Data.
 */
public class OpenWeatherMapResponse {

    private long id;
    private String name;           // Nom de la ville
    private Coord coord;           // Coordonnées
    private List<Weather> weather; // Liste des conditions météo
    private Main main;             // Données principales (temp, humidité, etc.)
    private Wind wind;             // Données du vent
    private Sys sys;

    // Getters et Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coord getCoord() {
        return coord;
    }

    public void setCoord(Coord coord) {
        this.coord = coord;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Sys getSys() { return sys; }
    public void setSys(Sys sys) { this.sys = sys; }

    // Classes internes pour la structure JSON

    public static class Coord {
        private double lat;
        private double lon;

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }
    }

    public static class Weather {
        private String description;
        private String icon;

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
    }

    public static class Main {
        private double temp;
        private double feels_like;
        private int humidity;
        private int pressure;

        public double getTemp() {
            return temp;
        }

        public void setTemp(double temp) {
            this.temp = temp;
        }

        public double getFeels_like() {
            return feels_like;
        }

        public void setFeels_like(double feels_like) {
            this.feels_like = feels_like;
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
    }

    public static class Wind {
        private double speed;

        public double getSpeed() {
            return speed;
        }

        public void setSpeed(double speed) {
            this.speed = speed;
        }
    }
    public static class Sys {
        private String country;

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
    }
}
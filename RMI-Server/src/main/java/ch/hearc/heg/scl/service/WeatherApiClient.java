package ch.hearc.heg.scl.service;

import ch.hearc.heg.scl.model.WeatherData;
import ch.hearc.heg.scl.model.WeatherStation;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Client pour interroger l'API OpenWeatherMap.
 * Utilise HttpClient (Java 11+) pour faire des appels HTTP.
 */
public class WeatherApiClient {

    private static final String API_BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    private final String apiKey;
    private final HttpClient httpClient;
    private final Gson gson;

    /**
     * Constructeur avec clé API.
     * @param apiKey Clé API OpenWeatherMap
     */
    public WeatherApiClient(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    /**
     * Récupère les données météo pour des coordonnées géographiques.
     *
     * @param latitude Latitude
     * @param longitude Longitude
     * @return Un objet WeatherStation avec les données météo actuelles
     * @throws IOException Si l'appel HTTP échoue
     * @throws IllegalArgumentException Si les coordonnées sont invalides
     */
    public WeatherStation getWeatherByCoordinates(double latitude, double longitude) throws IOException {
        // Validation des coordonnées
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude doit être entre -90 et 90");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude doit être entre -180 et 180");
        }

        // Construction de l'URL avec paramètres
        String url = String.format("%s?lat=%.6f&lon=%.6f&appid=%s&units=metric&lang=fr",
                API_BASE_URL, latitude, longitude, apiKey);

        // Création de la requête HTTP
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            // Envoi de la requête et récupération de la réponse
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Vérification du code de statut HTTP
            if (response.statusCode() != 200) {
                // Gérer les cas d'erreur courants
                if (response.statusCode() == 401) {
                    throw new IOException("Clé API invalide. Vérifiez votre fichier database.properties");
                } else if (response.statusCode() == 404) {
                    throw new IOException("Aucune station météo trouvée pour ces coordonnées");
                } else if (response.statusCode() == 429) {
                    throw new IOException("Limite de requêtes API atteinte. Veuillez réessayer plus tard");
                } else {
                    throw new IOException("Erreur API (code " + response.statusCode() + ")");
                }
            }

            // Désérialisation JSON vers objet Java
            OpenWeatherMapResponse apiResponse = gson.fromJson(response.body(), OpenWeatherMapResponse.class);

            // Conversion en objets du modèle
            return convertToWeatherStation(apiResponse);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Requête interrompue", e);
        }
    }

    /**
     * Convertit la réponse de l'API en objets du modèle métier.
     */
    private WeatherStation convertToWeatherStation(OpenWeatherMapResponse apiResponse) {
        OpenWeatherMapResponse.Coord coord = apiResponse.getCoord();
        OpenWeatherMapResponse.Sys sys = apiResponse.getSys();

        // Créer la station avec l'ID OpenWeatherMap
        WeatherStation station = new WeatherStation(
                apiResponse.getId(),                          // ID OpenWeatherMap
                apiResponse.getName(),
                sys != null ? sys.getCountry() : null,        // Pays
                coord.getLat(),
                coord.getLon()
        );

        // Créer les données météo
        OpenWeatherMapResponse.Main main = apiResponse.getMain();
        OpenWeatherMapResponse.Weather weather = apiResponse.getWeather().get(0);
        OpenWeatherMapResponse.Wind wind = apiResponse.getWind();

        WeatherData weatherData = new WeatherData(
                null,  // stationId sera défini plus tard
                main.getTemp(),
                main.getFeels_like(),
                main.getHumidity(),
                main.getPressure(),
                weather.getDescription(),
                weather.getIcon(),
                wind.getSpeed()
        );

        // Associer les données à la station
        station.setCurrentWeather(weatherData);

        return station;
    }
}
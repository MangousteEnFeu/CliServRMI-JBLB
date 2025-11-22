package ch.hearc.heg.scl;

import ch.hearc.heg.scl.model.WeatherStation;
import ch.hearc.heg.scl.service.WeatherApiClient;

import java.io.IOException;

/**
 * Classe de test pour vérifier que l'appel API fonctionne.
 * À supprimer une fois les tests terminés.
 */
public class TestWeatherApi {

    public static void main(String[] args) {

        String apiKey = "55adad33f202156b4aa5fe494059bd36";

        WeatherApiClient client = new WeatherApiClient(apiKey);

        try {
            // Test avec La Chaux-de-Fonds
            System.out.println("=== Test avec La Chaux-de-Fonds ===");
            WeatherStation station = client.getWeatherByCoordinates(47.1, 6.83);

            System.out.println("Ville : " + station.getName());
            System.out.println("Coordonnées : " + station.getLatitude() + ", " + station.getLongitude());
            System.out.println("Données météo : " + station.getCurrentWeather());

            System.out.println("\n=== Test avec Neuchâtel ===");
            WeatherStation station2 = client.getWeatherByCoordinates(46.99, 6.93);
            System.out.println("Ville : " + station2.getName());
            System.out.println("Données météo : " + station2.getCurrentWeather());

        } catch (IOException e) {
            System.err.println("Erreur lors de l'appel API : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
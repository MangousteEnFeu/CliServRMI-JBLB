package ch.hearc.heg.scl.rmi;

import ch.hearc.heg.scl.dao.WeatherDataDAO;
import ch.hearc.heg.scl.dao.WeatherStationDAO;
import ch.hearc.heg.scl.model.WeatherData;
import ch.hearc.heg.scl.model.WeatherStation;
import ch.hearc.heg.scl.service.WeatherApiClient;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.List;

/**
 * Implémentation du service RMI pour la gestion des stations météo.
 * Orchestre les appels à l'API météo et la persistance en base de données.
 */
public class WeatherServiceImpl extends UnicastRemoteObject implements WeatherService {

    private final WeatherStationDAO stationDAO;
    private final WeatherDataDAO weatherDataDAO;
    private final WeatherApiClient apiClient;

    /**
     * Constructeur avec injection des dépendances.
     *
     * @param apiKey Clé API OpenWeatherMap
     * @throws RemoteException Si erreur d'initialisation RMI
     */
    public WeatherServiceImpl(String apiKey) throws RemoteException {
        super();
        this.stationDAO = new WeatherStationDAO();
        this.weatherDataDAO = new WeatherDataDAO();
        this.apiClient = new WeatherApiClient(apiKey);
    }

    @Override
    public WeatherStation getStationByCoordinates(double latitude, double longitude) throws RemoteException {
        try {
            // 1. Vérifier si la station existe déjà en base
            WeatherStation existingStation = stationDAO.findByCoordinates(latitude, longitude);

            if (existingStation != null) {
                System.out.println("Station trouvée en base : " + existingStation.getName());

                // Charger les données météo actuelles depuis la base
                WeatherData latestWeather = weatherDataDAO.findLatestByStationId(existingStation.getId());
                existingStation.setCurrentWeather(latestWeather);

                return existingStation;
            }

            // 2. Si la station n'existe pas, interroger l'API
            System.out.println("Station non trouvée, interrogation de l'API...");
            WeatherStation newStation = apiClient.getWeatherByCoordinates(latitude, longitude);

            // 3. VALIDATION : Vérifier que l'API a retourné un nom valide
            if (newStation.getName() == null || newStation.getName().trim().isEmpty()) {
                throw new IllegalArgumentException(
                        "Aucune station météo trouvée pour ces coordonnées. " +
                                "Veuillez vérifier que les coordonnées correspondent à une zone habitée."
                );
            }

            // 4. Persister la nouvelle station
            newStation = stationDAO.insert(newStation);
            System.out.println("Nouvelle station créée avec ID : " + newStation.getId());

            // 5. Persister les données météo
            WeatherData weatherData = newStation.getCurrentWeather();
            weatherData.setStationId(newStation.getId());
            weatherData = weatherDataDAO.insert(weatherData);

            // 6. Réassocier les données à la station
            newStation.setCurrentWeather(weatherData);

            return newStation;

        } catch (SQLException e) {
            System.err.println("Erreur base de données : " + e.getMessage());
            throw new RemoteException("Erreur lors de l'accès à la base de données", e);
        } catch (IOException e) {
            System.err.println("Erreur API météo : " + e.getMessage());
            throw new RemoteException("Erreur lors de l'appel à l'API météo", e);
        } catch (IllegalArgumentException e) {
            System.err.println("Coordonnées invalides : " + e.getMessage());
            throw new RemoteException(e.getMessage(), e);
        }
    }

    @Override
    public List<WeatherStation> getAllStations() throws RemoteException {
        try {
            List<WeatherStation> stations = stationDAO.findAll();
            System.out.println("Récupération de " + stations.size() + " station(s)");
            return stations;

        } catch (SQLException e) {
            System.err.println("Erreur base de données : " + e.getMessage());
            throw new RemoteException("Erreur lors de la récupération des stations", e);
        }
    }

    @Override
    public WeatherStation getStationWithWeatherData(int stationId) throws RemoteException {
        try {
            // 1. Récupérer la station
            WeatherStation station = stationDAO.findById(stationId);

            if (station == null) {
                throw new IllegalArgumentException("Station non trouvée avec l'ID : " + stationId);
            }

            // 2. Récupérer les données météo les plus récentes
            WeatherData latestWeather = weatherDataDAO.findLatestByStationId(stationId);
            station.setCurrentWeather(latestWeather);

            System.out.println("Station récupérée : " + station.getName());
            return station;

        } catch (SQLException e) {
            System.err.println("Erreur base de données : " + e.getMessage());
            throw new RemoteException("Erreur lors de la récupération de la station", e);
        } catch (IllegalArgumentException e) {
            throw new RemoteException(e.getMessage(), e);
        }
    }

    @Override
    public int refreshAllStations() throws RemoteException {
        try {
            // 1. Récupérer toutes les stations
            List<WeatherStation> stations = stationDAO.findAll();
            int successCount = 0;

            System.out.println("Rafraîchissement de " + stations.size() + " station(s)...");

            // 2. Pour chaque station, interroger l'API et mettre à jour
            for (WeatherStation station : stations) {
                try {
                    // Appel API pour obtenir les nouvelles données
                    WeatherStation updatedStation = apiClient.getWeatherByCoordinates(
                            station.getLatitude(),
                            station.getLongitude()
                    );

                    // Persister les nouvelles données météo
                    WeatherData newWeatherData = updatedStation.getCurrentWeather();
                    newWeatherData.setStationId(station.getId());
                    weatherDataDAO.insert(newWeatherData);

                    // Mettre à jour la date de dernière mise à jour
                    stationDAO.updateLastUpdated(station.getId());

                    successCount++;
                    System.out.println("Station mise à jour : " + station.getName());

                } catch (IOException e) {
                    System.err.println("Échec pour " + station.getName() + " : " + e.getMessage());
                    // Continue avec les autres stations
                }
            }

            System.out.println("Rafraîchissement terminé : " + successCount + "/" + stations.size());
            return successCount;

        } catch (SQLException e) {
            System.err.println("Erreur base de données : " + e.getMessage());
            throw new RemoteException("Erreur lors du rafraîchissement des stations", e);
        }
    }
}
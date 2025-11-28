package ch.hearc.heg.scl;

import ch.hearc.heg.scl.model.WeatherStation;
import ch.hearc.heg.scl.rmi.WeatherService;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

/**
 * Client RMI avec menu interactif pour interroger le service météo.
 */
public class ClientMenu {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 1099;
    private static final String SERVICE_NAME = "WeatherService";

    private WeatherService weatherService;
    private Scanner scanner;

    public ClientMenu() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Connexion au serveur RMI.
     */
    public void connect() throws RemoteException, NotBoundException {
        System.out.println("=== Client RMI Météo ===");
        System.out.println("Connexion au serveur " + SERVER_HOST + ":" + SERVER_PORT + "...");

        Registry registry = LocateRegistry.getRegistry(SERVER_HOST, SERVER_PORT);
        weatherService = (WeatherService) registry.lookup(SERVICE_NAME);

        System.out.println("✓ Connecté au service météo\n");
    }

    /**
     * Affiche le menu principal et gère les choix utilisateur.
     */
    public void showMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║       SERVICE MÉTÉO - MENU PRINCIPAL   ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.println("1. Rechercher une station par coordonnées");
            System.out.println("2. Lister toutes les stations");
            System.out.println("3. Afficher les détails d'une station");
            System.out.println("4. Rafraîchir toutes les stations");
            System.out.println("5. Quitter");
            System.out.print("\nVotre choix : ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1 -> searchStationByCoordinates();
                    case 2 -> listAllStations();
                    case 3 -> showStationDetails();
                    case 4 -> refreshAllStations();
                    case 5 -> {
                        System.out.println("\nAu revoir !");
                        running = false;
                    }
                    default -> System.out.println("\nChoix invalide. Veuillez choisir entre 1 et 5.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\nVeuillez entrer un nombre valide.");
            } catch (RemoteException e) {
                System.err.println("\nErreur de communication avec le serveur : " + e.getMessage());
            }
        }

        scanner.close();
    }

    /**
     * Option 1 : Rechercher une station par coordonnées.
     */
    private void searchStationByCoordinates() throws RemoteException {
        System.out.println("\n--- Recherche par coordonnées ---");
        System.out.println("\n💡 Exemples de coordonnées suisses:");
        System.out.println("   • La Chaux-de-Fonds: 47.1 / 6.83");
        System.out.println("   • Neuchâtel: 46.99 / 6.93");
        System.out.println("   • Berne: 46.95 / 7.44");
        System.out.println("   • Genève: 46.20 / 6.15\n");

        try {
            // Saisie et validation de la latitude
            System.out.print("Latitude (-90 à 90) : ");
            double latitude = Double.parseDouble(scanner.nextLine());

            if (latitude < -90 || latitude > 90) {
                System.out.println("Erreur : La latitude doit être entre -90 et 90");
                return;
            }

            // Saisie et validation de la longitude
            System.out.print("Longitude (-180 à 180) : ");
            double longitude = Double.parseDouble(scanner.nextLine());

            if (longitude < -180 || longitude > 180) {
                System.out.println("Erreur : La longitude doit être entre -180 et 180");
                return;
            }

            System.out.println("\n⏳ Recherche en cours...");
            WeatherStation station = weatherService.getStationByCoordinates(latitude, longitude);

            if (station != null) {
                System.out.println("\nStation trouvée !");
                displayStationWithWeather(station);
            } else {
                System.out.println("\nAucune station trouvée pour ces coordonnées.");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nVeuillez entrer des nombres valides pour les coordonnées.");
        } catch (RemoteException e) {
            // Gestion propre des erreurs sans détection par emoji
            System.err.println("\nErreur : " + e.getMessage());
        }
    }

    /**
     * Option 2 : Lister toutes les stations.
     */
    private void listAllStations() throws RemoteException {
        System.out.println("\n--- Liste des stations ---");
        List<WeatherStation> stations = weatherService.getAllStations();

        if (stations.isEmpty()) {
            System.out.println("Aucune station enregistrée.");
        } else {
            System.out.println("\n✅ " + stations.size() + " station(s) trouvée(s) :\n");
            System.out.println("┌──────┬──────────────┬──────────┬─────────────────────────┬────────────┬─────────────┐");
            System.out.println("│  ID  │  ID OWM      │  Pays    │         Nom             │  Latitude  │  Longitude  │");
            System.out.println("├──────┼──────────────┼──────────┼─────────────────────────┼────────────┼─────────────┤");

            for (WeatherStation station : stations) {
                System.out.printf("│ %-4d │ %-12d │ %-8s │ %-23s │ %10.6f │ %11.6f │%n",
                        station.getId(),
                        station.getOpenWeatherMapId(),
                        station.getCountry() != null ? station.getCountry() : "N/A",
                        truncate(station.getName(), 23),
                        station.getLatitude(),
                        station.getLongitude());
            }
            System.out.println("└──────┴──────────────┴──────────┴─────────────────────────┴────────────┴─────────────┘");
        }
    }

    /**
     * Option 3 : Afficher les détails d'une station.
     */
    private void showStationDetails() throws RemoteException {
        System.out.println("\n--- Détails d'une station ---");

        try {
            System.out.print("ID de la station : ");
            int stationId = Integer.parseInt(scanner.nextLine());

            System.out.println("\nChargement...");
            WeatherStation station = weatherService.getStationWithWeatherData(stationId);

            if (station != null) {
                displayStationWithWeather(station);
            } else {
                System.out.println("\nStation introuvable.");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nVeuillez entrer un ID valide (nombre entier).");
        } catch (RemoteException e) {
            System.err.println("\nErreur : " + e.getMessage());
        }
    }

    /**
     * Option 4 : Rafraîchir toutes les stations.
     */
    private void refreshAllStations() throws RemoteException {
        System.out.println("\n--- Rafraîchissement des stations ---");
        System.out.print("⚠Cette opération peut prendre du temps. Continuer ? (o/n) : ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("o") || confirm.equalsIgnoreCase("oui")) {
            System.out.println("\n⏳ Rafraîchissement en cours...");
            int updatedCount = weatherService.refreshAllStations();
            System.out.println("\n" + updatedCount + " station(s) mise(s) à jour avec succès !");
        } else {
            System.out.println("\nOpération annulée.");
        }
    }

    /**
     * Affiche une station avec ses données météo détaillées.
     */
    private void displayStationWithWeather(WeatherStation station) {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║          STATION MÉTÉO                 ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("ID DB       : " + station.getId());
        System.out.println("ID OWM      : " + station.getOpenWeatherMapId());
        System.out.println("Nom         : " + station.getFullName());
        System.out.println("Coordonnées : " + station.getLatitude() + ", " + station.getLongitude());
        System.out.println("Dernière MÀJ: " + station.getLastUpdated());

        if (station.getCurrentWeather() != null) {
            System.out.println("\n--- Données météo actuelles ---");
            System.out.println("Température      : " + station.getCurrentWeather().getTemperature() + "°C");
            System.out.println("Ressenti         : " + station.getCurrentWeather().getFeelsLike() + "°C");
            System.out.println("Humidité         : " + station.getCurrentWeather().getHumidity() + "%");
            System.out.println("Pression         : " + station.getCurrentWeather().getPressure() + " hPa");
            System.out.println("Description      : " + station.getCurrentWeather().getDescription());
            System.out.println("Vent             : " + station.getCurrentWeather().getWindSpeed() + " m/s");
            System.out.println("Horodatage       : " + station.getCurrentWeather().getTimestamp());
        } else {
            System.out.println("\nAucune donnée météo disponible.");
        }
    }

    /**
     * Tronque une chaîne si elle dépasse la longueur maximale.
     *
     * @param str La chaîne à tronquer
     * @param maxLength Longueur maximale
     * @return La chaîne tronquée avec "..." si nécessaire, ou la chaîne originale
     */
    private String truncate(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }

    /**
     * Point d'entrée du client.
     */
    public static void main(String[] args) {
        ClientMenu client = new ClientMenu();

        try {
            client.connect();
            client.showMenu();
        } catch (RemoteException e) {
            System.err.println("Erreur de connexion au serveur RMI : " + e.getMessage());
            System.err.println("Assurez-vous que le serveur est démarré.");
        } catch (NotBoundException e) {
            System.err.println("Service introuvable : " + e.getMessage());
        }
    }
}
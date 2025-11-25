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

        try {
            System.out.print("Latitude (-90 à 90) : ");
            double latitude = Double.parseDouble(scanner.nextLine());

            System.out.print("Longitude (-180 à 180) : ");
            double longitude = Double.parseDouble(scanner.nextLine());

            System.out.println("\nRecherche en cours...");
            WeatherStation station = weatherService.getStationByCoordinates(latitude, longitude);

            if (station != null) {
                System.out.println("\nStation trouvée !");
                displayStationWithWeather(station);
            } else {
                System.out.println("\nAucune station trouvée pour ces coordonnées.");
            }
        } catch (NumberFormatException e) {
            System.out.println("/nVeuillez entrer des nombres valides pour les coordonnées.");
        } catch (RemoteException e) {
            // Extraire le message d'erreur plus propre
            String errorMessage = e.getMessage();
            if (errorMessage.contains("Aucune station météo trouvée")) {
                System.out.println(errorMessage);
            } else {
                System.err.println("\nErreur de communication avec le serveur : " + errorMessage);
            }
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
            System.out.println("\n" + stations.size() + " station(s) trouvée(s) :\n");
            System.out.println("┌──────┬─────────────────────────┬────────────┬─────────────┐");
            System.out.println("│  ID  │         Nom             │  Latitude  │  Longitude  │");
            System.out.println("├──────┼─────────────────────────┼────────────┼─────────────┤");

            for (WeatherStation station : stations) {
                System.out.printf("│ %-4d │ %-23s │ %10.6f │ %11.6f │%n",
                        station.getId(),
                        truncate(station.getName(), 23),
                        station.getLatitude(),
                        station.getLongitude());
            }
            System.out.println("└──────┴─────────────────────────┴────────────┴─────────────┘");
        }
    }

    /**
     * Option 3 : Afficher les détails d'une station.
     */
    private void showStationDetails() throws RemoteException {
        System.out.println("\n--- Détails d'une station ---");
        System.out.print("ID de la station : ");
        int stationId = Integer.parseInt(scanner.nextLine());

        System.out.println("\n⏳ Chargement...");
        WeatherStation station = weatherService.getStationWithWeatherData(stationId);

        if (station != null) {
            displayStationWithWeather(station);
        } else {
            System.out.println("\n❌ Station introuvable.");
        }
    }

    /**
     * Option 4 : Rafraîchir toutes les stations.
     */
    private void refreshAllStations() throws RemoteException {
        System.out.println("\n--- Rafraîchissement des stations ---");
        System.out.print("⚠️  Cette opération peut prendre du temps. Continuer ? (o/n) : ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("o") || confirm.equalsIgnoreCase("oui")) {
            System.out.println("\n⏳ Rafraîchissement en cours...");
            int updatedCount = weatherService.refreshAllStations();
            System.out.println("\n✓ " + updatedCount + " station(s) mise(s) à jour avec succès !");
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
        System.out.println("ID          : " + station.getId());
        System.out.println("Nom         : " + station.getName());
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
            System.out.println("\n❌ Aucune donnée météo disponible.");
        }
    }

    /**
     * Tronque une chaîne si elle dépasse la longueur maximale.
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
            System.err.println("❌ Erreur de connexion au serveur RMI : " + e.getMessage());
            System.err.println("Assurez-vous que le serveur est démarré.");
        } catch (NotBoundException e) {
            System.err.println("❌ Service introuvable : " + e.getMessage());
        }
    }
}
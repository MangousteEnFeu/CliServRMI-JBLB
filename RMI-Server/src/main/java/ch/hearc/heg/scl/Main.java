package ch.hearc.heg.scl;

import ch.hearc.heg.scl.database.DatabaseConfig;
import ch.hearc.heg.scl.rmi.WeatherServiceImpl;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;

/**
 * Point d'entrée du serveur RMI météo.
 * Initialise le registre RMI et enregistre le service WeatherService.
 */
public class Main {

    private static final int RMI_PORT = 1099;
    private static final String SERVICE_NAME = "WeatherService";

    public static void main(String[] args) {
        try {
            // 1. Vérifier la connexion à la base de données
            System.out.println("=== Démarrage du serveur RMI Météo ===");
            System.out.println("Vérification de la connexion à la base de données...");
            DatabaseConfig.getConnection();

            // 2. Créer le registre RMI
            System.out.println("Création du registre RMI sur le port " + RMI_PORT + "...");
            Registry registry = LocateRegistry.createRegistry(RMI_PORT);

            // 3. Créer et enregistrer le service météo
            System.out.println("Initialisation du service météo...");
            WeatherServiceImpl weatherService = new WeatherServiceImpl(DatabaseConfig.getApiKey());  // ← MODIF ICI
            registry.rebind(SERVICE_NAME, weatherService);

            System.out.println("\n=== Serveur RMI prêt ===");
            System.out.println("Service : " + SERVICE_NAME);
            System.out.println("Port : " + RMI_PORT);
            System.out.println("En attente de connexions clients...\n");

            // 4. Ajouter un hook pour fermer proprement la connexion DB
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nArrêt du serveur...");
                DatabaseConfig.closeConnection();
            }));

        } catch (RemoteException e) {
            System.err.println("Erreur RMI : " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
            System.err.println("Vérifiez votre fichier database.properties");
            e.printStackTrace();
        }
    }
}
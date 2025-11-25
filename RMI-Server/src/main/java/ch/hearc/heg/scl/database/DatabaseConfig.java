package ch.hearc.heg.scl.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Gestion de la connexion à la base de données Oracle.
 * Les identifiants sont chargés depuis database.properties.
 */
public class DatabaseConfig {

    private static String URL;
    private static String USERNAME;
    private static String PASSWORD;
    private static Connection connection = null;

    static {
        loadConfiguration();
    }

    /**
     * Charge la configuration depuis le fichier database.properties.
     */
    private static void loadConfiguration() {
        Properties props = new Properties();

        try (InputStream input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("database.properties")) {

            if (input == null) {
                throw new IOException("Fichier database.properties introuvable.\n" +
                        "Veuillez copier database.properties.template vers database.properties " +
                        "et configurer vos identifiants.");
            }

            props.load(input);

            URL = props.getProperty("db.url");
            USERNAME = props.getProperty("db.username");
            PASSWORD = props.getProperty("db.password");

            // Validation
            if (URL == null || USERNAME == null || PASSWORD == null) {
                throw new IllegalStateException("Configuration incomplète dans database.properties");
            }

            System.out.println("Configuration de base de données chargée");

        } catch (IOException e) {
            System.err.println("ERREUR : Impossible de charger la configuration de la base de données");
            System.err.println("   " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Récupère la connexion à la base de données (singleton).
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Connexion à la base de données établie");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver Oracle JDBC non trouvé", e);
            }
        }
        return connection;
    }

    /**
     * Ferme la connexion à la base de données.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connexion à la base de données fermée");
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
        }
    }
}
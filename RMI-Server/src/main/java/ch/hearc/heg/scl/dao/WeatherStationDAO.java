package ch.hearc.heg.scl.dao;

import ch.hearc.heg.scl.database.DatabaseConfig;
import ch.hearc.heg.scl.model.WeatherStation;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object pour la table WEATHER_STATION.
 * Gère toutes les opérations CRUD sur les stations météo.
 */
public class WeatherStationDAO {

    /**
     * Recherche une station par ses coordonnées géographiques.
     *
     * @param latitude Latitude de la station
     * @param longitude Longitude de la station
     * @return La station trouvée, ou null si elle n'existe pas
     */
    public WeatherStation findByCoordinates(double latitude, double longitude) throws SQLException {
        String sql = "SELECT ID, NAME, LATITUDE, LONGITUDE, LAST_UPDATED " +
                "FROM WEATHER_STATION " +
                "WHERE LATITUDE = ? AND LONGITUDE = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, latitude);
            stmt.setDouble(2, longitude);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStation(rs);
                }
            }
        }
        return null;
    }

    /**
     * Insère une nouvelle station dans la base de données.
     *
     * @param station La station à insérer (sans ID)
     * @return La station avec son ID généré
     */
    public WeatherStation insert(WeatherStation station) throws SQLException {
        String sql = "INSERT INTO WEATHER_STATION (ID, NAME, LATITUDE, LONGITUDE, LAST_UPDATED) " +
                "VALUES (WEATHER_STATION_SEQ.NEXTVAL, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"ID"})) {

            stmt.setString(1, station.getName());
            stmt.setDouble(2, station.getLatitude());
            stmt.setDouble(3, station.getLongitude());
            stmt.setTimestamp(4, Timestamp.valueOf(station.getLastUpdated()));

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Récupération de l'ID généré
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        station.setId(generatedKeys.getInt(1));
                    }
                }
            }
        }
        return station;
    }

    /**
     * Met à jour la date de dernière mise à jour d'une station.
     *
     * @param stationId ID de la station
     */
    public void updateLastUpdated(int stationId) throws SQLException {
        String sql = "UPDATE WEATHER_STATION SET LAST_UPDATED = ? WHERE ID = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(2, stationId);

            stmt.executeUpdate();
        }
    }

    /**
     * Récupère toutes les stations de la base de données.
     *
     * @return Liste de toutes les stations
     */
    public List<WeatherStation> findAll() throws SQLException {
        List<WeatherStation> stations = new ArrayList<>();
        String sql = "SELECT ID, NAME, LATITUDE, LONGITUDE, LAST_UPDATED " +
                "FROM WEATHER_STATION " +
                "ORDER BY NAME";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                stations.add(mapResultSetToStation(rs));
            }
        }
        return stations;
    }

    /**
     * Récupère une station par son ID.
     * @param id ID de la station
     * @return La station trouvée, ou null si elle n'existe pas
     */
    public WeatherStation findById(int id) throws SQLException {
        String sql = "SELECT ID, NAME, LATITUDE, LONGITUDE, LAST_UPDATED " +
                "FROM WEATHER_STATION " +
                "WHERE ID = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStation(rs);
                }
            }
        }
        return null;
    }

    /**
     * Convertit un ResultSet en objet WeatherStation.
     */
    private WeatherStation mapResultSetToStation(ResultSet rs) throws SQLException {
        return new WeatherStation(
                rs.getInt("ID"),
                rs.getString("NAME"),
                rs.getDouble("LATITUDE"),
                rs.getDouble("LONGITUDE"),
                rs.getTimestamp("LAST_UPDATED").toLocalDateTime()
        );
    }
}

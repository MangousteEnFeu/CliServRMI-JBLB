package ch.hearc.heg.scl.dao;

import ch.hearc.heg.scl.database.DatabaseConfig;
import ch.hearc.heg.scl.model.WeatherStation;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object pour la table WEATHER_STATION.
 * Gère toutes les opérations CRUD sur les stations météo.
 */
public class WeatherStationDAO {

    /**
     * Recherche une station par son ID OpenWeatherMap.
     *
     * @param openWeatherMapId ID unique de l'API OpenWeatherMap
     * @return Optional contenant la station si trouvée
     */
    public Optional<WeatherStation> findByOpenWeatherMapId(long openWeatherMapId) throws SQLException {
        String sql = "SELECT ID, OPENWEATHERMAP_ID, NAME, COUNTRY, LATITUDE, LONGITUDE, LAST_UPDATED " +
                "FROM WEATHER_STATION WHERE OPENWEATHERMAP_ID = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, openWeatherMapId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStation(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Insère une nouvelle station dans la base de données.
     *
     * @param station La station à insérer (sans ID)
     * @return La station avec son ID généré
     */
    public WeatherStation insert(WeatherStation station) throws SQLException {
        String sql = "INSERT INTO WEATHER_STATION " +
                "(ID, OPENWEATHERMAP_ID, NAME, COUNTRY, LATITUDE, LONGITUDE, LAST_UPDATED) " +
                "VALUES (WEATHER_STATION_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"ID"})) {

            stmt.setLong(1, station.getOpenWeatherMapId());
            stmt.setString(2, station.getName());
            stmt.setString(3, station.getCountry());
            stmt.setDouble(4, station.getLatitude());
            stmt.setDouble(5, station.getLongitude());
            stmt.setTimestamp(6, Timestamp.valueOf(station.getLastUpdated()));

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
        String sql = "SELECT ID, OPENWEATHERMAP_ID, NAME, COUNTRY, LATITUDE, LONGITUDE, LAST_UPDATED " +
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
        String sql = "SELECT ID, OPENWEATHERMAP_ID, NAME, COUNTRY, LATITUDE, LONGITUDE, LAST_UPDATED " +
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
        WeatherStation station = new WeatherStation();
        station.setId(rs.getInt("ID"));
        station.setOpenWeatherMapId(rs.getLong("OPENWEATHERMAP_ID"));
        station.setName(rs.getString("NAME"));
        station.setCountry(rs.getString("COUNTRY"));
        station.setLatitude(rs.getDouble("LATITUDE"));
        station.setLongitude(rs.getDouble("LONGITUDE"));
        station.setLastUpdated(rs.getTimestamp("LAST_UPDATED").toLocalDateTime());
        return station;
    }
}

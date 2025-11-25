package ch.hearc.heg.scl.dao;

import ch.hearc.heg.scl.database.DatabaseConfig;
import ch.hearc.heg.scl.model.WeatherData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object pour la table WEATHER_DATA.
 * Gère toutes les opérations CRUD sur les données météorologiques.
 */
public class WeatherDataDAO {

    /**
     * Insère de nouvelles données météo dans la base.
     *
     * @param weatherData Les données météo à insérer
     * @return Les données avec leur ID généré
     */
    public WeatherData insert(WeatherData weatherData) throws SQLException {
        String sql = "INSERT INTO WEATHER_DATA " +
                "(ID, STATION_ID, TEMPERATURE, FEELS_LIKE, HUMIDITY, PRESSURE, " +
                "DESCRIPTION, ICON, WIND_SPEED, TIMESTAMP) " +
                "VALUES (WEATHER_DATA_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"ID"})) {

            stmt.setInt(1, weatherData.getStationId());
            stmt.setDouble(2, weatherData.getTemperature());
            stmt.setDouble(3, weatherData.getFeelsLike());
            stmt.setInt(4, weatherData.getHumidity());
            stmt.setInt(5, weatherData.getPressure());
            stmt.setString(6, weatherData.getDescription());
            stmt.setString(7, weatherData.getIcon());
            stmt.setDouble(8, weatherData.getWindSpeed());
            stmt.setTimestamp(9, Timestamp.valueOf(weatherData.getTimestamp()));

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Récupération de l'ID généré
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        weatherData.setId(generatedKeys.getInt(1));
                    }
                }
            }
        }
        return weatherData;
    }

    /**
     * Récupère les dernières données météo d'une station.
     *
     * @param stationId ID de la station
     * @return Les données météo les plus récentes, ou null si aucune
     */
    public WeatherData findLatestByStationId(int stationId) throws SQLException {
        String sql = "SELECT ID, STATION_ID, TEMPERATURE, FEELS_LIKE, HUMIDITY, PRESSURE, " +
                "DESCRIPTION, ICON, WIND_SPEED, TIMESTAMP " +
                "FROM WEATHER_DATA " +
                "WHERE STATION_ID = ? " +
                "ORDER BY TIMESTAMP DESC " +
                "FETCH FIRST 1 ROW ONLY";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, stationId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToWeatherData(rs);
                }
            }
        }
        return null;
    }

    /**
     * Récupère toutes les données météo d'une station, triées par date décroissante.
     *
     * @param stationId ID de la station
     * @return Liste des données météo
     */
    public List<WeatherData> findAllByStationId(int stationId) throws SQLException {
        List<WeatherData> dataList = new ArrayList<>();
        String sql = "SELECT ID, STATION_ID, TEMPERATURE, FEELS_LIKE, HUMIDITY, PRESSURE, " +
                "DESCRIPTION, ICON, WIND_SPEED, TIMESTAMP " +
                "FROM WEATHER_DATA " +
                "WHERE STATION_ID = ? " +
                "ORDER BY TIMESTAMP DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, stationId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dataList.add(mapResultSetToWeatherData(rs));
                }
            }
        }
        return dataList;
    }

    /**
     * Convertit un ResultSet en objet WeatherData.
     */
    private WeatherData mapResultSetToWeatherData(ResultSet rs) throws SQLException {
        WeatherData data = new WeatherData();
        data.setId(rs.getInt("ID"));
        data.setStationId(rs.getInt("STATION_ID"));
        data.setTemperature(rs.getDouble("TEMPERATURE"));
        data.setFeelsLike(rs.getDouble("FEELS_LIKE"));
        data.setHumidity(rs.getInt("HUMIDITY"));
        data.setPressure(rs.getInt("PRESSURE"));
        data.setDescription(rs.getString("DESCRIPTION"));
        data.setIcon(rs.getString("ICON"));
        data.setWindSpeed(rs.getDouble("WIND_SPEED"));
        data.setTimestamp(rs.getTimestamp("TIMESTAMP").toLocalDateTime());
        return data;
    }
}

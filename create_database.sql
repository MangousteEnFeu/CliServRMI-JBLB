-- ========================================
-- Script de création de la base de données
-- Projet Météo RMI - HE-Arc
-- ========================================

-- Suppression des tables si elles existent (pour tests)
DROP TABLE WEATHER_DATA CASCADE CONSTRAINTS;
DROP TABLE WEATHER_STATION CASCADE CONSTRAINTS;
DROP SEQUENCE WEATHER_STATION_SEQ;
DROP SEQUENCE WEATHER_DATA_SEQ;

-- ========================================
-- Table WEATHER_STATION
-- Stocke les stations météo identifiées par leurs coordonnées
-- ========================================
CREATE TABLE WEATHER_STATION (
    ID              NUMBER(10)      PRIMARY KEY,
    NAME            VARCHAR2(100)   NOT NULL UNIQUE,
    COUNTRY         VARCHAR2(10),
    LATITUDE        NUMBER(10,6)    NOT NULL,
    LONGITUDE       NUMBER(10,6)    NOT NULL,
    LAST_UPDATED    TIMESTAMP       NOT NULL

);

-- Séquence pour l'auto-incrémentation de l'ID
CREATE SEQUENCE WEATHER_STATION_SEQ 
    START WITH 1 
    INCREMENT BY 1 
    NOCACHE 
    NOCYCLE;

-- ========================================
-- Table WEATHER_DATA
-- Stocke les données météorologiques pour chaque station
-- ========================================
CREATE TABLE WEATHER_DATA (
    ID              NUMBER(10)      PRIMARY KEY,
    STATION_ID      NUMBER(10)      NOT NULL,
    TEMPERATURE     NUMBER(5,2)     NOT NULL,   -- Ex: 23.45°C
    FEELS_LIKE      NUMBER(5,2)     NOT NULL,   -- Température ressentie
    HUMIDITY        NUMBER(3)       NOT NULL,   -- 0-100%
    PRESSURE        NUMBER(5)       NOT NULL,   -- en hPa (ex: 1013)
    DESCRIPTION     VARCHAR2(200),              -- "Partly cloudy", etc.
    ICON            VARCHAR2(10),               -- Code icône OpenWeatherMap
    WIND_SPEED      NUMBER(5,2),                -- en m/s
    TIMESTAMP       TIMESTAMP       NOT NULL,   -- Date/heure de la mesure
    
    -- Clé étrangère vers la station
    CONSTRAINT FK_WEATHER_STATION 
        FOREIGN KEY (STATION_ID) 
        REFERENCES WEATHER_STATION(ID) 
        ON DELETE CASCADE
);

-- Séquence pour l'auto-incrémentation de l'ID
CREATE SEQUENCE WEATHER_DATA_SEQ 
    START WITH 1 
    INCREMENT BY 1 
    NOCACHE 
    NOCYCLE;

-- Index sur STATION_ID pour optimiser les recherches
CREATE INDEX IDX_WEATHER_DATA_STATION ON WEATHER_DATA(STATION_ID);

-- Index sur TIMESTAMP pour recherches chronologiques
CREATE INDEX IDX_WEATHER_DATA_TIMESTAMP ON WEATHER_DATA(TIMESTAMP);

-- ========================================
-- Commentaires sur les tables (documentation)
-- ========================================
COMMENT ON TABLE WEATHER_STATION IS 'Stations météo identifiées par leurs coordonnées GPS';
COMMENT ON TABLE WEATHER_DATA IS 'Relevés météorologiques pour chaque station';

COMMENT ON COLUMN WEATHER_STATION.LATITUDE IS 'Latitude en degrés décimaux (-90 à 90)';
COMMENT ON COLUMN WEATHER_STATION.LONGITUDE IS 'Longitude en degrés décimaux (-180 à 180)';
COMMENT ON COLUMN WEATHER_DATA.TEMPERATURE IS 'Température en degrés Celsius';
COMMENT ON COLUMN WEATHER_DATA.HUMIDITY IS 'Humidité relative en pourcentage (0-100)';
COMMENT ON COLUMN WEATHER_DATA.PRESSURE IS 'Pression atmosphérique en hectopascals (hPa)';

-- ========================================
-- Vérification
-- ========================================
SELECT 'Tables créées avec succès!' AS STATUS FROM DUAL;
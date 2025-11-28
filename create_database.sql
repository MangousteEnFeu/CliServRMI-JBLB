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
-- ========================================
CREATE TABLE WEATHER_STATION (
                                 ID                  NUMBER(10)      PRIMARY KEY,
                                 OPENWEATHERMAP_ID   NUMBER(15)      NOT NULL UNIQUE,
                                 NAME                VARCHAR2(100)   NOT NULL,
                                 COUNTRY             VARCHAR2(10),
                                 LATITUDE            NUMBER(10,6)    NOT NULL,
                                 LONGITUDE           NUMBER(10,6)    NOT NULL,
                                 LAST_UPDATED        TIMESTAMP       NOT NULL
);

CREATE SEQUENCE WEATHER_STATION_SEQ
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- ========================================
-- Table WEATHER_DATA
-- ========================================
CREATE TABLE WEATHER_DATA (
                              ID              NUMBER(10)      PRIMARY KEY,
                              STATION_ID      NUMBER(10)      NOT NULL,
                              TEMPERATURE     NUMBER(5,2)     NOT NULL,
                              FEELS_LIKE      NUMBER(5,2)     NOT NULL,
                              HUMIDITY        NUMBER(3)       NOT NULL,
                              PRESSURE        NUMBER(5)       NOT NULL,
                              DESCRIPTION     VARCHAR2(200),
                              ICON            VARCHAR2(10),
                              WIND_SPEED      NUMBER(5,2),
                              TIMESTAMP       TIMESTAMP       NOT NULL,

                              CONSTRAINT FK_WEATHER_STATION
                                  FOREIGN KEY (STATION_ID)
                                      REFERENCES WEATHER_STATION(ID)
                                          ON DELETE CASCADE
);

CREATE SEQUENCE WEATHER_DATA_SEQ
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

CREATE INDEX IDX_WEATHER_DATA_STATION ON WEATHER_DATA(STATION_ID);
CREATE INDEX IDX_WEATHER_DATA_TIMESTAMP ON WEATHER_DATA(TIMESTAMP);
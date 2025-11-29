# Projet n°2 - Service Météo RMI

**Auteurs :** Loïc Barthoulot & Jérémie Bressoud (Sur la base d'un exercice de M. Francillon)

**Cours :** Services et composants logiciels

**Date :** Novembre 28.11.2025

---

## Description

Application client-serveur distribuée permettant de consulter et gérer des données météorologiques via RMI (Remote Method Invocation). Le système interroge l'API OpenWeatherMap et persiste les données dans une base de données Oracle.

### Fonctionnalités

1. **Recherche par coordonnées** : Recherche une station météo et l'ajoute automatiquement en base si elle n'existe pas
2. **Liste des stations** : Affiche toutes les stations enregistrées (sans données météo)
3. **Détails d'une station** : Affiche une station avec toutes ses données météorologiques
4. **Rafraîchissement** : Met à jour les données de toutes les stations depuis l'API

---

## Technologies utilisées

- **Java** : Langage de programmation
- **RMI** : Communication réseau client-serveur
- **HttpClient** : Interrogation de l'API REST OpenWeatherMap
- **Gson** : Désérialisation JSON
- **JDBC/OJDBC11** : Persistance en base de données Oracle
- **Maven** : Gestion des dépendances

---

## Structure du projet

```
SCL-CH7-Sample/
├── RMI-Server/                    # Module serveur
│   ├── src/main/java/
│   │   └── ch/hearc/heg/scl/
│   │       ├── dao/               # Accès données (JDBC)
│   │       ├── database/          # Configuration DB
│   │       ├── model/             # Classes métier
│   │       ├── rmi/               # Interface et implémentation RMI
│   │       ├── service/           # Client API OpenWeatherMap
│   │       └── Main.java          # Point d'entrée serveur
│   └── src/main/resources/
│       └── database.properties.template
│
├── RMI-Client/                    # Module client
│   └── src/main/java/
│       └── ch/hearc/heg/scl/
│           ├── model/             # Classes métier (copie)
│           ├── rmi/               # Interface RMI (copie)
│           └── ClientMenu.java   # Interface utilisateur console
│
└── create_database.sql            # Script de création des tables
```

---

## Configuration requise

### Prérequis

- **Java 23** ou supérieur
- **Maven 3.6+**
- **Oracle Database** (accessible via HE-Arc)
- **Compte OpenWeatherMap** (clé API gratuite)

### Configuration de la base de données

#### Étape 1 : Exécuter le script SQL

Connectez-vous à Oracle SQL Developer ou SQLPlus et exécutez le fichier `create_database.sql` fourni à la racine du projet.

```sql
-- Le script crée automatiquement :
-- - Table WEATHER_STATION
-- - Table WEATHER_DATA
-- - Séquences pour auto-incrémentation
-- - Index d'optimisation
```

#### Étape 2 : Configurer les identifiants

Dans le module **RMI-Server**, créez un fichier `database.properties` :

```bash
cd RMI-Server/src/main/resources/
cp database.properties.template database.properties
```

Éditez `database.properties` avec vos identifiants :

```properties
db.url=jdbc:oracle:thin:@db.ig.he-arc.ch:1521:ens
db.username=VOTRE_USERNAME_HE_ARC
db.password=VOTRE_PASSWORD_HE_ARC
api.key=VOTRE_CLE_API_OPENWEATHERMAP
```

> **Important** : Le fichier `database.properties` est ignoré par Git pour des raisons de sécurité.



---


## Structure de la base de données

### Table WEATHER_STATION

| Colonne           | Type          | Description                        |
|-------------------|---------------|------------------------------------|
| ID                | NUMBER(10)    | Clé primaire (auto-incrémenté)    |
| OPENWEATHERMAP_ID | NUMBER(15)    | ID unique de l'API (évite doublons)|
| NAME              | VARCHAR2(100) | Nom de la ville                   |
| COUNTRY           | VARCHAR2(10)  | Code pays (ex: CH, FR)            |
| LATITUDE          | NUMBER(10,6)  | Latitude (-90 à 90)               |
| LONGITUDE         | NUMBER(10,6)  | Longitude (-180 à 180)            |
| LAST_UPDATED      | TIMESTAMP     | Date de dernière mise à jour      |

**Contraintes :**
- Clé primaire sur ID
- **Unicité sur OPENWEATHERMAP_ID** (évite les doublons de villes - ex: Paris FR vs Paris TX)

### Table WEATHER_DATA

| Colonne       | Type          | Description                        |
|---------------|---------------|------------------------------------|
| ID            | NUMBER(10)    | Clé primaire (auto-incrémenté)    |
| STATION_ID    | NUMBER(10)    | Clé étrangère vers WEATHER_STATION|
| TEMPERATURE   | NUMBER(5,2)   | Température en °C                  |
| FEELS_LIKE    | NUMBER(5,2)   | Température ressentie en °C        |
| HUMIDITY      | NUMBER(3)     | Humidité (0-100%)                  |
| PRESSURE      | NUMBER(5)     | Pression atmosphérique (hPa)       |
| DESCRIPTION   | VARCHAR2(200) | Description météo                  |
| ICON          | VARCHAR2(10)  | Code icône OpenWeatherMap          |
| WIND_SPEED    | NUMBER(5,2)   | Vitesse du vent (m/s)              |
| TIMESTAMP     | TIMESTAMP     | Date/heure de la mesure            |

**Relation :** `WEATHER_DATA.STATION_ID` → `WEATHER_STATION.ID` (ON DELETE CASCADE)

---

## Architecture technique

### Communication RMI

1. Le serveur exporte l'interface `WeatherService` sur le port **1099**
2. Le client se connecte au registre RMI et obtient une référence au service
3. Les appels de méthodes sont transparents (comme des appels locaux)
4. Les objets `WeatherStation` et `WeatherData` sont sérialisés pour le transfert réseau

### Flux d'une recherche par coordonnées

**⚠️ NOUVELLE LOGIQUE avec ID OpenWeatherMap :**

```
CLIENT                 SERVEUR                API              DATABASE
  │                      │                     │                  │
  ├─RMI call────────────►│                     │                  │
  │  getStationBy        │                     │                  │
  │  Coordinates()       │                     │                  │
  │                      │                     │                  │
  │                      ├─HTTP GET────────────►│                  │
  │                      │  (lat, lon)         │                  │
  │                      │                     │                  │
  │                      │◄─JSON Response──────┤                  │
  │                      │  {id: 2661552,      │                  │
  │                      │   name: "Neuchâtel",│                  │
  │                      │   country: "CH"}    │                  │
  │                      │                     │                  │
  │                      ├─SELECT by OWM_ID────┼─────────────────►│
  │                      │  (WHERE OWM_ID=     │                  │
  │                      │   2661552)          │                  │
  │                      │                     │                  │
  │                      │◄─NULL ou STATION────┼──────────────────┤
  │                      │                     │                  │
  │                      │─SI PAS TROUVÉ:      │                  │
  │                      ├─INSERT STATION──────┼─────────────────►│
  │                      │  (avec OWM_ID)      │                  │
  │                      │                     │                  │
  │                      ├─INSERT WEATHER_DATA─┼─────────────────►│
  │                      │                     │                  │
  │◄─WeatherStation──────┤                     │                  │
  │  (serialized)        │                     │                  │
```

**🔑 Différence clé avec l'ancienne approche :**
- ✅ **Nouvelle logique** : API D'ABORD → Récupération ID unique → Recherche par ID → Insert si absent
- ❌ **Ancienne logique** : Recherche par coordonnées → Si absent → API → Insert

**Avantages de la nouvelle approche :**
- ✅ Évite les doublons (Paris FR ≠ Paris TX, même si coordonnées proches)
- ✅ Identifiant unique et fiable (ID OpenWeatherMap)
- ✅ Gestion cohérente des stations

---

## Livrable

**Contenu du ZIP :**
- Tous les fichiers sources
- `pom.xml` (modules parent, serveur, client)
- `create_database.sql`
- `database.properties.template`
- Ce README
- Pas de `database.properties` (sécurité)

---

## Notes importantes

### Conformité avec les exigences

- **Client et Serveur dans des modules séparés** : RMI-Server et RMI-Client
- **4 fonctionnalités serveur implémentées** : recherche, liste, détails, rafraîchissement
- **Menu client interactif** : ConsoleMenu avec saisies utilisateur
- **Technologies imposées** : Java, RMI, HttpClient, Gson, JDBC/OJDBC
- **README complet** : Instructions de démarrage détaillées
- **Script SQL fourni** : `create_database.sql`
- **Projet exécutable** : Instructions claires pour le lancement
- **Gestion des doublons** : Utilisation de l'ID unique OpenWeatherMap

### Points d'attention

- Les identifiants de base de données sont **dans un fichier séparé** ignoré par Git
- Le **script SQL** permet de recréer la structure complète
- Les **membres du groupe** sont indiqués en haut du README
- Le projet **compile et s'exécute** en suivant les instructions
- **L'ID OpenWeatherMap** garantit l'unicité des stations (pas de doublons)

---

## Apprentissages

Ce projet nous a permis de :

- Maîtriser **RMI** pour la communication distribuée
- Implémenter le **pattern DAO** pour l'abstraction des données
- Intégrer une **API REST externe** (OpenWeatherMap)
- Gérer la **persistance** avec JDBC et Oracle
- Gérer les **erreurs réseau** et les cas limites
- Comprendre l'importance des **identifiants uniques** pour éviter les doublons
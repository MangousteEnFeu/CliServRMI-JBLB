package ch.hearc.heg.scl.rmi;

import ch.hearc.heg.scl.model.WeatherStation;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Interface RMI pour le service météo.
 * Définit les méthodes distantes accessibles par le client.
 *
 * Toutes les méthodes doivent déclarer throws RemoteException
 * car elles impliquent une communication réseau qui peut échouer.
 */
public interface WeatherService extends Remote {

    /**
     * Recherche une station météo par ses coordonnées géographiques.
     * Si la station n'existe pas en base, elle est créée en interrogeant l'API météo.
     *
     * @param latitude Latitude de la station (ex: 46.9917 pour La Chaux-de-Fonds)
     * @param longitude Longitude de la station (ex: 6.9307)
     * @return La station météo avec ses données actuelles
     * @throws RemoteException En cas d'erreur réseau RMI
     * @throws IllegalArgumentException Si les coordonnées sont invalides
     */
    WeatherStation getStationByCoordinates(double latitude, double longitude)
            throws RemoteException;

    /**
     * Liste toutes les stations météo présentes dans la base de données.
     * Retourne uniquement les informations de station (sans les données météo).
     *
     * @return Liste des stations (peut être vide si aucune station en base)
     * @throws RemoteException En cas d'erreur réseau RMI
     */
    List<WeatherStation> getAllStations()
            throws RemoteException;

    /**
     * Récupère une station spécifique avec toutes ses données météorologiques.
     * Inclut les informations de la station ET ses données météo actuelles.
     *
     * @param stationId Identifiant unique de la station
     * @return La station avec ses données météo
     * @throws RemoteException En cas d'erreur réseau RMI
     * @throws IllegalArgumentException Si la station n'existe pas
     */
    WeatherStation getStationWithWeatherData(int stationId)
            throws RemoteException;

    /**
     * Rafraîchit les données météo de toutes les stations présentes en base.
     * Interroge l'API météo pour chaque station et met à jour les données.
     *
     * @return Le nombre de stations mises à jour avec succès
     * @throws RemoteException En cas d'erreur réseau RMI
     */
    int refreshAllStations()
            throws RemoteException;
}
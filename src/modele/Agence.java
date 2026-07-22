package modele;

import exceptions.VehiculeIndisponibleException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Agence {

    private List<Vehicule> flotte;
    private List<Client> clients;
    private List<Reservation> reservations;

    // Constructeur
    public Agence() {
        this.flotte = new ArrayList<>();
        this.clients = new ArrayList<>();
        this.reservations = new ArrayList<>();
    }

    // flotte (appelé par Gestionnaire)

    public void ajouterVehicule(Vehicule v) {
        flotte.add(v);
    }

    public void modifierVehicule(String immatriculation, double nouveauTarifBase) {
        Vehicule v = trouverVehiculeParImmatriculation(immatriculation);
        if (v != null) {
            v.setTarifBase(nouveauTarifBase);
        }
    }

    public void supprimerVehicule(String immatriculation) {
        flotte.removeIf(v -> v.getImmatriculation().equals(immatriculation)); // removeIf permet de supprimer si une condition est vrai
    }

    public void mettreEnMaintenance(Vehicule v) {
        v.changerStatut(StatutVehicule.MAINTENANCE);
    }

    // La recherche (correspond à "demanderListeVehicules" côté Client) 
    // Retourne tous les véhicules de la flotte, disponibles ou non 
    public List<Vehicule> getFlotte() {
        return flotte;
    }

    //  Recherche par catégorie, marque ou modèle
    public List<Vehicule> rechercheVehicule(String critere) {
        List<Vehicule> resultats = new ArrayList<>();
        for (Vehicule v : flotte) {
            if (v.getCategorie().equalsIgnoreCase(critere) // IgnoreCase permet d'ignorer la casse donc d'être insensible 
                    || v.getMarque().equalsIgnoreCase(critere)
                    || v.getModele().equalsIgnoreCase(critere)) {
                resultats.add(v);
            }
        }
        return resultats;
    }

    // Getters

    // Clients

    public void ajouterClient(Client c) {
        clients.add(c);
    }

    public List<Client> getClients() {
        return clients;
    }

    // Reservations

    public List<Reservation> getReservations() {
        return reservations;
    }


    // Méthodes de recherche 

    private Vehicule trouverVehiculeParImmatriculation(String immatriculation) {
        for (Vehicule v : flotte) {
            if (v.getImmatriculation().equals(immatriculation)) {
                return v;
            }
        }
        return null;
    }
}
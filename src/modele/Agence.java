package modele;

import exceptions.VehiculeIndisponibleException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe centrale du système : orchestre la flotte, les clients
 * et les réservations. Correspond à la classe "Agence" du diagramme
 * de classes ; ses méthodes publiques sont les points d'entrée appelés
 * par Client et Gestionnaire dans les diagrammes de séquence.
 */

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

    // Clients

    public void ajouterClient(Client c) {
        clients.add(c);
    }

    public List<Client> getClients() {
        return clients;
    }

    // Reservations


    // Point d'entrée appelé quand un Client choisit un véhicule et des dates 
    // on cree la reservation on change le statut et on ajoute à reservation 
    public Reservation validerReservation(Client client, Vehicule vehicule,
                                          LocalDate dateDebut, LocalDate dateFin)
            throws VehiculeIndisponibleException {

        if (!vehicule.estDisponible()) {
            throw new VehiculeIndisponibleException(
                    "Le véhicule " + vehicule.getImmatriculation() + " n'est pas disponible pour ces dates.");
        }

        Reservation reservation = new Reservation(client, vehicule, dateDebut, dateFin);
        reservation.confirmer();
        vehicule.changerStatut(StatutVehicule.RESERVE);
        reservations.add(reservation);
        client.ajouterReservation(reservation); // permet de garder la liste du client synchronisée
        return reservation;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    // Retour 
    // Le Gestionnaire signale le retour d'un véhicule. On retrouve nous-mêmes
    // la réservation active correspondante on facture, puis on libère le véhicule.
    public double enregistrerRetour(Vehicule vehicule) throws VehiculeIndisponibleException {
        Reservation reservation = trouverReservationActive(vehicule);
        if (reservation == null) {
            throw new VehiculeIndisponibleException(
                    "Aucune réservation active trouvée pour le véhicule " + vehicule.getImmatriculation());
        }

        double montantFinal = reservation.calculerMontant();
        reservation.cloturer();
        vehicule.changerStatut(StatutVehicule.DISPONIBLE);
        return montantFinal;
    }

    // Méthodes de recherche 
    // On parcours la flotte on compare les immatriculations une fois trouvé on retourne le vehicule
    private Vehicule trouverVehiculeParImmatriculation(String immatriculation) {
        for (Vehicule v : flotte) {
            if (v.getImmatriculation().equals(immatriculation)) {
                return v;
            }
        }
        return null;
    }

    //On parcours les reservation avec comme paramètre le vehicule si on trouve le vehicule
    // et si la reservation est active on retourne le vehicule.
    private Reservation trouverReservationActive(Vehicule vehicule) {
        for (Reservation r : reservations) {
            if (r.getVehicule().equals(vehicule) && r.estActive()) {
                return r;
            }
        }
        return null;
    }
}
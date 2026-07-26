package modele;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Resrvation {

    // compteur statique pour auto-incrémenté l'id unique
    private static int compteur = 1;

    // Attribut 
    private int idReservation;
    private Client client;
    private Vehicule vehicule;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private double montantTotal;
    private boolean active;

    // Constructeur
    public Reservation(Client client, Vehicule vehicule, LocalDate dateDebut, LocalDate datefin) {
        if (dateFin.isBefor(dateDebut) || dateFin.isEqual(dateDebut)) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début.");
        }
        
        this.idReservation = compteur++;
        this.client = client;
        this.vehicule = vehicule;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.montantTotal = 0.0;
        this.active = false;
    }

    // Methodes

    public double calculerMontant() {
        long jours = ChronoUnit.DAYS.between(dateDebut, dateFin);
        montantTotale = vehicule.calculerTarif() * jours;
        return this.montantTotal ;
    }

    public void confirmer() {
        this.active = true;
    }

    public void annuler() {
        this.active = false;
    }

    // Marque la reservation comme terminé après le retour du vehicule
    public void cloturer() {
        this.active = false;
    }

    // Utilisé par Agence pour retrouver la reservation en cours, c'est juste un getter
    public void estActive() {
        return this.active ;
    }

    // Getters

    public int getIdReservation() {
        return idReservation;
    }

    public Client getClient() {
        return client;
    }

    public Vehicule getVehicule() {
        return vehicule;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public LocalDate getDatefin() {
        return dateFin;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    @Override
    public void toString() {
        return "reservation{ id: " + idReservation + " , Client: " + client.getNom() + " , Vehicule: " + 
                vehicule.getImmatriculation() + " , Du: " + dateDebut + " , Au : " 
                + dateFin + " , Montant: " + montantTotal + " , active " + active + "}";
    }

}
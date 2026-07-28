package modele;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Reservation {
    private static int compteur = 1;
    private int idReservation;
    private Client client;
    private Vehicule vehicule;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private double montantTotal;
    private boolean estActive;

    public Reservation(Client client, Vehicule vehicule, LocalDate dateDebut, LocalDate dateFin) {
        this.idReservation = compteur++;
        this.client = client;
        this.vehicule = vehicule;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.estActive = true;
        this.montantTotal = calculerMontant();
    }

    public double calculerMontant() {
        // ChronoUnit calcul l'intervalle entre les deux dates
        long nbJours = ChronoUnit.DAYS.between(dateDebut, dateFin); 
        if (nbJours <= 0) nbJours = 1; 
        return vehicule.calculerTarif() * nbJours;
    }

    // Fonctions d'état
    public void confirmer() { this.estActive = true; }
    public boolean estActive() { return estActive; }
    public void terminer() { this.estActive = false; }

    // Getters indispensables pour Agence et FenetrePrincipale
    public int getId() { return idReservation; }
    public Client getClient() { return client; }
    public Vehicule getVehicule() { return vehicule; }
    public LocalDate getDateDebut() { return dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public double getMontantTotal() { return montantTotal; }

    @Override
    public String toString() {
        return "Reservation #" + idReservation + " [" + vehicule.getImmatriculation() + "] pour " + client.getNom();
    }
}
package modele;

import exceptions.VehiculeIndisponibleException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Client extends Utilisateur {

    // Attribut 
    private int idClient;
    private String nom;
    private String numPermis;
    private String telephone;
    private List<Reservation> reservations ;

    // Constructeur
    public Client(int idClient, String nom, String numPermis, String telephone, String email, String password) {
        super(email, password);
        this.idClient = idClient;
        this.nom = nom;
        this.numPermis = numPermis;
        this.telephone = telephone;
        this.reservations = new ArrayList<>();
    }

    //Methodes
    public Reservation reserverVehicule(Agence agence, Vehicule v, LocalDate debut, LocalDate fin) 
        throws VehiculeIndisponibleException {
        return agence.validerReservation(this, v, debut, fin);
    }

    public List<Reservation> consulterReservation() {
        return this.reservations;
    }

    public void ajouterReservation(Reservation r) {
        this.reservations.add(r);
    }

    // Getters
    public int getIdClient() {
        return idClient;
    }

    public String getNom() {
        return nom;
    }

    public String getNumPermis() {
        return numPermis;
    }

    public String getTelephone() {
        return telephone;
    }

    @Override
    public String toString() {
        return "Client{ IdClient: " + idClient + " , nom : " + nom + " , numero permis: " + numPermis + 
                " , tel : " + telephone + " , email : " + getEmail() + " }";
    }
}
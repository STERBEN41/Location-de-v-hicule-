package modele;

import exceptions.VehiculeIndisponibleException;

public class Gestionnaire extends Utilisateur {

    // Attributs
    private int idGestionnaire;
    private String nom;
    private Agence agence;

    // Constructeur
    public Gestionnaire(int idGestionnaire, String nom, String email, String password, Agence agence) {
        super(email, password);
        this.idGestionnaire = idGestionnaire;
        this.nom = nom;
        this.agence = agence;
    }

    // Methodes
    // chacune délégué à Agence qui detient les données réelles 
    public void ajouterVehicule(Vehicule v) {
        agence.ajouterVehicule(v);
    }

    public void modifierVehicule(String immatriculation, double nouveauTarifBase) {
        agence.modifierVehicule(immatriculation, nouveauTarifBase);
    }

    public void supprimerVehicule(String immatriculation) {
        agence.supprimerVehicule(immatriculation);
    }

    public void mettreEnMaintenance(Vehicule v) {
        agence.mettreEnMaintenance(v);
    }

    public double enregistrerRetour(Vehicule vehicule) {
        return agence.enregistrerRetour(vehicule);
    }

    // Getters
    public int getIdGestionnaire() {
        return idGestionnaire;
    }
    public String getNom() {
        return nom;
    }

}
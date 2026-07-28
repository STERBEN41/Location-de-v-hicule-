package modele;

import java.util.Objects;

public abstract class Vehicule {
    private int idVehicule;
    private String immatriculation;
    private String marque;
    private String modele;
    private String categorie;
    protected double tarifBase;
    private StatutVehicule statutVehicule;

    public Vehicule(int idVehicule, String immatriculation, String marque, String modele, String categorie, double tarifBase) {
        this.idVehicule = idVehicule;
        this.immatriculation = immatriculation;
        this.marque = marque;
        this.modele = modele;
        this.categorie = categorie;
        this.tarifBase = tarifBase;
        this.statutVehicule = StatutVehicule.DISPONIBLE;
    }

    // --- LOGIQUE MÉTIER & TRANSITIONS D'ÉTATS ---
    
    public boolean estDisponible() {
        return this.statutVehicule == StatutVehicule.DISPONIBLE;
    }

    public void changerStatut(StatutVehicule nouveauStatut) {
        this.statutVehicule = nouveauStatut;
    }

    // Directement mappé sur le Diagramme d'États
    public void reserver() {
        if (this.statutVehicule == StatutVehicule.DISPONIBLE) {
            this.statutVehicule = StatutVehicule.RESERVE;
        }
    }

    public void louer() {
        if (this.statutVehicule == StatutVehicule.RESERVE) {
            this.statutVehicule = StatutVehicule.LOUE;
        }
    }

    public void reparer() {
        if (this.statutVehicule == StatutVehicule.MAINTENANCE) {
            this.statutVehicule = StatutVehicule.DISPONIBLE;
        }
    }

    public abstract double calculerTarif();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicule vehicule = (Vehicule) o;
        return Objects.equals(immatriculation, vehicule.immatriculation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(immatriculation);
    }

    // Getters & Setters
    public int getId() { return idVehicule; }
    public String getImmatriculation() { return immatriculation; }
    public String getMarque() { return marque; }
    public String getModele() { return modele; } 
    public String getCategorie() { return categorie; }
    public double getTarifBase() { return tarifBase; }
    public StatutVehicule getStatut() { return statutVehicule; }
    public void setTarifBase(double tarifBase) { this.tarifBase = tarifBase; }
    public void setStatut(StatutVehicule statutVehicule) { this.statutVehicule = statutVehicule;}
    public void setMarque(String marque) { this.marque = marque;}
    public void setModele(String modele) { this.modele = modele;}

    @Override
    public String toString() {
        return "[" + categorie + "] " + marque + " " + modele + " (" + immatriculation + ") - Statut: " + statutVehicule;
    }
}
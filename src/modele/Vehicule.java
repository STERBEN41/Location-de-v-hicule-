package modele;

public abstract class Vehicule {

    //Attributs
    private int idVehicule;
    private String immatriculation;
    private String marque;
    private String modele;
    private String categorie;
    protected double tarifBase;
    private StatutVehicule statutVehicule; // Attribut appartenant à la class Statutvehicule

    // Constructeur 
    public Vehicule(int idVehicule, String immatriculation, String marque, String modele, String categorie, Double tarifBase) {
        this.idVehicule = idVehicule;
        this.immatriculation = immatriculation;
        this.marque = marque;
        this.modele = modele;
        this.categorie = categorie;
        this.tarifBase = tarifBase;
        this.statutVehicule = StatutVehicule.DISPONIBLE;
    }

    // Methodes
    public boolean estDisponible() {
        return this.statutVehicule == StatutVehicule.DISPONIBLE;
    }

    public void changerStatut(StatutVehicule nouveauStatut) {
        this.statutVehicule = nouveauStatut;
    }

    public abstract double calculerTarif(); // Override dans les sous class donc pas de corps ici 

    // Getters 
    public int getId() {
        return this.idVehicule;
    }

    public String getImmatriculation() {
        return this.immatriculation;
    }

    public String getMarque() {
        return this.marque;
    }

    public String getModele() {
        return this.modele;
    }

    public String getCategorie() {
        return this.categorie;
    }

    public double getTarifBase() {
        return this.tarifBase;
    }

    public StatutVehicule getStatut() {
        return this.statutVehicule;
    }

    // Setter utilisé par Agence
    public void setTarifBase(double tarifBase) {
        this.tarifBase = tarifBase;
    }

    @Override
    public String toString() {
        return "Vehicule{id : " + idVehicule + " , matricle : " + immatriculation
                + " , marque : " + marque + " , model : " + modele + " , category : "
                + categorie + " , tarif de base : " + tarifBase + " , statut : " + statutVehicule
                + "}";
    }
}

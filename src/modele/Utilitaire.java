package modele;

public class Utilitaire extends Vehicule {
    private final double multiplicateurPrix;

    public Utilitaire(int idVehicule, String immatriculation, String marque, String modele, String categorie, double tarifBase, double multiplicateurPrix) {
        super(idVehicule, immatriculation, marque, modele, categorie, tarifBase);
        this.multiplicateurPrix = multiplicateurPrix;
    }

    @Override
    public double calculerTarif() {
        return tarifBase * multiplicateurPrix;
    }

     public double getMultiplicateurPrix() { return this.multiplicateurPrix; }

    @Override
    public String toString() {
        String base = super.toString();
        if (base.endsWith("}")) base = base.substring(0, base.length() - 1);
        return base + " , multiplicateurPrix : " + multiplicateurPrix + "}";
    }

}
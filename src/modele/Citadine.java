package modele;

public class Citadine extends Vehicule {

    // Attribut
    private final double multiplicateurPrix;

    // Constructeur
    public Citadine(int idVehicule, String immatriculation, String marque,
            String modele, String categorie, double tarifBase,
            double multiplicateurPrix) {
        super(idVehicule, immatriculation, marque, modele, categorie, tarifBase);

        this.multiplicateurPrix = multiplicateurPrix;
    }

    // Redefinition de calculerTarif
    @Override
    public double calculerTarif() {
        return tarifBase * multiplicateurPrix;
    }

    // Getters
    public double getMultiplicateurPrix() {
        return this.multiplicateurPrix;
    }

    @Override
    public String toString() {
        String base = super.toString();
        base = base.substring(0, base.length() - 1); // on retire le dernier '}'
        return base + " , multiplicateurPrix : " + multiplicateurPrix + "}";
    }
}

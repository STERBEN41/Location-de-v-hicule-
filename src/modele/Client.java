package modele;

public class Client extends User {
    private int idClient;
    private String nom;
    private String numPermis;
    private String telephone;

    public Client(int idClient, String email, String password, String nom, String numPermis, String telephone) {
        super(email, password);
        this.idClient = idClient;
        this.nom = nom;
        this.numPermis = numPermis;
        this.telephone = telephone;
    }

    public int getIdClient() { return idClient; }
    public String getNom() { return nom; }
    public String getNumPermis() { return numPermis; }
    public String getTelephone() { return telephone; } 

    @Override
    public String toString() {
        return "Client{id=" + idClient + ", nom='" + nom + "'}";
    }
}
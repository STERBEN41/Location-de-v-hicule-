package modele;

public class Gestionnaire extends User {
    private int idGestionnaire;

    public Gestionnaire(int idGestionnaire, String email, String password) {
        super(email, password);
        this.idGestionnaire = idGestionnaire;
    }

    public int getIdGestionnaire() { return idGestionnaire; }
}
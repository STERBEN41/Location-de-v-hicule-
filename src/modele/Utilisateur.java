package modele;

public abstract class Utilisateur {

    // Attributs
    protected String email ;
    protected String password ;

    // Constructeur
    public Utilisateur(String email, String password) {
        this.email = email ;
        this.password = password ;
    }

    // Methodes
    public boolean connexion(String emailSaisi, String passwordSaisi) {
        return this.email.equals(emailSaisi) && this.password.equals(passwordSaisi);
    }

    public void deconnexion(){
        // À gerer plus tard !
    }

    // Getter
    public String getEmail() {
        return this.email;
    }

    // Getter utilisé uniquement par la persistance.
    // Pas idéal en termes de sécurité (mot de passe en clair), mais le sujet
    // n'autorise pas de bibliothèque de hachage externe donc c'est le seul moyen trouvé.
    public String getPassword() {
        return this.password;
    }
}
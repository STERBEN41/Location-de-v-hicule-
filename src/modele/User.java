package modele;

public abstract class User {
    protected String email;
    protected String password;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public boolean connexion(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; } 
}
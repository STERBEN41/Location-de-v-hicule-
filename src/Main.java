import modele.*;
import gui.FenetrePrincipale;
import gui.FenetreLogin; // AJOUT de l'import manquant
import exceptions.VehiculeIndisponibleException;
import javax.swing.SwingUtilities;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        // 1. Initialisation de l'agence
        Agence agence = new Agence();

        // 2. Chargement des données sauvegardées (Persistance)
        System.out.println("Chargement des données...");
        agence.chargerDonnees();

        // 3. Ajout de données de test si l'agence est vide (pour la démo)
        if (agence.getFlotte().isEmpty()) {
            System.out.println("Agence vide, ajout de véhicules de test...");
            agence.ajouterVehicule(new SUV(1, "SN-123-A", "Toyota", "Rav4", "SUV", 50000.0, 1.5));
            agence.ajouterVehicule(new Citadine(2, "SN-456-B", "Peugeot", "208", "Citadine", 25000.0, 1.1));
            agence.ajouterVehicule(new Utilitaire(3, "SN-789-C", "Renault", "Master", "Utilitaire", 40000.0, 1.3));
        }

        // 4. TEST CONSOLE (Preuve de la couche métier pour le prof)
        System.out.println("\n=== TEST LOGIQUE METIER ===");
        try {
            Client testClient = new Client(1, "jean@mail.com", "123", "Jean Dupont", "PERMIS-001", "771234567");
            if (!agence.getFlotte().isEmpty()) {
                Vehicule v = agence.getFlotte().get(0); 

                System.out.println("Tentative de réservation pour : " + v.getImmatriculation());
                Reservation r = agence.validerReservation(testClient, v, LocalDate.now(), LocalDate.now().plusDays(3));
                System.out.println("Réservation réussie. Prix total : " + r.getMontantTotal() + " FCFA");

                System.out.println("Tentative de double réservation (doit échouer)...");
                agence.validerReservation(testClient, v, LocalDate.now(), LocalDate.now().plusDays(1));
            }
        } catch (VehiculeIndisponibleException e) {
            System.out.println("SUCCÈS DU TEST : L'exception a bien été capturée : " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erreur imprévue : " + e.getMessage());
        }

        // 5. LANCEMENT DE L'INTERFACE DE LOGIN (Entry Point)
        System.out.println("\nLancement de l'interface de connexion...");
        
        SwingUtilities.invokeLater(() -> {
            // C'est le login qui ouvrira la FenetrePrincipale ensuite
            new FenetreLogin(agence);
        });
    }
}
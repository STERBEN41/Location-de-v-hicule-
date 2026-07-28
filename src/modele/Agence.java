package modele;

import exceptions.VehiculeIndisponibleException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.util.Scanner;

public class Agence {
    private List<Vehicule> flotte = new ArrayList<>();
    private List<Client> clients = new ArrayList<>();
    private List<Reservation> reservations = new ArrayList<>();
    private User sessionActuelle = null;

    // --- GESTION DE LA FLOTTE ---

    public void ajouterVehicule(Vehicule v) { flotte.add(v); }

    public void modifierVehicule(String immatriculation, double nouveauTarifBase) {
        Vehicule v = trouverVehiculeParImmat(immatriculation);
        if (v != null) { v.setTarifBase(nouveauTarifBase); }
    }

    public void supprimerVehicule(String immatriculation) {
        flotte.removeIf(v -> v.getImmatriculation().equals(immatriculation));
    }

    public List<Vehicule> getFlotte() { return this.flotte; }

    public List<Vehicule> rechercherVehicule(String critere) {
        List<Vehicule> resultats = new ArrayList<>();
        for (Vehicule v : flotte) {
            if (v.getCategorie().equalsIgnoreCase(critere) || 
                v.getMarque().equalsIgnoreCase(critere) || 
                v.getModele().equalsIgnoreCase(critere) ||
                v.getImmatriculation().equalsIgnoreCase(critere)) {
                resultats.add(v);
            }
        }
        return resultats;
    }

    // --- TRANSITIONS D'ÉTATS DU DIAGRAMME ---

    // 1. Transition DISPONIBLE -> RESERVE
    public Reservation validerReservation(Client client, Vehicule vehicule, LocalDate debut, LocalDate fin) throws VehiculeIndisponibleException {
        if (!vehicule.estDisponible()) {
            throw new VehiculeIndisponibleException("Le véhicule " + vehicule.getImmatriculation() + " n'est pas disponible.");
        }
        if (fin.isBefore(debut)) {
            throw new VehiculeIndisponibleException("Erreur : La date de fin est antérieure à la date de début.");
        }

        Reservation res = new Reservation(client, vehicule, debut, fin);
        vehicule.reserver(); // DISPONIBLE -> RESERVE
        reservations.add(res);
        return res;
    }

    // 2. Transition RESERVE -> LOUE
    public void louerVehicule(Vehicule v, User u) throws VehiculeIndisponibleException {
        if (v.getStatut() != StatutVehicule.RESERVE) {
            throw new VehiculeIndisponibleException("Le véhicule doit être au statut RESERVE pour être loué.");
        }
        if (!estLeReservataire(v, u) && !estGestionnaire()) {
            throw new VehiculeIndisponibleException("Seul le client qui a réservé peut prendre possession du véhicule.");
        }
        v.louer(); // RESERVE -> LOUE
    }

    // 3. Transitions LOUE -> DISPONIBLE ou LOUE -> MAINTENANCE
    public double enregistrerRetour(Vehicule v, boolean dommagesConstates) throws VehiculeIndisponibleException {
        Reservation res = trouverReservationActive(v);
        if (res == null) throw new VehiculeIndisponibleException("Aucune réservation active pour ce véhicule.");
        
        double prix = res.calculerMontant();
        res.terminer();

        if (dommagesConstates) {
            v.changerStatut(StatutVehicule.MAINTENANCE); // LOUE -> MAINTENANCE
        } else {
            v.changerStatut(StatutVehicule.DISPONIBLE);  // LOUE -> DISPONIBLE
        }
        return prix;
    }

    // 4. Transition MAINTENANCE -> DISPONIBLE
    public void reparerVehicule(Vehicule v) {
        if (v.getStatut() == StatutVehicule.MAINTENANCE) {
            v.reparer(); // MAINTENANCE -> DISPONIBLE
        }
    }

    // --- GESTION CLIENTS & SESSIONS ---

    public void ajouterClient(Client c) { clients.add(c); }
    public List<Client> getClients() { return this.clients; }

    public User getSessionActuelle() { return sessionActuelle; }
    public void setSessionActuelle(User user) { this.sessionActuelle = user; }

    public boolean estGestionnaire() { return sessionActuelle instanceof Gestionnaire; }
    public boolean estClient() { return sessionActuelle instanceof Client; }

    public boolean estLeReservataire(Vehicule v, User u) {
        if (!(u instanceof Client)) return false;
        Client c = (Client) u;
        for (Reservation r : reservations) {
            if (r.getVehicule().equals(v) && r.getClient().equals(c) && r.estActive()) {
                return true;
            }
        }
        return false;
    }

    private Reservation trouverReservationActive(Vehicule v) {
        for (Reservation r : reservations) {
            if (r.getVehicule().equals(v) && r.estActive()) return r;
        }
        return null;
    }

    public List<Reservation> getReservations() { return this.reservations; }

    // --- PERSISTANCE DES DONNÉES ---

    public void sauvegarderTout() {
        sauvegarderVehicules();
        sauvegarderClients();
        sauvegarderReservations();
    }

    private void sauvegarderVehicules() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("data/vehicules.txt"))) {
            for (Vehicule v : flotte) {
                String type = v instanceof SUV ? "SUV" : (v instanceof Citadine ? "Citadine" : "Utilitaire");
                pw.println(type + ";" + v.getId() + ";" + v.getImmatriculation() + ";" + 
                           v.getMarque() + ";" + v.getModele() + ";" + v.getCategorie() + ";" + 
                           v.getTarifBase() + ";" + v.getStatut());
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void sauvegarderClients() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("data/clients.txt"))) {
            for (Client c : clients) {
                pw.println(c.getIdClient() + ";" + c.getEmail() + ";" + c.getPassword() + ";" + 
                           c.getNom() + ";" + c.getNumPermis() + ";" + c.getTelephone());
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void sauvegarderReservations() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("data/reservations.txt"))) {
            for (Reservation r : reservations) {
                pw.println(r.getClient().getNom() + ";" + r.getVehicule().getImmatriculation() + ";" + 
                           r.getDateDebut() + ";" + r.getDateFin() + ";" + r.getMontantTotal() + ";" + r.estActive());
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void chargerDonnees() {
        chargerVehicules();
        chargerClients();
        chargerReservations();
    }

    private void chargerVehicules() {
        File f = new File("data/vehicules.txt");
        if (!f.exists()) return;
        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine()) {
                String[] d = sc.nextLine().split(";");
                int id = Integer.parseInt(d[1]);
                double tarif = Double.parseDouble(d[6]);
                Vehicule v;
                if (d[0].equals("SUV")) v = new SUV(id, d[2], d[3], d[4], d[5], tarif, 1.5);
                else if (d[0].equals("Citadine")) v = new Citadine(id, d[2], d[3], d[4], d[5], tarif, 1.1);
                else v = new Utilitaire(id, d[2], d[3], d[4], d[5], tarif, 1.3);
                
                v.changerStatut(StatutVehicule.valueOf(d[7]));
                flotte.add(v);
            }
        } catch (Exception e) { System.err.println("Erreur chargement véhicules"); }
    }

    private void chargerClients() {
        File f = new File("data/clients.txt");
        if (!f.exists()) return;
        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine()) {
                String[] d = sc.nextLine().split(";");
                clients.add(new Client(Integer.parseInt(d[0]), d[1], d[2], d[3], d[4], d[5]));
            }
        } catch (Exception e) { System.err.println("Erreur chargement clients"); }
    }

    private void chargerReservations() {
        File f = new File("data/reservations.txt");
        if (!f.exists()) return;
        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine()) {
                String[] d = sc.nextLine().split(";");
                Client c = trouverClientParNom(d[0]);
                Vehicule v = trouverVehiculeParImmat(d[1]);
                if (c != null && v != null) {
                    Reservation r = new Reservation(c, v, LocalDate.parse(d[2]), LocalDate.parse(d[3]));
                    if (d[5].equals("false")) r.terminer();
                    reservations.add(r);
                }
            }
        } catch (Exception e) { System.err.println("Erreur chargement réservations"); }
    }

    private Client trouverClientParNom(String nom) {
        for (Client c : clients) if (c.getNom().equals(nom)) return c;
        return null;
    }

    private Vehicule trouverVehiculeParImmat(String immat) {
        for (Vehicule v : flotte) if (v.getImmatriculation().equals(immat)) return v;
        return null;
    }
}
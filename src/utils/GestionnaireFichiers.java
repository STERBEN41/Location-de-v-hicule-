package utils;
 
import modele.Citadine;
import modele.Client;
import modele.Reservation;
import modele.StatutVehicule;
import modele.SUV;
import modele.Utilitaire;
import modele.Vehicule;
 
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Centralise toute la persistance : lecture/écriture de fichiers texte
 * pour les véhicules, les clients et les réservations.
 * le format sera en CSV simple avec un séparateur ";". Limite : aucune donnée
 * (marque, modèle, nom...) ne doit contenir de ";".
 */

public class GestionnaireFichiers {

}
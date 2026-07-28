package gui;

import modele.*;
import exceptions.VehiculeIndisponibleException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;

public class FenetrePrincipale extends JFrame {
    private Agence agence;
    private boolean isAdmin, isVisitor;
    
    private DefaultTableModel modelVehicules, modelHistorique;
    private JTable tableVehicules, tableHistorique;
    private TableRowSorter<DefaultTableModel> sorterVehicules;

    private JTextField txtImmat, txtMarque, txtModele, txtPrix, txtRecherche;
    private JComboBox<String> comboType;
    private JLabel lblStatsTotal, lblStatsDispo, lblStatsMaint;

    private static final Color COLOR_PRIMARY = new Color(41, 128, 185);
    private static final Color COLOR_ACCENT = new Color(46, 204, 113);
    private static final Color COLOR_BG = new Color(245, 247, 250);
    private static final Color COLOR_DARK = new Color(44, 62, 80);
    private static final Color COLOR_DANGER = new Color(192, 57, 43);

    public FenetrePrincipale(Agence agence) {
        this.agence = agence;
        User session = agence.getSessionActuelle();
        this.isAdmin = agence.estGestionnaire();
        this.isVisitor = (session == null);

        setTitle("IAM Rent-A-Car | " + (isVisitor ? "Mode Visiteur" : (isAdmin ? "GESTIONNAIRE" : "CLIENT : " + session.getEmail())));
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { quitterEtSauvegarder(); }
        });

        JPanel contentPrincipal = new JPanel(new BorderLayout(15, 15));
        contentPrincipal.setBackground(COLOR_BG);
        contentPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- DASHBOARD (HAUT) ---
        contentPrincipal.add(creerPanelStats(), BorderLayout.NORTH);

        // --- ONGLETS (CENTRE) ---
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab(" 🚗 Parc Automobile ", creerPanelGestionParc());
        if (isAdmin) tabs.addTab(" 📜 Historique des Réservations ", creerPanelHistorique());
        contentPrincipal.add(tabs, BorderLayout.CENTER);
        
        // --- ACTIONS & SESSION (BAS) ---
        JPanel panelBas = new JPanel(new BorderLayout());
        panelBas.setOpaque(false);
        panelBas.add(creerPanelActions(), BorderLayout.NORTH);
        panelBas.add(creerBarreSession(session), BorderLayout.SOUTH);
        contentPrincipal.add(panelBas, BorderLayout.SOUTH);

        setContentPane(contentPrincipal);
        rafraichirTableau();
        setVisible(true);
    }

    private JPanel creerPanelGestionParc() {
        JPanel p = new JPanel(new BorderLayout(15, 15));
        p.setOpaque(false);
        if (isAdmin) p.add(creerPanelFormulaire(), BorderLayout.WEST);
        p.add(creerPanelTableauVehicules(), BorderLayout.CENTER);
        return p;
    }

    private JPanel creerPanelHistorique() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(new EmptyBorder(15, 15, 15, 15));
        p.setBackground(Color.WHITE);
        modelHistorique = new DefaultTableModel(new String[]{"Client", "Véhicule", "Début", "Fin", "Total", "État"}, 0);
        tableHistorique = new JTable(modelHistorique);
        tableHistorique.setRowHeight(30);
        p.add(new JScrollPane(tableHistorique), BorderLayout.CENTER);
        return p;
    }

    private JPanel creerPanelActions() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        p.setOpaque(false);

        txtRecherche = new JTextField(15);
        p.add(new JLabel("🔍 Filtrer : ")); p.add(txtRecherche);
        
        JButton btnRes = creerBouton("Réserver", COLOR_PRIMARY);             // DISPONIBLE -> RESERVE
        JButton btnLouer = creerBouton("Louer (Clés)", COLOR_ACCENT);        // RESERVE -> LOUE
        JButton btnRet = creerBouton("Enregistrer Retour", COLOR_DARK);      // LOUE -> DISPONIBLE ou MAINTENANCE
        JButton btnReparer = creerBouton("🔧 Réparer", new Color(230, 126, 34)); // MAINTENANCE -> DISPONIBLE

        if (isAdmin) { 
            p.add(btnRet); 
            p.add(btnReparer); 
        } else if (!isVisitor) { 
            p.add(btnRes); 
            p.add(btnLouer);
        }

        // --- ATTACHE DES LISTENERS CONFORMES AU DIAGRAMME D'ÉTATS ---
        btnRes.addActionListener(e -> actionReserver());
        btnLouer.addActionListener(e -> actionLouer());
        btnRet.addActionListener(e -> actionRetour());
        btnReparer.addActionListener(e -> actionReparer());

        txtRecherche.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrer(); }
            public void removeUpdate(DocumentEvent e) { filtrer(); }
            public void changedUpdate(DocumentEvent e) { filtrer(); }
            void filtrer() { sorterVehicules.setRowFilter(RowFilter.regexFilter("(?i)" + txtRecherche.getText())); }
        });

        return p;
    }

    // --- TRANSITIONS D'ÉTATS ---

    // 1. Transition DISPONIBLE -> RESERVE
    private void actionReserver() {
        Vehicule v = getSelectedVehicule();
        if (v == null) { 
            JOptionPane.showMessageDialog(this, "Veuillez choisir un véhicule."); 
            return; 
        }
        if (v.getStatut() != StatutVehicule.DISPONIBLE) { 
            JOptionPane.showMessageDialog(this, "Seul un véhicule DISPONIBLE peut être réservé."); 
            return; 
        }

        String joursStr = JOptionPane.showInputDialog(this, "Nombre de jours ?");
        if (joursStr != null && !joursStr.trim().isEmpty()) {
            try {
                int nbJours = Integer.parseInt(joursStr);
                LocalDate debut = LocalDate.now();
                LocalDate fin = debut.plusDays(nbJours);

                // Agence crée la réservation et v.changerStatut(RESERVE)
                agence.validerReservation((Client) agence.getSessionActuelle(), v, debut, fin);
                rafraichirTableau();
                JOptionPane.showMessageDialog(this, "Véhicule réservé avec succès (Statut: RESERVE).");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 2. Transition RESERVE -> LOUE
    private void actionLouer() {
        Vehicule v = getSelectedVehicule();
        if (v == null) { 
            JOptionPane.showMessageDialog(this, "Sélectionnez un véhicule."); 
            return; 
        }
        if (v.getStatut() != StatutVehicule.RESERVE) { 
            JOptionPane.showMessageDialog(this, "Seul un véhicule en statut RESERVE peut être loué."); 
            return; 
        }

        // Vérification du réservataire
        if (!agence.estLeReservataire(v, agence.getSessionActuelle())) {
            JOptionPane.showMessageDialog(this, "Erreur : Ce véhicule n'a pas été réservé par vous !", "Sécurité", JOptionPane.ERROR_MESSAGE);
            return;
        }

        v.changerStatut(StatutVehicule.LOUE);
        rafraichirTableau();
        JOptionPane.showMessageDialog(this, "Clés récupérées. Véhicule maintenant LOUE.");
    }

    // 3. Transition LOUE -> DISPONIBLE ou LOUE -> MAINTENANCE
    private void actionRetour() {
    Vehicule v = getSelectedVehicule();
    if (v == null || v.getStatut() != StatutVehicule.LOUE) { 
        JOptionPane.showMessageDialog(this, "Sélectionnez un véhicule actuellement LOUE."); 
        return; 
    }

    // 1. On demande à l'utilisateur s'il y a des dommages
    int reponse = JOptionPane.showConfirmDialog(
        this, 
        "Des dommages ont-ils été constatés lors de l'inspection ?", 
        "Constat de Retour", 
        JOptionPane.YES_NO_OPTION
    );
    
    boolean dommagesConstates = (reponse == JOptionPane.YES_OPTION);

    try {
        // 2. On passe LE VÉHICULE et LE BOOLEEN à l'agence !
        double total = agence.enregistrerRetour(v, dommagesConstates);
        
        rafraichirTableau();

        String msg = "Retour enregistré avec succès.\nMontant à payer : " + total + " FCFA";
        if (dommagesConstates) {
            msg += "\nStatut : Le véhicule a été orienté vers la MAINTENANCE.";
        } else {
            msg += "\nStatut : Le véhicule est de nouveau DISPONIBLE.";
        }

        JOptionPane.showMessageDialog(this, msg, "Facturation & Inspection", JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur Retour", JOptionPane.ERROR_MESSAGE);
    }
}

    // 4. Transition MAINTENANCE -> DISPONIBLE
    private void actionReparer() {
        Vehicule v = getSelectedVehicule();
        if (v == null || v.getStatut() != StatutVehicule.MAINTENANCE) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un véhicule en MAINTENANCE.");
            return;
        }

        v.changerStatut(StatutVehicule.DISPONIBLE); // MAINTENANCE -> DISPONIBLE
        rafraichirTableau();
        JOptionPane.showMessageDialog(this, "Réparation terminée. Véhicule repassé à DISPONIBLE.");
    }

    // --- MÉTHODES TECHNIQUES (CRUD & UI) ---

    private void rafraichirTableau() {
        modelVehicules.setRowCount(0);
        int d = 0, m = 0;
        for (Vehicule v : agence.getFlotte()) {
            modelVehicules.addRow(new Object[]{
                v.getImmatriculation(), 
                v.getMarque(), 
                v.getModele(), 
                v.getCategorie(), 
                v.calculerTarif() + " FCFA", 
                v.getStatut()
            });
            if (v.getStatut() == StatutVehicule.DISPONIBLE) d++; 
            if (v.getStatut() == StatutVehicule.MAINTENANCE) m++;
        }
        lblStatsTotal.setText(String.valueOf(agence.getFlotte().size()));
        lblStatsDispo.setText(String.valueOf(d)); 
        lblStatsMaint.setText(String.valueOf(m));

        if (isAdmin && modelHistorique != null) {
            modelHistorique.setRowCount(0);
            for (Reservation r : agence.getReservations()) {
                modelHistorique.addRow(new Object[]{
                    r.getClient().getNom(), 
                    r.getVehicule().getImmatriculation(), 
                    r.getDateDebut(), 
                    r.getDateFin(), 
                    r.getMontantTotal() + " FCFA", 
                    r.estActive() ? "Active" : "Terminée"
                });
            }
        }
    }

    private JPanel creerPanelTableauVehicules() {
        modelVehicules = new DefaultTableModel(new String[]{"Immat", "Marque", "Modèle", "Type", "Prix/J", "Statut"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableVehicules = new JTable(modelVehicules);
        tableVehicules.setRowHeight(35);
        sorterVehicules = new TableRowSorter<>(modelVehicules);
        tableVehicules.setRowSorter(sorterVehicules);
        
        tableVehicules.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && isAdmin && tableVehicules.getSelectedRow() != -1) {
                int r = tableVehicules.getSelectedRow();
                txtImmat.setText(modelVehicules.getValueAt(tableVehicules.convertRowIndexToModel(r), 0).toString());
                txtMarque.setText(modelVehicules.getValueAt(tableVehicules.convertRowIndexToModel(r), 1).toString());
                txtModele.setText(modelVehicules.getValueAt(tableVehicules.convertRowIndexToModel(r), 2).toString());
                txtPrix.setText(modelVehicules.getValueAt(tableVehicules.convertRowIndexToModel(r), 4).toString().replace(" FCFA", ""));
                txtImmat.setEditable(false);
            }
        });
        
        tableVehicules.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                if (c == 5 && v != null) {
                    String st = v.toString();
                    if (st.equals("DISPONIBLE")) l.setForeground(new Color(39, 174, 96));
                    else if (st.equals("RESERVE")) l.setForeground(COLOR_PRIMARY);
                    else if (st.equals("LOUE")) l.setForeground(new Color(142, 68, 173));
                    else if (st.equals("MAINTENANCE")) l.setForeground(Color.ORANGE);
                    else l.setForeground(Color.RED);
                    l.setFont(l.getFont().deriveFont(Font.BOLD));
                } else l.setForeground(Color.BLACK);
                return l;
            }
        });
        return new JPanel(new BorderLayout()) {{ add(new JScrollPane(tableVehicules)); }};
    }

    private JPanel creerPanelFormulaire() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(" Gestion du Parc "));
        p.setPreferredSize(new Dimension(280, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.gridx = 0; gbc.gridy = 0;

        txtImmat = new JTextField(); txtMarque = new JTextField(); txtModele = new JTextField(); txtPrix = new JTextField();
        comboType = new JComboBox<>(new String[]{"SUV", "Citadine", "Utilitaire"});

        p.add(new JLabel("Immatriculation :"), gbc); gbc.gridy++; p.add(txtImmat, gbc); gbc.gridy++;
        p.add(new JLabel("Marque :"), gbc); gbc.gridy++; p.add(txtMarque, gbc); gbc.gridy++;
        p.add(new JLabel("Modèle :"), gbc); gbc.gridy++; p.add(txtModele, gbc); gbc.gridy++;
        p.add(new JLabel("Type :"), gbc); gbc.gridy++; p.add(comboType, gbc); gbc.gridy++;
        p.add(new JLabel("Tarif/Jour :"), gbc); gbc.gridy++; p.add(txtPrix, gbc); gbc.gridy++;

        gbc.insets = new Insets(15, 5, 5, 5);
        JPanel crud = new JPanel(new GridLayout(1, 3, 5, 0)); crud.setOpaque(false);
        JButton btnAdd = creerBouton("＋", COLOR_ACCENT);
        JButton btnUpd = creerBouton("💾", COLOR_PRIMARY);
        JButton btnDel = creerBouton("🗑", COLOR_DANGER);
        btnAdd.addActionListener(e -> actionAjouter());
        btnUpd.addActionListener(e -> actionModifier());
        btnDel.addActionListener(e -> actionSupprimer());
        crud.add(btnAdd); crud.add(btnUpd); crud.add(btnDel);
        p.add(crud, gbc);
        return p;
    }

    private void actionAjouter() {
        String imm = txtImmat.getText().trim();
        if (imm.isEmpty()) return;
        for (Vehicule v : agence.getFlotte()) {
            if (v.getImmatriculation().equalsIgnoreCase(imm)) {
                JOptionPane.showMessageDialog(this, "Immatriculation déjà existante."); 
                return;
            }
        }
        try {
            double p = Double.parseDouble(txtPrix.getText());
            String t = (String) comboType.getSelectedItem();
            Vehicule v = t.equals("SUV") ? new SUV(0, imm, txtMarque.getText(), txtModele.getText(), t, p, 1.5) :
                        t.equals("Citadine") ? new Citadine(0, imm, txtMarque.getText(), txtModele.getText(), t, p, 1.1) :
                        new Utilitaire(0, imm, txtMarque.getText(), txtModele.getText(), t, p, 1.3);
            agence.ajouterVehicule(v);
            rafraichirTableau(); 
            viderChamps();
        } catch (Exception ex) { 
            JOptionPane.showMessageDialog(this, "Format de tarif invalide."); 
        }
    }

    private void actionModifier() {
        Vehicule v = getSelectedVehicule();
        if (v == null) return;
        try {
            double nouveauTarif = Double.parseDouble(txtPrix.getText());
            v.setMarque(txtMarque.getText()); 
            v.setModele(txtModele.getText());
            agence.modifierVehicule(v.getImmatriculation(), nouveauTarif);
            rafraichirTableau(); 
            viderChamps(); 
            txtImmat.setEditable(true);
        } catch (Exception ex) { 
            JOptionPane.showMessageDialog(this, "Erreur de modification."); 
        }
    }

    private void actionSupprimer() {
        Vehicule v = getSelectedVehicule();
        if (v != null) {
            if (v.getStatut() != StatutVehicule.DISPONIBLE) {
                JOptionPane.showMessageDialog(this, "Impossible de supprimer un véhicule non disponible.");
                return;
            }
            agence.supprimerVehicule(v.getImmatriculation()); 
            rafraichirTableau(); 
            viderChamps(); 
            txtImmat.setEditable(true);
        }
    }

    private JPanel creerPanelStats() {
        JPanel p = new JPanel(new GridLayout(1, 3, 15, 0)); p.setOpaque(false); p.setBorder(new EmptyBorder(10, 15, 10, 15));
        lblStatsTotal = new JLabel("0"); lblStatsDispo = new JLabel("0"); lblStatsMaint = new JLabel("0");
        p.add(creerCard("TOTAL PARC", lblStatsTotal, COLOR_PRIMARY));
        p.add(creerCard("DISPONIBLES", lblStatsDispo, COLOR_ACCENT));
        p.add(creerCard("EN MAINTENANCE", lblStatsMaint, Color.ORANGE));
        return p;
    }

    private JPanel creerCard(String t, JLabel v, Color c) {
        JPanel card = new JPanel(new BorderLayout()); card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, c), new EmptyBorder(10, 15, 10, 15)));
        v.setFont(new Font("Segoe UI", Font.BOLD, 22));
        card.add(new JLabel(t), BorderLayout.NORTH); card.add(v, BorderLayout.CENTER);
        return card;
    }

    private JPanel creerBarreSession(User s) {
        JPanel p = new JPanel(new BorderLayout()); p.setBackground(COLOR_DARK); p.setBorder(new EmptyBorder(8, 15, 8, 15));
        JLabel l = new JLabel("Connecté : " + (isVisitor ? "Visiteur" : s.getEmail()));
        l.setForeground(Color.WHITE);
        JButton b = new JButton("Logout / Quitter");
        b.addActionListener(e -> { dispose(); new FenetreLogin(agence); });
        p.add(l, BorderLayout.WEST); p.add(b, BorderLayout.EAST);
        return p;
    }

    private JButton creerBouton(String t, Color c) {
        JButton b = new JButton(t); b.setBackground(c); b.setForeground(Color.BLACK); b.setOpaque(true); b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); return b;
    }

    private Vehicule getSelectedVehicule() {
        int r = tableVehicules.getSelectedRow();
        if (r == -1) return null;
        String immat = (String) modelVehicules.getValueAt(tableVehicules.convertRowIndexToModel(r), 0).toString();
        for (Vehicule v : agence.getFlotte()) if (v.getImmatriculation().equals(immat)) return v;
        return null;
    }

    private void viderChamps() { txtImmat.setText(""); txtMarque.setText(""); txtModele.setText(""); txtPrix.setText(""); txtImmat.setEditable(true); }

    private void quitterEtSauvegarder() {
        if (JOptionPane.showConfirmDialog(this, "Sauvegarder et quitter ?", "IAM Rent", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            agence.sauvegarderTout();
        }
        System.exit(0);
    }
}
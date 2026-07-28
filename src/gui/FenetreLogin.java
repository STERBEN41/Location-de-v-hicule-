package gui;

import modele.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FenetreLogin extends JFrame {

    // Palette de couleurs cohérente
    private static final Color COLOR_PRIMARY = new Color(41, 128, 185);
    private static final Color COLOR_BG = new Color(245, 247, 250);
    private static final Color COLOR_TEXT_DARK = new Color(44, 62, 80);

    public FenetreLogin(Agence agence) {
        appliquerTheme();

        // Configuration Fenêtre
        setTitle("IAM Rent-A-Car — Connexion");
        setSize(420, 520); // Augmentation légère de la hauteur pour le nouveau bouton
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Conteneur principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        setContentPane(mainPanel);

        // 1. EN-TÊTE
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel lblTitle = new JLabel("IAM Rent-A-Car", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(COLOR_PRIMARY);

        JLabel lblSubTitle = new JLabel("Veuillez vous identifier pour continuer", SwingConstants.CENTER);
        lblSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubTitle.setForeground(Color.GRAY);

        headerPanel.add(lblTitle);
        headerPanel.add(lblSubTitle);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. FORMULAIRE
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;

        // Email
        JLabel lblUser = new JLabel("Email / Login :");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(COLOR_TEXT_DARK);
        formPanel.add(lblUser, gbc);

        gbc.gridy++;
        JTextField txtUser = new JTextField();
        txtUser.setPreferredSize(new Dimension(0, 32));
        formPanel.add(txtUser, gbc);

        // Password
        gbc.gridy++;
        JLabel lblPass = new JLabel("Mot de passe :");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(lblPass, gbc);

        gbc.gridy++;
        JPasswordField txtPass = new JPasswordField();
        txtPass.setPreferredSize(new Dimension(0, 32));
        formPanel.add(txtPass, gbc);

        // Bouton Connexion
        gbc.gridy++;
        gbc.insets = new Insets(18, 6, 6, 6);
        JButton btnLogin = new JButton("Se connecter");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setBackground(COLOR_PRIMARY);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setOpaque(true);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(0, 36));
        formPanel.add(btnLogin, gbc);

        // Bouton Visiteur
        gbc.gridy++;
        gbc.insets = new Insets(5, 6, 0, 6);
        JButton btnVisiteur = new JButton("Continuer en Visiteur (Consultation)");
        btnVisiteur.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnVisiteur.setForeground(COLOR_PRIMARY);
        btnVisiteur.setContentAreaFilled(false);
        btnVisiteur.setBorderPainted(false);
        btnVisiteur.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formPanel.add(btnVisiteur, gbc);

        // --- NOUVEAU BOUTON INSCRIPTION ---
        gbc.gridy++;
        gbc.insets = new Insets(0, 6, 5, 6);
        JButton btnInscrire = new JButton("Pas de compte ? Créer un compte !");
        btnInscrire.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        btnInscrire.setForeground(new Color(127, 140, 141)); // Gris bleuté
        btnInscrire.setContentAreaFilled(false);
        btnInscrire.setBorderPainted(false);
        btnInscrire.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formPanel.add(btnInscrire, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        getRootPane().setDefaultButton(btnLogin);

        // --- LISTENERS ---

        btnLogin.addActionListener(e -> {
            String log = txtUser.getText().trim();
            String pass = new String(txtPass.getPassword()).trim();

            // Vérification Admin
            if (log.equals("admin") && pass.equals("admin")) {
                agence.setSessionActuelle(new Gestionnaire(1, "admin", "admin"));
                new FenetrePrincipale(agence);
                dispose();
            } 
            // Vérification Client (dans la liste chargée)
            else {
                Client trouve = null;
                for(Client c : agence.getClients()) {
                    if(c.getEmail().equals(log) && c.getPassword().equals(pass)) {
                        trouve = c;
                        break;
                    }
                }

                if (trouve != null) {
                    agence.setSessionActuelle(trouve);
                    new FenetrePrincipale(agence);
                    dispose();
                } else if (log.equals("client") && pass.equals("client")) {
                    // Secours pour la démo si fichier vide
                    Client cDemo = new Client(99, "client", "client", "Client Test", "P-123", "77");
                    agence.setSessionActuelle(cDemo);
                    new FenetrePrincipale(agence);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Identifiants invalides.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnVisiteur.addActionListener(e -> {
            agence.setSessionActuelle(null);
            new FenetrePrincipale(agence);
            dispose();
        });

        // ACTION DU BOUTON INSCRIPTION
        btnInscrire.addActionListener(e -> {
            new FenetreInscription(agence);
            // On ne ferme pas la fenêtre de login, l'inscription s'ouvre par-dessus
        });

        setVisible(true);
    }

    private void appliquerTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
    }
}
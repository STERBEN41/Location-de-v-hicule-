package gui;

import modele.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FenetreInscription extends JFrame {
    private static final Color COLOR_PRIMARY = new Color(41, 128, 185);
    private static final Color COLOR_BG = new Color(245, 247, 250);

    public FenetreInscription(Agence agence) {
        setTitle("Créer un compte - IAM Rent");
        setSize(450, 550);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        setContentPane(mainPanel);

        // Titre
        JLabel lblTitle = new JLabel("Inscription Client", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(COLOR_PRIMARY);
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Formulaire
        JPanel form = new JPanel(new GridLayout(10, 1, 5, 5));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JTextField txtNom = new JTextField();
        JTextField txtEmail = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        JTextField txtPermis = new JTextField();
        JTextField txtTel = new JTextField();

        form.add(new JLabel("Nom Complet :")); form.add(txtNom);
        form.add(new JLabel("Email (Identifiant) :")); form.add(txtEmail);
        form.add(new JLabel("Mot de passe :")); form.add(txtPass);
        form.add(new JLabel("Numéro de Permis :")); form.add(txtPermis);
        form.add(new JLabel("Téléphone :")); form.add(txtTel);

        mainPanel.add(form, BorderLayout.CENTER);

        // Bouton Valider
        JButton btnValider = new JButton("Créer mon compte");
        btnValider.setBackground(COLOR_PRIMARY);
        btnValider.setForeground(Color.WHITE);
        btnValider.setPreferredSize(new Dimension(0, 40));
        
        btnValider.addActionListener(e -> {
            String nom = txtNom.getText().trim();
            String email = txtEmail.getText().trim();
            String pass = new String(txtPass.getPassword()).trim();
            String permis = txtPermis.getText().trim();
            String tel = txtTel.getText().trim();

            if (nom.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez remplir les champs obligatoires.");
                return;
            }

            // Création du client
            int newId = agence.getClients().size() + 1;
            Client nouveauClient = new Client(newId, email, pass, nom, permis, tel);
            
            // Ajout et sauvegarde
            agence.ajouterClient(nouveauClient);
            agence.sauvegarderTout();

            JOptionPane.showMessageDialog(this, "Compte créé avec succès ! Connectez-vous.");
            dispose(); // On ferme l'inscription
        });

        mainPanel.add(btnValider, BorderLayout.SOUTH);
        setVisible(true);
    }
}
package de.wbs;

import javax.swing.*;
import java.awt.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginFrame extends JFrame {
    private final Validator validator = new Validator();
    private final JTextField emailField    = new JTextField(20);
    private final JPasswordField pwField  = new JPasswordField(20);
    private final JLabel statusLabel      = new JLabel(" ");
    private static final Logger log = LoggerFactory.getLogger(LoginFrame.class);
    // Passwort kommt aus Umgebungsvariable
    // In Produktion: von GitHub Secret gesetzt
    // Lokal: export APP_ADMIN_PASSWORD=testpasswort (Git Bash)
    private static final String ADMIN_PASSWORD = System.getenv("APP_ADMIN_PASSWORD");


    public LoginFrame() {
        super("Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Formular-Panel
        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));
        form.add(new JLabel("E-Mail:"));
        form.add(emailField);
        form.add(new JLabel("Passwort:"));
        form.add(pwField);
        form.add(new JLabel(""));

        // Login-Button mit Validierung
        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> onLogin());
        form.add(loginBtn);

        // Statuszeile unten
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 16, 12, 16));

        add(form,        BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null); // Bildschirmmitte
    }

    // Wird aufgerufen, wenn der Login-Button geklickt wird
    private void onLogin() {
        String email    = emailField.getText().trim();
        String password = new String(pwField.getPassword());

        // INFO: normaler Betrieb — wer versucht sich einzuloggen?
        log.info("Login-Versuch für: {}", email);

        String error = validator.validate(email, password);

        if (error == null) {
            // Admin-Check: ist es der Admin mit dem richtigen Secret-Passwort?
            if ("admin@firma.de".equals(email) && password.equals(ADMIN_PASSWORD)) {
                log.warn("Admin-Login: {}", email);
                statusLabel.setForeground(new Color(0, 0, 180));
                statusLabel.setText("Admin-Login erfolgreich!");
            } else {
                log.info("Login erfolgreich: {}", email);
                statusLabel.setForeground(new Color(0, 128, 0));
                statusLabel.setText("Login erfolgreich!");
            }
        } else {
            log.warn("Login fehlgeschlagen: {} — {}", email, error);
            statusLabel.setForeground(Color.RED);
            statusLabel.setText(error);
        }

    }
}

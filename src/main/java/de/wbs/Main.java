package de.wbs;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Swing-Regel: UI immer im Event Dispatch Thread starten
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
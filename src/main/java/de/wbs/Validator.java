package de.wbs;

public class Validator {
    /**
     * Prüft ob eine E-Mail-Adresse gültig ist.
     * Regel: muss @ enthalten, muss einen Punkt nach dem @ haben.
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) return false;
        int atIndex = email.indexOf("@");
        if (atIndex < 1) return false;
        // Punkt muss NACH dem @ kommen
        return email.indexOf(".", atIndex) > atIndex + 1;
    }

    /**
     * Prüft ob ein Passwort sicher genug ist.
     * Regeln: mind. 8 Zeichen, mind. 1 Großbuchstabe, mind. 1 Ziffer.
     */
    public boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasUpper = password.chars()
                .anyMatch(Character::isUpperCase);
        boolean hasDigit = password.chars()
                .anyMatch(Character::isDigit);
        return hasUpper && hasDigit;
    }

    /**
     * Gibt eine lesbare Fehlermeldung zurück (für das UI).
     * Gibt null zurück wenn alles gültig ist.
     */
    public String validate(String email, String password) {
        if (!isValidEmail(email))
            return "Ungültige E-Mail-Adresse.";
        if (!isValidPassword(password))
            return "Passwort: mind. 8 Zeichen, 1 Großbuchstabe, 1 Ziffer.";
        return null; // null = kein Fehler
    }
}

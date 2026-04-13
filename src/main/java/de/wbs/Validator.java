package de.wbs;

import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Validator {
    // Logger: static final — eine Instanz pro Klasse, wird geteilt
    // getLogger(Validator.class) sorgt für korrekten Klassennamen im Log
    private static final Logger log = LoggerFactory.getLogger(Validator.class);

    /**
     * Prüft ob eine E-Mail-Adresse syntaktisch gültig ist.
     * <p>
     * Der Regex ist in drei Teile aufgeteilt damit er lesbar bleibt.
     * Pattern.compile() wird einmalig beim Laden der Klasse ausgeführt
     * (static final) — nicht bei jedem Aufruf dieser Methode.
     * <p>
     * Aufbau:  local-part  @  domain
     * <p>
     *   local-part:  [a-zA-Z0-9._%+\-]+
     *     Erlaubt vor dem @: Buchstaben, Ziffern, Punkt, Unterstrich,
     *     Prozent, Plus, Bindestrich. Das + = mindestens 1 Zeichen.
     *     Nicht erlaubt: Leerzeichen, @, Komma, Klammern usw.
     * <p>
     *   @  — muss genau einmal vorkommen.
     * <p>
     *   domain:  [a-zA-Z0-9.\-]+  \.  [a-zA-Z]{2,}
     *     Domainname (Buchstaben, Ziffern, Punkt, Bindestrich),
     *     dann ein echter Punkt (\. statt . weil . allein "beliebiges Zeichen" heißt),
     *     dann TLD mit mindestens 2 Buchstaben (.de, .com, .museum ...).
     * <p>
     *   ^ und $ = Anfang und Ende des Strings.
     *     Ohne diese würde "xyz invalid@test.de xyz" als gültig durchgehen.
     * <p>
     *     String-Verkettung bei Pattern.compile() ist bewusst gewählt: Java fügt die Teile
     *     zur Compile-Zeit zusammen, der Regex ist also identisch zu einer Einzeiler-Version —
     *     aber man sieht sofort, was welcher Teil tut.
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^"                    // Anfang des Strings
            + "[a-zA-Z0-9._%+\\-]+"  // local-part: erlaubte Zeichen vor dem @
            + "@"                  // das @-Zeichen selbst
            + "[a-zA-Z0-9.\\-]+"  // Domainname
            + "\\."                // Punkt vor der TLD (escaped!)
            + "[a-zA-Z]{2,}"       // TLD: mindestens 2 Buchstaben
            + "$"                  // Ende des Strings
    );

    public boolean isBetterValidEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        boolean valid = EMAIL_PATTERN.matcher(email).matches();
        return valid;
    }

    /**
     * Prüft, ob eine E-Mail-Adresse gültig ist.
     * Regel: muss @ enthalten, muss einen Punkt nach dem @ haben.
     */
    public boolean isValidEmail(String email) {
        // DEBUG: Details für Entwickler, nicht für Produktion
        log.debug("Email-Prüfung gestartet für: {}", email);
        if (email == null || email.isBlank()) return false;
        int atIndex = email.indexOf("@");
        if (atIndex < 1) return false;
        // Punkt muss NACH dem @ kommen
        boolean result = email.indexOf(".", atIndex) > atIndex + 1;
        log.debug("Email [{}] -> {}", email, result ? "gültig" : "ungültig");
        return result;
    }

    /**
     * Prüft ob ein Passwort sicher genug ist.
     * Regeln: mind. 8 Zeichen, mind. 1 Großbuchstabe, mind. 1 Ziffer.
     */
    public boolean isValidPassword(String password) {
        // NIEMALS das Passwort selbst loggen — nur die Länge!
        log.debug("Passwort-Prüfung (Länge: {})", password == null ? "null" : password.length());
        if (password == null || password.length() < 8) {
            log.warn("Passwort zu kurz oder null");
            return false;
        }
        boolean hasUpper = password.chars()
                .anyMatch(Character::isUpperCase);
        boolean hasDigit = password.chars()
                .anyMatch(Character::isDigit);
        return hasUpper && hasDigit;
    }

    /**
     * Gibt eine lesbare Fehlermeldung zurück (für das UI).
     * Gibt null zurück, wenn alles gültig ist.
     */
    public String validate(String email, String password) {
        if (!isValidEmail(email))
            return "Ungültige E-Mail-Adresse.";
        if (!isValidPassword(password))
            return "Passwort: mind. 8 Zeichen, 1 Großbuchstabe, 1 Ziffer.";
        return null; // null = kein Fehler
    }
}

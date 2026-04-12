package de.wbs;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import static org.junit.jupiter.api.Assertions.*;

// @DisplayName: lesbarer Name im Test-Report statt Methodenname
@DisplayName("Validator — Unit-Tests")
class ValidatorTest {
    private Validator validator;

    // @BeforeEach läuft VOR JEDEM einzelnen Test.
    // Frische Instanz garantiert: kein Test beeinflusst den nächsten.
    @BeforeEach
    void setUp() {
        validator = new Validator();
    }

    // ── EMAIL-TESTS ─────────────────────────────────────────────
    @Nested
    @DisplayName("E-Mail-Validierung")
    class EmailTests {

        // @Test: diese Methode ist ein Testfall
        @Test
        @DisplayName("Gültige Email wird akzeptiert")
        void gueltigeEmail() {
            // Arrange-Act-Assert: Setup → Ausführen → Prüfen
            assertTrue(validator.isValidEmail("max@example.com"));
        }

        @Test
        @DisplayName("Email ohne @ wird abgelehnt")
        void emailOhneAt() {
            assertFalse(validator.isValidEmail("maxbeispiel.com"));
        }

        @Test
        @DisplayName("Email ohne Punkt nach @ wird abgelehnt")
        void emailOhnePunkt() {
            assertFalse(validator.isValidEmail("max@beispiel"));
        }

        // @ParameterizedTest + @NullAndEmptySource: JUnit übergibt
        // automatisch null und "" als Parameter — ein Test für beide Fälle.
        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Null und leer werden abgelehnt")
        void nullUndLeer(String email) {
            assertFalse(validator.isValidEmail(email));
        }
    }

    // ── PASSWORT-TESTS ──────────────────────────────────────────
    @Nested
    @DisplayName("Passwort-Validierung")
    class PasswortTests {

        // @CsvSource: ein Test mit mehreren Input/Output-Paaren.
        // Format: "Input, erwartetes Ergebnis"
        // So testet man viele Fälle ohne viele Methoden zu schreiben.
        @ParameterizedTest(name = "Passwort [{0}] erwartet {1}")
        @CsvSource({
                "Sicher1!,  true",
                "P@ssw0rd,  true",
                "kurz1A,    false",    // zu kurz (< 8 Zeichen)
                "alllower1, false",   // kein Großbuchstabe
                "GROSSOHNEZIFFER, false", // keine Ziffer
                "12345678,  false",   // kein Großbuchstabe
        })
        @DisplayName("Passwort verschiedene Fälle")
        void passwortVarianten(String pw, boolean erwartet) {
            assertEquals(erwartet, validator.isValidPassword(pw));
        }
    }

    // ── KOMBINIERTER VALIDIERUNGSTEST ───────────────────────────
    @Nested
    @DisplayName("Kombinierte Validierung")
    class ValidateTests {

        @Test
        @DisplayName("Gültige Kombination gibt null zurück")
        void allesGueltig() {
            // null = kein Fehler (so haben wir Validator.validate() definiert)
            assertNull(validator.validate("user@test.de", "Sicher1!"));
        }

        @Test
        @DisplayName("Ungültige Email gibt Fehlermeldung zurück")
        void emailFehler() {
            // assertNotNull: prüft dass IRGENDEINE Fehlermeldung zurückkommt
            assertNotNull(validator.validate("keineat", "Sicher1!"));
        }

        @Test
        @DisplayName("Fehlermeldungstext ist lesbar")
        void fehlermeldungText() {
            // assertNotNull + contains: Meldung existiert und ist verständlich
            String msg = validator.validate("x@x.de", "kurz");
            assertNotNull(msg);
            assertTrue(msg.contains("8"), "Meldung sollte min. Länge erwähnen");
        }
    }

    @Nested
    @DisplayName("isBetterValidEmail — Regex-Validierung")
    class BetterEmailTests {

        @ParameterizedTest(name = "[{0}] erwartet {1}")
        @CsvSource({
                "max@example.com,             true",
                "user.name+tag@sub.domain.de, true",
                "a@b.io,                      true",
                "x_%+test@firma-gmbh.de,      true",

                "kein-at-zeichen.de,          false",  // kein @
                "@nodomain.com,               false",  // nichts vor @
                "missing@tld,                 false",  // keine TLD
                "leerzeichen @test.de,        false",  // Leerzeichen
                "doppelt@@test.de,            false",  // zwei @
                "user@.de,                    false",  // Punkt direkt nach @
        })
        void varianten(String email, boolean erwartet) {
            assertEquals(erwartet, validator.isBetterValidEmail(email));
        }

        @ParameterizedTest
        @NullAndEmptySource
        void nullUndLeer(String email) {
            assertFalse(validator.isBetterValidEmail(email));
        }
    }
}
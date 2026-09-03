// training-examples/chapter-06 — MAUVAIS EXEMPLE : noms calqués sur la méthode appelée.
package domain;

import org.junit.jupiter.api.Test;

class NamingBadExampleTest {

    @Test
    void testEvaluatePrice() {
        // ... quel client ? quel passager ? impossible à savoir sans lire le corps du test.
    }

    @Test
    void shouldEvaluatePrice() {
        // même problème : "should" + nom de méthode ne raconte aucun scénario.
    }

    @Test
    void testReserve() {
        // Teste-t-on qu'on PEUT réserver ? qu'on ne peut PAS réserver deux fois ? on ne sait pas.
    }

    @Test
    void shouldCallReserve() {
        // décrit un appel de méthode, pas un comportement métier observable.
    }
}

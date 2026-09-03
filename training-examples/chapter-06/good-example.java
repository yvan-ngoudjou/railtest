// training-examples/chapter-06 — BON EXEMPLE : scénario + comportement attendu.
package domain;

import org.junit.jupiter.api.Test;

class NamingGoodExampleTest {

    @Test
    void premium_customer_gets_10_percent_discount() {
        // le scénario (client premium) et le résultat attendu (réduction de 10%) sont explicites.
    }

    @Test
    void child_under_10_travels_for_free() {
        // pas d'ambiguïté sur le scénario ni sur l'outcome.
    }

    @Test
    void reserved_seat_cannot_be_reserved_again() {
        // décrit un comportement métier (l'invariant), pas un appel de méthode.
    }
}

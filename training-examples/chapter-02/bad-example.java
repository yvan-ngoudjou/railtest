// training-examples/chapter-02 — MAUVAIS EXEMPLE, à but pédagogique uniquement.
// Ce test connaît un détail d'implémentation (PricingPolicy) qui n'est pas
// un comportement observable pour le Customer.
package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SeatFeeMethodTest {

    @Test
    void testSeatFeeFor() {
        PricingPolicy pricingPolicy = new PricingPolicy();
        SeatSelection chosenSeat = new SeatSelection(PassengerId.newId(), SeatId.newId(), true);

        Money fee = pricingPolicy.seatFeeFor(chosenSeat);

        assertEquals(Money.euros(5), fee);
    }
}

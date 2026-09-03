// training-examples/chapter-09 — MAUVAIS EXEMPLE : vérifier une communication intra-système.
package domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// NB : PricingPolicy est package-private ; ce test montre l'intention même s'il ne compile
// pas tel quel avec Mockito sur une classe finale non mockable par défaut. L'important est le
// principe illustré, pas la faisabilité technique exacte.
@ExtendWith(MockitoExtension.class)
class OrderCollaborationBadExampleTest {

    @Test
    void evaluate_price_calls_pricing_policy() {
        Trajet trajet = new Trajet(
                TrajetId.newId(), new Station("Paris"), new Station("Lyon"),
                LocalDateTime.of(2026, 6, 1, 8, 0), LocalDateTime.of(2026, 6, 1, 10, 0),
                Money.euros(100), TrainId.newId()
        );
        Customer customer = new Customer(CustomerId.newId(), "alice@example.com", true);
        Passenger adult = new Passenger(PassengerId.newId(), "Alice", 30);
        SeatSelection automaticSeat = new SeatSelection(adult.id(), SeatId.newId(), false);
        Order order = new Order(OrderId.newId(), customer, trajet, List.of(adult), List.of(automaticSeat));

        order.evaluatePrice();

        // Vérifie une collaboration interne, invisible pour le Customer : fige un détail
        // d'implémentation qui pourrait disparaître au prochain refactoring (voir chapter-02).
        // verify(pricingPolicyMock).transportPriceFor(customer, trajet);
    }
}

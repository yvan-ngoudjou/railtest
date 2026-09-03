// training-examples/chapter-12 — BON EXEMPLE : même comportement, Arrange réduit.
// Extrait réel : src/test/java/application/PurchaseTicketApplicationServiceTest.java
package application;

import demo.InMemoryTicketRepository;
import demo.InMemoryTrajetRepository;
import demo.InMemoryTrajetSeatRepository;
import domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ports.EmailSender;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ReducedArrangeGoodExampleTest {

    @Mock private EmailSender emailSender; // seul mock nécessaire

    @Test
    void successful_purchase_issues_one_ticket_per_passenger() {
        TrajetId trajetId = TrajetId.newId();
        InMemoryTrajetRepository trajetRepository = new InMemoryTrajetRepository();
        InMemoryTrajetSeatRepository trajetSeatRepository = new InMemoryTrajetSeatRepository();
        trajetRepository.save(new Trajet(trajetId, new Station("Paris"), new Station("Lyon"),
                LocalDateTime.of(2026, 6, 1, 8, 0), LocalDateTime.of(2026, 6, 1, 10, 0),
                Money.euros(100), TrainId.newId()));
        trajetSeatRepository.saveAll(List.of(new TrajetSeat(trajetId, SeatId.newId())));

        PurchaseTicketApplicationService sut = new PurchaseTicketApplicationService(
                trajetRepository, trajetSeatRepository, PaymentGatewayStub.succeeding(),
                new InMemoryTicketRepository(), emailSender
        );

        PurchaseResult result = sut.purchase(
                new Customer(CustomerId.newId(), "bob@example.com", false), trajetId,
                List.of(TicketRequest.automaticSeat(new Passenger(PassengerId.newId(), "Bob", 30)))
        );

        assertEquals(1, result.tickets().size());
    }
}

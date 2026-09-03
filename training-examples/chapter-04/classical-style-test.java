// training-examples/chapter-04 — exemple Classical.
// C'est la structure réelle de : src/test/java/application/PurchaseTicketApplicationServiceTest.java
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PurchaseTicketClassicalStyleTest {

    private final TrajetId trajetId = TrajetId.newId();
    private final SeatId seatId = SeatId.newId();

    private final InMemoryTrajetRepository trajetRepository = new InMemoryTrajetRepository();
    private final InMemoryTrajetSeatRepository trajetSeatRepository = new InMemoryTrajetSeatRepository();
    private final InMemoryTicketRepository ticketRepository = new InMemoryTicketRepository();

    @Mock private EmailSender emailSender; // seule frontière externe mockée

    @Test
    void a_successful_purchase_reserves_the_seat_and_issues_a_ticket() {
        trajetRepository.save(new Trajet(trajetId, new Station("Paris"), new Station("Lyon"),
                LocalDateTime.of(2026, 6, 1, 8, 0), LocalDateTime.of(2026, 6, 1, 10, 0),
                Money.euros(100), TrainId.newId()));
        trajetSeatRepository.saveAll(List.of(new TrajetSeat(trajetId, seatId)));
        Customer customer = new Customer(CustomerId.newId(), "bob@example.com", false);

        PurchaseTicketApplicationService sut = new PurchaseTicketApplicationService(
                trajetRepository, trajetSeatRepository, PaymentGatewayStub.succeeding(), ticketRepository, emailSender
        );

        PurchaseResult result = sut.purchase(customer, trajetId,
                List.of(TicketRequest.automaticSeat(new Passenger(PassengerId.newId(), "Bob", 30))));

        assertEquals(1, result.tickets().size());
        assertEquals(0, trajetSeatRepository.countAvailableSeats(trajetId));
        verify(emailSender).sendPurchaseConfirmation(eq(customer), any(), any());
    }
}

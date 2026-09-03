// training-examples/chapter-08 — BON EXEMPLE : Stub non vérifié, Mock vérifié à bon escient.
package application;

import demo.InMemoryTicketRepository;
import domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ports.EmailSender;
import ports.TrajetRepository;
import ports.TrajetSeatRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MockVsStubGoodExampleTest {

    @Mock private TrajetRepository trajetRepository;
    @Mock private TrajetSeatRepository trajetSeatRepository; // Stub : fournit une donnée, jamais vérifié
    @Mock private EmailSender emailSender;                    // Mock : son appel EST le comportement

    private final TrajetId trajetId = TrajetId.newId();
    private final Customer customer = new Customer(CustomerId.newId(), "bob@example.com", false);

    @Test
    void purchase_is_rejected_when_not_enough_seats_are_available() {
        Trajet trajet = new Trajet(trajetId, new Station("Paris"), new Station("Lyon"),
                LocalDateTime.of(2026, 6, 1, 8, 0), LocalDateTime.of(2026, 6, 1, 10, 0),
                Money.euros(100), TrainId.newId());
        when(trajetRepository.find(trajetId)).thenReturn(Optional.of(trajet));
        when(trajetSeatRepository.countAvailableSeats(trajetId)).thenReturn(0); // Stub

        PurchaseTicketApplicationService sut = new PurchaseTicketApplicationService(
                trajetRepository, trajetSeatRepository, PaymentGatewayStub.succeeding(),
                new InMemoryTicketRepository(), emailSender
        );

        assertThrows(NotEnoughAvailableSeatsException.class, () -> sut.purchase(
                customer, trajetId,
                List.of(TicketRequest.automaticSeat(new Passenger(PassengerId.newId(), "Bob", 30)))
        ));
        // Pas de verify(trajetSeatRepository)... : ce n'est pas ce que le comportement promet.
    }

    @Test
    void successful_purchase_sends_a_confirmation_email() {
        Trajet trajet = new Trajet(trajetId, new Station("Paris"), new Station("Lyon"),
                LocalDateTime.of(2026, 6, 1, 8, 0), LocalDateTime.of(2026, 6, 1, 10, 0),
                Money.euros(100), TrainId.newId());
        SeatId seatId = SeatId.newId();
        when(trajetRepository.find(trajetId)).thenReturn(Optional.of(trajet));
        when(trajetSeatRepository.countAvailableSeats(trajetId)).thenReturn(1);
        when(trajetSeatRepository.findAvailableSeats(trajetId, 1))
                .thenReturn(List.of(new TrajetSeat(trajetId, seatId)));

        PurchaseTicketApplicationService sut = new PurchaseTicketApplicationService(
                trajetRepository, trajetSeatRepository, PaymentGatewayStub.succeeding(),
                new InMemoryTicketRepository(), emailSender
        );

        sut.purchase(customer, trajetId,
                List.of(TicketRequest.automaticSeat(new Passenger(PassengerId.newId(), "Bob", 30))));

        // Ici, verify est légitime : l'envoi de la confirmation EST le comportement attendu.
        verify(emailSender).sendPurchaseConfirmation(eq(customer), any(), any());
    }
}

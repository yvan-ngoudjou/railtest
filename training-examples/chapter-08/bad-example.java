// training-examples/chapter-08 — MAUVAIS EXEMPLE : le Stub est aussi vérifié avec verify(...).
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailabilityStubVerifiedTest {

    @Mock private TrajetRepository trajetRepository;
    @Mock private TrajetSeatRepository trajetSeatRepository; // utilisé comme Stub...
    @Mock private EmailSender emailSender;

    @Test
    void purchase_is_rejected_when_not_enough_seats_are_available() {
        TrajetId trajetId = TrajetId.newId();
        Trajet trajet = new Trajet(trajetId, new Station("Paris"), new Station("Lyon"),
                LocalDateTime.of(2026, 6, 1, 8, 0), LocalDateTime.of(2026, 6, 1, 10, 0),
                Money.euros(100), TrainId.newId());
        when(trajetRepository.find(trajetId)).thenReturn(Optional.of(trajet));
        when(trajetSeatRepository.countAvailableSeats(trajetId)).thenReturn(0);

        PurchaseTicketApplicationService sut = new PurchaseTicketApplicationService(
                trajetRepository, trajetSeatRepository, PaymentGatewayStub.succeeding(),
                new InMemoryTicketRepository(), emailSender
        );

        try {
            sut.purchase(new Customer(CustomerId.newId(), "bob@example.com", false), trajetId,
                    List.of(TicketRequest.automaticSeat(new Passenger(PassengerId.newId(), "Bob", 30))));
        } catch (NotEnoughAvailableSeatsException expected) {
            // ok
        }

        // ... mais on vérifie quand même son appel : un détail d'implémentation, pas un comportement.
        verify(trajetSeatRepository, times(1)).countAvailableSeats(trajetId);
    }
}

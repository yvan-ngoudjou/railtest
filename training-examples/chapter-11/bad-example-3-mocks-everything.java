// training-examples/chapter-11 — Démonstration 3 : forêt de mocks + vérification de l'algorithme.
package application;

import domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ports.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeatAssignmentAlgorithmBadExampleTest {

    @Mock private TrajetRepository trajetRepository;
    @Mock private TrajetSeatRepository trajetSeatRepository;
    @Mock private PaymentGateway paymentGateway;
    @Mock private TicketRepository ticketRepository;
    @Mock private EmailSender emailSender;

    @Test
    void assigns_seats_by_pre_fetching_the_exact_batch_needed_in_one_call() {
        TrajetId trajetId = TrajetId.newId();
        Trajet trajet = new Trajet(trajetId, new Station("Paris"), new Station("Lyon"),
                LocalDateTime.of(2026, 6, 1, 8, 0), LocalDateTime.of(2026, 6, 1, 10, 0),
                Money.euros(100), TrainId.newId());
        SeatId seatA = SeatId.newId();
        SeatId seatB = SeatId.newId();
        when(trajetRepository.find(trajetId)).thenReturn(Optional.of(trajet));
        when(trajetSeatRepository.countAvailableSeats(trajetId)).thenReturn(2);
        // Suppose un seul appel groupé, comme avant le commit 08 :
        when(trajetSeatRepository.findAvailableSeats(trajetId, 2)).thenReturn(
                List.of(new TrajetSeat(trajetId, seatA), new TrajetSeat(trajetId, seatB))
        );
        when(paymentGateway.pay(any(), any())).thenReturn(PaymentResult.succeeded());

        PurchaseTicketApplicationService service = new PurchaseTicketApplicationService(
                trajetRepository, trajetSeatRepository, paymentGateway, ticketRepository, emailSender
        );

        service.purchase(new Customer(CustomerId.newId(), "bob@example.com", false), trajetId, List.of(
                TicketRequest.automaticSeat(new Passenger(PassengerId.newId(), "A", 30)),
                TicketRequest.automaticSeat(new Passenger(PassengerId.newId(), "B", 30))
        ));

        // Ce test fige l'algorithme exact d'avant le commit 08 : un seul appel groupé
        // "findAvailableSeats(trajetId, 2)". Après le commit 08 (une boucle incrémentale
        // appelant findAvailableSeats(trajetId, 1) puis findAvailableSeats(trajetId, 2)),
        // cet appel exact n'a jamais lieu et le test échoue pour un comportement inchangé.
        verify(trajetSeatRepository, times(1)).findAvailableSeats(trajetId, 2);
        verify(trajetSeatRepository, never()).findAvailableSeats(trajetId, 1);
    }
}

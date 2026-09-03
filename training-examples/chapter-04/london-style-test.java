// training-examples/chapter-04 — exemple London raisonnable (ni caricatural, ni "mauvais").
package application;

import domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ports.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseTicketLondonStyleTest {

    @Mock private TrajetRepository trajetRepository;
    @Mock private TrajetSeatRepository trajetSeatRepository;
    @Mock private PaymentGateway paymentGateway;
    @Mock private TicketRepository ticketRepository;
    @Mock private EmailSender emailSender;

    @Test
    void a_successful_purchase_reserves_the_seat_issues_a_ticket_and_sends_a_confirmation() {
        TrajetId trajetId = TrajetId.newId();
        SeatId seatId = SeatId.newId();
        Trajet trajet = new Trajet(trajetId, new Station("Paris"), new Station("Lyon"),
                LocalDateTime.of(2026, 6, 1, 8, 0), LocalDateTime.of(2026, 6, 1, 10, 0),
                Money.euros(100), TrainId.newId());
        TrajetSeat availableSeat = new TrajetSeat(trajetId, seatId);
        Customer customer = new Customer(CustomerId.newId(), "bob@example.com", false);
        Passenger adult = new Passenger(PassengerId.newId(), "Bob", 30);

        when(trajetRepository.find(trajetId)).thenReturn(Optional.of(trajet));
        when(trajetSeatRepository.countAvailableSeats(trajetId)).thenReturn(1);
        when(trajetSeatRepository.findAvailableSeats(trajetId, 1)).thenReturn(List.of(availableSeat));
        when(paymentGateway.pay(eq(customer), any())).thenReturn(PaymentResult.succeeded());

        PurchaseTicketApplicationService sut = new PurchaseTicketApplicationService(
                trajetRepository, trajetSeatRepository, paymentGateway, ticketRepository, emailSender
        );

        sut.purchase(customer, trajetId, List.of(TicketRequest.automaticSeat(adult)));

        // Chaque interaction jugée "importante" est vérifiée explicitement.
        verify(trajetSeatRepository).saveAll(List.of(availableSeat));
        verify(paymentGateway).pay(eq(customer), eq(Money.euros(100)));
        verify(ticketRepository).saveAll(any());
        verify(emailSender).sendPurchaseConfirmation(eq(customer), any(), any());
    }
}

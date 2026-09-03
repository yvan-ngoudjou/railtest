// training-examples/chapter-09 — BON EXEMPLE : vérifier une communication inter-système.
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
import ports.PaymentGateway;
import ports.PaymentResult;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentInterSystemGoodExampleTest {

    @Mock private PaymentGateway paymentGateway; // dépendance externe réelle : la banque
    @Mock private EmailSender emailSender;

    @Test
    void a_successful_purchase_charges_the_customer_for_the_exact_evaluated_price() {
        TrajetId trajetId = TrajetId.newId();
        InMemoryTrajetRepository trajetRepository = new InMemoryTrajetRepository();
        InMemoryTrajetSeatRepository trajetSeatRepository = new InMemoryTrajetSeatRepository();
        Trajet trajet = new Trajet(trajetId, new Station("Paris"), new Station("Lyon"),
                LocalDateTime.of(2026, 6, 1, 8, 0), LocalDateTime.of(2026, 6, 1, 10, 0),
                Money.euros(100), TrainId.newId());
        trajetRepository.save(trajet);
        SeatId seatId = SeatId.newId();
        trajetSeatRepository.saveAll(List.of(new TrajetSeat(trajetId, seatId)));
        Customer customer = new Customer(CustomerId.newId(), "alice@example.com", true);
        when(paymentGateway.pay(eq(customer), eq(Money.euros(90)))).thenReturn(PaymentResult.succeeded());

        PurchaseTicketApplicationService service = new PurchaseTicketApplicationService(
                trajetRepository, trajetSeatRepository, paymentGateway, new InMemoryTicketRepository(), emailSender
        );

        service.purchase(customer, trajetId,
                List.of(TicketRequest.automaticSeat(new Passenger(PassengerId.newId(), "Alice", 30))));

        // L'appel à une dépendance externe réelle (le paiement) EST le comportement observable.
        verify(paymentGateway).pay(customer, Money.euros(90));
    }
}

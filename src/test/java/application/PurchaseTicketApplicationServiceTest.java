package application;

import demo.InMemoryTicketRepository;
import demo.InMemoryTrajetRepository;
import demo.InMemoryTrajetSeatRepository;
import domain.Customer;
import domain.CustomerId;
import domain.Money;
import domain.Passenger;
import domain.PassengerId;
import domain.SeatId;
import domain.Station;
import domain.Trajet;
import domain.TrajetId;
import domain.TrajetSeat;
import domain.TrainId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ports.EmailSender;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Reference behavioral tests for {@link PurchaseTicketApplicationService}.
 *
 * <p>{@link InMemoryTrajetSeatRepository} is a real, working collaborator here (classical
 * style): its {@code countAvailableSeats} return value is used to make a decision, which is
 * exactly what a Stub is for — we never verify how many times it was called.
 *
 * <p>{@link EmailSender} is mocked and its invocation verified, because sending the confirmation
 * is itself the externally observable outcome the "successful purchase" behavior promises.
 */
@ExtendWith(MockitoExtension.class)
class PurchaseTicketApplicationServiceTest {

    private final TrajetId trajetId = TrajetId.newId();
    private final SeatId seatA = SeatId.newId();
    private final SeatId seatB = SeatId.newId();

    private final InMemoryTrajetRepository trajetRepository = new InMemoryTrajetRepository();
    private final InMemoryTrajetSeatRepository trajetSeatRepository = new InMemoryTrajetSeatRepository();
    private final InMemoryTicketRepository ticketRepository = new InMemoryTicketRepository();

    @Mock
    private EmailSender emailSender;

    private final Customer customer = new Customer(CustomerId.newId(), "customer@example.com", false);

    @BeforeEach
    void givenATrajetWithTwoAvailableSeats() {
        Trajet trajet = new Trajet(
                trajetId, new Station("Paris"), new Station("Lyon"),
                LocalDateTime.of(2026, 6, 1, 8, 0), LocalDateTime.of(2026, 6, 1, 10, 0),
                Money.euros(100), TrainId.newId()
        );
        trajetRepository.save(trajet);
        trajetSeatRepository.saveAll(List.of(new TrajetSeat(trajetId, seatA), new TrajetSeat(trajetId, seatB)));
    }

    @Test
    void successful_purchase_reserves_the_assigned_seats() {
        PurchaseTicketApplicationService service = serviceWithPaymentThat(FakePaymentGateway.alwaysSucceeds());

        service.purchase(customer, trajetId, List.of(
                TicketRequest.automaticSeat(new Passenger(PassengerId.newId(), "Adult", 30))
        ));

        assertEquals(1, trajetSeatRepository.countAvailableSeats(trajetId));
    }

    @Test
    void successful_purchase_issues_one_ticket_per_passenger() {
        PurchaseTicketApplicationService service = serviceWithPaymentThat(FakePaymentGateway.alwaysSucceeds());
        Passenger adult = new Passenger(PassengerId.newId(), "Adult", 30);
        Passenger child = new Passenger(PassengerId.newId(), "Child", 8);

        PurchaseResult result = service.purchase(customer, trajetId, List.of(
                TicketRequest.chosenSeat(adult, seatA),
                TicketRequest.automaticSeat(child)
        ));

        assertEquals(2, result.tickets().size());
    }

    @Test
    void successful_purchase_sends_a_purchase_confirmation() {
        PurchaseTicketApplicationService service = serviceWithPaymentThat(FakePaymentGateway.alwaysSucceeds());

        service.purchase(customer, trajetId, List.of(
                TicketRequest.automaticSeat(new Passenger(PassengerId.newId(), "Adult", 30))
        ));

        verify(emailSender).sendPurchaseConfirmation(eq(customer), any(), any());
    }

    @Test
    void failed_payment_releases_the_reserved_seats_and_issues_no_ticket() {
        PurchaseTicketApplicationService service = serviceWithPaymentThat(FakePaymentGateway.alwaysFails());

        PurchaseResult result = service.purchase(customer, trajetId, List.of(
                TicketRequest.automaticSeat(new Passenger(PassengerId.newId(), "Adult", 30))
        ));

        assertEquals(0, result.tickets().size());
        assertEquals(2, trajetSeatRepository.countAvailableSeats(trajetId));
    }

    @Test
    void failed_payment_does_not_send_a_confirmation() {
        PurchaseTicketApplicationService service = serviceWithPaymentThat(FakePaymentGateway.alwaysFails());

        service.purchase(customer, trajetId, List.of(
                TicketRequest.automaticSeat(new Passenger(PassengerId.newId(), "Adult", 30))
        ));

        verify(emailSender, never()).sendPurchaseConfirmation(any(), any(), any());
    }

    @Test
    void purchase_is_rejected_when_there_are_not_enough_available_seats() {
        PurchaseTicketApplicationService service = serviceWithPaymentThat(FakePaymentGateway.alwaysSucceeds());
        List<TicketRequest> threeRequestsForTwoSeats = List.of(
                TicketRequest.automaticSeat(new Passenger(PassengerId.newId(), "A", 30)),
                TicketRequest.automaticSeat(new Passenger(PassengerId.newId(), "B", 30)),
                TicketRequest.automaticSeat(new Passenger(PassengerId.newId(), "C", 30))
        );

        assertThrows(NotEnoughAvailableSeatsException.class,
                () -> service.purchase(customer, trajetId, threeRequestsForTwoSeats));
    }

    private PurchaseTicketApplicationService serviceWithPaymentThat(FakePaymentGateway paymentGateway) {
        return new PurchaseTicketApplicationService(
                trajetRepository, trajetSeatRepository, paymentGateway, ticketRepository, emailSender
        );
    }
}

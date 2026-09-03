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
import domain.Ticket;
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
import ports.PaymentGateway;
import ports.PaymentResult;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Reference behavioral tests for {@link PurchaseTicketApplicationService}.
 *
 * <p>{@code sut} names the Application Service under test: the entry point of the behavior each
 * test exercises.
 *
 * <p>{@link InMemoryTrajetSeatRepository} and {@link InMemoryTicketRepository} are real, working
 * collaborators here (classical style). Data read from them can provide indirect input to the
 * SUT (e.g. {@code countAvailableSeats}), while their final state can also be inspected to verify
 * an observable outcome (e.g. which tickets were persisted). Calls to these repositories are
 * never interaction-tested.
 *
 * <p>{@link EmailSender} is mocked and its invocation verified, because sending the confirmation
 * is itself the externally observable outcome the "successful purchase" behavior promises. The
 * data passed to it is checked too, since it matters which tickets the confirmation refers to.
 *
 * <p>The amount charged through {@link PaymentGateway} is verified in one dedicated test: the
 * money requested from an external payment provider is itself an observable outcome of the
 * purchase, unlike, say, the exact number of calls to a repository.
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
        PurchaseTicketApplicationService sut = serviceWithPaymentThat(PaymentGatewayStub.succeeding());

        sut.purchase(customer, trajetId, List.of(
                TicketRequest.automaticSeat(new Passenger(PassengerId.newId(), "Adult", 30))
        ));

        assertEquals(1, trajetSeatRepository.countAvailableSeats(trajetId));
    }

    @Test
    void successful_purchase_issues_one_ticket_per_passenger() {
        PurchaseTicketApplicationService sut = serviceWithPaymentThat(PaymentGatewayStub.succeeding());
        Passenger adult = new Passenger(PassengerId.newId(), "Adult", 30);
        Passenger child = new Passenger(PassengerId.newId(), "Child", 8);

        PurchaseResult result = sut.purchase(customer, trajetId, List.of(
                TicketRequest.chosenSeat(adult, seatA),
                TicketRequest.automaticSeat(child)
        ));

        assertEquals(2, result.tickets().size());
        assertTrue(result.tickets().stream().anyMatch(ticket -> ticket.passengerId().equals(adult.id())));
        assertTrue(result.tickets().stream().anyMatch(ticket -> ticket.passengerId().equals(child.id())));
    }

    @Test
    void a_ticket_for_an_explicitly_chosen_seat_matches_that_exact_seat() {
        PurchaseTicketApplicationService sut = serviceWithPaymentThat(PaymentGatewayStub.succeeding());
        Passenger adult = new Passenger(PassengerId.newId(), "Adult", 30);

        PurchaseResult result = sut.purchase(customer, trajetId, List.of(
                TicketRequest.chosenSeat(adult, seatA)
        ));

        Ticket adultTicket = result.tickets().stream()
                .filter(ticket -> ticket.passengerId().equals(adult.id()))
                .findFirst()
                .orElseThrow();
        assertEquals(seatA, adultTicket.seatId());
    }

    @Test
    void successful_purchase_persists_the_issued_tickets() {
        PurchaseTicketApplicationService sut = serviceWithPaymentThat(PaymentGatewayStub.succeeding());
        Passenger adult = new Passenger(PassengerId.newId(), "Adult", 30);
        Passenger child = new Passenger(PassengerId.newId(), "Child", 8);

        sut.purchase(customer, trajetId, List.of(
                TicketRequest.chosenSeat(adult, seatA),
                TicketRequest.automaticSeat(child)
        ));

        // Observe the repository's final state, not how it was called.
        List<Ticket> persistedTickets = ticketRepository.findByTrajetId(trajetId);
        assertEquals(2, persistedTickets.size());
        assertTrue(persistedTickets.stream().anyMatch(ticket -> ticket.passengerId().equals(adult.id())));
        assertTrue(persistedTickets.stream().anyMatch(ticket -> ticket.passengerId().equals(child.id())));
    }

    @Test
    void successful_purchase_sends_a_purchase_confirmation_for_every_issued_ticket() {
        PurchaseTicketApplicationService sut = serviceWithPaymentThat(PaymentGatewayStub.succeeding());
        Passenger adult = new Passenger(PassengerId.newId(), "Adult", 30);
        Passenger child = new Passenger(PassengerId.newId(), "Child", 8);

        sut.purchase(customer, trajetId, List.of(
                TicketRequest.chosenSeat(adult, seatA),
                TicketRequest.automaticSeat(child)
        ));

        verify(emailSender).sendPurchaseConfirmation(eq(customer), any(), argThat(tickets ->
                tickets.size() == 2
                        && tickets.stream().anyMatch(t -> t.passengerId().equals(adult.id()))
                        && tickets.stream().anyMatch(t -> t.passengerId().equals(child.id()))
        ));
    }

    @Test
    void successful_purchase_charges_the_payment_gateway_the_evaluated_price() {
        // Premium customer + one chosen seat: 100 EUR - 10% + 5 EUR seat fee = 95 EUR.
        // Verifying this interaction is legitimate: the amount requested from an external
        // payment provider is itself an observable outcome of the purchase.
        Customer premiumCustomer = new Customer(CustomerId.newId(), "premium@example.com", true);
        PaymentGateway paymentGateway = mock(PaymentGateway.class);
        when(paymentGateway.pay(eq(premiumCustomer), any())).thenReturn(PaymentResult.succeeded());
        PurchaseTicketApplicationService sut = serviceWithPaymentThat(paymentGateway);

        sut.purchase(premiumCustomer, trajetId, List.of(
                TicketRequest.chosenSeat(new Passenger(PassengerId.newId(), "Adult", 30), seatA)
        ));

        verify(paymentGateway).pay(premiumCustomer, Money.euros(95));
    }

    @Test
    void failed_payment_releases_the_reserved_seats_and_issues_no_ticket() {
        PurchaseTicketApplicationService sut = serviceWithPaymentThat(PaymentGatewayStub.failing());

        PurchaseResult result = sut.purchase(customer, trajetId, List.of(
                TicketRequest.automaticSeat(new Passenger(PassengerId.newId(), "Adult", 30))
        ));

        assertEquals(0, result.tickets().size());
        assertEquals(2, trajetSeatRepository.countAvailableSeats(trajetId));
    }

    @Test
    void failed_payment_does_not_send_a_confirmation() {
        PurchaseTicketApplicationService sut = serviceWithPaymentThat(PaymentGatewayStub.failing());

        sut.purchase(customer, trajetId, List.of(
                TicketRequest.automaticSeat(new Passenger(PassengerId.newId(), "Adult", 30))
        ));

        verify(emailSender, never()).sendPurchaseConfirmation(any(), any(), any());
    }

    @Test
    void purchase_is_rejected_when_there_are_not_enough_available_seats() {
        PurchaseTicketApplicationService sut = serviceWithPaymentThat(PaymentGatewayStub.succeeding());
        List<TicketRequest> threeRequestsForTwoSeats = List.of(
                TicketRequest.automaticSeat(new Passenger(PassengerId.newId(), "A", 30)),
                TicketRequest.automaticSeat(new Passenger(PassengerId.newId(), "B", 30)),
                TicketRequest.automaticSeat(new Passenger(PassengerId.newId(), "C", 30))
        );

        assertThrows(NotEnoughAvailableSeatsException.class,
                () -> sut.purchase(customer, trajetId, threeRequestsForTwoSeats));
    }

    private PurchaseTicketApplicationService serviceWithPaymentThat(PaymentGateway paymentGateway) {
        return new PurchaseTicketApplicationService(
                trajetRepository, trajetSeatRepository, paymentGateway, ticketRepository, emailSender
        );
    }
}

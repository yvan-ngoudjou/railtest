package application;

import domain.Customer;
import domain.Money;
import domain.Order;
import domain.OrderId;
import domain.Passenger;
import domain.SeatSelection;
import domain.SeatId;
import domain.Ticket;
import domain.Trajet;
import domain.TrajetId;
import domain.TrajetSeat;
import ports.EmailSender;
import ports.PaymentGateway;
import ports.PaymentResult;
import ports.TicketRepository;
import ports.TrajetRepository;
import ports.TrajetSeatRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Orchestrates the "purchase tickets" use case: check availability, assign seats, price the
 * order, reserve the seats, request payment, then either issue tickets and confirm by email, or
 * release the seats back.
 *
 * <p>Pricing itself is not decided here: it is delegated to {@link Order#evaluatePrice()}, which
 * is the natural owner of that business rule.
 */
public class PurchaseTicketApplicationService {

    private final TrajetRepository trajetRepository;
    private final TrajetSeatRepository trajetSeatRepository;
    private final PaymentGateway paymentGateway;
    private final TicketRepository ticketRepository;
    private final EmailSender emailSender;

    public PurchaseTicketApplicationService(
            TrajetRepository trajetRepository,
            TrajetSeatRepository trajetSeatRepository,
            PaymentGateway paymentGateway,
            TicketRepository ticketRepository,
            EmailSender emailSender
    ) {
        this.trajetRepository = trajetRepository;
        this.trajetSeatRepository = trajetSeatRepository;
        this.paymentGateway = paymentGateway;
        this.ticketRepository = ticketRepository;
        this.emailSender = emailSender;
    }

    public PurchaseResult purchase(Customer customer, TrajetId trajetId, List<TicketRequest> requests) {
        Trajet trajet = trajetRepository.find(trajetId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown trajet " + trajetId.value()));

        int availableSeats = trajetSeatRepository.countAvailableSeats(trajetId);
        if (requests.size() > availableSeats) {
            throw new NotEnoughAvailableSeatsException(trajetId, requests.size(), availableSeats);
        }

        List<Assignment> assignments = assignSeats(trajetId, requests);
        Order order = buildOrder(customer, trajet, assignments);
        Money price = order.evaluatePrice();

        List<TrajetSeat> reservedSeats = reserveSeats(assignments);

        PaymentResult paymentResult = paymentGateway.pay(customer, price);
        if (!paymentResult.success()) {
            releaseSeats(reservedSeats);
            return PurchaseResult.failed(price);
        }

        List<Ticket> tickets = issueTickets(trajetId, assignments);
        ticketRepository.saveAll(tickets);
        emailSender.sendPurchaseConfirmation(customer, order, tickets);

        return PurchaseResult.succeeded(price, tickets);
    }

    private List<Assignment> assignSeats(TrajetId trajetId, List<TicketRequest> requests) {
        Set<SeatId> claimedSeatIds = new HashSet<>();
        List<Assignment> assignments = new ArrayList<>();

        for (TicketRequest request : requests) {
            TrajetSeat seat = request.hasChosenSeat()
                    ? trajetSeatRepository.find(trajetId, request.chosenSeatId())
                        .orElseThrow(() -> new IllegalArgumentException("Unknown seat " + request.chosenSeatId()))
                    : pickNextAvailableSeat(trajetId, claimedSeatIds);
            claimedSeatIds.add(seat.seatId());
            assignments.add(new Assignment(request, seat));
        }
        return assignments;
    }

    /** Picks the next available seat not already claimed earlier in this same purchase. */
    private TrajetSeat pickNextAvailableSeat(TrajetId trajetId, Set<SeatId> claimedSeatIds) {
        return trajetSeatRepository.findAvailableSeats(trajetId, claimedSeatIds.size() + 1).stream()
                .filter(seat -> !claimedSeatIds.contains(seat.seatId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No available seat left for trajet " + trajetId.value()));
    }

    private Order buildOrder(Customer customer, Trajet trajet, List<Assignment> assignments) {
        List<Passenger> passengers = assignments.stream().map(a -> a.request().passenger()).toList();
        List<SeatSelection> seatSelections = assignments.stream()
                .map(a -> new SeatSelection(a.request().passenger().id(), a.seat().seatId(), a.request().hasChosenSeat()))
                .toList();
        return new Order(OrderId.newId(), customer, trajet, passengers, seatSelections);
    }

    private List<TrajetSeat> reserveSeats(List<Assignment> assignments) {
        List<TrajetSeat> seats = assignments.stream().map(Assignment::seat).toList();
        seats.forEach(TrajetSeat::reserve);
        trajetSeatRepository.saveAll(seats);
        return seats;
    }

    private void releaseSeats(List<TrajetSeat> seats) {
        seats.forEach(TrajetSeat::release);
        trajetSeatRepository.saveAll(seats);
    }

    private List<Ticket> issueTickets(TrajetId trajetId, List<Assignment> assignments) {
        return assignments.stream()
                .map(a -> Ticket.issueFor(trajetId, a.request().passenger().id(), a.seat().seatId()))
                .toList();
    }

    private record Assignment(TicketRequest request, TrajetSeat seat) {
    }
}

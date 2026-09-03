package demo;

import application.CreateTrajetApplicationService;
import application.PurchaseResult;
import application.PurchaseTicketApplicationService;
import application.TicketRequest;
import domain.Customer;
import domain.CustomerId;
import domain.Money;
import domain.Passenger;
import domain.PassengerId;
import domain.Seat;
import domain.SeatId;
import domain.Station;
import domain.Train;
import domain.TrainId;
import domain.TrajetId;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Runs one end-to-end scenario through the two application services, printing what happens.
 * This is a demonstration, not a substitute for the automated test suite.
 */
public class DemoApplication {

    public static void main(String[] args) {
        InMemoryTrainRepository trainRepository = new InMemoryTrainRepository();
        InMemoryTrainMovementRepository trainMovementRepository = new InMemoryTrainMovementRepository();
        InMemoryTrajetRepository trajetRepository = new InMemoryTrajetRepository();
        InMemoryTrajetSeatRepository trajetSeatRepository = new InMemoryTrajetSeatRepository();
        InMemoryTicketRepository ticketRepository = new InMemoryTicketRepository();

        Seat chosenSeat = new Seat(SeatId.newId(), "1A");
        Train train = new Train(TrainId.newId(), "TGV-8421", "TGV", List.of(chosenSeat, new Seat(SeatId.newId(), "1B")));
        trainRepository.add(train);

        CreateTrajetApplicationService createTrajet = new CreateTrajetApplicationService(
                trainRepository, trainMovementRepository, trajetRepository, trajetSeatRepository
        );
        TrajetId trajetId = createTrajet.createTrajet(
                new Station("Paris"),
                new Station("Lyon"),
                LocalDateTime.of(2026, 6, 1, 8, 0),
                LocalDateTime.of(2026, 6, 1, 10, 0),
                Money.euros(100),
                train.id()
        );
        System.out.println("Trajet created: Paris -> Lyon, 100 EUR base price");

        Customer premiumCustomer = new Customer(CustomerId.newId(), "premium.customer@example.com", true);
        Passenger adult = new Passenger(PassengerId.newId(), "Adult passenger", 30);
        Passenger child = new Passenger(PassengerId.newId(), "Child passenger", 8);

        PurchaseTicketApplicationService purchaseTicket = new PurchaseTicketApplicationService(
                trajetRepository, trajetSeatRepository, new SimplePaymentGateway(), ticketRepository, new ConsoleEmailSender()
        );
        PurchaseResult result = purchaseTicket.purchase(
                premiumCustomer,
                trajetId,
                List.of(
                        TicketRequest.chosenSeat(adult, chosenSeat.id()),
                        TicketRequest.automaticSeat(child)
                )
        );

        System.out.println("Total price: " + result.price());
        System.out.println("Payment successful: " + result.successful());
        System.out.println("Tickets issued: " + result.tickets().size());
    }
}

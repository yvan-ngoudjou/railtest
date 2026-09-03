package application;

import demo.InMemoryTrainMovementRepository;
import demo.InMemoryTrainRepository;
import demo.InMemoryTrajetRepository;
import demo.InMemoryTrajetSeatRepository;
import domain.Money;
import domain.OverlappingTrainMovementException;
import domain.Seat;
import domain.SeatId;
import domain.Station;
import domain.Train;
import domain.TrainId;
import domain.Trajet;
import domain.TrajetId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Behavioral tests for {@link CreateTrajetApplicationService}.
 *
 * <p>The SUT collaborates with simple in-memory implementations of its persistence ports.
 * The tests observe the resulting state instead of verifying repository interactions,
 * so they remain independent of the orchestration details used to reach that state.
 */
class CreateTrajetApplicationServiceTest {

    private final InMemoryTrainRepository trainRepository = new InMemoryTrainRepository();
    private final InMemoryTrainMovementRepository trainMovementRepository = new InMemoryTrainMovementRepository();
    private final InMemoryTrajetRepository trajetRepository = new InMemoryTrajetRepository();
    private final InMemoryTrajetSeatRepository trajetSeatRepository = new InMemoryTrajetSeatRepository();

    private final CreateTrajetApplicationService sut = new CreateTrajetApplicationService(
            trainRepository, trainMovementRepository, trajetRepository, trajetSeatRepository
    );

    @Test
    void creating_a_trajet_makes_every_seat_of_the_train_available_for_it() {
        Train train = new Train(TrainId.newId(), "TGV-1", "TGV",
                List.of(new Seat(SeatId.newId(), "1A"), new Seat(SeatId.newId(), "1B")));
        trainRepository.add(train);

        TrajetId trajetId = sut.createTrajet(
                new Station("Paris"), new Station("Lyon"),
                LocalDateTime.of(2026, 6, 1, 8, 0), LocalDateTime.of(2026, 6, 1, 10, 0),
                Money.euros(100), train.id()
        );

        assertEquals(2, trajetSeatRepository.countAvailableSeats(trajetId));
    }

    @Test
    void creating_a_trajet_registers_the_requested_route_and_schedule() {
        Train train = new Train(TrainId.newId(), "TGV-1", "TGV", List.of(new Seat(SeatId.newId(), "1A")));
        trainRepository.add(train);
        Station departure = new Station("Paris");
        Station arrival = new Station("Lyon");
        LocalDateTime departureAt = LocalDateTime.of(2026, 6, 1, 8, 0);
        LocalDateTime arrivalAt = LocalDateTime.of(2026, 6, 1, 10, 0);

        TrajetId trajetId = sut.createTrajet(departure, arrival, departureAt, arrivalAt, Money.euros(100), train.id());

        Trajet trajet = trajetRepository.find(trajetId).orElseThrow();
        assertEquals(departure, trajet.departureStation());
        assertEquals(arrival, trajet.arrivalStation());
        assertEquals(departureAt, trajet.period().start());
        assertEquals(arrivalAt, trajet.period().end());
        assertEquals(Money.euros(100), trajet.basePrice());
    }

    @Test
    void a_train_cannot_be_assigned_to_two_trajets_with_overlapping_periods() {
        Train train = new Train(TrainId.newId(), "TGV-1", "TGV", List.of(new Seat(SeatId.newId(), "1A")));
        trainRepository.add(train);
        sut.createTrajet(
                new Station("Paris"), new Station("Lyon"),
                LocalDateTime.of(2026, 6, 1, 8, 0), LocalDateTime.of(2026, 6, 1, 10, 0),
                Money.euros(100), train.id()
        );

        assertThrows(OverlappingTrainMovementException.class, () -> sut.createTrajet(
                new Station("Paris"), new Station("Marseille"),
                LocalDateTime.of(2026, 6, 1, 9, 0), LocalDateTime.of(2026, 6, 1, 12, 0),
                Money.euros(80), train.id()
        ));
    }

    @Test
    void a_train_can_start_a_new_trajet_when_the_previous_one_has_ended() {
        Train train = new Train(TrainId.newId(), "TGV-1", "TGV", List.of(new Seat(SeatId.newId(), "1A")));
        trainRepository.add(train);
        sut.createTrajet(
                new Station("Paris"), new Station("Lyon"),
                LocalDateTime.of(2026, 6, 1, 8, 0), LocalDateTime.of(2026, 6, 1, 10, 0),
                Money.euros(100), train.id()
        );

        // Boundary case: the second trajet starts exactly when the first one ends.
        TrajetId secondTrajetId = sut.createTrajet(
                new Station("Lyon"), new Station("Marseille"),
                LocalDateTime.of(2026, 6, 1, 10, 0), LocalDateTime.of(2026, 6, 1, 12, 0),
                Money.euros(80), train.id()
        );

        assertEquals(1, trajetSeatRepository.countAvailableSeats(secondTrajetId));
    }

    @Test
    void rejected_overlapping_trajet_is_not_created() {
        Train train = new Train(TrainId.newId(), "TGV-1", "TGV", List.of(new Seat(SeatId.newId(), "1A")));
        trainRepository.add(train);
        sut.createTrajet(
                new Station("Paris"), new Station("Lyon"),
                LocalDateTime.of(2026, 6, 1, 8, 0), LocalDateTime.of(2026, 6, 1, 10, 0),
                Money.euros(100), train.id()
        );

        assertThrows(OverlappingTrainMovementException.class, () -> sut.createTrajet(
                new Station("Paris"), new Station("Marseille"),
                LocalDateTime.of(2026, 6, 1, 9, 0), LocalDateTime.of(2026, 6, 1, 12, 0),
                Money.euros(80), train.id()
        ));

        // assertThrows only proves an error was signaled; it does not by itself prove that the
        // rejected attempt left no trace. The overlap check happens before any write, so the
        // train must still have exactly the one movement registered by the first, successful call.
        assertEquals(1, trainMovementRepository.findByTrain(train.id()).size());
    }
}

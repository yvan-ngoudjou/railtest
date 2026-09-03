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
import domain.TrajetId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * These tests exercise the real (in-memory) adapters rather than mocks: the orchestration is a
 * classical collaboration between the application sut and small, genuinely working
 * collaborators, so the tests describe the outcome of "create trajet" without knowing which
 * repository method was called how many times.
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
    void a_train_can_be_assigned_to_a_second_trajet_once_the_first_one_is_over() {
        Train train = new Train(TrainId.newId(), "TGV-1", "TGV", List.of(new Seat(SeatId.newId(), "1A")));
        trainRepository.add(train);
        sut.createTrajet(
                new Station("Paris"), new Station("Lyon"),
                LocalDateTime.of(2026, 6, 1, 8, 0), LocalDateTime.of(2026, 6, 1, 10, 0),
                Money.euros(100), train.id()
        );

        TrajetId secondTrajetId = sut.createTrajet(
                new Station("Lyon"), new Station("Marseille"),
                LocalDateTime.of(2026, 6, 1, 11, 0), LocalDateTime.of(2026, 6, 1, 13, 0),
                Money.euros(80), train.id()
        );

        assertEquals(1, trajetSeatRepository.countAvailableSeats(secondTrajetId));
    }
}

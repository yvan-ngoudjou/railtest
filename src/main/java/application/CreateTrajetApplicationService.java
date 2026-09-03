package application;

import domain.Money;
import domain.OverlappingTrainMovementException;
import domain.Period;
import domain.Station;
import domain.Train;
import domain.TrainId;
import domain.TrainMovement;
import domain.TrainMovementId;
import domain.Trajet;
import domain.TrajetId;
import domain.TrajetSeat;
import ports.TrainMovementRepository;
import ports.TrainRepository;
import ports.TrajetRepository;
import ports.TrajetSeatRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Orchestrates the "create trajet" use case:
 * fetch the train's movements, check for scheduling conflicts, then create and persist the
 * trajet, its train movement and one available seat per physical seat of the train.
 *
 * <p>The overlap rule itself is not decided here: it is delegated to
 * {@link TrainMovement#anyOverlaps}, which is the natural owner of that business rule.
 */
public class CreateTrajetApplicationService {

    private final TrainRepository trainRepository;
    private final TrainMovementRepository trainMovementRepository;
    private final TrajetRepository trajetRepository;
    private final TrajetSeatRepository trajetSeatRepository;

    public CreateTrajetApplicationService(
            TrainRepository trainRepository,
            TrainMovementRepository trainMovementRepository,
            TrajetRepository trajetRepository,
            TrajetSeatRepository trajetSeatRepository
    ) {
        this.trainRepository = trainRepository;
        this.trainMovementRepository = trainMovementRepository;
        this.trajetRepository = trajetRepository;
        this.trajetSeatRepository = trajetSeatRepository;
    }

    public TrajetId createTrajet(
            Station departureStation,
            Station arrivalStation,
            LocalDateTime departureAt,
            LocalDateTime arrivalAt,
            Money basePrice,
            TrainId trainId
    ) {
        Train train = trainRepository.find(trainId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown train " + trainId.value()));

        Period period = new Period(departureAt, arrivalAt);
        List<TrainMovement> existingMovements = trainMovementRepository.findByTrain(trainId);
        if (TrainMovement.anyOverlaps(existingMovements, period)) {
            throw new OverlappingTrainMovementException(trainId, period);
        }

        TrajetId trajetId = TrajetId.newId();
        Trajet trajet = new Trajet(
                trajetId, departureStation, arrivalStation, departureAt, arrivalAt, basePrice, trainId
        );
        trajetRepository.save(trajet);

        TrainMovement movement = new TrainMovement(TrainMovementId.newId(), trainId, departureAt, arrivalAt);
        trainMovementRepository.save(movement);

        List<TrajetSeat> trajetSeats = train.seats().stream()
                .map(seat -> new TrajetSeat(trajetId, seat.id()))
                .toList();
        trajetSeatRepository.saveAll(trajetSeats);

        return trajetId;
    }
}

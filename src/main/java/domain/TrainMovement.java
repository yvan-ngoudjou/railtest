package domain;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a period during which a train is committed to a trajet.
 *
 * <p>A single {@code TrainMovement} only knows how to compare itself to one period; it does not
 * decide, on its own, whether a whole train is available. That decision requires looking at every
 * movement already booked for the train, see {@link #anyOverlaps(List, Period)}.
 */
public final class TrainMovement {

    private final TrainMovementId id;
    private final TrainId trainId;
    private final LocalDateTime departureAt;
    private final LocalDateTime arrivalAt;

    public TrainMovement(TrainMovementId id, TrainId trainId, LocalDateTime departureAt, LocalDateTime arrivalAt) {
        if (!arrivalAt.isAfter(departureAt)) {
            throw new IllegalArgumentException("arrivalAt must be after departureAt");
        }
        this.id = id;
        this.trainId = trainId;
        this.departureAt = departureAt;
        this.arrivalAt = arrivalAt;
    }

    public boolean overlaps(Period period) {
        return departureAt.isBefore(period.end()) && period.start().isBefore(arrivalAt);
    }

    /** True if the given period conflicts with any of the train's already booked movements. */
    public static boolean anyOverlaps(List<TrainMovement> existingMovements, Period candidate) {
        return existingMovements.stream().anyMatch(movement -> movement.overlaps(candidate));
    }

    public TrainMovementId id() {
        return id;
    }

    public TrainId trainId() {
        return trainId;
    }
}

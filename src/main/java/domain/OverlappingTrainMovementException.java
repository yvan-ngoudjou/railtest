package domain;

/** Thrown when a train would be assigned to two trajets whose periods overlap. */
public class OverlappingTrainMovementException extends RuntimeException {

    public OverlappingTrainMovementException(TrainId trainId, Period period) {
        super("Train " + trainId.value() + " is already committed during " + period);
    }
}

package domain;

import java.time.LocalDateTime;

/** A journey offered to customers: a train running between two stations at a given time. */
public final class Trajet {

    private final TrajetId id;
    private final Station departureStation;
    private final Station arrivalStation;
    private final LocalDateTime departureAt;
    private final LocalDateTime arrivalAt;
    private final Money basePrice;
    private final TrainId trainId;

    public Trajet(
            TrajetId id,
            Station departureStation,
            Station arrivalStation,
            LocalDateTime departureAt,
            LocalDateTime arrivalAt,
            Money basePrice,
            TrainId trainId
    ) {
        if (!arrivalAt.isAfter(departureAt)) {
            throw new IllegalArgumentException("arrivalAt must be after departureAt");
        }
        this.id = id;
        this.departureStation = departureStation;
        this.arrivalStation = arrivalStation;
        this.departureAt = departureAt;
        this.arrivalAt = arrivalAt;
        this.basePrice = basePrice;
        this.trainId = trainId;
    }

    public Period period() {
        return new Period(departureAt, arrivalAt);
    }

    public TrajetId id() {
        return id;
    }

    public Station departureStation() {
        return departureStation;
    }

    public Station arrivalStation() {
        return arrivalStation;
    }

    public Money basePrice() {
        return basePrice;
    }

    public TrainId trainId() {
        return trainId;
    }
}

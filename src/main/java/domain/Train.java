package domain;

import java.util.List;

/** A physical train, with the fixed list of seats it is equipped with. */
public final class Train {

    private final TrainId id;
    private final String number;
    private final String brand;
    private final List<Seat> seats;

    public Train(TrainId id, String number, String brand, List<Seat> seats) {
        this.id = id;
        this.number = number;
        this.brand = brand;
        this.seats = List.copyOf(seats);
    }

    public TrainId id() {
        return id;
    }

    public String number() {
        return number;
    }

    public String brand() {
        return brand;
    }

    public List<Seat> seats() {
        return seats;
    }
}

package domain;

/**
 * The state of one physical seat for one specific trajet.
 *
 * <p>{@code version} exists to represent, conceptually, where an optimistic-locking strategy
 * would plug in for a real persistence adapter. This training project does not implement a
 * database, so no actual concurrency guarantee is provided here: that guarantee belongs to the
 * integration level and cannot be proven by the Domain Model's unit tests alone.
 */
public final class TrajetSeat {

    private final TrajetId trajetId;
    private final SeatId seatId;
    private TrajetSeatStatus status;
    private long version;

    public TrajetSeat(TrajetId trajetId, SeatId seatId) {
        this(trajetId, seatId, TrajetSeatStatus.AVAILABLE, 0);
    }

    public TrajetSeat(TrajetId trajetId, SeatId seatId, TrajetSeatStatus status, long version) {
        this.trajetId = trajetId;
        this.seatId = seatId;
        this.status = status;
        this.version = version;
    }

    public void reserve() {
        if (status == TrajetSeatStatus.RESERVED) {
            throw new SeatAlreadyReservedException(trajetId, seatId);
        }
        status = TrajetSeatStatus.RESERVED;
        version++;
    }

    public void release() {
        status = TrajetSeatStatus.AVAILABLE;
        version++;
    }

    public TrajetId trajetId() {
        return trajetId;
    }

    public SeatId seatId() {
        return seatId;
    }

    public TrajetSeatStatus status() {
        return status;
    }

    public long version() {
        return version;
    }
}

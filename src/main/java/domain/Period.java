package domain;

import java.time.LocalDateTime;

/** A time period with a start and an end, used to detect scheduling conflicts. */
public record Period(LocalDateTime start, LocalDateTime end) {

    public Period {
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("end must be after start");
        }
    }

    public boolean overlaps(Period other) {
        return this.start.isBefore(other.end) && other.start.isBefore(this.end);
    }
}

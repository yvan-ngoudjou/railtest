package domain;

import java.util.UUID;

public record SeatId(String value) {

    public static SeatId newId() {
        return new SeatId(UUID.randomUUID().toString());
    }
}

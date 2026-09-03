package domain;

import java.util.UUID;

public record TrainMovementId(String value) {

    public static TrainMovementId newId() {
        return new TrainMovementId(UUID.randomUUID().toString());
    }
}

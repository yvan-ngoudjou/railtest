package domain;

import java.util.UUID;

public record TrainId(String value) {

    public static TrainId newId() {
        return new TrainId(UUID.randomUUID().toString());
    }
}

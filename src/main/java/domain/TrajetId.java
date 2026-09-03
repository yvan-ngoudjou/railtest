package domain;

import java.util.UUID;

public record TrajetId(String value) {

    public static TrajetId newId() {
        return new TrajetId(UUID.randomUUID().toString());
    }
}

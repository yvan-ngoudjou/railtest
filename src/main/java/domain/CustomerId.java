package domain;

import java.util.UUID;

public record CustomerId(String value) {

    public static CustomerId newId() {
        return new CustomerId(UUID.randomUUID().toString());
    }
}

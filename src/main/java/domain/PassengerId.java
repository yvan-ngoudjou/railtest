package domain;

import java.util.UUID;

public record PassengerId(String value) {

    public static PassengerId newId() {
        return new PassengerId(UUID.randomUUID().toString());
    }
}

package domain;

import java.util.UUID;

public record TicketId(String value) {

    public static TicketId newId() {
        return new TicketId(UUID.randomUUID().toString());
    }
}

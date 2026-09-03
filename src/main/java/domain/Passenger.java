package domain;

/** A person who will travel and receive a {@link Ticket}. May differ from the {@link Customer}. */
public record Passenger(PassengerId id, String name, int age) {

    private static final int CHILD_AGE_LIMIT = 10;

    public boolean isChild() {
        return age < CHILD_AGE_LIMIT;
    }
}

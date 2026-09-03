package domain;

/** The person who places and pays for an {@link Order}. */
public record Customer(CustomerId id, String email, boolean premium) {

    public boolean isPremium() {
        return premium;
    }
}

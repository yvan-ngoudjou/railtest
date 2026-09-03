package domain;

/** The attempt to collect payment for an {@link Order}. */
public final class Payment {

    private final PaymentId id;
    private final Money amount;
    private PaymentStatus status;

    public Payment(PaymentId id, Money amount) {
        this.id = id;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
    }

    public void succeed() {
        status = PaymentStatus.SUCCEEDED;
    }

    public void fail() {
        status = PaymentStatus.FAILED;
    }

    public boolean isSuccessful() {
        return status == PaymentStatus.SUCCEEDED;
    }

    public PaymentId id() {
        return id;
    }

    public Money amount() {
        return amount;
    }

    public PaymentStatus status() {
        return status;
    }
}

package ports;

public record PaymentResult(boolean success) {

    public static PaymentResult succeeded() {
        return new PaymentResult(true);
    }

    public static PaymentResult failure() {
        return new PaymentResult(false);
    }
}

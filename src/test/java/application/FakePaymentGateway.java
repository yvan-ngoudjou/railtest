package application;

import domain.Customer;
import domain.Money;
import ports.PaymentGateway;
import ports.PaymentResult;

/**
 * A hand-written test double whose outcome is set in advance: a stub used to steer the
 * "payment succeeds" / "payment fails" branches of {@link PurchaseTicketApplicationService},
 * without needing Mockito for something this simple.
 */
class FakePaymentGateway implements PaymentGateway {

    private final PaymentResult result;

    private FakePaymentGateway(PaymentResult result) {
        this.result = result;
    }

    static FakePaymentGateway alwaysSucceeds() {
        return new FakePaymentGateway(PaymentResult.succeeded());
    }

    static FakePaymentGateway alwaysFails() {
        return new FakePaymentGateway(PaymentResult.failure());
    }

    @Override
    public PaymentResult pay(Customer customer, Money amount) {
        return result;
    }
}

package application;

import domain.Customer;
import domain.Money;
import ports.PaymentGateway;
import ports.PaymentResult;

/**
 * A hand-written test double that returns a predetermined result: it plays the role of a Stub,
 * used to steer the "payment succeeds" / "payment fails" branches of
 * {@link PurchaseTicketApplicationService} without needing Mockito for something this simple.
 *
 * <p>Note the distinction between the implementation and the role: this class is a plain Java
 * object, not a Mockito mock, yet it still plays a Stub role. Conversely, a Mockito mock can play
 * a Stub role too (see {@code training-examples/chapter-08}) — the mechanism and the role are not
 * the same thing.
 */
class PaymentGatewayStub implements PaymentGateway {

    private final PaymentResult result;

    private PaymentGatewayStub(PaymentResult result) {
        this.result = result;
    }

    static PaymentGatewayStub succeeding() {
        return new PaymentGatewayStub(PaymentResult.succeeded());
    }

    static PaymentGatewayStub failing() {
        return new PaymentGatewayStub(PaymentResult.failure());
    }

    @Override
    public PaymentResult pay(Customer customer, Money amount) {
        return result;
    }
}

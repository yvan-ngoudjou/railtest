package demo;

import domain.Customer;
import domain.Money;
import ports.PaymentGateway;
import ports.PaymentResult;

/** Always accepts the payment. No real payment provider is involved. */
public class SimplePaymentGateway implements PaymentGateway {

    @Override
    public PaymentResult pay(Customer customer, Money amount) {
        return PaymentResult.succeeded();
    }
}

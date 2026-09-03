package ports;

import domain.Customer;
import domain.Money;

/**
 * An external system able to charge a customer. Never implemented for real in this training
 * project: no real payment provider is called.
 */
public interface PaymentGateway {

    PaymentResult pay(Customer customer, Money amount);
}

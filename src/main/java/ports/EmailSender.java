package ports;

import domain.Customer;
import domain.Order;
import domain.Ticket;

import java.util.List;

/**
 * An external system able to notify a customer. Never implemented for real in this training
 * project: no email is actually sent.
 */
public interface EmailSender {

    void sendPurchaseConfirmation(Customer customer, Order order, List<Ticket> tickets);
}

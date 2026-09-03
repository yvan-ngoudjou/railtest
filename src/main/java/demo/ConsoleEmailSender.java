package demo;

import domain.Customer;
import domain.Order;
import domain.Ticket;
import ports.EmailSender;

import java.util.List;

/** Prints the confirmation to the console instead of sending a real email. */
public class ConsoleEmailSender implements EmailSender {

    @Override
    public void sendPurchaseConfirmation(Customer customer, Order order, List<Ticket> tickets) {
        System.out.println("Confirmation sent to " + customer.email() + ": " + tickets.size() + " ticket(s) issued.");
    }
}

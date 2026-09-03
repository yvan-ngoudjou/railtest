package application;

import domain.Money;
import domain.Ticket;

import java.util.List;

public record PurchaseResult(boolean successful, Money price, List<Ticket> tickets) {

    public static PurchaseResult succeeded(Money price, List<Ticket> tickets) {
        return new PurchaseResult(true, price, List.copyOf(tickets));
    }

    public static PurchaseResult failed(Money price) {
        return new PurchaseResult(false, price, List.of());
    }
}

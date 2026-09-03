package ports;

import domain.Ticket;

import java.util.List;

public interface TicketRepository {

    void saveAll(List<Ticket> tickets);
}

package ports;

import domain.Ticket;
import domain.TrajetId;

import java.util.List;

public interface TicketRepository {

    void saveAll(List<Ticket> tickets);

    List<Ticket> findByTrajetId(TrajetId trajetId);
}

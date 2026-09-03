package demo;

import domain.Ticket;
import domain.TrajetId;
import ports.TicketRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryTicketRepository implements TicketRepository {

    private final List<Ticket> tickets = new ArrayList<>();

    @Override
    public void saveAll(List<Ticket> ticketsToSave) {
        tickets.addAll(ticketsToSave);
    }

    @Override
    public List<Ticket> findByTrajetId(TrajetId trajetId) {
        return tickets.stream().filter(ticket -> ticket.trajetId().equals(trajetId)).toList();
    }
}

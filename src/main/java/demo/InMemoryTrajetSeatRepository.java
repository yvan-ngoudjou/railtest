package demo;

import domain.SeatId;
import domain.TrajetId;
import domain.TrajetSeat;
import domain.TrajetSeatStatus;
import ports.TrajetSeatRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryTrajetSeatRepository implements TrajetSeatRepository {

    private final List<TrajetSeat> seats = new ArrayList<>();

    @Override
    public void saveAll(List<TrajetSeat> seatsToSave) {
        for (TrajetSeat seat : seatsToSave) {
            seats.removeIf(existing -> existing.trajetId().equals(seat.trajetId()) && existing.seatId().equals(seat.seatId()));
            seats.add(seat);
        }
    }

    @Override
    public int countAvailableSeats(TrajetId trajetId) {
        return (int) seats.stream()
                .filter(seat -> seat.trajetId().equals(trajetId) && seat.status() == TrajetSeatStatus.AVAILABLE)
                .count();
    }

    @Override
    public List<TrajetSeat> findAvailableSeats(TrajetId trajetId, int quantity) {
        return seats.stream()
                .filter(seat -> seat.trajetId().equals(trajetId) && seat.status() == TrajetSeatStatus.AVAILABLE)
                .limit(quantity)
                .toList();
    }

    @Override
    public Optional<TrajetSeat> find(TrajetId trajetId, SeatId seatId) {
        return seats.stream()
                .filter(seat -> seat.trajetId().equals(trajetId) && seat.seatId().equals(seatId))
                .findFirst();
    }
}

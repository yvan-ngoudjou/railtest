package ports;

import domain.SeatId;
import domain.TrajetId;
import domain.TrajetSeat;

import java.util.List;
import java.util.Optional;

public interface TrajetSeatRepository {

    int countAvailableSeats(TrajetId trajetId);

    List<TrajetSeat> findAvailableSeats(TrajetId trajetId, int quantity);

    Optional<TrajetSeat> find(TrajetId trajetId, SeatId seatId);

    void saveAll(List<TrajetSeat> seats);
}

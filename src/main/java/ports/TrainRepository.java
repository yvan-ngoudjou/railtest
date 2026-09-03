package ports;

import domain.Train;
import domain.TrainId;

import java.util.Optional;

public interface TrainRepository {

    Optional<Train> find(TrainId id);
}

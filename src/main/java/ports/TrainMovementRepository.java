package ports;

import domain.TrainId;
import domain.TrainMovement;

import java.util.List;

public interface TrainMovementRepository {

    List<TrainMovement> findByTrain(TrainId trainId);

    void save(TrainMovement movement);
}

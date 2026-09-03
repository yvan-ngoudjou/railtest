package demo;

import domain.TrainId;
import domain.TrainMovement;
import ports.TrainMovementRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryTrainMovementRepository implements TrainMovementRepository {

    private final List<TrainMovement> movements = new ArrayList<>();

    @Override
    public List<TrainMovement> findByTrain(TrainId trainId) {
        return movements.stream().filter(movement -> movement.trainId().equals(trainId)).toList();
    }

    @Override
    public void save(TrainMovement movement) {
        movements.add(movement);
    }
}

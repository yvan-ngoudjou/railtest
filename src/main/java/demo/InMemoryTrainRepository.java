package demo;

import domain.Train;
import domain.TrainId;
import ports.TrainRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Extremely simple in-memory adapter, only meant to run {@link DemoApplication}. It is not a
 * pedagogical subject on its own: real persistence is out of scope for this training project.
 */
public class InMemoryTrainRepository implements TrainRepository {

    private final Map<TrainId, Train> trains = new HashMap<>();

    public void add(Train train) {
        trains.put(train.id(), train);
    }

    @Override
    public Optional<Train> find(TrainId id) {
        return Optional.ofNullable(trains.get(id));
    }
}

package demo;

import domain.Trajet;
import domain.TrajetId;
import ports.TrajetRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryTrajetRepository implements TrajetRepository {

    private final Map<TrajetId, Trajet> trajets = new HashMap<>();

    @Override
    public void save(Trajet trajet) {
        trajets.put(trajet.id(), trajet);
    }

    @Override
    public Optional<Trajet> find(TrajetId id) {
        return Optional.ofNullable(trajets.get(id));
    }
}

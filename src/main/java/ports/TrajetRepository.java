package ports;

import domain.Trajet;
import domain.TrajetId;

import java.util.Optional;

public interface TrajetRepository {

    void save(Trajet trajet);

    Optional<Trajet> find(TrajetId id);
}

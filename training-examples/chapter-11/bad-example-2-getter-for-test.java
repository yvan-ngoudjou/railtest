// training-examples/chapter-11 — Démonstration 2 : assertion sur un état interne technique.
//
// NOTE : TrajetSeat n'expose volontairement aucun getVersion() dans le projet réel — c'est
// justement le point de ce mauvais exemple. Ce fichier montre ce qu'un développeur pourrait être
// tenté d'ajouter, et pourquoi il ne faut pas le faire.
package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VersionCounterBadExampleTest {

    @Test
    void reserving_then_releasing_increments_version_twice() {
        TrajetSeat seat = new TrajetSeat(TrajetId.newId(), SeatId.newId());

        seat.reserve();
        seat.release();

        // Nécessiterait d'ajouter TrajetSeat.getVersion() uniquement pour ce test :
        // assertEquals(2, seat.getVersion());
        //
        // "version" n'a de sens que pour un futur mécanisme d'optimistic locking (§9 du cahier
        // des charges) : ce n'est un comportement observable pour personne d'autre que la couche
        // de persistance, qui n'existe pas dans ce projet.
    }
}

package domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Reference behavioral tests for {@link TrainMovement}.
 */
class TrainMovementTest {

    private final TrainId trainId = TrainId.newId();

    @Test
    void two_periods_that_do_not_touch_do_not_overlap() {
        TrainMovement movement = new TrainMovement(
                TrainMovementId.newId(), trainId,
                LocalDateTime.of(2026, 6, 1, 8, 0),
                LocalDateTime.of(2026, 6, 1, 10, 0)
        );
        Period laterSameDay = new Period(
                LocalDateTime.of(2026, 6, 1, 11, 0),
                LocalDateTime.of(2026, 6, 1, 13, 0)
        );

        assertFalse(movement.overlaps(laterSameDay));
    }

    @Test
    void two_periods_that_intersect_overlap() {
        TrainMovement movement = new TrainMovement(
                TrainMovementId.newId(), trainId,
                LocalDateTime.of(2026, 6, 1, 8, 0),
                LocalDateTime.of(2026, 6, 1, 10, 0)
        );
        Period overlapping = new Period(
                LocalDateTime.of(2026, 6, 1, 9, 0),
                LocalDateTime.of(2026, 6, 1, 11, 0)
        );

        assertTrue(movement.overlaps(overlapping));
    }

    @Test
    void a_new_period_conflicting_with_any_existing_movement_is_detected() {
        TrainMovement morningTrip = new TrainMovement(
                TrainMovementId.newId(), trainId,
                LocalDateTime.of(2026, 6, 1, 8, 0),
                LocalDateTime.of(2026, 6, 1, 10, 0)
        );
        Period conflicting = new Period(
                LocalDateTime.of(2026, 6, 1, 9, 0),
                LocalDateTime.of(2026, 6, 1, 12, 0)
        );

        assertTrue(TrainMovement.anyOverlaps(List.of(morningTrip), conflicting));
    }

    @Test
    void a_free_period_does_not_conflict_with_existing_movements() {
        TrainMovement morningTrip = new TrainMovement(
                TrainMovementId.newId(), trainId,
                LocalDateTime.of(2026, 6, 1, 8, 0),
                LocalDateTime.of(2026, 6, 1, 10, 0)
        );
        Period afternoon = new Period(
                LocalDateTime.of(2026, 6, 1, 14, 0),
                LocalDateTime.of(2026, 6, 1, 16, 0)
        );

        assertFalse(TrainMovement.anyOverlaps(List.of(morningTrip), afternoon));
    }
}

package domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Reference behavioral tests for {@link TrainMovement}.
 *
 * <p>The {@code overlaps} tests below cover the full matrix of relative positions two periods
 * can have towards one another, including the boundary cases where periods touch exactly without
 * overlapping. These belong here rather than in an Application Service test, so that a use case
 * test (e.g. {@code CreateTrajetApplicationServiceTest}) only needs a couple of representative
 * scenarios and can rely on this class for the exhaustive rule.
 */
class TrainMovementTest {

    private final TrainId trainId = TrainId.newId();
    private final TrainMovement movement = new TrainMovement(
            TrainMovementId.newId(), trainId,
            LocalDateTime.of(2026, 6, 1, 8, 0),
            LocalDateTime.of(2026, 6, 1, 10, 0)
    );

    @Test
    void two_periods_that_do_not_touch_do_not_overlap() {
        Period laterSameDay = new Period(
                LocalDateTime.of(2026, 6, 1, 11, 0),
                LocalDateTime.of(2026, 6, 1, 13, 0)
        );

        assertFalse(movement.overlaps(laterSameDay));
    }

    @Test
    void a_period_starting_before_and_ending_during_another_overlaps() {
        Period partialOverlapAtStart = new Period(
                LocalDateTime.of(2026, 6, 1, 7, 0),
                LocalDateTime.of(2026, 6, 1, 9, 0)
        );

        assertTrue(movement.overlaps(partialOverlapAtStart));
    }

    @Test
    void a_period_starting_during_and_ending_after_another_overlaps() {
        Period partialOverlapAtEnd = new Period(
                LocalDateTime.of(2026, 6, 1, 9, 0),
                LocalDateTime.of(2026, 6, 1, 11, 0)
        );

        assertTrue(movement.overlaps(partialOverlapAtEnd));
    }

    @Test
    void a_period_fully_included_in_another_overlaps() {
        Period fullyIncluded = new Period(
                LocalDateTime.of(2026, 6, 1, 8, 30),
                LocalDateTime.of(2026, 6, 1, 9, 30)
        );

        assertTrue(movement.overlaps(fullyIncluded));
    }

    @Test
    void a_period_fully_enclosing_another_overlaps() {
        Period fullyEnclosing = new Period(
                LocalDateTime.of(2026, 6, 1, 7, 0),
                LocalDateTime.of(2026, 6, 1, 11, 0)
        );

        assertTrue(movement.overlaps(fullyEnclosing));
    }

    @Test
    void two_identical_periods_overlap() {
        Period identical = new Period(
                LocalDateTime.of(2026, 6, 1, 8, 0),
                LocalDateTime.of(2026, 6, 1, 10, 0)
        );

        assertTrue(movement.overlaps(identical));
    }

    @Test
    void a_period_ending_exactly_when_another_starts_does_not_overlap() {
        Period endingAtTheOtherStart = new Period(
                LocalDateTime.of(2026, 6, 1, 6, 0),
                LocalDateTime.of(2026, 6, 1, 8, 0)
        );

        assertFalse(movement.overlaps(endingAtTheOtherStart));
    }

    @Test
    void a_period_starting_exactly_when_another_ends_does_not_overlap() {
        Period startingAtTheOtherEnd = new Period(
                LocalDateTime.of(2026, 6, 1, 10, 0),
                LocalDateTime.of(2026, 6, 1, 12, 0)
        );

        assertFalse(movement.overlaps(startingAtTheOtherEnd));
    }

    @Test
    void a_new_period_conflicting_with_any_existing_movement_is_detected() {
        Period conflicting = new Period(
                LocalDateTime.of(2026, 6, 1, 9, 0),
                LocalDateTime.of(2026, 6, 1, 12, 0)
        );

        assertTrue(TrainMovement.anyOverlaps(List.of(movement), conflicting));
    }

    @Test
    void a_free_period_does_not_conflict_with_existing_movements() {
        Period afternoon = new Period(
                LocalDateTime.of(2026, 6, 1, 14, 0),
                LocalDateTime.of(2026, 6, 1, 16, 0)
        );

        assertFalse(TrainMovement.anyOverlaps(List.of(movement), afternoon));
    }
}

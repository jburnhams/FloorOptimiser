package org.burnhams.flooring.floors.wallenclosed;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.burnhams.flooring.floors.wallenclosed.Direction.*;

public class DirectionTest {
    @Test
    public void testTurn() {
        assertThat(DOWN.turn(Corner.LEFT)).isEqualTo(RIGHT);
        assertThat(DOWN.turn(Corner.RIGHT)).isEqualTo(LEFT);
        assertThat(LEFT.turn(Corner.LEFT)).isEqualTo(DOWN);
        assertThat(LEFT.turn(Corner.RIGHT)).isEqualTo(UP);
        assertThat(UP.turn(Corner.LEFT)).isEqualTo(LEFT);
        assertThat(UP.turn(Corner.RIGHT)).isEqualTo(RIGHT);
        assertThat(RIGHT.turn(Corner.LEFT)).isEqualTo(UP);
        assertThat(RIGHT.turn(Corner.RIGHT)).isEqualTo(DOWN);
    }

    @Test
    public void testGetNewXY() {
        assertThat(DOWN.getNewXY(0, 0, 5)).isEqualTo(new int[]{0,5});
        assertThat(LEFT.getNewXY(0, 0, 5)).isEqualTo(new int[]{-5,0});
        assertThat(RIGHT.getNewXY(0, 0, 5)).isEqualTo(new int[]{5,0});
        assertThat(UP.getNewXY(0, 0, 5)).isEqualTo(new int[]{0,-5});
    }
}

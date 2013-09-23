package org.burnhams.flooring.floors;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.burnhams.flooring.floors.WallEnclosedFloor.*;
import static org.burnhams.flooring.floors.WallEnclosedFloor.CornerWallLength.left;
import static org.burnhams.flooring.floors.WallEnclosedFloor.CornerWallLength.right;
import static org.burnhams.flooring.floors.WallEnclosedFloor.Direction.*;

public class WallEnclosedFloorTest {
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
        assertThat(getNewXY(0, 0, DOWN, 5)).isEqualTo(new int[]{0,5});
        assertThat(getNewXY(0, 0, LEFT, 5)).isEqualTo(new int[]{-5,0});
        assertThat(getNewXY(0, 0, RIGHT, 5)).isEqualTo(new int[]{5,0});
        assertThat(getNewXY(0, 0, UP, 5)).isEqualTo(new int[]{0,-5});
    }

    @Test
    public void testShouldCalculateMaxWidthHeightOffsets1() {
        WallEnclosedFloor floor = new WallEnclosedFloor(10, right(10), right(10), right(10));
        assertThat(floor.getMaxLength()).isEqualTo(10);
        assertThat(floor.getWidth()).isEqualTo(10);
        assertThat(floor.getHorizontalLengthOffsetX()).isEqualTo(0);
        assertThat(floor.getHorizontalLengthOffsetY()).isEqualTo(0);
    }

    @Test
    public void testGetGcd() {
        assertThat(gcd(newArrayList(5,10,25,100))).isEqualTo(5);
    }

    @Test
    public void testShouldCalculateMaxWidthHeightOffsets2() {
        WallEnclosedFloor floor = new WallEnclosedFloor(10, left(10), left(10), left(10));
        assertThat(floor.getMaxLength()).isEqualTo(10);
        assertThat(floor.getWidth()).isEqualTo(10);
        //assertThat(floor.getGcd()).isEqualTo(10);
        assertThat(floor.getFloorBit(0,0)).isTrue();
        assertThat(floor.getHorizontalLengthOffsetX()).isEqualTo(0);
        assertThat(floor.getHorizontalLengthOffsetY()).isEqualTo(10);
        assertThat(floor.getFloorBitsSize()).isEqualTo(1);
    }

    @Test
    public void testShouldCalculateMaxWidthHeightOffsets3() throws IOException {
        WallEnclosedFloor floor = new WallEnclosedFloor(10, right(4), right(5), left(2), left(4), right(4), right(10), right(5), right(1), left(5));
        assertThat(floor.getMaxLength()).isEqualTo(11);
        assertThat(floor.getWidth()).isEqualTo(10);
        assertThat(floor.getHorizontalLengthOffsetX()).isEqualTo(1);
        assertThat(floor.getHorizontalLengthOffsetY()).isEqualTo(0);
        assertThat(floor.getFloorBitsSize()).isEqualTo(110);
        BufferedImage result = floor.createImage(1000);
        ImageIO.write(result, "PNG", new File("testwallenclosedfloor.png"));
    }


}

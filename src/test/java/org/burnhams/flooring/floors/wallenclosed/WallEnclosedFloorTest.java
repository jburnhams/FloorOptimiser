package org.burnhams.flooring.floors.wallenclosed;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.burnhams.flooring.floors.wallenclosed.CornerWallLength.left;
import static org.burnhams.flooring.floors.wallenclosed.CornerWallLength.right;

public class WallEnclosedFloorTest {

    @Test
    public void testShouldCalculateMaxWidthHeightOffsets1() {
        WallEnclosedFloor floor = new WallEnclosedFloor(10, right(10), right(10), right(10));
        assertThat(floor.getMaxLength()).isEqualTo(10);
        assertThat(floor.getWidth()).isEqualTo(10);
        assertThat(floor.getHorizontalLengthOffsetX()).isEqualTo(0);
        assertThat(floor.getHorizontalLengthOffsetY()).isEqualTo(0);
    }

    @Test
    public void testShouldCalculateMaxWidthHeightOffsets2() {
        WallEnclosedFloor floor = new WallEnclosedFloor(10, left(10), left(10), left(10));
        assertThat(floor.getMaxLength()).isEqualTo(10);
        assertThat(floor.getWidth()).isEqualTo(10);
        assertThat(floor.getFloorBit(0,0)).isTrue();
        assertThat(floor.getHorizontalLengthOffsetX()).isEqualTo(0);
        assertThat(floor.getHorizontalLengthOffsetY()).isEqualTo(10);
        assertThat(floor.getFloorBitsSize()).isEqualTo(100);
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
        assertThat(floor.getFloorBit(0,0)).isFalse();
        assertThat(floor.getFloorBit(10,9)).isFalse();
    }


}

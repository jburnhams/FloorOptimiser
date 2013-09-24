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
        assertThat(floor.getSegments(0,1)).isEqualTo(1);
        assertThat(floor.getSegments(0,10)).isEqualTo(1);
    }

    @Test
    public void testShouldCalculateMaxWidthHeightOffsets3() throws IOException {
        WallEnclosedFloor floor = new WallEnclosedFloor(90,
                right(10), left(10), right(30), right(50), left(20), left(40), right(40),
                right(60),right(40),left(20),left(30),right(10),right(10),left(10),
                right(30), right(10), left(50));
        assertThat(floor.getMaxLength()).isEqualTo(110);
        assertThat(floor.getWidth()).isEqualTo(100);
        assertThat(floor.getHorizontalLengthOffsetX()).isEqualTo(10);
        assertThat(floor.getHorizontalLengthOffsetY()).isEqualTo(0);
        assertThat(floor.getFloorBitsSize()).isEqualTo(11000);
        BufferedImage result = floor.createImage(1000);
        ImageIO.write(result, "PNG", new File("testwallenclosedfloor.png"));
        assertThat(floor.getFloorBit(0,0)).isFalse();
        assertThat(floor.getFloorBit(100,90)).isFalse();

        assertThat(floor.getSegments(0,10)).isEqualTo(1);
        assertThat(floor.getSegmentLength(0,10,0)).isEqualTo(90);
        assertThat(floor.getSegmentStart(0,10,0)).isEqualTo(10);

        assertThat(floor.getSegments(40,80)).isEqualTo(1);
        assertThat(floor.getSegmentLength(40,80,0)).isEqualTo(100);
        assertThat(floor.getSegmentStart(40,80,0)).isEqualTo(0);

        assertThat(floor.getSegments(60,80)).isEqualTo(2);
        assertThat(floor.getSegmentLength(60,80,0)).isEqualTo(20);
        assertThat(floor.getSegmentLength(60,80,1)).isEqualTo(60);
        assertThat(floor.getSegmentStart(60,80,0)).isEqualTo(0);
        assertThat(floor.getSegmentStart(60,80,1)).isEqualTo(40);

        assertThat(floor.getSegments(80,100)).isEqualTo(2);
        assertThat(floor.getSegmentLength(80,100,0)).isEqualTo(10);
        assertThat(floor.getSegmentLength(80,100,1)).isEqualTo(60);
        assertThat(floor.getSegmentStart(80,100,0)).isEqualTo(10);
        assertThat(floor.getSegmentStart(80,100,1)).isEqualTo(40);

        assertThat(floor.getSegments(90,100)).isEqualTo(1);
        assertThat(floor.getSegmentLength(90,100,0)).isEqualTo(60);
        assertThat(floor.getSegmentStart(90,100,0)).isEqualTo(40);
    }


}

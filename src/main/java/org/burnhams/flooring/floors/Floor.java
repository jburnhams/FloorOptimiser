package org.burnhams.flooring.floors;

import java.awt.*;

public interface Floor {

    int getWidth();

    int getMaxLength();

    int getSegments(int widthStartOffset, int widthEndOffset);

    int getSegmentStart(int widthStartOffset, int widthEndOffset, int segment);

    int getSegmentLength(int widthStartOffset, int widthEndOffset, int segment);

    void drawBoundary(Graphics graphics, double xMultiple, double yMultiple);

}

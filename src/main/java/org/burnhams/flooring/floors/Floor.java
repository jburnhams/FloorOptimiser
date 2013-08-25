package org.burnhams.flooring.floors;

import java.awt.*;

public interface Floor {

    int getWidth();

    int getMaxLength();

    int getLength(int widthOffset);

    int getLength(int widthStartOffset, int widthEndOffset);

    void drawBoundary(Graphics graphics, double xMultiple, double yMultiple);

}

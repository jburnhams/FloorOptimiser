package org.burnhams.utils;

import java.awt.*;

public class GraphicsUtils {
    public static void drawLine(Graphics graphics, double xMultiple, double yMultiple, int x1, int y1, int x2, int y2) {
        graphics.drawLine((int)Math.round(xMultiple*x1), (int)Math.round(yMultiple*y1), (int)Math.round(xMultiple*x2), (int)Math.round(yMultiple*y2));
    }
}

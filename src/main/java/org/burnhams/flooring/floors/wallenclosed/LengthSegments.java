package org.burnhams.flooring.floors.wallenclosed;

import java.util.List;

public class LengthSegments {

    private final int segments;
    private final int[] segmentStarts;
    private final int[] segmentEnds;


    public LengthSegments(List<Integer> segmentStartEnds) {
        segments = segmentStartEnds.size() / 2;
        segmentStarts = new int[segments];
        segmentEnds = new int[segments];
        for (int i = 0; i < segments; i++) {
            segmentStarts[i] = segmentStartEnds.get(i*2);
            segmentEnds[i] = segmentStartEnds.get(i*2 + 1);
        }
        if (segments == 0 || segmentStarts.length != segments || segmentEnds.length != segments) {
            throw new IllegalArgumentException("Unexpected segments: "+segmentStartEnds);
        }
    }

    public int getSegments() {
        return segments;
    }

    public int getSegmentStart(int segment) {
        return segmentStarts[segment];
    }

    public int getSegmentLength(int segment) {
        return segmentEnds[segment] - segmentStarts[segment];
    }

    public int getSegmentEnd(int segment) {
        return segmentEnds[segment];
    }
}

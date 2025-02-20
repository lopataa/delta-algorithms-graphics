package models;

import java.awt.*;

public class DashedLine extends Line {
    private final int dashLength;

    public DashedLine(Point a, Point b, int dashLength, Color color) {
        super(a, b, color);
        this.dashLength = dashLength;
    }

    public int getDashLength() {
        return dashLength;
    }
}

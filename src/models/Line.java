package models;

import rasterizers.Rasterizer;

import java.awt.*;

public class Line {
    private Point a, b;
    private Color color;

    public Line(Point a, Point b, Color color) {
        this.a = a;
        this.b = b;
        this.color = color;
    }

    public Point getA() {
        return a;
    }

    public Point getB() {
        return b;
    }

    public Color getColor() {
        return color;
    }

}

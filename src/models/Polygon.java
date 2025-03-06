package models;

import java.util.ArrayList;

public class Polygon {
    private ArrayList<Point> vertices;

    public Polygon() {
        vertices = new ArrayList<>();
    }

    public Polygon(ArrayList<Point> vertices) {
        this.vertices = vertices;
    }

    public void addPoint(Point point) {
        vertices.add(point);
    }

    public ArrayList<Point> getPoints() {
        return vertices;
    }
}

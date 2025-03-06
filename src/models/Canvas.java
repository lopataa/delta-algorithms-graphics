package models;

import java.util.ArrayList;
import java.util.List;

public class Canvas {
    private List<Line> lines;
    private List<Polygon> polygons;

    public Canvas() {
        this.polygons = new ArrayList<Polygon>();
        this.lines = new ArrayList<Line>();
    }

    public void add(Line line) {
        this.lines.add(line);
    }

    public void add(Polygon polygon) {
        this.polygons.add(polygon);
    }

    public List<Line> getLines() {
        return this.lines;
    }

    public List<Polygon> getPolygons() {
        return this.polygons;
    }

    public void clear() {
        this.lines.clear();
        this.polygons.clear();
    }
}

package models;

import java.util.ArrayList;

public class Polygon {
    private ArrayList<Line> lines;

    public Polygon() {
        lines = new ArrayList<>();
    }

    public Polygon(ArrayList<Line> lines) {
        this.lines = lines;
    }

    public void addLine(Line line) {
        lines.add(line);
    }

    public ArrayList<Line> getLines() {
        return lines;
    }
}

package models;

import java.util.ArrayList;
import java.util.List;

public class LineCanvas {
    private List<Line> lines;

    public LineCanvas() {
        this.lines = new ArrayList<Line>();
    }

    public void add(Line line) {
        this.lines.add(line);
    }

    public List<Line> getLines() {
        return this.lines;
    }

    public void clear() {
        this.lines.clear();
    }
}

package rasterizers;

import models.Line;
import models.Canvas;

import java.awt.*;

public interface Rasterizer {

    void setColor(Color color);

    default void rasterize(Line line) {}

    default void rasterize(Canvas canvas) {}

    default void rasterize(Polygon polygon) {}
}

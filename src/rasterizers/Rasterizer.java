package rasterizers;

import models.Line;
import models.LineCanvas;

import java.awt.*;

public interface Rasterizer {

    void setColor(Color color);

    void rasterize(Line line);

    void rasterize(LineCanvas canvas);
}

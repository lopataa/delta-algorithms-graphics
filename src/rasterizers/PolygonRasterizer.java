package rasterizers;

import models.Line;
import models.Canvas;
import models.Polygon;
import rasters.Raster;

import java.awt.*;

public class PolygonRasterizer implements Rasterizer {
    private Color color;
    private Raster raster;

    private LineRasterizer lineRasterizer;

    public void setColor(Color color) {
        color = color;
    }

    public PolygonRasterizer(LineRasterizer lineRasterizer, Raster raster, Color color) {
        this.color = color;
        this.raster = raster;
        this.lineRasterizer = lineRasterizer;
    }

    public void rasterize(Polygon polygon) {
        for (Line line : polygon.getLines()) {
            lineRasterizer.rasterize(line);
        }
    }

    public void rasterize(Canvas canvas) {
        for (Polygon polygon : canvas.getPolygons()) {
            rasterize(polygon);
        }
    }
}

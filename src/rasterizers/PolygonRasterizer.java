package rasterizers;

import models.Canvas;
import models.Line;
import rasters.Raster;

import java.awt.*;

public class PolygonRasterizer implements Rasterizer {

    private Color color;
    private Raster raster;
    private SimpleLineRasterizer simpleLineRasterizer;

    @Override
    public void setColor(Color color) {

    }

    public PolygonRasterizer(Raster raster, Color color) {
        this.color = color;
        this.raster = raster;

        this.simpleLineRasterizer = new SimpleLineRasterizer(raster, color);
    }

    public void rasterize(models.Polygon polygon) {
        models.Point prevPoint = null;
        for (models.Point point : polygon.getPoints()) {
            if(prevPoint == null) {
                prevPoint = point;
                continue;
            }

            // create a line and rasterize it
            simpleLineRasterizer.rasterize(new Line(prevPoint, point, Color.white));

            prevPoint = point;
        }

        simpleLineRasterizer.rasterize(new Line(prevPoint, polygon.getPoints().getFirst(), Color.white));
    }

    @Override
    public void rasterize(Canvas canvas) {
        for (Line line : canvas.getLines()) {
            rasterize(line);
        }

        raster.repaint(raster.getGraphics());
    }
}

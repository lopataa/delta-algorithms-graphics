package rasterizers;

import models.DashedLine;
import models.Line;
import models.Canvas;
import models.Polygon;
import rasters.Raster;

import java.awt.*;

public class RasterizerSelector implements Rasterizer {
    private Color color;
    private Raster raster;

    private SimpleLineRasterizer simpleLineRasterizer;
    private DashedLineRasterizer dashedLineRasterizer;
    private PolygonRasterizer polygonRasterizer;

    @Override
    public void setColor(Color color) {
        color = color;
    }

    public RasterizerSelector(Raster raster, Color color) {
        this.color = color;
        this.raster = raster;
        simpleLineRasterizer = new SimpleLineRasterizer(raster, color);
        dashedLineRasterizer = new DashedLineRasterizer(raster, color);
        polygonRasterizer = new PolygonRasterizer(raster, color);
    }

    @Override
    public void rasterize(Line line) {
        if (line instanceof DashedLine) {
            dashedLineRasterizer.rasterize((DashedLine) line);
        } else {
            simpleLineRasterizer.rasterize(line);
        }
    }

    public void rasterize(Polygon polygon) {
        polygonRasterizer.rasterize(polygon);
    }

    public void rasterize(Canvas canvas) {
        for (Line line : canvas.getLines()) {
            rasterize(line);
        }
        for (Polygon polygon : canvas.getPolygons()) {
            rasterize(polygon);
        }
    }
}

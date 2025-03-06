package rasterizers;

import models.DashedLine;
import models.Line;
import models.Canvas;
import rasters.Raster;

import java.awt.*;

public class LineRasterizer implements Rasterizer {
    private Color color;
    private Raster raster;

    private SimpleLineRasterizer simpleLineRasterizer;
    private DashedLineRasterizer dashedLineRasterizer;

    @Override
    public void setColor(Color color) {
        color = color;
    }

    public LineRasterizer(Raster raster, Color color) {
        this.color = color;
        this.raster = raster;
        simpleLineRasterizer = new SimpleLineRasterizer(raster, color);
        dashedLineRasterizer = new DashedLineRasterizer(raster, color);
    }

    @Override
    public void rasterize(Line line) {
        if (line instanceof DashedLine) {
            dashedLineRasterizer.rasterize((DashedLine) line);
        } else {
            simpleLineRasterizer.rasterize(line);
        }
    }

    public void rasterize(Canvas canvas) {
        for (Line line : canvas.getLines()) {
            rasterize(line);
        }
    }
}

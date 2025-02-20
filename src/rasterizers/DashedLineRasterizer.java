package rasterizers;

import models.DashedLine;
import models.Line;
import models.LineCanvas;
import rasters.Raster;

import java.awt.*;

public class DashedLineRasterizer implements Rasterizer {

    private Color color;
    private Raster raster;

    @Override
    public void setColor(Color color) {

    }

    public DashedLineRasterizer(Raster raster, Color color) {
        this.color = color;
        this.raster = raster;
    }

    @Override
    public void rasterize(Line line) {
        int x1 = line.getA().getX();
        int y1 = line.getA().getY();
        int x2 = line.getB().getX();
        int y2 = line.getB().getY();

        // check if it istn out of bound
        if(x1 < 0 || x1 >= raster.getWidth() || x2 < 0 || x2 >= raster.getWidth() || y1 < 0 || y1 >= raster.getHeight() || y2 < 0 || y2 >= raster.getHeight()) {
            return;
        }

        int dy = (y2-y1);
        int dx = (x2-x1);
        float k = (float) dy/dx;
        float q = y1 - (k*x1);

        int dashCounter = 0;
        int lineDashLength = ((DashedLine) line).getDashLength();
        if(Math.abs(k)<1) {
            if(x1 > x2) {
                int tmp = x1;
                x1 = x2;
                x2 = tmp;
            }
            for (int x = x1; x <= x2; x++) {
                int y = Math.round(k * x + q);

                if(dashCounter == lineDashLength) {
                    dashCounter = -lineDashLength;
                    continue;
                }
                if(dashCounter > 0) {
                    raster.setPixel(x, y, line.getColor().getRGB());
                }
                dashCounter++;
            }
        } else {
            if(y1 > y2) {
                int tmp = y1;
                y1 = y2;
                y2 = tmp;
            }
            for (int y = y1; y <= y2; y++) {
                int x = Math.round((y-q)/k);

                if(dashCounter == lineDashLength) {
                    dashCounter = -lineDashLength;
                    continue;
                }
                if(dashCounter > 0) {
                    raster.setPixel(x, y, line.getColor().getRGB());
                }
                dashCounter++;
            }
        }
    }

    @Override
    public void rasterize(LineCanvas canvas) {
        for (Line line : canvas.getLines()) {
            rasterize(line);
        }

        raster.repaint(raster.getGraphics());
    }
}

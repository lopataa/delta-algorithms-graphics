import models.DashedLine;
import models.Line;
import models.Canvas;
import rasterizers.RasterizerSelector;
import rasterizers.Rasterizer;
import rasters.Raster;
import rasters.RasterBufferedImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Serial;
import java.util.ArrayList;

public class Main {

    private final JPanel panel;
    private final Raster raster;
    private MouseAdapter mouseAdapter;
    private KeyAdapter keyAdapter;

    // currently drawn objects
    public models.Point pointA;

    public models.Point nearbyPoint;

    private final RasterizerSelector rasterizer;
    private final Canvas canvas;

    // state info
    private boolean isCtrlPressed = false;
    private boolean isShiftPressed = false;
    private models.Polygon polygonMode;
    private int dashLength = 10;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main(800, 600).start());
    }

    public void clear(int color) {
        raster.setClearColor(color);
        raster.clear();
    }

    public void present(Graphics graphics) {
        raster.repaint(graphics);
    }

    public void start() {
        clear(0xaaaaaa);
        panel.repaint();
    }

    public Main(int width, int height) {
        canvas = new Canvas();
        pointA = new models.Point(0, 0);

        JFrame frame = new JFrame();


        frame.setLayout(new BorderLayout());

        frame.setTitle("meow meow : " + this.getClass().getName());
        frame.setResizable(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        raster = new RasterBufferedImage(width, height);

        panel = new JPanel() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                present(g);
            }
        };
        this.createAdapters();
        panel.setPreferredSize(new Dimension(width, height));
        panel.addMouseMotionListener(mouseAdapter);
        panel.addMouseListener(mouseAdapter);
        panel.addMouseWheelListener(mouseAdapter);
        panel.addKeyListener(keyAdapter);

        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        panel.requestFocus();
        panel.requestFocusInWindow();

        rasterizer = new RasterizerSelector(raster, new Color(0, 0, 0));
    }


    private void createAdapters() {
        mouseAdapter = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                models.Point mousePoint = new models.Point(e.getX(), e.getY());
                // if it was the right button
                if (e.getButton() == MouseEvent.BUTTON3) {
                    for (models.Line line : canvas.getLines()) {
                        if (line.getA().getDistance(mousePoint) < 10) {
                            nearbyPoint = line.getA();
                            pointA = line.getB();
                            return;
                        }
                        if (line.getB().getDistance(mousePoint) < 10) {
                            nearbyPoint = line.getB();
                            pointA = line.getA();
                            return;
                        }
                    }
                    for (models.Polygon polygon : canvas.getPolygons()) {
                        for (models.Point point : polygon.getPoints()) {
                            if(point.getDistance(mousePoint) < 10) {
                                nearbyPoint = point;
                            }
                        }
                    }
                }

                if(polygonMode != null) {
                    polygonMode.addPoint(mousePoint);
                    raster.clear();
                    rasterizer.rasterize(polygonMode);
                    rasterizer.rasterize(canvas);
                    panel.repaint();
                    return;
                }

                pointA = mousePoint;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);

                if(e.getButton() == MouseEvent.BUTTON3) {
                    nearbyPoint = null;
                    return;
                }
                if(polygonMode != null) {
                    return;
                }

                // add current to points
                Point p = e.getPoint();

                models.Point pointB = new models.Point(p.x, p.y);

                if (isShiftPressed) {
                    pointB = LineSnapper.snap(pointA.getX(), pointA.getY(), pointB.getX(), pointB.getY());
                }

                Line line;
                if (isCtrlPressed) {
                    line = new DashedLine(pointA, pointB, dashLength, new Color(255, 255, 255));
                } else {
                    line = new Line(pointA, pointB, new Color(255, 255, 255));
                }


                canvas.add(line);


                rasterizer.rasterize(canvas);
                panel.repaint();
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                super.mouseWheelMoved(e);

                // modify the dash length
                dashLength += e.getWheelRotation();
                if (dashLength < 2) {
                    dashLength = 2;
                } else if (dashLength > 100) {
                    dashLength = 100;
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);

                Point p = e.getPoint();
                models.Point pointB = new models.Point(p.x, p.y);
                if(polygonMode != null) {
                    return;
                }

                if (e.getButton() == MouseEvent.BUTTON3) {
                    raster.clear();
                    if(isShiftPressed && pointA != null) {
                        pointB = LineSnapper.snap(pointA.getX(), pointA.getY(), pointB.getX(), pointB.getY());
                    }

                    nearbyPoint.setX(pointB.getX());
                    nearbyPoint.setY(pointB.getY());
                    rasterizer.rasterize(canvas);
                    panel.repaint();
                    return;
                }

                if (isShiftPressed) {
                    pointB = LineSnapper.snap(pointA.getX(), pointA.getY(), pointB.getX(), pointB.getY());
                }

                raster.clear();
                if (isCtrlPressed) {
                    rasterizer.rasterize(new DashedLine(pointA, pointB, dashLength, new Color(128, 128, 128)));
                } else {
                    rasterizer.rasterize(new Line(pointA, pointB, new Color(128, 128, 128)));
                }
                rasterizer.rasterize(canvas);
                panel.repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);

                models.Point point = new models.Point(e.getX(), e.getY());
                if(polygonMode != null) {
                    models.Polygon polygon = new models.Polygon(new ArrayList(polygonMode.getPoints()));
                    polygon.addPoint(point);
                    raster.clear();
                    rasterizer.rasterize(polygon);
                    rasterizer.rasterize(canvas);
                    panel.repaint();
                    return;
                }
            }
        };

        keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);

                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    isCtrlPressed = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    isShiftPressed = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    isCtrlPressed = false;
                }

                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    isShiftPressed = false;
                }

                if (e.getKeyChar() == 'c') {
                    canvas.clear();
                    raster.clear();
                    panel.repaint();
                }

                if(e.getKeyChar() == 'p') {
                    if(polygonMode != null) {
                        canvas.add(polygonMode);
                        polygonMode = null;
                        return;
                    }
                    polygonMode = new models.Polygon();
                }

                super.keyReleased(e);
            }
        };
    }

}
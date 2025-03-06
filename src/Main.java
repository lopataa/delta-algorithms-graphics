import models.DashedLine;
import models.Line;
import models.Canvas;
import rasterizers.LineRasterizer;
import rasterizers.PolygonRasterizer;
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
    private ArrayList<models.Point> points; // for polygons

    private final Rasterizer rasterizer;
    private final Rasterizer polygonRasterizer;
    private final Canvas canvas;

    // state info
    private boolean isCtrlPressed = false;
    private boolean isShiftPressed = false;
    private boolean isPolygonModeEnabled = false;
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
        points = new ArrayList<>();

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

        rasterizer = new LineRasterizer(raster, new Color(0, 0, 0));
        polygonRasterizer = new PolygonRasterizer((LineRasterizer) rasterizer, raster, new Color(0, 0, 0));
    }


    private void createAdapters() {
        mouseAdapter = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                Point p = e.getPoint();

                points.add(new models.Point(p.x, p.y));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);

                // add current to points
                Point p = e.getPoint();
                points.add(new models.Point(p.x, p.y));

                // print all points in da buffer
                for (models.Point pt : points) {
                    System.out.println("X: " + pt.getX() + " Y: " + pt.getY());
                }
                System.out.println();

                if(!isPolygonModeEnabled) {
                    models.Point pointA = points.getFirst();
                    models.Point pointB = points.getLast();
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
                    points.clear();

                    rasterizer.rasterize(canvas);
                    panel.repaint();
                }
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
                models.Point pointA = points.getFirst();
                models.Point pointB = new models.Point(p.x, p.y);
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

                if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    isShiftPressed = false;
                }

                if (e.getKeyChar() == 'c') {
                    canvas.clear();
                    raster.clear();
                    panel.repaint();
                }

                if (e.getKeyChar() == 'p') {
                    // if were quitting the polygon mode, render the polygon and save it
                    if(isPolygonModeEnabled) {
                        System.out.println("Polygon mode is quitting...");
                        // go through all the points
                        models.Point prev = null;
                        ArrayList<Line> lines = new ArrayList<Line>();
                        for (models.Point p : points) {
                            if (prev != null) {
                                System.out.println("X: " + prev.getX() + " Y: " + prev.getY() + " X2:" + p.getX() + " Y2:" + p.getY());
                                lines.add(new Line(p, prev, new Color(255, 255, 255)));
                            }
                            prev = p;
                        }

                        canvas.add(new models.Polygon(lines));
                    }
                    isPolygonModeEnabled = !isPolygonModeEnabled;
                    System.out.println("Polygon mode is rasterizing...");
                    polygonRasterizer.rasterize(canvas);
                }

                super.keyReleased(e);
            }
        };
    }

}
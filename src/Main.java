import models.DashedLine;
import models.Line;
import models.LineCanvas;
import rasterizers.DashedLineRasterizer;
import rasterizers.LineRasterizer;
import rasterizers.Rasterizer;
import rasterizers.SimpleLineRasterizer;
import rasters.Raster;
import rasters.RasterBufferedImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Console;
import java.io.Serial;

public class Main {

    private final JPanel panel;
    private final Raster raster;
    private MouseAdapter mouseAdapter;
    private KeyAdapter keyAdapter;
    private models.Point pointA;
    private final Rasterizer rasterizer;
    private final LineCanvas lineCanvas;

    private boolean isCtrlPressed = false;
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
        lineCanvas = new LineCanvas();

        JFrame frame = new JFrame();


        frame.setLayout(new BorderLayout());

        frame.setTitle("UHK FIM PGRF : " + this.getClass().getName());
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
    }


    private void createAdapters() {
        mouseAdapter = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                Point p = e.getPoint();

                pointA = new models.Point(p.x, p.y);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                Point p = e.getPoint();

                models.Point pointB = new models.Point(p.x, p.y);

                Line line;
                if (isCtrlPressed) {
                    line = new DashedLine(pointA, pointB, dashLength, new Color(255, 255, 255));
                } else {
                    line = new Line(pointA, pointB, new Color(255, 255, 255));
                }

                lineCanvas.add(line);

                rasterizer.rasterize(lineCanvas);
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

                raster.clear();
                if (isCtrlPressed) {
                    rasterizer.rasterize(new DashedLine(pointA, pointB, dashLength, new Color(128, 128, 128)));
                } else {
                    rasterizer.rasterize(new Line(pointA, pointB, new Color(128, 128, 128)));
                }
                rasterizer.rasterize(lineCanvas);
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
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    isCtrlPressed = false;
                }

                if (e.getKeyChar() == 'c') {
                    lineCanvas.clear();
                    raster.clear();
                    panel.repaint();
                }

                super.keyReleased(e);
            }
        };
    }

}
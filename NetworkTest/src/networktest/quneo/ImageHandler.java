package networktest.quneo;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageHandler {

    private static final Color orange = new Color(255, 127, 39);
    private static final Color red = new Color(255, 0, 0);
    private static final Color green = new Color(0, 255, 0);

    public static void displayImage(Pad[][] pads, String fileName) {
        try {
            BufferedImage i = ImageIO.read(ImageHandler.class.getResource(fileName));

            for (int x = 0; x < 4; x++) {
                for (int y = 0; y < 4; y++) {
                    Pad p = pads[x][y];
                    QuNeoColor topL = null, topR = null, botL = null, botR = null;
                    Point[] points = new Point[]{
                        new Point(x * 2, y * 2),
                    new Point((x * 2) + 1, y * 2),
                    new Point(x * 2, (y * 2) + 1),
                    new Point((x * 2) + 1, (y * 2) + 1)};
                    int currPoint = 0;
                    for (Point point : points) {
                        QuNeoColor thisColor = null;
                        Color c = getColor(i, point.x, point.y);
                        if (c.getRGB() == orange.getRGB()) {
                           thisColor = QuNeoColor.ORANGE;
                            System.out.println("no kill p");
                        } else if (c.getRGB() == green.getRGB()) {
                            thisColor = QuNeoColor.GREEN;
                        } else if (c.getRGB() == red.getRGB()) {
                            thisColor = QuNeoColor.RED;
                        } else {
                           thisColor = QuNeoColor.NONE;
                        }
                        switch (currPoint) {
                            case 0:
                                topL = thisColor;
                                break;
                            case 1:
                                topR = thisColor;
                                break;
                            case 2:
                                botL = thisColor;
                                break;
                            case 3:
                                botR = thisColor;
                                break;
                        }
                        currPoint++;
                    }
                    p.setGridColor(topL, topR, botL, botR, 127, 127, 127, 127);
                }
            }
            for (int x = 0; x < 9; x++) {
                for (int y = 0; y < 9; y++) {
                    Pad p = pads[x][y];
                    Color c = getColor(i, x, y);
                    if (c.getRGB() == orange.getRGB()) {
                        p.setColor(QuNeoColor.ORANGE, 127);
                        System.out.println("no kill p");
                    } else if (c.getRGB() == green.getRGB()) {
                        p.setColor(QuNeoColor.GREEN, 127);
                    } else if (c.getRGB() == red.getRGB()) {
                        p.setColor(QuNeoColor.RED, 127);
                    } else {
                        System.out.println("killed p");
                        p.setColorOff();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Color getColor(BufferedImage i, int x, int y) {
        return new Color(i.getRGB(x, y));
    }
}

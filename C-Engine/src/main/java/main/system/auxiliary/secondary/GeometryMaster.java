package main.system.auxiliary.secondary;

import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.DIRECTION;

import java.awt.*;

public class GeometryMaster {

    public static Point getFarthestPointInRectangleForImage(int w, int h, Image img,
                                                            DIRECTION direction) {
        return getFarthestPointInRectangleForImage(w, h, img.getWidth(null), img.getHeight(null),
                direction);
    }

    public static Point getFarthestPointInRectangleForImage(int w, int h, int width, int height,
                                                            DIRECTION direction) {
        Boolean xSide = direction.isGrowX();
        Boolean ySide = direction.isGrowY();
        int x = (w - width) / 2;
        int y = (h - height) / 2;
        if (xSide != null)
            if (xSide)
                x = (w - width);
            else
                x = 0;
        if (ySide != null)
            if (ySide)
                y = (h - height);
            else
                y = 0;
        return new Point(x, y);
    }

    public static double getAngle(Coordinates p1, Coordinates p2) {
//        double n1 = sqrt(x1*x1+y1*y1), n2 = sqrt(x2*x2+y2*y2);
//        double angle = acos((x1*x2+y1*y2)/(n1*n2)) * 180 / PI;

        double xDiff = p2.x - p1.x;
        double yDiff = p2.y - p1.y;
        return (Math.toDegrees(Math.atan2(yDiff, xDiff)));


    }
}

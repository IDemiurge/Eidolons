package main.system.auxiliary.secondary;

import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class GeometryMaster {

    public static Point getFarthestPointInRectangleForImage(int w, int h, Image img,
                                                            DIRECTION direction) {
        return getFarthestPointInRectangleForImage(w, h, img.getWidth(null), img.getHeight(null),
         direction);
    }

    public static Point getFarthestPointInRectangleForImage(int w, int h, int width, int height,
                                                            DIRECTION direction) {
        Boolean xSide = direction.growX;
        Boolean ySide = direction.growY;
        int x = (w - width) / 2;
        int y = (h - height) / 2;
        if (xSide != null) {
            if (xSide) {
                x = (w - width);
            } else {
                x = 0;
            }
        }
        if (ySide != null) {
            if (ySide) {
                y = (h - height);
            } else {
                y = 0;
            }
        }
        return new Point(x, y);
    }

    public static double getAngle(Coordinates p1, Coordinates p2) {
//        double n1 = sqrt(x1*x1+y1*y1), n2 = sqrt(x2*x2+y2*y2);
//        double angle = acos((x1*x2+y1*y2)/(n1*n2)) * 180 / PI;

        double xDiff = p2.x - p1.x;
        double yDiff = p2.y - p1.y;
        return (Math.toDegrees(Math.atan2(yDiff, xDiff)));


    }


    public static Point2D[] getIntersectionPoint(Line2D line, Rectangle2D rectangle) {

        Point2D[] p = new Point2D[4];

        // Top line
        p[0] = getIntersectionPoint(line,
         new Line2D.Double(
          rectangle.getX(),
          rectangle.getY(),
          rectangle.getX() + rectangle.getWidth(),
          rectangle.getY()));
        // Bottom line
        p[1] = getIntersectionPoint(line,
         new Line2D.Double(
          rectangle.getX(),
          rectangle.getY() + rectangle.getHeight(),
          rectangle.getX() + rectangle.getWidth(),
          rectangle.getY() + rectangle.getHeight()));
        // Left side...
        p[2] = getIntersectionPoint(line,
         new Line2D.Double(
          rectangle.getX(),
          rectangle.getY(),
          rectangle.getX(),
          rectangle.getY() + rectangle.getHeight()));
        // Right side
        p[3] = getIntersectionPoint(line,
         new Line2D.Double(
          rectangle.getX() + rectangle.getWidth(),
          rectangle.getY(),
          rectangle.getX() + rectangle.getWidth(),
          rectangle.getY() + rectangle.getHeight()));

        return p;

    }

    public static Point2D getIntersectionPoint(Line2D lineA, Line2D lineB) {

        double x1 = lineA.getX1();
        double y1 = lineA.getY1();
        double x2 = lineA.getX2();
        double y2 = lineA.getY2();

        double x3 = lineB.getX1();
        double y3 = lineB.getY1();
        double x4 = lineB.getX2();
        double y4 = lineB.getY2();

        Point2D p = null;

        double d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (d != 0) {
            double xi = ((x3 - x4) * (x1 * y2 - y1 * x2) - (x1 - x2) * (x3 * y4 - y3 * x4)) / d;
            double yi = ((y3 - y4) * (x1 * y2 - y1 * x2) - (y1 - y2) * (x3 * y4 - y3 * x4)) / d;

            p = new Point2D.Double(xi, yi);

        }
        return p;
    }

    public static float hyp(float a, float b) {
        return (float) Math.sqrt(a * a + b * b);
    }
}

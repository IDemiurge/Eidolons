package main.system.auxiliary.secondary;

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

}

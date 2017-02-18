package main.swing;

import java.awt.*;

public class PointX extends Point {
    public PointX(int x, int y) {
        super(x, y);
    }

    public PointX(Point p, int x, int y) {
        this(p.x + x, p.y + y);
    }

    public PointX(float x, float y) {
        this((int)x , (int)y);
    }

    public String toString() {
        return x + "-" + y;
    }
}

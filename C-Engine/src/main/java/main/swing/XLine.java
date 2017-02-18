package main.swing;

import main.game.battlefield.Coordinates;

import java.awt.*;

public class XLine {
    public Point p1;
    public Point p2;

    public XLine(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public XLine(Coordinates coordinates, Coordinates coordinates2) {
        this(new Point(coordinates.x, coordinates.y), new Point(coordinates2.x, coordinates2.y));
    }

    public Point getP1() {
        return p1;
    }

    public void setP1(Point p1) {
        this.p1 = p1;
    }

    public Point getP2() {
        return p2;
    }

    public void setP2(Point p2) {
        this.p2 = p2;
    }

    public int getX1() {
        return getP1().x;
    }

    public int getY1() {
        return getP1().y;
    }

    public int getX2() {
        return getP2().x;
    }

    public int getY2() {
        return getP2().y;
    }
}

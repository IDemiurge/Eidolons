package eidolons.system.graphics;

import eidolons.system.graphics.AnimationManager.MOUSE_ITEM;
import eidolons.system.graphics.AnimationManager.MouseItem;

import java.awt.*;

public class MouseItemImpl implements MouseItem {
    private MOUSE_ITEM type;
    private Object arg;
    private Point point;
    private Rectangle rectangle;

    public MouseItemImpl(MOUSE_ITEM type, Object arg) {
        this.type = type;
        this.arg = arg;
    }

    @Override
    public Object getArg() {
        return arg;
    }

    @Override
    public MOUSE_ITEM getType() {
        return type;
    }

    @Override
    public Point getPoint() {
        return point;
    }

    @Override
    public void setPoint(Point point) {
        this.point = point;
    }

    @Override
    public Rectangle getRectangle() {
        return rectangle;
    }

    @Override
    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;

    }

}

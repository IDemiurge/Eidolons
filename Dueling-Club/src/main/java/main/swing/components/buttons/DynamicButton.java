package main.swing.components.buttons;

import main.swing.components.obj.drawing.GuiMaster;
import main.swing.components.obj.drawing.GuiMaster.DYNAMIC_BUTTON;

import java.awt.*;
import java.awt.event.MouseEvent;

public class DynamicButton {

    private Rectangle rectangle;
    private DYNAMIC_BUTTON type;
    private Object arg;
    private GuiMaster master;

    public DynamicButton(GuiMaster master, Rectangle rectangle, DYNAMIC_BUTTON type, Object arg) {
        this.rectangle = rectangle;
        this.type = type;
        this.arg = arg;
        this.master = master;
    }

    public void clicked(MouseEvent e) {
        master.buttonClicked(e, type, arg);
    }

    public Rectangle getRect() {
        return rectangle;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public DYNAMIC_BUTTON getType() {
        return type;
    }

    public void setType(DYNAMIC_BUTTON type) {
        this.type = type;
    }

    public Object getArg() {
        return arg;
    }

    public void setArg(Object arg) {
        this.arg = arg;
    }

}

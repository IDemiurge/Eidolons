package main.swing.generic.services.layout;

import main.system.graphics.GuiManager;

public class LayoutInfo {

    private String mig;
    private int x;
    private int y;

    public LayoutInfo(String string) {
        mig = string;
    }

    public LayoutInfo(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String getMiGString() {

        return mig;
    }

    public String getMigForBF() {
        return getMigForBF(false);
    }

    public String getMigForBF(boolean overlaying) {
        int X = GuiManager.getCellWidth() * x;
        int Y = GuiManager.getCellHeight() * y;
        if (overlaying) {
            X += (GuiManager.getCellWidth() - GuiManager.getSmallObjSize()) / 2;
            Y += (GuiManager.getCellHeight() - GuiManager.getSmallObjSize()) / 2;
        }
        return "x " + X + ", y " + Y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

}

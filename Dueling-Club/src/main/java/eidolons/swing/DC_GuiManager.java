package eidolons.swing;

import main.system.graphics.GuiManager;

import java.awt.*;

public class DC_GuiManager {
    static Dimension objSize;
    static Dimension cellSize;
    private static Dimension bigObjSize;

    public static void init() {
        // DC_ObjComponent.setCellSize(new Dimension(GuiManager.getCellSize(),
        // GuiManager.getCellSize()));
        // DC_ObjComponent.setObjSize(new Dimension(GuiManager.getObjSize(),
        // GuiManager.getObjSize()));
        setBigObjSize(new Dimension(GuiManager.getCellHeight(),
         GuiManager.getCellHeight()));
        cellSize = new Dimension(GuiManager.getCellWidth(),
         GuiManager.getCellHeight());
        objSize = new Dimension(GuiManager.getObjSize(),
         GuiManager.getObjSize());
    }

    public static Dimension getObjSize() {
        return objSize;
    }

    public static Dimension getCellSize() {
        return cellSize;
    }

    public static Dimension getBigObjSize() {
        return bigObjSize;
    }

    public static void setBigObjSize(Dimension bigObjSize) {
        DC_GuiManager.bigObjSize = bigObjSize;
    }
}

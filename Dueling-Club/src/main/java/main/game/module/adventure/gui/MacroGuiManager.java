package main.game.module.adventure.gui;

public class MacroGuiManager {
    public static final int PARTY_MAP_DISPLACEMENT_X = -12;
    public static final int PARTY_MAP_DISPLACEMENT_Y = -12;

    public static int getMapWidth() {
        return 1000;
    }

    public static double getMapHeight() {
        return 800;
    }

    public static int getMapObjSize() {
        return 96;
    }

    public static int getRouteOffset() {
        return 12;
    }

    public static int getMapOffsetX() {
        return 0;
//        return MacroManager.getMapView().getMapOffsetX();
    }

    public static int getMapOffsetY() {
//        return MacroManager.getMapView().getMapOffsetY();
        return 0;
    }

}

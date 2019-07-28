package eidolons.game.battlecraft.logic.dungeon.module;

import eidolons.libgdx.particles.ambi.AmbienceDataSource;
import main.game.bf.Coordinates;

public class Module {
    private int x;
    private int y;
    private AmbienceDataSource.AMBIENCE_TEMPLATE vfx;
    private Coordinates origin;
    private int width;
    private int height;

    private String name;

    public Module(Coordinates origin, int width, int height, String name) {
        this.origin = origin;
        this.width = width;
        this.height = height;
        this.name = name;
        x = origin.x;
        y = origin.y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public AmbienceDataSource.AMBIENCE_TEMPLATE getVfx() {
        return vfx;
    }

    public Coordinates getOrigin() {
        return origin;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getName() {
        return name;
    }

    public enum MODULE_VALUE{
        //like zone?
        ambience,
        vfx,

    }

}

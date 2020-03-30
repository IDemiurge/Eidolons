package eidolons.game.battlecraft.logic.dungeon.module;

import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.libgdx.particles.ambi.AmbienceDataSource;
import main.game.bf.Coordinates;

import java.util.List;

public class Module {
    private AmbienceDataSource.AMBIENCE_TEMPLATE vfx;
    private Coordinates origin;
    private int width;
    private int height;

    private String name;
//    private final String path;
    private List<LevelZone> zones;

    public Module(Coordinates origin, int width, int height, String name ) {
        this.origin = origin;
        this.width = width;
        this.height = height;
        this.name = name;
    }

    public Module() {

    }


    public int getX() {
        return origin.x;
    }

    public int getY() {
        return origin.y;
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

    public int getX2() {
        return getX()+getWidth();
    }

    public int getY2() {
        return getY()+getHeight();
    }

    public void toggleCoordinate(Coordinates c) {
    }

    public List<LevelZone> getZones() {
        return zones;
    }

    public void setZones(List<LevelZone> zones) {
        this.zones = zones;
    }

    public void setVfx(AmbienceDataSource.AMBIENCE_TEMPLATE vfx) {
        this.vfx = vfx;
    }

    public void setOrigin(Coordinates origin) {
        this.origin = origin;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Module: " +
                "name=" + name +
                "origin=" + origin +
                ", width=" + width +
                ", height=" + height +
                ", zones=" + zones;
    }
}

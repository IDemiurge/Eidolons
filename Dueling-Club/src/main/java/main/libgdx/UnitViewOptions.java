package main.libgdx;

import com.badlogic.gdx.graphics.Texture;

public class UnitViewOptions {
    private Runnable runnable;
    private Texture portrateTexture;
    private Texture directionPointerTexture;
    private Texture iconTexture;
    private Texture clockTexture;
    private String clockValue;
    private int directionValue;


    public Runnable getRunnable() {
        return runnable;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public Texture getPortrateTexture() {
        return portrateTexture;
    }

    public void setPortrateTexture(Texture portrateTexture) {
        this.portrateTexture = portrateTexture;
    }

    public Texture getDirectionPointerTexture() {
        return directionPointerTexture;
    }

    public void setDirectionPointerTexture(Texture directionPointerTexture) {
        this.directionPointerTexture = directionPointerTexture;
    }

    public Texture getIconTexture() {
        return iconTexture;
    }

    public void setIconTexture(Texture iconTexture) {
        this.iconTexture = iconTexture;
    }

    public Texture getClockTexture() {
        return clockTexture;
    }

    public void setClockTexture(Texture clockTexture) {
        this.clockTexture = clockTexture;
    }

    public String getClockValue() {
        return clockValue;
    }

    public void setClockValue(String clockValue) {
        this.clockValue = clockValue;
    }

    public int getDirectionValue() {
        return directionValue;
    }

    public void setDirectionValue(int directionValue) {
        this.directionValue = directionValue;
    }
}

package eidolons.libgdx.gui.generic;

import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.generic.GearCluster.GEAR;

/**
 * Created by JustMe on 3/28/2018.
 * <p>
 * speed should be inverse of the size... 2pi *r
 */
public class GearActor extends ImageContainer {
    static boolean paused;
    GEAR gear;
    float speed;
    boolean clockwise;
    private Float degreesPerSecond;

    public GearActor(GEAR gear, boolean small, float speed, boolean clockwise) {
        super(gear.getImagePath(small));
        this.gear = gear;
        speed = (float) (speed * Math.PI * 2 / getHeight());
        this.speed = speed;
        this.clockwise = clockwise;
        setOrigin(getWidth() / 2, getHeight() / 2);
    }

    public static boolean isPaused() {
        return paused;
    }

    public static void setPaused(boolean paused) {
        GearActor.paused = paused;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (isPaused())
            return;
        setRotation(getRotation() + getDegreesPerSecond() * delta);
    }


    private float getDegreesPerSecond() {
        if (degreesPerSecond == null) {
        degreesPerSecond = (clockwise ? -speed : speed) * (gear.getSpeedBasis() / getWidth() * 10);
        }
        return degreesPerSecond;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public boolean isClockwise() {
        return clockwise;
    }

    public void setClockwise(boolean clockwise) {
        this.clockwise = clockwise;
        degreesPerSecond=null;
    }
}

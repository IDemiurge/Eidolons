package main.libgdx.gui.panels.dc.clock;

import main.libgdx.bf.generic.ImageContainer;
import main.libgdx.gui.panels.dc.clock.GearCluster.GEAR;

/**
 * Created by JustMe on 3/28/2018.
 * <p>
 * speed should be inverse of the size... 2pi *r
 */
public class GearActor extends ImageContainer {
    GEAR gear;
    float speed;
    boolean clockwise;

    public GearActor(GEAR gear, float scale, float speed, boolean clockwise) {
        super(gear.getImagePath());
        this.gear = gear;
        setScale(scale);
        speed = (float) (speed*Math.PI*2/getHeight());
        this.speed = speed;
        this.clockwise = clockwise;
        setOrigin(getWidth() / 2, getHeight() / 2);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        setRotation(getRotation() +getDegreesPerSecond()*delta);
    }

    private float getDegreesPerSecond() {
        return (clockwise ? -speed : speed)*gear.getSpeedBasis() / getWidth()*10;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}

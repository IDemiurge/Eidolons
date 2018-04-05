package eidolons.libgdx.gui.panels.dc.clock;

import com.badlogic.gdx.scenes.scene2d.Action;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.anims.actions.AutoFloatAction;
import eidolons.libgdx.gui.panels.GroupX;
import main.data.filesys.PathFinder;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by JustMe on 3/28/2018.
 */
public class GearCluster extends GroupX {
    public static final int DEFAULT_SIZE = 100;
    private final ArrayList<GEAR> gearPool;
    List<GearActor> gears = new ArrayList<>();
    float defaultSpeed = 1;
    Float speed = defaultSpeed;
    float scale = 1;
    AutoFloatAction speedAction;
    private boolean clockwise;

    public GearCluster(int gearCount, float scale) {
        gearPool = new ArrayList<>(Arrays.asList(GEAR.values()));
        Collections.shuffle(gearPool);
        while (gearPool.size() < gearCount) {
            gearPool.add(new EnumMaster<GEAR>().getRandomEnumConst(GEAR.class));
        }
        this.scale = scale;
        for (int i = 0; i < gearCount; i++) {
            addGear(i);
        }

    }

    public GearCluster(float scale) {
        this(3, scale);
    }

    private void addGear(int i) {
        float scale = this.scale * (1 - i * 0.2f);
        GEAR gear = gearPool.remove(0);
        GearActor gearActor = new GearActor(gear, scale, speed, RandomWizard.random());
        addActor(gearActor);
        float y = getGearY(i, scale);
        float x = getGearX(i, scale);
        gearActor.setPosition(x, y);
        gears.add(gearActor);
    }

    private float getGearY(int i, float scale) {
        switch (i % 3) {
            case 0:
                return DEFAULT_SIZE * 0.2f * scale;
            case 1:
                return DEFAULT_SIZE * 0.1f * scale;
            case 2:
                return DEFAULT_SIZE * 0.5f * scale;
        }
        return 0;
    }

    private float getGearX(int i, float scale) {
        switch (i % 3) {
            case 0:
                return DEFAULT_SIZE * 0.1f * scale;
            case 1:
                return DEFAULT_SIZE * 0.3f * scale;
            case 2:
                return DEFAULT_SIZE * 0.4f * scale;
        }
        return 0;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (speedAction != null) {
            setSpeed(speedAction.getValue());
        }
        for (GearActor sub : gears) {
            if (clockwise) {
                sub.setSpeed(-speed);
            } else {
                sub.setSpeed(speed);
            }
        }
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void applySpeedChange(float dur, float forTime, float to) {
        clearActions();
        Float cached = speed;
        speedAction = ActorMaster.addFloatAction(this, speed, speed, to, dur);
        if (forTime > 0) {
            ActorMaster.addDelayedAction(this, forTime + dur, new Action() {
                @Override
                public boolean act(float delta) {
                    ActorMaster.addFloatAction(GearCluster.this, speed, speed, cached, dur);
                    return true;
                }
            });
        }
    }

    public void activeWork(float dur, float forTime) {
        applySpeedChange(dur, forTime, getActiveWorkSpeed());

    }

    public void work(float dur, float forTime) {
        applySpeedChange(dur, forTime, defaultSpeed);
    }

    public void stop(float dur, float forTime) {
        applySpeedChange(dur, forTime, 0);
    }

    public void activeWork(float dur) {
        applySpeedChange(dur, 0, getActiveWorkSpeed());

    }

    private float getActiveWorkSpeed() {
        return defaultSpeed * 10;
    }

    public void work(float dur) {
        applySpeedChange(dur, 0, defaultSpeed);
    }

    public void stop(float dur) {
        applySpeedChange(dur, 0, 0);
    }

    public void activeWork() {
        activeWork(0.5f);
    }

    public void work() {
        work(0.5f);
    }

    public void stop() {
        stop(0.5f);
    }

    public boolean isClockwise() {
        return clockwise;
    }

    public void setClockwise(boolean clockwise) {
        this.clockwise = clockwise;
    }

    public void setDefaultSpeed(float defaultSpeed) {
        this.defaultSpeed = defaultSpeed;
    }

    public enum GEAR {
        GEAR_1, GEAR_2(125), GEAR_3(85);

        private float speedBasis = 100;

        GEAR() {
        }

        GEAR(float speedBasis) {
            this.speedBasis = speedBasis;
        }

        public String getImagePath() {
            return StrPathBuilder.build(PathFinder.getComponentsPath(),
             "2018", "clock", StringMaster.getWellFormattedString(toString()) + ".png");
        }

        public float getSpeedBasis() {
            return speedBasis;
        }

    }
}

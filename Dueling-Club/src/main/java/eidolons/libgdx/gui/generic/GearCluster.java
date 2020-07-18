package eidolons.libgdx.gui.generic;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import eidolons.libgdx.anims.actions.ActionMaster;
import eidolons.libgdx.anims.actions.AutoFloatAction;
import main.data.filesys.PathFinder;
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
    static GEAR[] base_gears = {
            GEAR.GEAR_1,
            GEAR.GEAR_2,
            GEAR.GEAR_3,
    };
    static GEAR[] dark_gears = {
            GEAR.GEAR_1,
            GEAR.GEAR_6,
            GEAR.GEAR_7,
    };
    static GEAR[] light_gears = {
            GEAR.GEAR_2,
            GEAR.GEAR_3,
            GEAR.GEAR_4,
            GEAR.GEAR_5,
    };
    private final boolean allSmall;

    public GearCluster(int gearCount, float scale, Boolean dark_light_both) {
        this(false, gearCount, scale, dark_light_both);
    }

    public GearCluster(boolean allSmall, int gearCount, float scale, Boolean dark_light_both) {
        this.allSmall = allSmall;
        gearPool = new ArrayList<>(Arrays.asList(base_gears));
        //        if (dark_light_both == null) {
        //            gearPool = new ArrayList<>(Arrays.asList(GEAR.values()));
        //        } else {
        //            gearPool = new ArrayList<>(Arrays.asList(dark_light_both? dark_gears : light_gears));
        //        }
        Collections.shuffle(gearPool);
        while (gearPool.size() < gearCount) {
            gearPool.add(new RandomWizard<GEAR>().getRandomListItem(gearPool));
        }
        this.scale = scale;
        for (int i = 0; i < gearCount; i++) {
            addGear(i);
        }
        pack();
    }

    public GearCluster(float scale) {
        this(3, scale, null);
    }

    public void reverse() {
        for (GearActor gear : gears) {
            gear.setClockwise(!gear.isClockwise());
        }
    }

    private void addGear(int i) {
        boolean small = allSmall || i % 2 == 0;
        GEAR gear = gearPool.remove(0);
        GearActor gearActor = new GearActor(gear, small, speed, RandomWizard.random());
        float scale = small ? 0.6f : 1;
        //        this.scale * (1 - i * 0.2f);
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
        speedAction = ActionMaster.addFloatAction(this, speed, speed, to, dur);
        speedAction.setInterpolation(Interpolation.bounce);
        if (forTime > 0) {
            ActionMaster.addDelayedAction(this, forTime + dur, new Action() {
                @Override
                public boolean act(float delta) {
                    ActionMaster.addFloatAction(GearCluster.this, speed, speed, cached, dur);
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

    public void toggle() {
        if (speed > 0) {
            stop();
        } else {
            work();
        }
    }

    public enum GEAR {
        GEAR_1, GEAR_2(75), GEAR_3(85), GEAR_4(85), GEAR_5(125), GEAR_6(85), GEAR_7(125);

        private float speedBasis = 100;

        GEAR() {
        }

        GEAR(float speedBasis) {
            this.speedBasis = speedBasis;
        }

        public String getImagePath(boolean small) {
            String path = StrPathBuilder.build(PathFinder.getComponentsPath(),
                    "dc", "clock", StringMaster.format(toString()) + ".png");
            if (small) {
                return StringMaster.getAppendedFile(path, " small");
            }
            return path;
        }

        public float getSpeedBasis() {
            return speedBasis;
        }

    }
}

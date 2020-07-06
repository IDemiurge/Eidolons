package eidolons.game.module.cinematic.flight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.screens.CustomSpriteBatch;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.datatypes.WeightMap;

import java.util.LinkedHashSet;
import java.util.Set;

public class FlightHandler extends GroupX {
    public static final boolean TEST = false;
    private boolean on;

    public void test() {
        startFlight(TEST_DATA, true);
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public enum FLIGHT_ENVIRON {
        astral("objs_over:mist(20),comet_bright(10),comet_pale(20),mist(10),;" +
                "objs_under:stars(15);" +
                "angle:30;"),
        voidmaze("objs_over:comet_bright(10),comet_pale(10),;" +
                "objs_under:cloud(980),thunder(12),thunder3(5);hue:cloud;" +
                "angle:30;"),
        ;
        public String data;

        FLIGHT_ENVIRON(String data) {
            this.data = data;
        }
    }

    public static final String TEST_DATA =
            "objs_over:cinders(10),cloud(20),wraith(30),;" +
                    "objs_under:mist(20),stars(5),isle(10),cloud(10),;" +
                    "angle:30;";
    Set<FlyingObjs> objs = new LinkedHashSet<>();
    private FlightData data;
    private final GroupX objsUnder = new GroupX();
    private final GroupX objsOver = new GroupX();
    private final GroupX objsVfx = new GroupX() {
        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
            ((CustomSpriteBatch) batch).resetBlending();
        }
    };

    public FlightHandler() {
        GuiEventManager.bind(GuiEventType.FLIGHT_START, p -> startFlight(p.get().toString(), true));
        GuiEventManager.bind(GuiEventType.FLIGHT_END, p -> endFlight(1f));
    }
    public GroupX getObjsOver() {
        return objsOver;
    }

    public GroupX getObjsUnder() {
        return objsUnder;
    }

    public GroupX getObjsVfx() {
        return objsVfx;
    }

    @Override
    public void act(float delta) {
        objsOver.act(delta);
        objsUnder.act(delta);
    }

    public void endFlight(float maxDelay) {
        for (FlyingObjs obj : objs) {
            obj.stop(maxDelay);
        }
        on=false;
    }

    public void startFlight(String s, boolean cinematic) {
        this.data = new FlightData(s);
        CinematicPlatform platform = new CinematicPlatform(data.getFloatValue(FlightData.FLIGHT_VALUE.angle));

        String value = data.getValue(FlightData.FLIGHT_VALUE.objs_under);
        initObjsMap(platform, value, true, cinematic, data.getHue());
        value = data.getValue(FlightData.FLIGHT_VALUE.objs_over);
        initObjsMap(platform, value, false, cinematic, data.getHue());

        if (cinematic) {
            act(10f);
            /*
shakes,
             */
            // String value = data.getValue(FlightData.FLIGHT_VALUE.camera_shake);
            // value = data.getValue(FlightData.FLIGHT_VALUE.soundscape);
            // value = data.getValue(FlightData.FLIGHT_VALUE.camera_displace);
            // value = data.getValue(FlightData.FLIGHT_VALUE.trail);
        }
        on=true;
        //transit between flights?
    }

    private void initObjsMap(CinematicPlatform platform, String data, boolean under, boolean cinematic, Color hue) {
        WeightMap<FlyingObjs.FLY_OBJ_TYPE> map = new WeightMap<>(data, FlyingObjs.FLY_OBJ_TYPE.class);

        for (FlyingObjs.FLY_OBJ_TYPE type : map.keySet()) {
            int intensity = map.get(type);
            FlyingObjs obj = new FlyingObjs(type, platform, intensity, cinematic);
            obj.setHue(hue);
            objs.add(obj);
            obj.reset();
            if (type.vfx != null) {
                objsVfx.addActor(obj);
            } else if (under) {
                objsUnder.addActor(obj);
            } else {
                objsOver.addActor(obj);
            }
        }

    }

}

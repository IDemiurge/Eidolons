package libgdx.bf.grid.moving.flight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.content.consts.VisualEnums;
import eidolons.content.data.FlightData;
import libgdx.gui.generic.GroupX;
import libgdx.screens.batch.CustomSpriteBatch;
import libgdx.shaders.ShaderDrawer;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.datatypes.WeightMap;
import main.system.launch.Flags;

import java.util.LinkedHashSet;
import java.util.Set;

import static eidolons.content.data.FlightData.*;

public class FlightHandler extends GroupX {
    private boolean on;

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public static final String TEST_DATA =
            "objs_over:cinders(10),cloud(20),wraith(30),;" +
                    "objs_under:mist(20),stars(5),isle(10),cloud(10),;" +
                    "angle:30;";
    Set<FlyingObjs> objs = new LinkedHashSet<>();
    private final GroupX objsUnder = new GroupX() {
        @Override
        public void draw(Batch batch, float parentAlpha) {
            if (parentAlpha== ShaderDrawer.SUPER_DRAW) {
                super.draw(batch,1f);
                return;
            }
            ((CustomSpriteBatch) batch).resetBlending();
            drawScreen(batch, false);
            drawScreen(batch, true);
            ((CustomSpriteBatch) batch).resetBlending();
        }
    };
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
        on = false;
    }

    public void startFlight(String s, boolean cinematic) {
        FlightData data = new FlightData(s);
        CinematicPlatform platform = new CinematicPlatform(data.getFloatValue(FLIGHT_VALUE.angle));

        String value = data.getValue(FLIGHT_VALUE.objs_under);
        initObjsMap(platform, value, true, cinematic, data.getHue());
        value = data.getValue(FLIGHT_VALUE.objs_over);
        initObjsMap(platform, value, false, cinematic, data.getHue());

        if (Flags.isIDE())
            if (cinematic) {
                // act(10f);
                // String value = data.getValue(FLIGHT_VALUE.camera_shake);
                // value = data.getValue(FLIGHT_VALUE.soundscape);
                // value = data.getValue(FLIGHT_VALUE.camera_displace);
                // value = data.getValue(FLIGHT_VALUE.trail);
            }
        on = true;
        //transit between flights?
    }

    private void initObjsMap(CinematicPlatform platform, String data, boolean under, boolean cinematic, Color hue) {
        WeightMap<VisualEnums.FLY_OBJ_TYPE> map = new WeightMap<>(data, VisualEnums.FLY_OBJ_TYPE.class);

        for (VisualEnums.FLY_OBJ_TYPE type : map.keySet()) {
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

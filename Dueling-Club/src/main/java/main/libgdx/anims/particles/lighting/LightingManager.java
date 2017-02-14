package main.libgdx.anims.particles.lighting;

import main.entity.obj.unit.DC_HeroObj;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.datatypes.DequeImpl;

/**
 * Created by JustMe on 12/28/2016.
 */
public class LightingManager {
    public static float ambient_light = 0.35f;
    public static boolean debug = false;
    public static float darkening = 0;
    public static float mouse_light_distance = 450;
    public static float mouse_light_distance_to_turn_off = 10;
    public static boolean mouse_light = false;
    private static boolean lightOn;
    private static boolean testMode;
    private LightMap lightMap;

    public LightingManager(DequeImpl<DC_HeroObj> units, int rows, int cols) {
        lightMap = new LightMap(units, rows, cols);
        GuiEventManager.bind(GuiEventType.GRID_CREATED, p -> {
            //TODO init emitterMap and lightMap
        });
    }

    public static boolean isMouse_light() {
        return mouse_light;
    }

    public static void setMouse_light(boolean mouse_light) {
        LightingManager.mouse_light = mouse_light;

    }

    public static boolean isLightOn() {
        return lightOn;
    }

    public static void setLightOn(boolean lightOn) {
        LightingManager.lightOn = lightOn;
    }

    public static boolean isTestMode() {
        return testMode;
    }

    public static void setTestMode(boolean testMode) {
        LightingManager.testMode = testMode;
    }

    public void updateAll() {
        if (!lightMap.isValid()) {
            return;
        }
        lightMap.updateMap();
        lightMap.updateLight();
    }

    public void updateLight() {
        lightMap.updateLight();
    }

    public void updatePos(DC_HeroObj obj) {
        lightMap.updatePos(obj);
    }

    public void updateObject(DC_HeroObj targetObj) {
        lightMap.updateObject(targetObj);
    }
}

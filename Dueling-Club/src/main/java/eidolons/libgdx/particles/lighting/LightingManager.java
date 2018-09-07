package eidolons.libgdx.particles.lighting;

import eidolons.entity.obj.BattleFieldObject;
import main.system.datatypes.DequeImpl;

/**
 * Created by JustMe on 12/28/2016.
 */
public class LightingManager {
    public static float ambient_light = 0.55f;
    public static boolean debug = false;
    public static float darkening = 0;
    public static float mouse_light_distance = 450;
    public static float mouse_light_distance_to_turn_off = 10;
    public static boolean mouse_light = true;
    private static boolean lightOn = false;
    private static boolean testMode;
    private LightMap lightMap;

    public LightingManager(DequeImpl<BattleFieldObject> units, int rows, int cols) {
        lightMap = new LightMap(units, rows, cols);
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

    public void updatePos(BattleFieldObject obj) {
        lightMap.updatePos(obj);
    }

    public void updateObject(BattleFieldObject targetObj) {
        lightMap.updateObject(targetObj);
    }
}

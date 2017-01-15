package main.libgdx.anims.particles.lighting;

import main.system.GuiEventManager;
import main.system.GraphicEvent;

/**
 * Created by JustMe on 12/28/2016.
 */
public class LightingManager {
    public static float ambient_light = 0.35f;
    public static boolean debug = false;
    public static float darkening= 0;
    public static float mouse_light_distance = 450;
    public static float mouse_light_distance_to_turn_off = 10;
    public static boolean mouse_light = false;

    LightMap lightMap;

    public   LightingManager(LightMap map) {
        GuiEventManager.bind(GraphicEvent.GRID_CREATED, p -> {
            //TODO init emitterMap and lightMap
        });
        GuiEventManager.bind(GraphicEvent.UPDATE_LIGHT, p -> {
             lightMap.updateMap();
    });
    }

    public static boolean isMouse_light() {
        return mouse_light;
    }

    public static void setMouse_light(boolean mouse_light) {
        LightingManager.mouse_light = mouse_light;

    }
}

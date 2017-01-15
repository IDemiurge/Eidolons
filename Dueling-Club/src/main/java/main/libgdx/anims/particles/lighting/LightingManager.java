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

    LightMap lightMap;

    public   LightingManager(LightMap map) {
        GuiEventManager.bind(GraphicEvent.GRID_CREATED, p -> {
            //TODO init emitterMap and lightMap
        });
        GuiEventManager.bind(GraphicEvent.UPDATE_LIGHT, p -> {
             lightMap.updateMap();
    });
    }

}

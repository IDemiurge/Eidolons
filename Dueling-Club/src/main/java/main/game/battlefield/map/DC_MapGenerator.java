package main.game.battlefield.map;

import main.content.CONTENT_CONSTS;
import main.content.CONTENT_CONSTS.ASPECT;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.battlefield.map.DC_Map.BF_OBJ_OWNER;
import main.system.auxiliary.GuiManager;
import main.system.auxiliary.Manager;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DC_MapGenerator extends Manager {

    private static boolean initialized = false;
    private static Map<BF_OBJ_OWNER, ObjType> crystalsMap = new HashMap<BF_OBJ_OWNER, ObjType>();
    private static Map<BF_OBJ_OWNER, ObjType> gatewaysMap = new HashMap<BF_OBJ_OWNER, ObjType>();

    public static Point getCorner(boolean left, boolean top) {
        return new Point((left) ? 0 : GuiManager.getBF_CompDisplayedCellsX(),
                (top) ? 0 : GuiManager.getBF_CompDisplayedCellsY());
    }

    public static ObjType getGatewayObjType(BF_OBJ_OWNER TYPE) {
        if (!initialized) {
            init();
        }
        ObjType type;
        type = gatewaysMap.get(TYPE);
        if (TYPE == BF_OBJ_OWNER.NEUTRAL) {
            type = DataManager.getType(ASPECT.NEUTRAL.getGateway());
        }
        if (TYPE == BF_OBJ_OWNER.RANDOM) {
            type = DataManager.getType(ASPECT.getAspectByCode(new Random()
                    .nextInt(ASPECT.values().length)).getGateway());
        }

        return type;
    }

    public static ObjType getCrystalObjType(BF_OBJ_OWNER TYPE) {
        if (!initialized) {
            init();
        }
        ObjType type;
        type = crystalsMap.get(TYPE);
        if (TYPE == BF_OBJ_OWNER.NEUTRAL) {
            type = DataManager.getType(ASPECT.NEUTRAL.getCrystalName());
        }
        if (TYPE == BF_OBJ_OWNER.RANDOM) {
            type = DataManager.getType(ASPECT.getAspectByCode(new Random()
                    .nextInt(ASPECT.values().length)).getCrystalName());
        }

        return type;
    }

    private static void init() {
        initialized = true;
        crystalsMap.put(BF_OBJ_OWNER.MY, DataManager
                .getType(CONTENT_CONSTS.ASPECT.getAspect(game.getPlayer(true)
                        .getAllegiance()).getCrystalName()));
        crystalsMap.put(BF_OBJ_OWNER.ENEMY, DataManager
                .getType(CONTENT_CONSTS.ASPECT.getAspect(game.getPlayer(false)
                        .getAllegiance()).getCrystalName()));

        gatewaysMap.put(BF_OBJ_OWNER.MY, DataManager
                .getType(CONTENT_CONSTS.ASPECT.getAspect(game.getPlayer(true)
                        .getAllegiance()).getGateway()));
        gatewaysMap.put(BF_OBJ_OWNER.ENEMY, DataManager
                .getType(CONTENT_CONSTS.ASPECT.getAspect(game.getPlayer(false)
                        .getAllegiance()).getGateway()));

    }

}

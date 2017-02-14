package main.simulation;

import main.client.DC_Engine;
import main.content.OBJ_TYPES;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.obj.unit.DC_HeroObj;
import main.entity.type.ObjType;
import main.game.DC_Game;
import main.game.logic.battle.player.DC_Player;
import main.launch.ArcaneVault;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SimulationManager {

    private static final String[] unitTypes = {OBJ_TYPES.UNITS.getName(),
            OBJ_TYPES.BF_OBJ.getName(), OBJ_TYPES.CHARS.getName(),};
    private static Map<ObjType, DC_HeroObj> unitMap = new HashMap<>();

    public static void initUnitObj(String name) {
        ObjType type = getUnitType(name);
        if (type == null) {
            return;
        }
        createUnit(type);
    }

    public static void createUnit(ObjType type) {
        if (unitMap.containsKey(type)) {
            return;
        }
        DC_HeroObj unit = new DC_HeroObj(type, 0, 0, DC_Player.NEUTRAL, getGame(), new Ref(
                getGame()));
        getGame().getState().addObject(unit);
        try {
            resetUnit(unit);
        } catch (Exception e) {
            e.printStackTrace();
        }
        unitMap.put(type, unit);

    }

    private static void resetUnit(DC_HeroObj unit) {
        unit.toBase();
        unit.afterEffects();
        applyEffectForUnit(unit);
    }

    private static void applyEffectForUnit(DC_HeroObj unit) {
        // getGame().getState().getAttachedEffects()

    }

    private static ObjType getUnitType(String name) {
        ObjType type = DataManager.getType(name, OBJ_TYPES.UNITS);
        if (type == null) {
            type = DataManager.getType(name, OBJ_TYPES.CHARS);
        }
        return type;
    }

    public static DC_HeroObj getUnit(String typeName) {
        return getUnit(getUnitType(typeName));
    }

    public static DC_HeroObj getUnit(ObjType type) {
        if (!unitMap.containsKey(type)) {
            createUnit(type);
        }
        return unitMap.get(type);
    }

    public static void refreshType(ObjType type) {
        DC_HeroObj unit = unitMap.get(type);
        if (unit == null) {
            createUnit(type);
        }
        try {
            resetUnit(unit);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // resetPassives();
        // resetMasteries();
        // resetAttrs();

    }

    public static void refreshAll() {

    }

    public static boolean isUnitType(String selected) {
        return Arrays.asList(unitTypes).contains(selected);
    }

    private static DC_Game getGame() {
        return (DC_Game) ArcaneVault.getGame();
    }

    public static void init() {
        DC_Engine.microInitialization(true);
    }
}

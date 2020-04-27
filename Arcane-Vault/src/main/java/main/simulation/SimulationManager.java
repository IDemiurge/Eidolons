package main.simulation;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.logic.mission.universal.DC_Player;
import eidolons.game.core.game.DC_Game;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.launch.ArcaneVault;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SimulationManager {

    private static final String[] unitTypes = {DC_TYPE.UNITS.getName(),
            DC_TYPE.BF_OBJ.getName(), DC_TYPE.CHARS.getName(),};
    private static Map<ObjType, Unit> unitMap = new HashMap<>();

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
        Unit unit = new Unit(type, 0, 0, DC_Player.NEUTRAL, getGame(), new Ref(
                getGame()));
        getGame().getState().addObject(unit);
        try {
            resetUnit(unit);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        unitMap.put(type, unit);

    }

    private static void resetUnit(Unit unit) {
        unit.toBase();
        unit.afterEffects();
        applyEffectForUnit(unit);
    }

    private static void applyEffectForUnit(Unit unit) {
        // getGame().getState().getAttachedEffects()

    }

    private static ObjType getUnitType(String name) {
        ObjType type = DataManager.getType(name, DC_TYPE.UNITS);
        if (type == null) {
            type = DataManager.getType(name, DC_TYPE.CHARS);
        }
        return type;
    }

    public static Unit getUnit(String typeName) {
        return getUnit(getUnitType(typeName));
    }

    public static Unit getUnit(ObjType type) {
        if (!unitMap.containsKey(type)) {
            createUnit(type);
        }
        return unitMap.get(type);
    }

    public static void refreshType(ObjType type) {
        Unit unit = unitMap.get(type);
        if (unit == null) {
            createUnit(type);
        }
        try {
            resetUnit(unit);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        // resetPassives();
        // resetMasteries();
        // resetAttrs();

    }


    public static boolean isUnitType(String selected) {
        return Arrays.asList(unitTypes).contains(selected);
    }

    private static DC_Game getGame() {
        return ArcaneVault.getGame();
    }

    public static void init() {
        DC_Engine.gameInit( );
    }
}

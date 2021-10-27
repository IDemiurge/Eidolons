package eidolons.game;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.exploration.handlers.ExplorationMaster;
import eidolons.system.hotkey.Accessibility;
import main.content.values.parameters.PARAMETER;
import main.data.StringMap;
import main.game.bf.directions.FACING_DIRECTION;

import java.util.Map;

/**
 * Created by JustMe on 5/13/2017.
 */
public class EidolonsGame {
    public static boolean TESTER_VERSION = false;
    public static boolean SELECT_SCENARIO;
    public static boolean FOOTAGE;
    private static final Map<String, Boolean> varMap = new StringMap<>();
    private static final Map<String, Boolean> actionMap = new StringMap<>();
    public static String lvlPath;

    private MetaGameMaster metaMaster;
    private boolean aborted;

    public boolean isAborted() {
        return aborted;
    }

    public EidolonsGame() {
    }

    public static void reset() {
    }

    public static void set(String field, boolean val) {
        setVar(field, val);
        try {
            EidolonsGame.class.getField(field.toUpperCase()).set(null, val);
            main.system.auxiliary.log.LogMaster.important(field + ": global var set to " + val);
        } catch (IllegalAccessException e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } catch (NoSuchFieldException e) {
            try {
                EidolonsGame.class.getField(field).set(null, val);
            } catch (IllegalAccessException e1) {
                main.system.ExceptionMaster.printStackTrace(e);
            } catch (NoSuchFieldException e1) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            main.system.ExceptionMaster.printStackTrace(e);
        }

    }

    public static boolean get(String field) {
        try {
            return (boolean) EidolonsGame.class.getField(field.toUpperCase()).get(null);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return false;
    }

    public static boolean getAny(String field) {
        if (varMap.get(field) == null) {
            return get(field);
        }
        return varMap.get(field);
    }

    public static boolean getVar(String field) {
        if (varMap.get(field) == null) {
            return false;
        }
        return varMap.get(field);
    }

    public static void setVar(String value, Boolean valueOf) {
        main.system.auxiliary.log.LogMaster.important(value + ": setting var to " + valueOf);
        varMap.put(value, valueOf);
    }

    public static boolean getActionSwitch(String field) {
        if (actionMap.get(field) == null) {
            return false;
        }
        return actionMap.get(field);
    }

    public static void setActionSwitch(String value, Boolean valueOf) {
        actionMap.put(value, valueOf);
    }


    public static boolean isHqEnabled() {
        return true;
    }

    public static FACING_DIRECTION getPresetFacing(Unit unit) {
        return null;
    }


    public static boolean isLordPanelEnabled() {
        return false;
    }


    static Map<TUTORIAL_STAGE, Boolean> completionMap;

    public static void stageDone(TUTORIAL_STAGE stage) {
        completionMap.put(stage, true);
    }
    public enum TUTORIAL_STAGE {

        alert,
        essence,
        meditate,


    }

    public static boolean isActionBlocked(DC_ActiveObj activeObj) {
        if (activeObj == null) {
            return false;
        }
        if (activeObj.isDisabled()) {
            return true;
        }
        if (!activeObj.getOwnerUnit().isPlayerCharacter()) {
            return false;
        }
        if (!Accessibility.isActionNotBlocked(activeObj, ExplorationMaster.isExplorationOn())) {
            return true;
        }
        if (activeObj.isSpell()) {
            return false;
        }
        if (!actionMap.containsKey(activeObj.getName().toLowerCase())) {
            return false;
        }
        return !actionMap.get(activeObj.getName().toLowerCase());
    }

    public MetaGameMaster getMetaMaster() {
        return metaMaster;
    }

    public void setMetaMaster(MetaGameMaster metaMaster) {
        this.metaMaster = metaMaster;
    }

    public void init() {
//        if (metaMaster.getData().equalsIgnoreCase("ashen path")) {
//            BRIDGE = true;
//        }
        metaMaster.init();
    }


}

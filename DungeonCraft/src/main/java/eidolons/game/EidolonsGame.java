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
    public static  boolean BRIDGE = false; //TODO refactor it out!
    public static boolean DEMO = false; //use for tut?


    public static boolean TESTER_VERSION = false;
    public static boolean SELECT_HERO;
    public static boolean SELECT_SCENARIO;
    public static boolean FOOTAGE;
    public static boolean DUEL_TEST;
    // public static boolean IDE = Flags.isIDE();

    public static boolean BOSS_FIGHT;
    public static boolean TUTORIAL_MISSION;
    public static boolean TUTORIAL_PATH;
    public static boolean FIRST_BATTLE_STARTED;
    public static boolean DUEL = false;

    public static boolean INTRO_STARTED;
    public static boolean ATTACKS_DISABLED;
    public static boolean TURNS_DISABLED;
    public static boolean MOVES_DISABLED;
    public static boolean MOVES_FORWARD_ONLY;
    public static boolean TUTORIAL;
    public static boolean PUZZLES;
    public static boolean TOWN;
    private static final Map<String, Boolean> varMap = new StringMap<>();
    private static final Map<String, Boolean> actionMap = new StringMap<>();

    //convenience fields
    public static String lvlPath;

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

    private MetaGameMaster metaMaster;
    private boolean aborted;

    static Map<TUTORIAL_STAGE, Boolean> completionMap;

    public static boolean isHqEnabled() {
        return true;
    }

    public static FACING_DIRECTION getPresetFacing(Unit unit) {
        return null;
    }

    public static void stageDone(TUTORIAL_STAGE stage) {
        completionMap.put(stage, true);
    }

    public static boolean isLordPanelEnabled() {
        return false;
    }


    public enum TUTORIAL_STAGE {

        alert,
        essence,
        meditate,


    }

    public static boolean isParamBlocked(PARAMETER parameter) {
        return false;
    }

    public static boolean isActionBlocked(DC_ActiveObj activeObj) {
        /**
         * boolean map?
         *
         */
        if (activeObj == null) {
            return false;
        }
        if (activeObj.isDisabled()) {
            return true;
        }
        if (!activeObj.getOwnerUnit().isPlayerCharacter()) {
            return false;
        }
        if (activeObj.isMove()) {
            if (MOVES_DISABLED) {
                return true;
            }
            if (MOVES_FORWARD_ONLY) {
                return !activeObj.getName().equalsIgnoreCase("Move");
            }
            return false;
        }
        if (activeObj.isTurn()) {
            return TURNS_DISABLED;
        }
        if (!EidolonsGame.DUEL) {
            return false;
        }
        if (!EidolonsGame.TUTORIAL) {
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

    public boolean isAborted() {
        return aborted;
    }

    public void setAborted(boolean aborted) {
        if (aborted) main.system.auxiliary.log.LogMaster.log
                (1, "game aborted!!!!!!");
        this.aborted = aborted;
    }

}

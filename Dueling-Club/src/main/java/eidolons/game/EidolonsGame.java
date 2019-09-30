package eidolons.game;

import eidolons.entity.active.ActionInitializer;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMap;
import eidolons.system.options.GameplayOptions;
import eidolons.system.options.OptionsMaster;
import eidolons.system.options.SystemOptions;
import main.content.values.parameters.PARAMETER;
import main.data.StringMap;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.secondary.Bools;
import main.system.launch.CoreEngine;

import java.util.Map;

/**
 * Created by JustMe on 5/13/2017.
 */
public class EidolonsGame {
    public static  boolean TESTER_VERSION = false;
    public static   boolean FOOTAGE ;
    public static   boolean DUEL_TEST  ;
    public static   boolean TRANSIT_TEST  ;
    public static boolean LEVI_TEST = false;
    public static boolean IDE = CoreEngine.isIDE();


    public static boolean BRIDGE = false;
    public static boolean BOSS_FIGHT;
    public static boolean TUTORIAL_MISSION;
    public static boolean TUTORIAL_PATH;
    public static boolean BRIDGE_CROSSED;
    public static boolean FIRST_BATTLE_STARTED;
    public static boolean DUEL = false;

    public static boolean INTRO_STARTED;
    public static boolean ATTACKS_DISABLED;
    public static boolean TURNS_DISABLED;
    public static boolean MOVES_DISABLED;
    public static boolean MOVES_FORWARD_ONLY;
    public static boolean TUTORIAL;
    public static boolean PUZZLES;
    private static Map<String, Boolean> varMap = new StringMap<>();
    private static Map<String, Boolean> actionMap = new StringMap<>();

    public static final void set(String field, boolean val) {
        setVar(field, val);
        try {
            EidolonsGame.class.getField(field.toUpperCase()).set(null, val);
            main.system.auxiliary.log.LogMaster.important(field + ": global var set to " +val);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            try {
                EidolonsGame.class.getField(field).set(null, val);
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (NoSuchFieldException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }

    }
    public static final boolean get(String field ) {
        try {
            return (boolean) EidolonsGame.class.getField(field.toUpperCase()).get(null );
        } catch (Exception e) {
            e.printStackTrace();
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
        main.system.auxiliary.log.LogMaster.important(value + ": setting var to " +valueOf);
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
        if (EidolonsGame.BRIDGE)
            if (!EidolonsGame.BRIDGE_CROSSED)
                return false;
        return true;
    }

    public static boolean isAltControlPanel() {
        if (EidolonsGame.BRIDGE_CROSSED)
            return false;
        return BRIDGE;
    }

    public static FACING_DIRECTION getPresetFacing(Unit unit) {
        if (BRIDGE) {

            if (OptionsMaster.getSystemOptions().getBooleanValue(SystemOptions.SYSTEM_OPTION.TESTER_VERSION)) {
                return FACING_DIRECTION.EAST;
            }
            return FACING_DIRECTION.NORTH;
        }
        return null;
    }

    public static void stageDone(TUTORIAL_STAGE stage) {
        completionMap.put(stage, true);
    }

    public static boolean isLordPanelEnabled() {
        return !BRIDGE;
    }


    public enum TUTORIAL_STAGE {

        alert,
        essence,
        meditate,


    }

    public static boolean isSpellsEnabled() {
        return CoreEngine.isIDE() && !EidolonsGame.DUEL;
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
        if (!ActionInitializer.isActionNotBlocked(activeObj, ExplorationMaster.isExplorationOn())) {
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

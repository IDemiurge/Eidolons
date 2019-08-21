package eidolons.game;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.system.options.OptionsMaster;
import eidolons.system.options.SystemOptions;
import main.content.values.parameters.PARAMETER;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.launch.CoreEngine;

import java.util.Map;

/**
 * Created by JustMe on 5/13/2017.
 */
public class EidolonsGame {
    public static boolean BRIDGE = false;
    public static boolean BOSS_FIGHT;
    public static boolean TUTORIAL_MISSION;
    public static boolean TUTORIAL_PATH;
    public static boolean BRIDGE_CROSSED;
    public static boolean firstBattleStarted;

    public static boolean INTRO_STARTED;

    public static final void set(String field, boolean val) {
        try {
            EidolonsGame.class.getField(field.toUpperCase()).set(null, val);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

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
        return CoreEngine.isIDE();
    }

    public static boolean isParamBlocked(PARAMETER parameter) {
        return false;
    }

    public static boolean isActionBlocked(DC_ActiveObj activeObj) {
        /**
         * boolean map?
         *
         */
        return false;
    }

    public MetaGameMaster getMetaMaster() {
        return metaMaster;
    }

    public void setMetaMaster(MetaGameMaster metaMaster) {
        this.metaMaster = metaMaster;
    }

    public void init() {
        if (metaMaster.getData().equalsIgnoreCase("ashen path")) {
            BRIDGE = true;
        }
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

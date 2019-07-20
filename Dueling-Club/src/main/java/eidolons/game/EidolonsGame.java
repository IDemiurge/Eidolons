package eidolons.game;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import main.game.bf.directions.FACING_DIRECTION;

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

    private MetaGameMaster metaMaster;
    private boolean aborted;

    public static boolean isHqEnabled() {
        if (EidolonsGame.BRIDGE)
            if (!EidolonsGame.BRIDGE_CROSSED)
                return false;
        return true;
    }

    public static boolean isAltControlPanel() {
        if ( EidolonsGame.BRIDGE_CROSSED)
            return false;
        return BRIDGE ;
    }

    public static FACING_DIRECTION getPresetFacing(Unit unit) {
        if (BRIDGE) {
            return FACING_DIRECTION.EAST;
        }
        return null;
    }

    public MetaGameMaster getMetaMaster() {
        return metaMaster;
    }

    public void setMetaMaster(MetaGameMaster metaMaster) {
        this.metaMaster = metaMaster;
    }

    public void init() {
        if (metaMaster.getData().equalsIgnoreCase("ashen path")) {
            BRIDGE=true;
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

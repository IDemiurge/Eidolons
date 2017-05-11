package main.test.frontend;

import main.client.cc.logic.items.ItemGenerator;
import main.entity.type.ObjType;
import main.game.core.game.DC_Game;
import main.game.battlecraft.logic.dungeon.test.UnitGroupMaster;
import main.test.debug.DebugMaster;
import main.test.debug.DebugMaster.DEBUG_FUNCTIONS;

public class TestLauncher {

    public static void main(String[] args) {
        /*
        init GameLauncher from args!
         */


    }

    public static void launchDC(String[] args) {
        ItemGenerator.setGenerationOn(true);

        String playerParty = null;
        ObjType type = null;

        String enemyParty = UnitGroupMaster.getRandomReadyGroup(type.getLevel());

        // UnitGroupMaster.readGroupFile(UnitGroupMaster
        // .getRandomReadyGroup(1));

        if (FAST_DC.isRunning()) {
            FAST_DC.getTestLauncher().ENEMY_PARTY = enemyParty;
            FAST_DC.getTestLauncher().PLAYER_PARTY = playerParty;
            FAST_DC.getTestLauncher().initData();
            DebugMaster.setAltMode(true);
            DC_Game.game.getDebugMaster().executeDebugFunction(DEBUG_FUNCTIONS.RESTART);

            return;
        }

        ItemGenerator.init();
//        String[] args = new String[]{FAST_DC.PRESET_ARG, playerParty, enemyParty};
        FAST_DC.main(args);
//        break;
    }

}

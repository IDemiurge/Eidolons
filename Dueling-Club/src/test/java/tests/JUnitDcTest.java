package tests;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.ai.AI_Manager;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.launch.PresetLauncher.LAUNCH;
import eidolons.test.frontend.FAST_DC;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;
import org.junit.Before;
import res.JUnitResources;
import tests.utils.JUnitUtils;

/**
 * Created by JustMe on 3/27/2017.
 */
public class JUnitDcTest {

    protected DC_Game game;
    protected DcHelper helper;
    protected CheckHelper checker;
    protected JUnitUtils utils;
    protected JUnitResources resources;
    protected AtbHelper atbHelper;


    protected String getDungeonPath() {
        return JUnitResources.EMPTY_DUNGEON;
    }

    protected String getPlayerParty() {
        return JUnitResources.DEFAULT_UNIT;
    }

    protected String getEnemyParty() {
        return "";
    }

    @Before
    public void init() {
        LogMaster.setOff(isLoggingOff()); //log everything* or nothing to speed up
        CoreEngine.setGraphicsOff(isGraphicsOff());
        CoreEngine.setjUnit(true);
        CoreEngine.setSelectivelyReadTypes(getXmlTypesToRead());
        AI_Manager.setOff(isAiOff());
        DC_Engine.setTrainingOff(isTrainingOff());
        if (isOldLauncher()) {
            FAST_DC.main(new String[]{
             FAST_DC.PRESET_OPTION_ARG + StringMaster.wrapInParenthesis(LAUNCH.JUnit.name()),
             getPlayerParty(),
             getEnemyParty(),
             getDungeonPath()
            });
        } else {

            MultiLauncher launcher = new MultiLauncher(getPlayerParty(),
             getEnemyParty(),
             getDungeonPath());
            launcher.setAfterEngineInit(() -> resources = new JUnitResources());
            launcher.launch();

        }
        game = Eidolons.game;
        helper = new DcHelper(game);
        atbHelper = new AtbHelper(game);
        checker = new CheckHelper(game);
        utils = new JUnitUtils(game);
    }

    protected String getXmlTypesToRead() {
        return "bf obj;buffs;weapons;armor;terrain;dungeons;units;actions;abils;";
    }

    protected boolean isTrainingOff() {
        return true;
    }
    protected boolean isAiOff() {
        return true;
    }

    private boolean isOldLauncher() {
        return false;
    }

    protected boolean isLoggingOff() {
        return true;
    }

    protected boolean isGraphicsOff() {
        return true;
    }

    protected Unit getHero() {
        return game.getManager().getMainHero();
    }
}

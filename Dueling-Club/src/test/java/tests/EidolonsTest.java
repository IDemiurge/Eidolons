package tests;

import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.ai.AI_Manager;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.launch.MainLauncher;
import eidolons.libgdx.screens.menu.MainMenu.MAIN_MENU_ITEM;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import org.junit.Before;
import res.JUnitResources;
import tests.utils.JUnitUtils;

/**
 * Created by JustMe on 3/27/2017.
 */
public class EidolonsTest {

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

    @Deprecated
    @Before
    public void init() {
        commonInit();

        if (isScenario()) {
            scenarioInit();
        } else if (isOldLauncher()) {
            oldInit();
        } else {
            newInit();
        }
    }

    private void afterInit() {
        game = Eidolons.game;
        helper = new DcHelper(game);
        atbHelper = new AtbHelper(game);
        checker = new CheckHelper(game);
        utils = new JUnitUtils(game);
    }

    private void commonInit() {
        LogMaster.setOff(isLoggingOff()); //log everything* or nothing to speed up
        CoreEngine.setGraphicsOff(isGraphicsOff());
        Flags.setjUnit(true);
        if (isSelectiveXml())
            CoreEngine.setSelectivelyReadTypes(getXmlTypesToRead());
        AI_Manager.setOff(isAiOff());
        DC_Engine.setTrainingOff(isTrainingOff());
    }

    protected void newInit() {
        commonInit();
        MultiLauncher launcher = new MultiLauncher(getPlayerParty(),
                getEnemyParty(),
                getDungeonPath(),
                isTestMeta());
        launcher.setAfterEngineInit(() -> resources = new JUnitResources());
        launcher.launch();
        afterInit();
    }

    protected void scenarioInit() {
        commonInit();
        new MainLauncher().main(new String[]{
                getLaunchArgString()
        });
        WaitMaster.waitForInputAnew(WAIT_OPERATIONS.GAME_LOOP_STARTED);

        afterInit();
    }

    protected void oldInit() {
        commonInit();
//  TODO renew
//   FAST_DC.main(new String[]{
//                FAST_DC.PRESET_OPTION_ARG + StringMaster.wrapInParenthesis(LAUNCH.JUnit.name()),
//                getPlayerParty(),
//                getEnemyParty(),
//                getDungeonPath()
//        });
        afterInit();
    }

    protected boolean isTestMeta() {
        return true;
    }

    protected String getLaunchArgString() {
        return MAIN_MENU_ITEM.PLAY.name() + "," +
                getScenarioIndex() + "," + getHeroIndex();
    }

    protected Integer getScenarioIndex() {
        return null;
    }

    protected Integer getHeroIndex() {
        return null;
    }

    protected boolean isScenario() {
        return false;
    }

    protected void log(String s) {
        System.out.println(s);
    }

    protected boolean isSelectiveXml() {
        return true;
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

    protected boolean isOldLauncher() {
        return false;
    }

    protected boolean isLoggingOff() {
        return true;
    }

    protected boolean isGraphicsOff() {
        return true;
    }

}

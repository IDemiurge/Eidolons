package tests;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.launch.PresetLauncher.LAUNCH;
import eidolons.test.frontend.FAST_DC;
import main.content.DC_TYPE;
import main.data.xml.XML_Reader;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;
import org.junit.Before;
import res.JUnitResources;
import tests.utils.JUnitUtils;

/**
 * Created by JustMe on 3/27/2017.
 */
public class FastDcTest {

    protected DC_Game game;
    protected DcHelper helper;
    protected CheckHelper checker;
    protected JUnitUtils utils;


    protected String getDungeonPath() {
        return JUnitResources.EMPTY_DUNGEON;
    }

    protected String getPlayerParty() {
        return null;
    }

    protected String getEnemyParty() {
        return null;
    }

    @Before
    public void init() {
        LogMaster.setOff(isLoggingOff()); //log everything* or nothing to speed up
        CoreEngine.setGraphicsOff(isGraphicsOff());
        CoreEngine.setjUnit(true);
        injectJUnitResources();
        FAST_DC.main(new String[]{
         FAST_DC.PRESET_OPTION_ARG + StringMaster.wrapInParenthesis(LAUNCH.JUnit.name()),
         getPlayerParty(),
         getEnemyParty(),
         getDungeonPath()
        });
        game = Eidolons.game;
        helper = new DcHelper(game);
        checker = new CheckHelper(game);
        utils = new JUnitUtils(game);
    }
   public static final  DC_TYPE [] RESOURCE_TYPE = {
     DC_TYPE.UNITS,
    };
    private void injectJUnitResources() {
        for (DC_TYPE TYPE : RESOURCE_TYPE) {
        XML_Reader.readCustomTypeFile(FileManager.getFile(getJUnitXml(TYPE)), TYPE, game);

        }

    }

    private String getJUnitXml(DC_TYPE type) {
        return StrPathBuilder.build("");
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

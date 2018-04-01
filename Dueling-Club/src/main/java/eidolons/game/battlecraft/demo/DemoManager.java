package eidolons.game.battlecraft.demo;

import eidolons.client.cc.gui.neo.choice.ChoiceSequence;
import eidolons.client.cc.gui.neo.choice.EntityChoiceView;
import eidolons.client.dc.Launcher;
import eidolons.client.dc.Launcher.VIEWS;
import eidolons.client.dc.SequenceManager;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.arcade.Arcade;
import eidolons.game.battlecraft.logic.meta.universal.PartyHelper;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game.GAME_MODES;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.VALUE;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 5/3/2017.
 */
public class DemoManager {


    public static final String PARTY_NAME = "Demo Party";
    private static Unit leader;
    private static String demoParty = "Demo Party";

    public static void loadLast() {

    }

    public static void showInfo() {

    }

    public static void save() {
//append leader, ...
    }

    public static void hqEntered() {
        initHero();
        initParty();
        initArcade(); //choose level? Yeah!
    }

    private static void initParty() {
        PartyHelper.createParty(DataManager.getType(demoParty, DC_TYPE.PARTY),
         leader);
    }

    private static void initHero() {
        //TODO JUST USE 'CHOOSE CENTER HERO' ?
        List<ObjType> demoHeroes = new ArrayList<>();
        DataManager.getTypesSubGroup(DC_TYPE.CHARS, "Demo");
        Unit entity = null;

        ChoiceSequence sequence = new ChoiceSequence();
        sequence.addView(new EntityChoiceView(sequence, entity, demoHeroes) {


            @Override
            public String getInfo() {
                return null;
            }

            @Override
            protected PROPERTY getPROP() {
                return null;
            }

//            @Override
//            protected Condition getFilterConditions() {
////                return new NumericCondition("level");
//            }

            @Override
            protected VALUE getFilterValue() {
                return null;
            }

            protected OBJ_TYPE getTYPE() {
                return DC_TYPE.CHARS;
            }
        });
        sequence.setManager(new SequenceManager() {
            @Override
            public void doneSelection() {
                WaitMaster.receiveInput(WAIT_OPERATIONS.SELECTION, sequence.getValue());
                Launcher.resetView(VIEWS.HC);
//                setLeader()

                PartyHelper.loadParty(demoParty);
//                initSelectedHero();
//                newParty();
//                launchHC();
            }

            @Override
            public void cancelSelection() {

            }
        });
        sequence.start();

    }

    public static void setLeader(Unit leader) {
        DemoManager.leader = leader;
    }

    public static void battleEntered() {
        String path = getDungeonPath(PartyHelper.getParty().getLevel());
//        Eidolons.game.getDungeonMaster().initDungeon(path);
        Eidolons.game.setGameMode(GAME_MODES.ARENA);
    }

    private static String getDungeonPath(int level) {
        return DEMO_DUNGEONS.values()[0].getPath();
    }

    private static void initArcade() {
        Arcade arcade = new Arcade();
//        arcade.setLevel(level);

    }

    public static void init() {
        initDungeon();
    }

    private static void initDungeon() {
//        Eidolons.game.getDungeonMaster().

    }

    public enum DEMO_DUNGEONS {
        NIGHT_ROAD,
        CEMETARY,
        RAVENWOOD,
        UNDERWORLD,
        ELDRITCH_SHRINE,;
        String path;

        DEMO_DUNGEONS() {
            path = "demo\\" +
             StringMaster.getWellFormattedString(name() + ".xml");

        }

        public String getPath() {
            return path;
        }
    }
}

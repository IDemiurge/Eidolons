package main.game.battlecraft.demo;

import main.client.cc.gui.neo.choice.ChoiceSequence;
import main.client.cc.gui.neo.choice.EntityChoiceView;
import main.client.dc.Launcher;
import main.client.dc.Launcher.VIEWS;
import main.client.dc.SequenceManager;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.VALUE;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.meta.arcade.Arcade;
import main.game.battlecraft.logic.meta.universal.PartyHelper;
import main.game.battlecraft.logic.meta.PartyManager;
import main.game.battlecraft.logic.meta.arcade.Arcade;
import main.game.core.Eidolons;
import main.game.core.game.DC_Game.GAME_MODES;
import main.system.auxiliary.StringMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 5/3/2017.
 */
public class DemoManager {


    public static final String PARTY_NAME ="Demo Party" ;
    private static Unit leader;
    private static String demoParty="Demo Party";

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
        List<ObjType> demoHeroes = new LinkedList<>();
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
        sequence .setManager(new SequenceManager() {
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
        DEMO_DUNGEONS(){
            path = "demo\\"+
             StringMaster.getWellFormattedString(name() + ".xml");

        }

        public String getPath() {
            return path;
        }
    }
}

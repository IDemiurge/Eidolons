package main.game.battlecraft.logic.meta.skirmish;

import main.client.cc.gui.neo.choice.ChoiceMaster;
import main.client.cc.gui.neo.choice.ChoiceSequence;
import main.client.cc.gui.neo.choice.EnumChoiceView;
import main.client.cc.gui.neo.choice.ListChoiceView;
import main.client.dc.Launcher;
import main.client.dc.Launcher.VIEWS;
import main.client.dc.SequenceManager;
import main.content.DC_TYPE;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.content.values.properties.MACRO_PROPS;
import main.data.DataManager;
import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.battle.arena.Wave;
import main.game.battlecraft.logic.battle.universal.BattleOptions.DIFFICULTY;
import main.game.battlecraft.logic.dungeon.universal.Dungeon;
import main.game.battlecraft.logic.dungeon.universal.DungeonBuilder;
import main.game.battlecraft.logic.meta.scenario.ObjectiveMaster.OBJECTIVE_TYPE;
import main.game.core.game.DC_Game;
import main.game.core.game.DC_Game.GAME_TYPE;
import main.swing.generic.components.G_Panel.VISUALS;
import main.swing.generic.components.misc.GraphicComponent;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.images.ImageManager;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkirmishMaster {
    static Skirmish skirmish;

    public static void preLaunch() {
        // TODO conditions for party heroes?

    }

    public static Map<Wave, Integer> constructWaveSequences(Integer round) {
        Map<Wave, Integer> map = new XLinkedMap<>();
        // customize nemesis groups! spawn coordinates - how to specify?
        for (NEMESIS_GROUP g : skirmish.getNemesisGroups()) {
            for (ObjType type : DataManager.getTypesSubGroup(DC_TYPE.ENCOUNTERS, StringMaster
                    .getWellFormattedString(g.toString()))) {
                Wave wave = new Wave(type, DC_Game.game, new Ref(), DC_Game.game.getPlayer(false));
                map.put(wave, round);
//                round += DC_Game.game.getBattleMaster().getBattleConstructor().getRoundsToFight(
//                        type);
                // this is just the basic construction - custom waves
                // dungeon-encounters are the way to go then?
                // getSkirmish().getProperty(MACRO_PROPS.CUSTOM_ENCOUNTERS)
            }
        }
        return map;
    }

    public static List<ObjType> getWaveTypes() {
        List<ObjType> waves = new ArrayList<>();
        for (NEMESIS_GROUP g : skirmish.getNemesisGroups()) {
            // skirmish.getSpeed();
            // skirmish.getDifficulty();
        }
//        DC_Game.game.getParty();
        // getOrCreate power level?
        return waves;
    }

    public static void newCustomSkirmish() {
        ChoiceSequence cs = new ChoiceSequence();
        new EnumChoiceView<>(cs, null, SKIRMISH_TYPE.class);
        // battlefield
        // max level
        // nemesis groups
        // objective

        // filtering based on chosen template/type/bf
        String info = "";
        String data = "";
        String path = PathFinder.getDungeonLevelFolder();
        final Map<String, File> map = new HashMap<>();
        for (File file : FileManager.getFilesFromDirectory(path, false)) {
            data += file.getName() + ";";
            map.put(file.getName(), file);
        }
        ListChoiceView battlefieldChoiceView = new ListChoiceView(cs, info, data) {
            public Component getListCellRendererComponent(JList<? extends String> list,
                                                          String value, int index, boolean isSelected, boolean cellHasFocus) {
                return getBattlefieldImageAndText(map.get(value), value, isSelected);

            }

        };
        // filter!
        final ArrayList<OBJECTIVE_TYPE> allowed = new ArrayList<>();
        EnumChoiceView<OBJECTIVE_TYPE> objectiveChoiceView = new EnumChoiceView<OBJECTIVE_TYPE>(cs,
                null, OBJECTIVE_TYPE.class) {
            @Override
            public boolean isOkBlocked() {
                return allowed.contains(getSelectedItem());
            }
        };
        cs.addView(battlefieldChoiceView);
        cs.addView(objectiveChoiceView); // or mission?
        // mode choice
        // dungeon choice? mission type choice?
        cs.setManager(getChoiceMaster(map, battlefieldChoiceView, objectiveChoiceView));
        cs.start();

    }

    public static void chooseSkirmish() {
        // generated mission types?
        // DataManager.getTypesSubGroup(MACRO_OBJ_TYPES.MISSIONS, SKIRMISH);

        // this will be on EDT, so choosing can be same thread, but waiting for
        // result on another

        ChoiceMaster.chooseTypeNewThread(MACRO_OBJ_TYPES.MISSION, DataManager.getTypesGroup(
                MACRO_OBJ_TYPES.MISSION, "Skirmish"), "Choose Skirmish to Fight in", null, null);
        new Thread(new Runnable() {
            public void run() {
                ObjType type = (ObjType) WaitMaster.waitForInput(WAIT_OPERATIONS.SELECTION);
                if (type != null) {
                    File file = FileManager.getFile(type.getProperty(MACRO_PROPS.ROOT_LEVEL));
                    if (!file.isFile()) {
                        return;
                    }
//                    Dungeon dungeon = DungeonBuilder.buildDungeon(FileManager.readFile(file));
//                    initSkirmish(type, dungeon);
//                    Launcher.resetView(VIEWS.MENU);
                } else {
                    Launcher.getMainManager().exitToMainMenu();
                }
            }
        }, " thread").start();

        // String name = ListChooser.chooseType(MACRO_OBJ_TYPES.MISSIONS,
        // "Skirmish");
        // if (type == null)
        // Launcher.getMainManager().exitToMainMenu();
        // else
        // // ObjType type = DataManager.getType(name,
        // MACRO_OBJ_TYPES.MISSIONS);
        // {
        // skirmish = new Skirmish(type,
        // type.getProperty(MACRO_PROPS.LEVEL_PATH));
        // Launcher.resetView(VIEWS.MENU);
        // }
    }

    protected static Component getBattlefieldImageAndText(File file, String value,
                                                          boolean isSelected) {
        String typeName = value;
        ObjType type = DataManager.getType(typeName, DC_TYPE.DUNGEONS);
        if (type == null) {
            String data = FileManager.readFile(file);
            int beginIndex = data.indexOf(XML_Converter.openXmlFormatted(DungeonBuilder.DUNGEON_TYPE_NODE));
            int endIndex = data.indexOf(XML_Converter.closeXmlFormatted(DungeonBuilder.DUNGEON_TYPE_NODE));
            if (beginIndex != -1 && endIndex != -1) {
                typeName = data.substring(beginIndex, endIndex);
                type = DataManager.getType(typeName, DC_TYPE.DUNGEONS);
            }
        }
        // crop entrance suffix etc
        Image img = ImageManager.getEmptyUnitIcon().getImage();
        if (type != null) {
            img = type.getIcon().getImage();
        }
        int height = 12;
        Image image = ImageManager.applyImage(VISUALS.PORTRAIT_BORDER.getImage(), img, 2, height);
        BufferedImage buffered = ImageManager.getBufferedImage(image);
        if (!StringMaster.isEmpty(value)) {
            buffered.getGraphics().drawString(value, 10, height);
        }
        // TextComp comp = new TextComp(new
        // CompVisuals(VISUALS.PORTRAIT_BORDER.getSize(), image), value){
        // protected boolean isCentering() {
        // return false;
        // }
        // };
        return new GraphicComponent(buffered);
    }

    private static SequenceManager getChoiceMaster(final Map<String, File> map,
                                                   final ListChoiceView battlefieldChoiceView,
                                                   final EnumChoiceView<OBJECTIVE_TYPE> objectiveChoiceView) {
        return new SequenceManager() {

            @Override
            public void doneSelection() {
                WaitMaster.receiveInput(WAIT_OPERATIONS.CUSTOM_SELECT, true);
                Object bfName = battlefieldChoiceView.getSelectedItem();
                File file = map.get(bfName);
                if (!file.isFile()) {
                    return;
                }
//                Dungeon dungeon = DungeonBuilder.buildDungeon(FileManager.readFile(file));
                ObjType missionType = new ObjType(); // specified where? used
                // for
                // what?
                OBJECTIVE_TYPE objective = objectiveChoiceView.getSelectedItem();
//                initSkirmish(missionType, dungeon);
                Launcher.resetView(VIEWS.MENU);
            }

            @Override
            public void cancelSelection() {
                WaitMaster.receiveInput(WAIT_OPERATIONS.CUSTOM_SELECT, false);
                Launcher.getMainManager().exitToMainMenu();
            }
        };
    }

    public static void generateStdSkirmishTypes() {
        for (SKIRMISH template : SKIRMISH.values()) {
            // name =
            // StringMaster.getWellFormattedString(missionType.getName());

        }

    }

    public static void initSkirmish(ObjType missionType, Dungeon dungeon) {
        // choose various templates... a special window or sequence of
        // ask()s?
        skirmish = new Skirmish(missionType, dungeon);
    }

    public static boolean isSkirmish() {
        return DC_Game.game.getGameType() == GAME_TYPE.SKIRMISH;
    }

    public static Skirmish getSkirmish() {
        return skirmish;
    }

    public enum SKIRMISH_TYPE {
        ARENA,
    }

    public enum SKIRMISH {
        Blood_and_Sand, Feat_of_Arms, For_the_King, // take up arms and protect
        // the All-King, Lord of the
        // Western Realms
        Dust_to_Dust,
        Leading_the_Charge,
        Last_Stand,
        Rescue_Mission,
        Through_Enemy_Lines, //
        // enemy commander, careless and arrogant, has sent all troops in
        // frontal assault, leaving himself relatively exposed... now is the
        // time for heroes to strike!
        Slaying_the_Beast, // claim the underworld for yourself
        Calling_the_Devil, // slay the lords of oblivion and unleash the
        // all-consuming wrath of the Archfiend onto the
        // nether realm!
        Facing_the_Shadows, // through the dark mirror and onto bright shadow
        Orc_King,
        Street_Ambush, // don't let the merchant prince escape! - mission
        // objective data... synced with level file

        ;
        private int minLvl;
        private int maxLvl;
        private DIFFICULTY difficultyDefault;
        private String levelPath;
        private String missionTypeName;
        private NEMESIS_GROUP[] nemesis_groups;

        SKIRMISH() {

        }

        SKIRMISH(int minLvl, int maxLvl, DIFFICULTY difficultyDefault, String levelPath,
                 String missionTypeName, NEMESIS_GROUP... nemesis_groups) {
            this.minLvl = minLvl;
            this.maxLvl = maxLvl;
            this.difficultyDefault = difficultyDefault;
            this.levelPath = levelPath;
            this.missionTypeName = missionTypeName;
            this.nemesis_groups = nemesis_groups;
            // rpg-limitations here too
            // can be non-NG or replace preset encounter with NG ones
        }

        public int getMinLvl() {
            return minLvl;
        }

        public int getMaxLvl() {
            return maxLvl;
        }

        public DIFFICULTY getDifficultyDefault() {
            return difficultyDefault;
        }

        public String getlevelPath() {
            return levelPath;
        }

        public String getMissionTypeName() {
            return missionTypeName;
        }

        public NEMESIS_GROUP[] getNemesis_groups() {
            return nemesis_groups;
        }
    }

    public enum NEMESIS_GROUP {
        // TODO filtering property for Encounter objects!
        RAVENGUARD,
        GOBLINS,
        ORCS,
        ABYSSAL_DEMONS,
        OBLIVION_DEMONS,
        FIENDS,
        WARP_SPAWN,
        SPIDERS,
        COLONY_BROOD,
        MUTANTS,
        MAGIC_BEASTS,
        ARCANE_CONSTRUCTS,
        MECHANICUM,
    }

}

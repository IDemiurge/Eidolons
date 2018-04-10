package eidolons.game.battlecraft.logic.meta.skirmish;

import eidolons.game.battlecraft.logic.battle.arena.Wave;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonBuilder;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.game.DC_Game.GAME_TYPE;
import main.content.DC_TYPE;
import main.content.enums.GenericEnums.DIFFICULTY;
import main.data.DataManager;
import main.data.XLinkedMap;
import main.data.xml.XML_Converter;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.swing.generic.components.G_Panel.VISUALS;
import main.swing.generic.components.misc.GraphicComponent;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.images.ImageManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
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

    public enum SKIRMISH_TYPE {
        ARENA,
    }

}

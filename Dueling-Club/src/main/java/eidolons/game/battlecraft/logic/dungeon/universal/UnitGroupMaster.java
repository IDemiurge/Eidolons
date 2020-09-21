package eidolons.game.battlecraft.logic.dungeon.universal;

import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.DC_ObjInitializer;
import eidolons.game.module.herocreator.logic.UnitLevelManager;
import eidolons.system.audio.DC_SoundMaster;
import main.content.CONTENT_CONSTS.FLIP;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.content.enums.system.MetaEnums;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Writer;
import main.entity.Entity;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.logic.battle.player.Player;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.ListMaster;
import main.system.entity.FilterMaster;
import main.system.sound.AudioEnums;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UnitGroupMaster {

    public static final int maxY = 3;
    public static final int maxX = 3;
    public static final Integer LEADER_REQUIRED = 10;
    public static final String TYPE_LEVEL_SEPARATOR = "_";
    public static final java.lang.String UNIT_SEPARATOR = "=";
    public static final java.lang.String PAIR_SEPARATOR = ";";
    public static boolean factionLeaderRequired = false;
    public static boolean factionMode = false;
    private static boolean mirror;
    private static FLIP flip;
    private static int currentGroupHeight;
    private static int currentGroupWidth;
    private static Map<Player, Map<ObjType, Integer>> levelMap;
    private static Integer remainingPower;
    private static int power;
    private static int limit;
    private static int min;
    private static int max;
    private static int power_limit;
    private static List<ObjType> unitList;
    private static Integer powerLevel = 3;
    private static ObjType hero;
    private static Object myHero;
    private static Object enemyHero;

    static {
        // FACTION.THE_COVEN.setDeity(DEITY.WITCH_KING);
        // FACTION.CIRCLE_OF_MAGI.setDeity(DEITY.LORD_OF_MAGIC);
        // FACTION.THE_STRIX.setDeity(DEITY.LORD_OF_MAGIC);
        // FACTION.CELESTIALS.setDeity(DEITY.SKY_QUEEN);
        // FACTION.CHAOS_LEGION.setDeity(DEITY.ANNIHILATOR);
        // FACTION.NIGHT_HAUNTERS.setDeity(DEITY.NIGHT_LORD);
        // FACTION.DEEP_ONES.setDeity(DEITY.UNDERKING);
        // FACTION.TWILIGHT_ORDER.setDeity(DEITY.KEEPER_OF_TWILIGHT);
        // FACTION.PALE_LEGIONS.setDeity(DEITY.WRAITH_GOD);
        // FACTION.INQUISITION.setDeity(DEITY.REDEEMER);
        // FACTION.WOOD_ELVES.setDeity(DEITY.WORLD_TREE);
        // FACTION.DEMONS_OF_ABYSS.setDeity(DEITY.CONSUMER_OF_WORLDS);
        // FACTION.WARP_MINIONS.setDeity(DEITY.QUEEN_OF_LUST);
        // FACTION.SAVAGE_TRIBES.setDeity(DEITY.WARMASTER);
        // FACTION.DWARVEN_CLANS.setDeity(DEITY.ALLFATHER);
        // FACTION.PLAGUE_EATERS.setDeity(DEITY.LORD_OF_DECAY);
    }

    String groupPrefix;

    public static String getRandomReadyGroup(int level) {

        return FileManager.getRandomFilePath(getReadyGroupsPath());

    }

    public static boolean isGroupReady(String name) {
        String readyGroups = "";
        for (File sub : FileManager.getFilesFromDirectory(getReadyGroupsPath(), false)) {
            readyGroups += sub.getName() + ";";
        }
        // TODO preCheck units inside!
        return StringMaster.contains(readyGroups, name);
    }

    private static String getReadyGroupsPath() {
        return PathFinder.getUnitGroupPath() + "ready/";
    }

    public static String getUnitGroupData(String groupName, int level) {
        String suffix = translateLevel(level);
        FileManager.readFile(PathFinder.getUnitGroupPath());

        return groupName;
        // random
    }


    public static ObjType getLeveledType(ObjType type, Player owner, String objData) {
        if (levelMap == null) {
            return type;
        }
        Integer level = levelMap.get(owner).get(type);
        if (level != null) {
            if (level != 0) {
                type = UnitLevelManager.getLeveledType(type, level % 10);
                levelMap.get(owner).put(type, level / 10);
            }
        }
        return type;
    }


    private static String getNewName(String string, int level) {
        return FileManager.getUniqueFileVersion(string + " "
                + StringMaster.wrapInBrackets("" + level), getGroupFilePath(string));
    }

    private static List<ObjAtCoordinate> mapPositions(List<ObjType> list, Entity hero) {
        Unit unit = null;

        // posView.in
        List<Unit> units = new ArrayList<>();
        if (hero != null) {
            if (hero instanceof Unit) {
                unit = (Unit) hero;
            } else {
                unit = (new Unit((ObjType) hero));
            }
            units.add(unit);

        }
        for (ObjType type : list) {
            if (type != null) {
                units.add(new Unit(type));
            }
        }

        DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.OK);
        return ListMaster.toObjAtCoordinate(units);
    }

    public static void copyUnitGroup() {
        // TODO Auto-generated method stub

    }

    private static String saveGroup(List<ObjAtCoordinate> units, String faction, String name) {
        StringBuilder contentBuilder = new StringBuilder();
        for (ObjAtCoordinate obj : units) {
            contentBuilder.append(DC_ObjInitializer.getObjString(obj)).append(DC_ObjInitializer.OBJ_SEPARATOR);
        }
        String content = contentBuilder.toString();
        XML_Writer.write(content, getGroupFilePath(faction), name + ".xml");

        return content;
    }

    private static String getFactionPath(Entity faction) {
        return getGroupFilePath(faction.getName());
    }

    private static String getGroupFilePath(String faction) {
        return PathFinder.getUnitGroupPath() + "/faction/" + "" + "" + faction;
    }

    protected static Integer getRemainingPower() {
        return limit - power;
    }


    private static int getUnitCost(ObjType unit, ObjType factionType) {
        int costMod = 100;
        //        if (!factionType.getProperty(PROPS.UNIT_POOL).contains(unit.getName())) {
        //            costMod += 10;
        //            for (String f : ContainerUtils
        //             .openContainer(factionType.getProperty(PROPS.ALLY_FACTIONS))) {
        //                costMod += 5;
        //                ObjType allyFactionType = DataManager.getType(f, MACRO_OBJ_TYPES.FACTIONS);
        //                if (allyFactionType.getProperty(PROPS.UNIT_POOL).contains(unit.getName())) {
        //                    break;
        //                }
        //            }
        //        }
        return unit.getIntParam(PARAMS.POWER) * costMod / 100;
    }

    private static int getPowerForLevel(int level) {
        switch (level) {
            case 1:
                return 100;
            case 2:
                return 140;
            case 3:
                return 200;
            case 4:
                return 280;
            case 5:
                return 380;
            case 6:
                return 500;
        }

        return 100 + 20 * (level - 1) + 20 * (level - 1); // TODO
    }

    public static String getGroupForLevel(String groups, int level) {
        String suffix = StringMaster.getLastPart(groups, " ");
        if (NumberUtils.isInteger(suffix)) {
            groups = groups.substring(0, groups.lastIndexOf(" "));
        }
        return groups + " " + translateLevel(level);
    }

    private static String translateLevel(int level) {
        if (level == 0) {
            return "";
        }
        return "" + (level - 1);
    }

    public static int getUnitGroupPower(String groupName) {
        return 0; // TODO
    }

    public static void initUnitGroupLists() {

    }

    public static String chooseGroup(Entity faction, int level) {
        File groupFile = ListChooser.chooseFile(getFactionPath(faction), StringMaster
                .wrapInBrackets("" + level), SELECTION_MODE.SINGLE);
        if (groupFile != null) {
            return groupFile.getPath();
        }
        return null;
    }

    public static String chooseGroup(boolean me) {
        if (factionMode) {
            List<ObjType> available = new ArrayList<>(DataManager.getTypes(MACRO_OBJ_TYPES.FACTIONS));
            // DC_HeroObj
            FilterMaster.filterByProp(available, G_PROPS.WORKSPACE_GROUP.getName(), ""
                    + MetaEnums.WORKSPACE_GROUP.COMPLETE);
            ObjType faction = ListChooser.chooseType_(available, MACRO_OBJ_TYPES.FACTIONS);
            if (factionLeaderRequired) {
                hero = UnitGroupMaster.createGroupLeader(me, faction, powerLevel);
                if (hero == null) {
                    return null;
                }
            }
            return chooseGroup(faction, powerLevel);

        }
        File groupFile = ListChooser.chooseFile(PathFinder.getUnitGroupPath());
        if (groupFile != null) {
            return groupFile.getPath();
        }
        return null;
    }

    public static ObjType createGroupLeader(boolean me, Entity faction, int unitGroupLevel) {
        return null;
        //        List<ObjType> list = DataManager.getTypesSubGroup(DC_TYPE.CHARS, StringMaster.PRESET);
        //        String backgrounds = faction.getProperty(PROPS.HERO_BACKGROUNDS);
        //        FilterMaster.filterOut(list, new NotCondition(new StringComparison(backgrounds,
        //         StringMaster.getValueRef(KEYS.MATCH, G_PROPS.BACKGROUND), false)));
        //        FilterMaster.filterOut(list, new NotCondition(new NumericCondition("2", "abs("
        //         + StringMaster.getValueRef(KEYS.MATCH, PARAMS.LEVEL) + "-" + unitGroupLevel + ")",
        //         false)));
        //        String name = ListChooser.chooseType(DataManager.toStringList(list), DC_TYPE.CHARS);
        //
        //        if (name == null) {
        //            return null;
        //        }
        //        if (me) {
        //            myHero = name;
        //        } else {
        //            enemyHero = name;
        //        }
        //        return DataManager.getType(name, DC_TYPE.CHARS);
    }

    public static boolean isMirror() {
        return mirror;
    }

    public static void setMirror(boolean mirror) {
        UnitGroupMaster.mirror = mirror;
    }

    public static int getCurrentGroupHeight() {
        return currentGroupHeight;
    }

    public static void setCurrentGroupHeight(int currentGroupHeight) {
        UnitGroupMaster.currentGroupHeight = currentGroupHeight;
    }

    public static int getCurrentGroupWidth() {
        return currentGroupWidth;
    }

    public static void setCurrentGroupWidth(int currentGroupWidth) {
        UnitGroupMaster.currentGroupWidth = currentGroupWidth;
    }

    public static boolean isFactionLeaderRequired() {
        return factionLeaderRequired;
    }

    public static void setFactionLeaderRequired(boolean factionLeaderRequired) {
        UnitGroupMaster.factionLeaderRequired = factionLeaderRequired;
    }

    public static FLIP getFlip() {
        return flip;
    }

    public static void setFlip(FLIP flip) {
        UnitGroupMaster.flip = flip;
    }

    public static Map<Player, Map<ObjType, Integer>> getLevelMap() {
        return levelMap;
    }

    public static Integer getPowerLevel() {
        return powerLevel;
    }

    public static void setPowerLevel(Integer powerLevel) {
        UnitGroupMaster.powerLevel = powerLevel;
    }

    public static int getPower() {
        return power;
    }

    public static int getLimit() {
        return limit;
    }

    public static int getMin() {
        return min;
    }

    public static int getMax() {
        return max;
    }

    public static int getPower_limit() {
        return power_limit;
    }

    public static List<ObjType> getUnitList() {
        return unitList;
    }

    public static void setUnitList(List<ObjType> unitList) {
        UnitGroupMaster.unitList = unitList;
    }

    public static String readGroupFile(String path) {
        if (!PathFinder.isFullPath(path)) {
            path = PathFinder.getUnitGroupPath() + "/" + path;
        }
        return FileManager.readFile(path);
    }

    public static int getGroupSizeY(Player owner) {
        return maxY;
    }

    public static int getGroupSizeX(Player owner) {
        return maxX;
    }

    public static boolean isFactionMode() {
        return factionMode;
    }

    public static void setFactionMode(boolean factionMode) {
        UnitGroupMaster.factionMode = factionMode;
    }

    public static String getHeroData(boolean me) {
        return getMiddleCoordinate() + "=" + (me ? myHero : enemyHero) + ",";
    }

    private static Coordinates getMiddleCoordinate() {
        return Coordinates.get(maxX / 2, maxY / 2);
    }


}

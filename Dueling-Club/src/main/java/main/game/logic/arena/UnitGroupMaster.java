package main.game.logic.arena;

import main.client.cc.gui.neo.choice.ChoiceSequence;
import main.client.cc.gui.neo.choice.PositionChoiceView;
import main.client.cc.logic.UnitLevelManager;
import main.client.dc.Launcher;
import main.client.dc.Launcher.VIEWS;
import main.client.dc.MainManager.MAIN_MENU_ITEMS;
import main.client.dc.SequenceManager;
import main.content.CONTENT_CONSTS.DEITY;
import main.content.CONTENT_CONSTS.FLIP;
import main.content.CONTENT_CONSTS.WORKSPACE_GROUP;
import main.content.CONTENT_CONSTS2.FACTION;
import main.content.OBJ_TYPES;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.properties.G_PROPS;
import main.data.DataManager;
import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Writer;
import main.elements.conditions.NotCondition;
import main.elements.conditions.NumericCondition;
import main.elements.conditions.StringComparison;
import main.entity.Entity;
import main.entity.Ref.KEYS;
import main.entity.obj.unit.DC_HeroObj;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;
import main.game.battlefield.Coordinates;
import main.game.battlefield.DC_ObjInitializer;
import main.game.logic.battle.Positioner;
import main.game.player.Player;
import main.swing.components.panels.page.log.WrappedTextComp;
import main.swing.generic.Decorator;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.panels.G_ListPanel;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.entity.FilterMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.StringMaster;
import main.system.launch.CoreEngine;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UnitGroupMaster {

    public static final int maxY = 3;
    public static final int maxX = 3;
    public static final Integer LEADER_REQUIRED = 10;
    public static final String TYPE_LEVEL_SEPARATOR = "_";
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
        // TODO check units inside!
        if (StringMaster.contains(readyGroups, name)) {
            return true;
        }
        return false;
    }

    private static String getReadyGroupsPath() {
        return PathFinder.getUnitGroupPath() + "ready\\";
    }

    public static String getUnitGroupData(String groupName, int level) {
        String suffix = translateLevel(level);
        FileManager.readFile(PathFinder.getUnitGroupPath());

        return groupName;
        // random
    }

    public static void initUnitGroupLevels(String group, String group2, Player p1, Player p2) {
        WaveAssembler wa = new WaveAssembler(null);
        // wa.setForcedPower(forcedPower);

    }

    public static ObjType getLeveledType(ObjType type, Player owner, String objData) {
        if (levelMap == null) {
            return type;
        }
        Integer level = levelMap.get(owner).get(type);
        if (level != null) {
            if (level != 0) {
                type = new UnitLevelManager().getLeveledType(type, level % 10);
                levelMap.get(owner).put(type, level / 10);
            }
        }
        return type;
    }

    public static void modifyFactions() {
        for (FACTION f : FACTION.values()) {
            DEITY deity = f.getDeity();
            ObjType faction = DataManager.getType(f.toString(), OBJ_TYPES.FACTIONS);
            if (faction == null) {
                continue;
            }
            if (deity != null) {
                ObjType deityType = DataManager.getType(deity.name(), OBJ_TYPES.DEITIES);
                for (ObjType unit : DataManager.getTypes(OBJ_TYPES.UNITS)) {
                    if (unit.getProperty(G_PROPS.DEITY).equals(deityType.getName())) {
                        faction.addProperty(PROPS.UNIT_POOL, unit.getName(), true);
                    }
                }
            }
            // List<String> list =
            // StringMaster.openContainer(faction.getProperty(PROPS.UNIT_POOL));
            // SortMaster.sortByValue(list, PARAMS.POWER, OBJ_TYPES.FACTIONS,
            // false);

        }

    }

    public static String createUnitGroup(DC_HeroObj her0) {
        if (her0 != null) {
            hero = her0.getType();
        }
        return createUnitGroup();
    }

    public static String createUnitGroup() {
        int level = hero == null ? DialogMaster.inputInt("Power Level? (1-6)", getPowerLevel())
                : (hero.getLevel()) / 2;

        ObjType factionType;
        //
        if (hero == null) {
            String faction = ListChooser.chooseEnum(FACTION.class);
            factionType = DataManager.getType(faction.toString().replace(";", ""),
                    OBJ_TYPES.FACTIONS);
        } else {
            List<String> list = new LinkedList<>();

            factionType = DataManager.getType(ListChooser.chooseType(list, OBJ_TYPES.FACTIONS),
                    OBJ_TYPES.FACTIONS);
        }
        return createUnitGroup(hero, factionType, level);
    }

    public static String createUnitGroup(Entity hero, ObjType factionType, int level) {
        power = 0;
        limit = getPowerForLevel(level);
        if (CoreEngine.isArcaneVault()) {
            limit += limit / 5;
        }
        min = 2;
        max = 5 + level / 2;
        power_limit = limit / 2;
        unitList = (new LinkedList<>());
        int i = 0;
        while (true) {
            ObjType unit = chooseUnit(factionType, limit - power, power_limit);
            if (unit == null)

            {
                if (i < min) {
                    if (!DialogMaster.confirm("You must select at least " + (min - i)
                            + " more units, proceed or abort?")) {
                        return null;
                    }
                    continue;
                }
                if (i >= max) {
                    if (DialogMaster.confirm("You have selected maximum number of units "
                            + (max - i) + " do you wish to proceed? Power points remaining: "
                            + getRemainingPower())) {
                        break;
                    }
                    ObjType last = getUnitList().get(getUnitList().size() - 1);
                    if (!DialogMaster.confirm("Do you wish to remove last - " + last.getName()
                            + "?")) {
                        break;
                    }
                    getUnitList().remove(last);
                    power += getUnitCost(last, factionType);
                    continue;
                }
                break;
            }
            i++;
            power += getUnitCost(unit, factionType);
            getUnitList().add(unit);
        }
        String name = DialogMaster.inputText("Enter a name for this group", getNewName(factionType
                .getName(), level));
        List<ObjAtCoordinate> units = mapPositions(getUnitList(), hero);
        if (CoreEngine.isArcaneVault()) {
            name = "std " + name;
        }
        return saveGroup(units, factionType.getName(), name);
    }

    private static String getNewName(String string, int level) {
        return FileManager.getUniqueFileVersion(string + " "
                + StringMaster.wrapInBraces("" + level), getGroupFilePath(string));
    }

    private static List<ObjAtCoordinate> mapPositions(List<ObjType> list, Entity hero) {
        ChoiceSequence sequence = new ChoiceSequence();
        DC_HeroObj unit = null;

        // posView.in
        List<DC_HeroObj> units = new LinkedList<>();
        if (hero != null) {
            if (hero instanceof DC_HeroObj) {
                unit = (DC_HeroObj) hero;
            } else {
                unit = (new DC_HeroObj((ObjType) hero));
            }
            units.add(unit);

        }
        for (ObjType type : list) {
            if (type != null) {
                units.add(new DC_HeroObj(type));
            }
        }
        PositionChoiceView posView = new PositionChoiceView(sequence, unit) {
            @Override
            protected boolean isReady() {
                return false;
            }
        };
        sequence.addView(posView);
        Map<DC_HeroObj, Coordinates> map = new Positioner().getPartyCoordinates(units);
        posView.setPartyCoordinates(map);
        sequence.setManager(new SequenceManager() {

            @Override
            public void doneSelection() {
                WaitMaster.receiveInput(WAIT_OPERATIONS.CUSTOM_SELECT, true);

            }

            @Override
            public void cancelSelection() {
                WaitMaster.receiveInput(WAIT_OPERATIONS.CUSTOM_SELECT, false);

            }
        });
        sequence.start();
        Launcher.setView(VIEWS.CHOICE);
        boolean result = (boolean) WaitMaster.waitForInput(WAIT_OPERATIONS.CUSTOM_SELECT);
        Launcher.initMenu(MAIN_MENU_ITEMS.FACTION);
        Launcher.setView(VIEWS.MENU);
        if (!result) {
            return null;
        }
        SoundMaster.playStandardSound(STD_SOUNDS.OK);
        return ListMaster.toObjAtCoordinate(units);
    }

    public static void copyUnitGroup() {
        // TODO Auto-generated method stub

    }

    private static String saveGroup(List<ObjAtCoordinate> units, String faction, String name) {
        String content = "";
        for (ObjAtCoordinate obj : units) {
            content += DC_ObjInitializer.getObjString(obj) + DC_ObjInitializer.OBJ_SEPARATOR;
        }
        XML_Writer.write(content, getGroupFilePath(faction), name + ".xml");

        return content;
    }

    private static String getFactionPath(Entity faction) {
        return getGroupFilePath(faction.getName());
    }

    private static String getGroupFilePath(String faction) {
        return PathFinder.getUnitGroupPath() + "\\faction\\" + "" + "" + faction.toString();
    }

    private static ObjType chooseUnit(ObjType factionType, int power, int max_power) {
        Map<ObjType, Integer> pool = initPool(factionType);
        List<ObjType> available = new LinkedList<>();
        for (ObjType l : pool.keySet()) {
            if (getUnitCost(l, factionType) > power) {
                continue;
            }
            if (l.getIntParam(PARAMS.POWER) > max_power) {
                continue;
            }

            available.add(l);
        }
        if (available.isEmpty()) {
            return null;
        }
        // chooseUnit();
        Map<String, String> map = new HashMap<>();
        for (ObjType type : pool.keySet()) {
            map.put(type.getName(), "Cost: " + pool.get(type) + "; Power: "
                    + type.getIntParam(PARAMS.POWER));
        }
        ListChooser.setDecorator(new Decorator() {
            @Override
            public void addComponents(G_Panel panel) {
                G_ListPanel<ObjType> list =
                        // new CustomList<>(getUnitList());
                        new G_ListPanel<ObjType>(getUnitList()) {
                            @Override
                            public void setInts() {
                                wrap = 1;
                                minItems = max;
                                rowsVisible = 1;
                                layoutOrientation = JList.HORIZONTAL_WRAP;

                            }

                            @Override
                            public boolean isVertical() {
                                return false;
                            }
                        };
                list.setPanelSize(new Dimension(300, 120));
                panel.add(list, "pos tp.x decor_info.y2");
                WrappedTextComp textComp = new WrappedTextComp(null) {
                    @Override
                    protected Color getColor() {
                        return Color.black;
                    }

                    @Override
                    public synchronized List<String> getTextLines() {
                        List<String> list = new LinkedList<>();
                        list.add(UnitGroupMaster.getRemainingPower() + " points remaining");
                        list.add("Size contraints: between " + min + " and " + max);
                        return list;
                    }
                };
                textComp.setDefaultSize(new Dimension(300, 120));
                panel.add(textComp, "id decor_info, pos tp.x tp.y2");

            }
        });
        ListChooser.setTooltipMapForComponent(map);
        ListChooser.setSortingDisabled(true);
        return ListChooser.chooseType_(available, OBJ_TYPES.UNITS);

        // gui - a list per ally
    }

    protected static Integer getRemainingPower() {
        return limit - power;
    }

    private static Map<ObjType, Integer> initPool(ObjType factionType) {
        Map<ObjType, Integer> map = new XLinkedMap<>();
        addUnits(factionType, map, factionType);
        for (String f : StringMaster.openContainer(factionType.getProperty(PROPS.ALLY_FACTIONS))) {
            ObjType allyFactionType = DataManager.getType(f.toString(), OBJ_TYPES.FACTIONS);
            addUnits(factionType, map, allyFactionType);
        }
        return map;
    }

    private static void addUnits(ObjType factionType, Map<ObjType, Integer> map,
                                 ObjType allyFactionType) {
        for (String unit : StringMaster.openContainer(allyFactionType.getProperty(PROPS.UNIT_POOL))) {
            ObjType type = DataManager.getType(unit, OBJ_TYPES.UNITS);
            map.put(type, getUnitCost(type, factionType));
        }
    }

    private static int getUnitCost(ObjType unit, ObjType factionType) {
        int costMod = 100;
        if (!factionType.getProperty(PROPS.UNIT_POOL).contains(unit.getName())) {
            costMod += 10;
            for (String f : StringMaster
                    .openContainer(factionType.getProperty(PROPS.ALLY_FACTIONS))) {
                costMod += 5;
                ObjType allyFactionType = DataManager.getType(f.toString(), OBJ_TYPES.FACTIONS);
                if (allyFactionType.getProperty(PROPS.UNIT_POOL).contains(unit.getName())) {
                    break;
                }
            }
        }
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
        if (StringMaster.isInteger(suffix)) {
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
                .wrapInBraces("" + level));
        if (groupFile != null) {
            return groupFile.getPath();
        }
        return null;
    }

    public static String chooseGroup(boolean me) {
        if (factionMode) {
            List<ObjType> available = new LinkedList<>(DataManager.getTypes(OBJ_TYPES.FACTIONS));
            // DC_HeroObj
            FilterMaster.filterByProp(available, G_PROPS.WORKSPACE_GROUP.getName(), ""
                    + WORKSPACE_GROUP.COMPLETE);
            ObjType faction = ListChooser.chooseType_(available, OBJ_TYPES.FACTIONS);
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
        // type =
        List<ObjType> list = DataManager.getTypesSubGroup(OBJ_TYPES.CHARS, StringMaster.PRESET);
        String backgrounds = faction.getProperty(PROPS.HERO_BACKGROUNDS);
        FilterMaster.filterOut(list, new NotCondition(new StringComparison(backgrounds,
                StringMaster.getValueRef(KEYS.MATCH, G_PROPS.BACKGROUND), false)));
        FilterMaster.filterOut(list, new NotCondition(new NumericCondition("2", "abs("
                + StringMaster.getValueRef(KEYS.MATCH, PARAMS.LEVEL) + "-" + unitGroupLevel + ")",
                false)));
        String name = ListChooser.chooseType(DataManager.toStringList(list), OBJ_TYPES.CHARS);

        if (name == null) {
            return null;
        }
        if (me) {
            myHero = name;
        } else {
            enemyHero = name;
        }
        return DataManager.getType(name, OBJ_TYPES.CHARS);
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
            path = PathFinder.getUnitGroupPath() + "\\" + path;
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
        return new Coordinates(maxX / 2, maxY / 2);
    }

    public static Object getMyHero() {
        return myHero;
    }

    public static void setMyHero(Object myHero) {
        UnitGroupMaster.myHero = myHero;
    }

    public static Object getEnemyHero() {
        return enemyHero;
    }

    public static void setEnemyHero(Object enemyHero) {
        UnitGroupMaster.enemyHero = enemyHero;
    }

    public String getGroupPrefix() {
        return groupPrefix;
    }

}

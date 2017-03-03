package main.game.logic.dungeon;

import main.content.DC_TYPE;
import main.content.PROPS;
import main.content.enums.DungeonEnums.SUBDUNGEON_TYPE;
import main.content.enums.GenericEnums;
import main.content.values.properties.MACRO_PROPS;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.data.filesys.PathFinder;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.DIRECTION;
import main.game.battlefield.CoordinatesMaster;
import main.game.battlefield.DirectionMaster;
import main.game.battlefield.map.DungeonMapGenerator;
import main.game.logic.dungeon.DungeonLevelMaster.ENTRANCE_LAYOUT;
import main.game.logic.dungeon.building.DungeonBuilder;
import main.game.logic.dungeon.building.DungeonPlan;
import main.game.logic.dungeon.scenario.Scenario;
import main.game.logic.macro.map.Place;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.secondary.BooleanMaster;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Location {
    private static final DIRECTION FLIP_X_EDGE = DIRECTION.RIGHT;
    private static final DIRECTION FLIP_Y_EDGE = DIRECTION.UP;
    private Dungeon root;
    private int maxLevel;
    private boolean up;
    private Dungeon dungeon;
    private Dungeon bossLevel;
    private Place place;
    private Scenario scenario;

    // public Location(Mission mission) {
    // //difficulty/length
    // //MISSION_TYPE.BOSS
    // // missionMaster
    // // mission.getLevels() ++ random sublevels where? levels meta-data
    // required!
    // }
    public Location(Place destination) {
        this.setPlace(destination);
        maxLevel = 10
        // + destination.getGame().getDifficulty().getModifier()/100*
        // TimeMaster.getTimeFactor()/100*
        // destination.getLocationType().getModifier()/100
        ;
    }

    public Location(Scenario scenario) {
        this.scenario = scenario;
    }

    public Dungeon construct() {
        // prefDepth = place.getIntParam(param);
        initRootLevel();
        initBossLevel();
        if (bossLevel != null) {
            up = bossLevel.checkBool(GenericEnums.STD_BOOLS.UPWARD_Z);
        }
        // List<Dungeon> list = new LinkedList<>();
        // constructSublevels();
        return root;
    }

    private void initBossLevel() {
        if (isScenario()) {
            String data = FileManager.readFile(PathFinder.getDungeonLevelFolder()
                    + getPlaceOrScenario().getProperty(MACRO_PROPS.BOSS_LEVEL));
            if (data.isEmpty()) {
                return;
            }
            DungeonPlan plan = new DungeonBuilder().loadDungeonMap(data);
            bossLevel = plan.getDungeon();
            return;
        } else {
            ObjType type = RandomWizard.getObjTypeByWeight(getPlaceOrScenario().getProperty(
                    MACRO_PROPS.BOSS_LEVEL_POOL), DC_TYPE.DUNGEONS);
            bossLevel = new Dungeon(type);
        }

        // new DungeonMapGenerator().generateMap(bossLevel); ???
    }

    private void initRootLevel() {
        // TODO entrance fit?
        if (isScenario()) {
            String data = FileManager.readFile(PathFinder.getDungeonLevelFolder()
                    + getPlaceOrScenario().getProperty(MACRO_PROPS.ROOT_LEVEL));
            DungeonPlan plan = new DungeonBuilder().loadDungeonMap(data);
            root = plan.getDungeon();
            return;
        } else {
            ObjType type = RandomWizard.getObjTypeByWeight(getPlaceOrScenario().getProperty(
                    MACRO_PROPS.ROOT_POOL), DC_TYPE.DUNGEONS);
            root = new Dungeon(type);
        }
    }

    private boolean isScenario() {
        return place == null && scenario != null;
    }

    private void constructSublevels() {
        dungeon = bossLevel;
        int z = dungeon.getZ();
        ENTRANCE_LAYOUT entranceLayout = getFirstEntranceLayout(root, DirectionMaster
                .getRandomDirection());

        if (isScenario()) {
            // Map<SUBDUNGEON_TYPE, String> map = new
            // RandomWizard<SUBDUNGEON_TYPE>()
            // .constructStringWeightMap(scenario.getProperty(MACRO_PROPS.RANDOM_SUBLEVELS),
            // SUBDUNGEON_TYPE.class);
            // e.g. "cave(2);dungeon(3);caste(1); ++ insert *BETWEEN* preset

            // string - lvlN(n) - which could be key?.. perhaps just a list of
            // strings!
            // new RandomWizard<>().constructStringWeightMapInversed(scenario
            // .getProperty(MACRO_PROPS.RANDOM_SUBLEVELS), SUBDUNGEON_TYPE)
            List<String> list = new LinkedList<>();
            for (String stretch : StringMaster.openContainer(scenario
                    .getProperty(MACRO_PROPS.RANDOM_SUBLEVELS))) {
                int i = 0;
                if (StringMaster.isInteger(stretch)) {
                    // for (int i = 0; i < StringMaster.getInteger(stretch);
                    // i++) {
                    // dungeon = dungeon.getSubLevels().getOrCreate(0); // TODO unsafe
                    // }
                    i = StringMaster.getInteger(stretch);
                    continue;
                }
                String typeName;
                if (stretch.contains(StringMaster.OR)) {
                    List<String> names = StringMaster.openContainer(stretch, StringMaster.OR);
                    stretch = names.get(RandomWizard.getRandomListIndex(names));
                }
                typeName = VariableManager.removeVarPart(stretch);
                int depth = (StringMaster.getInteger(VariableManager.getVarPart(stretch)));
                SUBDUNGEON_TYPE type = new EnumMaster<SUBDUNGEON_TYPE>().retrieveEnumConst(
                        SUBDUNGEON_TYPE.class, typeName);

                String element = i + "=" + typeName + StringMaster.wrapInParenthesis("" + depth);
                list.add(element);
                // depth = Math.max(1, depth);

            }
            Dungeon dungeon = root;
            for (String stretch : StringMaster.openContainer(scenario.getProperty(PROPS.SUBLEVELS))) {

                // create a map for random sublevels... lvlN=dngType(n)
                // inverse... find... generate... link

                for (String s : list) {
                    int i = StringMaster.getInteger(s.split("=")[0]);

                    VariableManager.removeVarPart((s.split("=")[1]));
                    VariableManager.getVarPart((s.split("=")[1]));
                }
            }


            String stretch = null;
            int depth = RandomWizard.getRandomInt(StringMaster.getInteger(VariableManager
                    .getVarPart(stretch)));
            for (int level = 0; level < depth; level++) {
                z = (up) ? z++ : z--;
                String typeName = null;
                File randomFile = FileManager.getRandomFile(PathFinder.getDungeonLevelFolder()
                        + typeName);
                // filter?
                String data = FileManager.readFile(randomFile);
                DungeonPlan plan = new DungeonBuilder().loadDungeonMap(data);
                ObjType dungeonType = null;

                newSublevel(dungeon, dungeonType, z, level, entranceLayout);
                // adjust plan , set z
            }

        } else {

            int i = maxLevel;
            List<ObjType> typePool = DataManager.toTypeList(getPlaceOrScenario()
                    .getProperty(MACRO_PROPS.SUBLEVEL_POOL), DC_TYPE.DUNGEONS);
            Map<Integer, ObjType> map = distributeLevels(typePool, maxLevel);
            ObjType prevType = null;
            int level = 0;
            while (true) {
                i--;
                ObjType type = map.get(i);

                if (type != prevType) {
                    level = 0;
                }
                z = (up) ? z++ : z--;
                level++;
                entranceLayout = getNextEntranceLayout(entranceLayout);
                dungeon = newSublevel(dungeon, type, z, level, entranceLayout);
                // checkAddSecretLevels(dungeon);
                if (i <= 0) {
                    break;
                }
                prevType = type;
            }
        }
        dungeon.getSubLevels().add(bossLevel);
    }

    // TODO FILTER BY CHOSEN SUBDUNGEON TYPE!
    // for (String group : StringMaster.openContainer(place
    // .getProperty(MACRO_PROPS.SUBLEVEL_GROUP_POOL))) {
    // typePool.addAll(DataManager.getTypesSubGroup(OBJ_TYPES.DUNGEONS,
    // group));
    // }
    private ENTRANCE_LAYOUT getNextEntranceLayout(ENTRANCE_LAYOUT entranceLayout) {
        // TODO Auto-generated method stub
        return null;
    }

    private ENTRANCE_LAYOUT getFirstEntranceLayout(Dungeon surface2, DIRECTION randomDirection) {
        // TODO Auto-generated method stub
        return null;
    }

    private Dungeon newSublevel(Dungeon dungeon, ObjType type, int z, int level,
                                ENTRANCE_LAYOUT entranceLayout) {
        ObjType dungeonType = new ObjType(type);
        if (level != 0) {
            dungeonType.setName(type.getName() + ", Level " + level);
        }
        String group = type.getSubGroupingKey();
        String path = PathFinder.getDungeonLevelFolder() + group + "\\";

        File file = FileManager.getRandomFile(path);
        String data = FileManager.readFile(file);
        DungeonPlan plan = new DungeonBuilder().loadDungeonMap(data);
        adjustPlanToEntrance(plan, entranceLayout, file.getName());

        new DungeonBuilder().transformDungeonPlan(plan);

        Dungeon sublevel = new Dungeon(dungeonType);
        sublevel.setPlan(plan);
        sublevel.setSublevel(true);
        String entranceData = DungeonLevelMaster.getEntranceData(sublevel, entranceLayout);
        sublevel.setEntranceData(entranceData);
        new DungeonMapGenerator().generateMap(sublevel);
        dungeon.getSubLevels().add(sublevel);
        return sublevel;
    }

    private void adjustPlanToEntrance(DungeonPlan plan, ENTRANCE_LAYOUT requiredEntranceLayout,
                                      String fileName) {
        ENTRANCE_LAYOUT entranceLayout = DungeonLevelMaster.getEnterLayout(fileName);
        Boolean flipX = null;
        Boolean flipY = null;
        Boolean rotate = null;
        if (entranceLayout != requiredEntranceLayout) {
            transformLoop:
            while (true) {
                // Rotate
                for (Boolean b : BooleanMaster.FALSE_TRUE_NULL) {
                    flipX = b;
                    for (Boolean b1 : BooleanMaster.FALSE_TRUE_NULL) {
                        flipY = b1;
                        entranceLayout = DungeonLevelMaster.transformLayout(entranceLayout, flipX,
                                flipY, rotate);
                        if (entranceLayout == requiredEntranceLayout) {
                            break transformLoop;
                        }
                    }
                }
                break;
            }
        }
        ENTRANCE_LAYOUT exitLayout = DungeonLevelMaster.getExitLayout(fileName);
        exitLayout = DungeonLevelMaster.transformLayout(entranceLayout, flipX, flipY, rotate);
        plan.setExitLayout(exitLayout);
        plan.setEntranceLayout(entranceLayout);

        plan.setFlippedX(flipX);
        plan.setFlippedY(flipY);
        plan.setRotated(rotate);
    }

    private Map<Integer, ObjType> distributeLevels(List<ObjType> typePool, int maxLevel) {
        Map<ObjType, Integer> invmap = new HashMap<>();
        Map<Integer, ObjType> map = new HashMap<>();
        int totalDepth = 0;
        int prefLevelTypeNumber = Math.max(1, maxLevel / 3 + typePool.size() / 10);
        while (true) {
            ObjType type = new RandomWizard<ObjType>().getRandomListItem(typePool);
            // type.getIntParam(min/max);
            int minDepth = Math.max(1, prefLevelTypeNumber / 3 * 2);
            int maxDepth = Math.min(maxLevel, prefLevelTypeNumber * 3 / 2);
            int depth = RandomWizard.getRandomIntBetween(minDepth, maxDepth);
            invmap.put(type, depth);
            totalDepth += depth;
            if (totalDepth >= maxLevel) { // ++ adjust
                break;
            }
        }

        for (ObjType type : invmap.keySet()) {
            int depth = 0;
            for (int i = 0; i < invmap.get(type); depth++) {
                map.put(depth, type);
                i++;
            }

        }

        return map;
    }

    private void initDungeonPlanAdjustments(Coordinates enterCoordinate) {
        if (!CoordinatesMaster.getClosestEdge(enterCoordinate, dungeon.getCellsX(),
                dungeon.getCellsY()).isVertical()) {
            dungeon.setRotated(true);
        }
        if (isFlipX(enterCoordinate)) {
            dungeon.setFlippedX(true);
        }
        if (isFlipY(enterCoordinate)) {
            dungeon.setFlippedY(true);
        }
    }

    public boolean isFlipY(Coordinates enterCoordinate) {
        return CoordinatesMaster.getClosestEdgeY(enterCoordinate, dungeon.getCellsX(), dungeon
                .getCellsY()) == FLIP_Y_EDGE;
    }

    public boolean isFlipX(Coordinates enterCoordinate) {
        return CoordinatesMaster.getClosestEdgeX(enterCoordinate, dungeon.getCellsX(), dungeon
                .getCellsY()) == FLIP_X_EDGE;
    }

    //
    // public void constructMission() {
    // //
    // dungeon.getGame().getArenaManager().getSpawnManager().getPositioner().getOrCreate
    // FACING_DIRECTION side = EncounterMaster.getPlayerBfSide();
    // if (side == null)
    // side = Positioner.DEFAULT_PLAYER_SIDE;
    //
    // // sublevel-dungeons - diff. name?
    // // should root have all of them? what for?
    //
    // // root.getEntrances().add(e);
    //
    // // root.generateSublevels(); // this can work for a single sublevel
    // ENTRANCE_POINT_TEMPLATE template;
    // FACING_DIRECTION entranceSide = FacingMaster.rotate180(side);
    // if (entranceSide == FACING_DIRECTION.NONE)
    // entranceSide = FacingMaster.getRandomFacing();
    // Coordinates enterCoordinate = DungeonLevelMaster
    // .getEntranceCoordinates(entranceSide, template, root);
    // List<Dungeon> subLevels = new LinkedList<>();
    // Entrance mainExit = root.getEntrances().getOrCreate(0);
    //
    // distributeDungeonDepth(root, i); // this creates dungeons, now adjust -
    // // entrances, ...
    // for (int i = 0; i < maxLevel; i++) {
    // Dungeon prevDungeon = dungeon;
    // dungeon = getSublevelDungeon(dungeon, i);
    // // determine whether new dungeons are to be made at all
    // if (!dungeon.isALevelOf(prevDungeon)) {
    // int depth = getPreferredLevelDepth(dungeon, i);
    // for (int j = 0; i < depth; j++) {
    //
    // }
    // }
    // // dungeon = last level
    //
    // String entranceData;
    // root.setEntranceData(entranceData); // only after dungeons are
    // // generated!
    //
    // subLevels.add(dungeon);
    // enterCoordinate = getEnterCoordinate(dungeon);
    // initDungeonPlanAdjustments(enterCoordinate);
    // dungeon.setEntranceData(entranceData);
    // // same object as last...
    // List<Entrance> entrances = dungeon.getEntrances();
    // dungeon.setMainEntrance(mainExit);
    // mainExit = entrances.getOrCreate(0);
    //
    // // DungeonLevelMaster.generateEntranceData(dungeon)
    //
    // dungeon.setProperty(PROPS.SUBLEVELS, string);
    //
    // }
    // root.setSubLevels(subLevels);
    // }
    //
    // private Map<Dungeon, Integer> distributeDungeonDepth(Dungeon root, int i)
    // {
    // //TODO PRESET DUNGEONS - FROM STRING
    // //
    // Map<Dungeon, Integer> map = new XLinkedMap();
    // root.getProperty(PROPS.SUBLEVELS);
    // //weight string?
    // while (i>0){
    // dungeon = newDungeon(dungeon);
    //
    //
    // }
    // int depth = i-2;
    //
    // root.getProperty(G_PROPS.DUNGEON_GROUP);
    // Dungeon bossDungeon;
    // Dungeon prebossDungeon;
    // Integer maxDepth = root.getIntParam(PARAMS.MAX_DEPTH);
    // Integer minDepth = root.getIntParam(PARAMS.MIN_DEPTH);
    // if (i==2){
    // //no preboss?
    // }
    // //recursive - getOrCreate sublevels from next sublevel? step by step I mean...
    // reserve depth for boss/preboss
    // typeMap = new
    // RandomWizard<ObjType>().constructWeightMap(root.getProperty(PROPS.SUBLEVELS),
    // ObjType.class);
    // FilterMaster.filterByProp(typeMap.keySet(), "dungeon type", "boss");
    // new RandomWizard<ObjType>().getObjectByWeight(string, CLASS)
    // bossDungeon.setBoss(true);
    //
    // map.put(root , depth);
    // // map.put(root.getType(),i-2-depth);
    // map.put(prebossDungeon, 1);
    // map.put(bossDungeon, 1);
    //
    // return map;
    //
    // }
    //
    // private Dungeon getSublevelDungeon(Place place, Dungeon dungeon, int i) {
    // if ()
    // return new Dungeon(dungeon, depth);
    //
    // ObjType type = new RandomWizard<ObjType>().getObjectByWeight(
    // root.getProperty(PROPS.SUBLEVELS), ObjType.class);
    // if (type == null) {
    // // getOrCreate by group
    // }
    // Dungeon sublevel = new Dungeon(type, true);
    // sublevel.setZ(dungeon.getZ() - 1);
    // return sublevel;
    // }
    //
    // private int getPreferredLevelDepth(Dungeon dungeon2) {
    // // TODO Auto-generated method stub
    // return 1;
    // }
    //

    public Dungeon getBossLevel() {
        // TODO Auto-generated method stub
        return null;
    }

    public Entity getPlaceOrScenario() {
        if (scenario != null) {
            return scenario;
        }
        return place;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

}

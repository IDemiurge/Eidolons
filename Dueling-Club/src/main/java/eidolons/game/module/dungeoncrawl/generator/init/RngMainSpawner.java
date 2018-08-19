package eidolons.game.module.dungeoncrawl.generator.init;

import eidolons.content.PARAMS;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.LEVEL_VALUES;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.model.AbstractCoordinates;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileConverter.DUNGEON_STYLE;
import main.content.DC_TYPE;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.enums.entity.UnitEnums.UNIT_GROUP;
import main.data.DataManager;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.SortMaster;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.datatypes.WeightMap;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 8/1/2018.
 */
public class RngMainSpawner {

    private static final float SINGLE_UNIT_BONUS_COEF = 1.3f;
    Map<LevelBlock, Float> coefMap = new LinkedHashMap<>();
    private DungeonLevel level;
    private LevelData data;

    public static UNIT_GROUP getUnitGroup(
     LOCATION_TYPE locationType, LevelZone zone,
     SPAWN_GROUP_TYPE groupType) {
        if (zone.getUnitGroupWeightMap() != null) {
            return zone.getUnitGroupWeightMap().getRandomByWeight();
        }
        //2 unit grops per zone?
        boolean surface = locationType.isSurface();
        WeightMap<UNIT_GROUP> map = new WeightMap<>();
        zone.setUnitGroupWeightMap(map);
        int n = getMaxGroupsForType(groupType);
        for (int i = 0; i < n; i++) {
            UNIT_GROUP group = getUnitGroup(surface, zone.getStyle());
            map.put(group, i + 1);
        }
        return map.getRandomByWeight();
    }

    private static int getMaxGroupsForType(SPAWN_GROUP_TYPE groupType) {
        return 2;
    }

    public static UNIT_GROUP getUnitGroup(
     boolean surface, DUNGEON_STYLE style) {
        switch (style) {
            case PureEvil:
                return surface ?
                 new WeightMap<>(UNIT_GROUP.class)
                  .chain(UNIT_GROUP.UNDEAD, 10)
                  .chain(UNIT_GROUP.DEMONS_WARPED, 10)
                  .chain(UNIT_GROUP.UNDEAD_PLAGUE, 7)
                  .chain(UNIT_GROUP.UNDEAD_CRIMSON, 9)
                  .chain(UNIT_GROUP.UNDEAD_WRAITH, 7)
                  .chain(UNIT_GROUP.DARK_ONES, 8)
                  .chain(UNIT_GROUP.CULT_DEATH, 5)
                  .chain(UNIT_GROUP.CULT_DARK, 10)
                  .getRandomByWeight()
                 :
                 new WeightMap<>(UNIT_GROUP.class)
                  .chain(UNIT_GROUP.DEMONS_ABYSS, 10)
                  .chain(UNIT_GROUP.DEMONS_HELLFIRE, 10)
                  .chain(UNIT_GROUP.DEMONS_WARPED, 6)
                  .chain(UNIT_GROUP.CULT_DEATH, 7)
                  .chain(UNIT_GROUP.UNDEAD_PLAGUE, 5)
                  .chain(UNIT_GROUP.UNDEAD_CRIMSON, 4)
                  .chain(UNIT_GROUP.CULT_DARK, 5)
                  .getRandomByWeight();
            case Grimy:
                return surface ?
                 new WeightMap<>(UNIT_GROUP.class)
                  .chain(UNIT_GROUP.HUMANS_BANDITS, 10)
                  .chain(UNIT_GROUP.HUMANS_PIRATES, 6)
                  .chain(UNIT_GROUP.PRISONERS, 5)
                  .chain(UNIT_GROUP.ANIMALS, 5)
                  .chain(UNIT_GROUP.MUTANTS, 5)
                  .getRandomByWeight()
                 :
                 new WeightMap<>(UNIT_GROUP.class)
                  .chain(UNIT_GROUP.DWARVES, 15)
                  .chain(UNIT_GROUP.DUNGEON, 10)
                  .chain(UNIT_GROUP.MUTANTS, 6)
                  .chain(UNIT_GROUP.PALE_ORCS, 6)
                  .chain(UNIT_GROUP.CRITTERS, 6)
                  .getRandomByWeight();
            case Pagan:
                return surface ?
                 new WeightMap<>(UNIT_GROUP.class)
                  .chain(UNIT_GROUP.NORTH, 12)
                  .chain(UNIT_GROUP.ANIMALS, 5)
                  .chain(UNIT_GROUP.DWARVES, 5)
                  .chain(UNIT_GROUP.HUMANS_BARBARIANS, 3)
                  .getRandomByWeight()
                 :
                 new WeightMap<>(UNIT_GROUP.class)
                  .chain(UNIT_GROUP.DWARVES, 15)
                  .chain(UNIT_GROUP.DUNGEON, 10)
                  .chain(UNIT_GROUP.ELEMENTALS, 6)
                  .chain(UNIT_GROUP.UNDEAD_WRAITH, 6)
                  .chain(UNIT_GROUP.PALE_ORCS, 5)
                  .getRandomByWeight();
            case Survivor:
                return surface ?
                 new WeightMap<>(UNIT_GROUP.class)
                  .chain(UNIT_GROUP.HUMANS_BANDITS, 10)
                  .chain(UNIT_GROUP.HUMANS_PIRATES, 6)
                  .chain(UNIT_GROUP.PRISONERS, 5)
                  .chain(UNIT_GROUP.ANIMALS, 5)
                  .chain(UNIT_GROUP.MUTANTS, 5)
                  //                  .chain(UNIT_GROUP.BARBARIANS, 3)
                  .getRandomByWeight()
                 :
                 new WeightMap<>(UNIT_GROUP.class)
                  .chain(UNIT_GROUP.DWARVES, 15)
                  .chain(UNIT_GROUP.DUNGEON, 10)
                  .chain(UNIT_GROUP.MUTANTS, 6)
                  .chain(UNIT_GROUP.PALE_ORCS, 6)
                  .chain(UNIT_GROUP.CRITTERS, 6)
                  .getRandomByWeight();
            case Somber:
                return surface ?
                 new WeightMap<>(UNIT_GROUP.class)
                  .chain(UNIT_GROUP.UNDEAD, 10)
                  .chain(UNIT_GROUP.UNDEAD_PLAGUE, 7)
                  .chain(UNIT_GROUP.UNDEAD_CRIMSON, 9)
                  .chain(UNIT_GROUP.UNDEAD_WRAITH, 7)
                  .chain(UNIT_GROUP.DARK_ONES, 8)
                  .chain(UNIT_GROUP.CULT_DEATH, 5)
                  .chain(UNIT_GROUP.CULT_DARK, 10)
                  .chain(UNIT_GROUP.PRISONERS, 4)
                  .getRandomByWeight()
                 :
                 new WeightMap<>(UNIT_GROUP.class)
                  .chain(UNIT_GROUP.UNDEAD, 10)
                  .chain(UNIT_GROUP.CULT_DEATH, 7)
                  .chain(UNIT_GROUP.UNDEAD_PLAGUE, 5)
                  .chain(UNIT_GROUP.UNDEAD_CRIMSON, 4)
                  .chain(UNIT_GROUP.UNDEAD_WRAITH, 5)
                  .chain(UNIT_GROUP.CRITTERS, 5)
                  .chain(UNIT_GROUP.CULT_DARK, 5)
                  .chain(UNIT_GROUP.PRISONERS, 5)
                  .getRandomByWeight();
        }
        return UNIT_GROUP.Ravenguard;
    }

    public void spawn(DungeonLevel level) {
        //via groups/encounters?
        this.level = level;
        this.data = level.getData();
        //some meta data to take from?
        spawnForSymbols();
        spawnGroups();

    }

    private void spawnForSymbols() {
        for (LevelBlock block : level.getBlocks()) {
            Map<Coordinates, ROOM_CELL> map = block.getTileMap().getMap();

            List<Coordinates> filledCells = block.getTileMap().getMap().keySet().stream().filter(
             c -> isSpawnSymbol(map.get(c))).collect(Collectors.toList());
            filledCells.forEach(c -> {
                SPAWN_GROUP_TYPE type=  new EnumMaster<SPAWN_GROUP_TYPE>()
                 .retrieveEnumConst(SPAWN_GROUP_TYPE.class,
                 map.get(c).name());
                List<ObjType> units = getUnitsForGroup(getPowerCoef(block, type), type,
                 getUnitGroup(level.getLocationType(), block.getZone(), type), 3, 1);
                units.forEach(unit -> addUnit(new ObjAtCoordinate(unit, c), block));

            });


        }
    }

    private boolean isSpawnSymbol(ROOM_CELL cell) {
        switch (cell) {
            case PATROL:
            case AMBUSH:
            case CROWD:
            case IDLERS:
            case STALKER:
            case MINI_BOSS:
            case BOSS:
            case GUARDS:
                return true;
        }
        return false;
    }

    private float calculateFill(LevelBlock block) {
        float square = block.getWidth() * block.getHeight();
        List<ObjAtCoordinate> units = block.getUnits();
        return units.size() / square;
    }

    private void spawnGroups() {
        //control power level

        // control fill level

        //N per groupType, or? at least preferred, min/max
        List<LevelBlock> spawned = new ArrayList<>();
        for (LevelZone zone : level.getSubParts()) {
            for (SPAWN_GROUP_TYPE groupType : SPAWN_GROUP_TYPE.values()) {
                List<LevelBlock> spawnedOfType = new ArrayList<>();
                List<LevelBlock> blocks = getBlocksForSpawn(groupType, zone);
                if (blocks.isEmpty())
                    continue;
                int limit = getLimit(groupType, zone, blocks.size());
                blocks = blocks.subList(0, limit);
                if (blocks.size() < limit) {
                    //TODO ?

                }
                blocks.removeAll(spawnedOfType);
                List<LevelBlock> filtered = new ArrayList<>(blocks);
                filtered.removeIf(b -> spawned.contains(b));
                if (!filtered.isEmpty()) {
                    blocks = filtered;
                    //TODO improve...
                }

                UNIT_GROUP group = getUnitGroup(level.getLocationType(), zone, groupType);
                for (LevelBlock block : blocks) {
                    float powerCoef = getPowerCoef(block, groupType);
                    spawnForGroup(block, groupType, group, powerCoef);
                    spawned.add(block);
                    spawnedOfType.add(block);
                }
            }
            //            level.getAiMap().put(c, aiType)
            //            filter weight map?
        }
    }

    private List<LevelBlock> getBlocksForSpawn(SPAWN_GROUP_TYPE groupType, LevelZone zone) {
        return
         level.getBlocks().stream()
          .filter(block -> block.getZone() == zone)
          .filter(block -> isBlockForGroup(block, groupType)).sorted(
          new SortMaster<LevelBlock>().getSorterByExpression_(block ->
           (int) (-100 * calculateFill(block) + RandomWizard.getRandomIntBetween(0, 5))))
          .collect(Collectors.toList());
    }

    private float getPowerCoef(LevelBlock block, SPAWN_GROUP_TYPE groupType) {
        float coef = 1f;
        switch (block.getRoomType()) {
            case THRONE_ROOM:
                coef = 4f;
                break;
            case COMMON_ROOM:
            case TREASURE_ROOM:
                coef = 2f;
                break;
            case DEATH_ROOM:
            case GUARD_ROOM:
                coef = 3f;
                break;
        }
        switch (groupType) {
            case GUARDS:
            case PATROL:
            case AMBUSH:
                coef *= 1.25f;
                break;
            case CROWD:
            case IDLERS:
                coef *= 0.75f;
                break;
            case STALKER:
                coef *= 1.5f;
                break;
            case BOSS:
                break;
        }

        Float random = coefMap.get(block);
        if (random != null)
            return coef * random;

        //check 3 closest rooms
        //peaks of difficulty!
        List<LevelBlock> sorted = coefMap.keySet().stream().sorted(new SortMaster<LevelBlock>().getSorterByExpression_(
         b ->
          -block.getCoordinates().dst(b.getCoordinates())
        )).limit(3).collect(Collectors.toList());

        float min;
        float max;
        if (sorted.isEmpty()) {
            min = 0.35f;
            max = 1f;
        } else {
            float maxClosest = 0f;
            float minClosest = 1f;
            float avrg = 0;
            for (LevelBlock levelBlock : sorted) {
                Float r = coefMap.get(levelBlock);
                if (r > maxClosest)
                    maxClosest = r;
                if (r < minClosest)
                    minClosest = r;
                avrg += r;
            }
            avrg /= sorted.size();

            min = 1 - avrg;
            max = 1 - maxClosest / 4;
        }

        random = RandomWizard.getRandomFloatBetween(min, max);
        coefMap.put(block, random);
        return coef * random;
    }

    private int getLimit(SPAWN_GROUP_TYPE group, LevelZone zone, int size) {
        int max =
         data.getIntValue(LEVEL_VALUES.valueOf("SPAWN_GROUP_COEF_" + group.name())) *
          size / 100;
        return RandomWizard.getRandomIntBetween(max / 2, max * 3 / 2) + 1;
    }

    private boolean isBlockForGroup(LevelBlock block, SPAWN_GROUP_TYPE group) {
        switch (group) {
            case GUARDS:
                return block.getRoomType() == ROOM_TYPE.TREASURE_ROOM
                 || block.getRoomType() == ROOM_TYPE.GUARD_ROOM;
            case PATROL:
                return block.getRoomType() == ROOM_TYPE.CORRIDOR
                 || block.getRoomType() == ROOM_TYPE.GUARD_ROOM;
            case AMBUSH:
                return block.getRoomType() == ROOM_TYPE.CORRIDOR
                 || block.getRoomType() == ROOM_TYPE.DEATH_ROOM;
            case CROWD:
            case IDLERS:
                return block.getRoomType() == ROOM_TYPE.COMMON_ROOM;
            case STALKER:
                return block.getRoomType() == ROOM_TYPE.CORRIDOR;
            case BOSS:
                return block.getRoomType() == ROOM_TYPE.THRONE_ROOM;
        }
        return true;
    }

    private void spawnForGroup(LevelBlock levelBlock, SPAWN_GROUP_TYPE groupType, UNIT_GROUP group, float powerCoef) {
        spawnForGroup(levelBlock, groupType, group, Integer.MAX_VALUE, 1, powerCoef);
    }

    private List<ObjType> getUnitsForGroup(float powerCoef,
                                           SPAWN_GROUP_TYPE groupType,
                                           UNIT_GROUP group, int max, int minPreferred ) {
        int powerLevel = Math.round(level.getPowerLevel() * powerCoef);

        List<ObjType> units = new ArrayList<>();
        Loop loop = new Loop(50 + minPreferred * 10);
        boolean oneFromAboveRank = max >= 6;
        WeightMap<String> map = RngUnitProvider.getWeightMap(group, groupType, false);
        WeightMap<String> altMap = RngUnitProvider.getWeightMap(group, groupType, true);

        while (true) {
            if (loop.ended())
                break;
            String unit = (oneFromAboveRank ? altMap : map).getRandomByWeight();
            ObjType type = DataManager.getType(unit, DC_TYPE.UNITS);

            int pouvoir = type.getIntParam(PARAMS.POWER);
            boolean add = pouvoir <= powerLevel;
            if (add) {
                if (minPreferred > 0) {
                    if (pouvoir > powerLevel / minPreferred)
                        if (RandomWizard.chance(50))
                            add = false; // won't fill the minimum at this rate, don't do that so often
                } else if (max < 0) {
                    if (pouvoir < powerLevel / max) {
                        if (RandomWizard.chance(50))
                            add = false;//will be exceeding the max at this rate, don't do that so often
                    }
                }
            } else {
                if (unit.isEmpty()) {
                    if (pouvoir <= powerLevel * SINGLE_UNIT_BONUS_COEF)
                        if (RandomWizard.chance(50))
                            add = true;
                }
            }
            if (add) {
                powerLevel -= pouvoir;
                units.add(type);
                max--;
                minPreferred--;
                oneFromAboveRank = false;
            }

        }
        return units;
    }

    private void spawnForGroup(LevelBlock levelBlock,
                               SPAWN_GROUP_TYPE groupType,
                               UNIT_GROUP group, int max, int minPreferred, float powerCoef) {
        List<ObjType> units = getUnitsForGroup(powerCoef, groupType, group,
         max, minPreferred);

        List<ObjAtCoordinate> unitsAtCoordinates = spawnUnits(levelBlock, units);
        main.system.auxiliary.log.LogMaster.log(1, groupType + " spawned: "
         + ContainerUtils.toStringContainer(unitsAtCoordinates));
        levelBlock.getAiGroups().add(new ImmutablePair<>(unitsAtCoordinates, groupType));
    }


    private List<ObjAtCoordinate> spawnUnits(LevelBlock levelBlock, List<ObjType> units) {
        //TODO
        int maxStack = 2;
        AbstractCoordinates center = new AbstractCoordinates(levelBlock.getWidth() / 2, levelBlock.getHeight() / 2);
        List<Coordinates> emptyCells = levelBlock.getTileMap().getMap().keySet().stream().filter(
         c ->
          levelBlock.getTileMap().getMap().get(c) == ROOM_CELL.FLOOR).
         sorted(new SortMaster<Coordinates>().getSorterByExpression_(c ->
          c.dst(center))).limit(units.size() * maxStack).
         collect(Collectors.toList());
        List<ObjAtCoordinate> list = new ArrayList<>();
        if (emptyCells.isEmpty())
            return list;
        Iterator<Coordinates> iterator = emptyCells.listIterator();
        for (ObjType unit : units) {
            if (!iterator.hasNext())
                iterator = emptyCells.listIterator();
            Coordinates c = iterator.next();
            ObjAtCoordinate at = new ObjAtCoordinate(unit, c);
            addUnit(at, levelBlock);
            list.add(at);

        }
        return list;
    }

    private void addUnit(ObjAtCoordinate at, LevelBlock levelBlock) {
        level.getUnits().add(at);
        levelBlock.getUnits().add(at);

    }

    /*
    Guard Ai - stand still, only turn this way or that
    > alerted - goes to investigate
    ++ voice comments
    ++ chance to Sleep
    Idlers/Crowd - chaotic wandering within the Block
    > alerted - hold still and enters Alert Mode
    ++ periodic Rest
    Patrol Ai - orderly traversal of the Block
    > alerted - investigate
    Stalker Ai - moves in stealth mode within the Zone, follows enemy until they enter combat, then add to the AggroGroup
    > alerted -
    Boss Ai - walks in small circles
     */
    public enum SPAWN_GROUP_TYPE {
        //this is gonna be used for fill probably too
        GUARDS,
        PATROL,
        AMBUSH,
        CROWD,
        IDLERS,
        STALKER, BOSS,
        //determines what? Except AI behavior -
        // N preference, power level, placement,
    }


}

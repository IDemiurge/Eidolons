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
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.datatypes.WeightMap;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 8/1/2018.
 */
public class RngMainSpawner {

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
            map.put(group, i);
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
                  .chain(UNIT_GROUP.DEATH_CULT, 5)
                  .chain(UNIT_GROUP.DARK_CULT, 10)
                  .getRandomByWeight()
                 :
                 new WeightMap<>(UNIT_GROUP.class)
                  .chain(UNIT_GROUP.DEMONS_ABYSS, 10)
                  .chain(UNIT_GROUP.DEMONS_HELLFIRE, 10)
                  .chain(UNIT_GROUP.DEMONS_WARPED, 6)
                  .chain(UNIT_GROUP.DEATH_CULT, 7)
                  .chain(UNIT_GROUP.UNDEAD_PLAGUE, 5)
                  .chain(UNIT_GROUP.UNDEAD_CRIMSON, 4)
                  .chain(UNIT_GROUP.DARK_CULT, 5)
                  .getRandomByWeight();
            case Grimy:
                return surface ?
                 new WeightMap<>(UNIT_GROUP.class)
                  .chain(UNIT_GROUP.BANDITS, 10)
                  .chain(UNIT_GROUP.PIRATES, 6)
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
                  .chain(UNIT_GROUP.BARBARIANS, 3)
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
                  .chain(UNIT_GROUP.BANDITS, 10)
                  .chain(UNIT_GROUP.PIRATES, 6)
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
                  .chain(UNIT_GROUP.DEATH_CULT, 5)
                  .chain(UNIT_GROUP.DARK_CULT, 10)
                  .chain(UNIT_GROUP.PRISONERS, 4)
                  .getRandomByWeight()
                 :
                 new WeightMap<>(UNIT_GROUP.class)
                  .chain(UNIT_GROUP.UNDEAD, 10)
                  .chain(UNIT_GROUP.DEATH_CULT, 7)
                  .chain(UNIT_GROUP.UNDEAD_PLAGUE, 5)
                  .chain(UNIT_GROUP.UNDEAD_CRIMSON, 4)
                  .chain(UNIT_GROUP.UNDEAD_WRAITH, 5)
                  .chain(UNIT_GROUP.CRITTERS, 5)
                  .chain(UNIT_GROUP.DARK_CULT, 5)
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
        spawnGroups();

    }

    private float calculateFill(LevelBlock block) {
        float square = block.getWidth() * block.getHeight();
        List<ObjAtCoordinate> units = block.getUnits();
        return   units.size() / square;
    }
    private void spawnGroups() {
        //control power level

        // control fill level

        //N per groupType, or? at least preferred, min/max
        List<LevelBlock> spawned = new ArrayList<>();
        for (SPAWN_GROUP_TYPE groupType : SPAWN_GROUP_TYPE.values()) {
            List<LevelBlock> spawnedOfType = new ArrayList<>();
            for (LevelZone zone : level.getSubParts()) {
                int limit = getLimit(groupType, zone);
                List<LevelBlock> blocks =getBlocksForSpawn(groupType, zone, limit );

                if (blocks.size() < limit) {
                //TODO ?

                }
                blocks.removeAll(spawnedOfType);
                List<LevelBlock> filtered = new ArrayList<>(blocks);
                filtered.removeIf(b-> spawned.contains(b));
                if (!filtered.isEmpty()) {
                    blocks=filtered;
                    //TODO improve...
                }

                UNIT_GROUP group = getUnitGroup(level.getLocationType(), zone, groupType);
                for (LevelBlock block : blocks) {
                    float  powerCoef=getPowerCoef(block);
                    spawnForGroup(block, groupType, group, powerCoef);
                    spawned.add(block);
                    spawnedOfType.add(block);
                }
            }
            //            level.getAiMap().put(c, aiType)
            //            filter weight map?
        }
    }

    private List<LevelBlock> getBlocksForSpawn(SPAWN_GROUP_TYPE groupType, LevelZone zone, int limit) {
        return
         level.getBlocks().stream()
          .filter(block-> block.getZone()==zone)
          .filter(block -> isBlockForGroup(block, groupType)).sorted(
          new SortMaster<LevelBlock>().getSorterByExpression_(block->
           (int) (-100*calculateFill(block)+RandomWizard.getRandomIntBetween(0,5))))
          .limit(limit).collect(Collectors.toList());
    }

    private float getPowerCoef(LevelBlock block) {
        float coef = 1f;
//       peaks and slopes
//     Room room =(Room) MapMaster.getKeyForValue_(level.getModel().getBlocks() , block);
//     level.getModel().getGraph().getAdj().get(node);
//        powerMap.put(block, coef);
        return coef;
    }

    private int getLimit(SPAWN_GROUP_TYPE group, LevelZone zone) {
        int max =
         data.getIntValue(LEVEL_VALUES.valueOf("SPAWN_GROUP_COEF_" + group.name())) *
          level.getBlocks().size() / SPAWN_GROUP_TYPE.values().length / 100;
        return RandomWizard.getRandomIntBetween(max / 2, max * 3 / 2);
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

    private void spawnForGroup(LevelBlock levelBlock,
                               SPAWN_GROUP_TYPE groupType,
                               UNIT_GROUP group, int max, int minPreferred, float powerCoef) {
        int powerLevel = Math.round(level.getPowerLevel() * powerCoef);
        List<ObjType> units = new ArrayList<>();
        Loop loop = new Loop(50);
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
            if (add)
                if (minPreferred > 0) {
                    if (pouvoir > powerLevel / minPreferred)
                        add = false;
                } else if (max < 0) {
                    if (pouvoir < powerLevel / max) {
                        if (RandomWizard.chance(80))
                            add = false;
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
        List<ObjAtCoordinate> unitsAtCoordinates = spawnUnits(levelBlock, units);
        main.system.auxiliary.log.LogMaster.log(1,groupType+ " spawned: "
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

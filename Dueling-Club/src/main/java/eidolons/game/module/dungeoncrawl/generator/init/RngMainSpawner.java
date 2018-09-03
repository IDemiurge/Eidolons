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

import java.util.*;
import java.util.stream.Collectors;

import static main.system.auxiliary.log.LogMaster.log;

/**
 * Created by JustMe on 8/1/2018.
 */
public class RngMainSpawner {

    public static final boolean TEST_MODE = false;
    private static final float SINGLE_UNIT_BONUS_COEF = 1.5f;
    private static final int TEST_POWER = 250;
    private static final UNIT_GROUP_TYPE[] MANDATORY_SPAWN_GROUPS = {
     UNIT_GROUP_TYPE.BOSS,
     UNIT_GROUP_TYPE.GUARDS,
     UNIT_GROUP_TYPE.PATROL,
     UNIT_GROUP_TYPE.STALKER,
     UNIT_GROUP_TYPE.AMBUSH,
    };
    private static final UNIT_GROUP_TYPE[] ADDITIONAL_SPAWN_GROUPS = {
     UNIT_GROUP_TYPE.CROWD,
     UNIT_GROUP_TYPE.IDLERS,
     UNIT_GROUP_TYPE.PATROL,
    };
    private static final float POWER_FILL_COEF = 1.25f;
    Map<LevelBlock, Float> coefMap = new LinkedHashMap<>();
    private DungeonLevel level;
    private LevelData data;

    public static UNIT_GROUP getUnitGroup(
     LOCATION_TYPE locationType, LevelZone zone,
     UNIT_GROUP_TYPE groupType) {
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

    private static int getMaxGroupsForType(UNIT_GROUP_TYPE groupType) {
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
            case Stony:
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
                  .chain(UNIT_GROUP.DUNGEON, 20)
                  .chain(UNIT_GROUP.DWARVES, 12)
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
        if (TEST_MODE) {
            level.setPowerLevel(TEST_POWER);
        }
        //some meta data to take from?
        log(1, "Spawning for symbols ");
        spawnForSymbols();
        log(1, "Spawning  Mandatory ");
        spawnMandatory();
        log(1, "Spawning  Additional ");
        spawnAdditional();

        reportSpawning();

    }

    private void reportSpawning() {
        int groups = 0;
        int units = 0;
        int power = 0;
        for (LevelBlock block : level.getBlocks()) {
            if (block.getUnitGroups().isEmpty()) {
                main.system.auxiliary.log.LogMaster.log(1, block.getRoomType() + " has NO groups: ");
                continue;
            } else
                main.system.auxiliary.log.LogMaster.log(1, block.getRoomType() + " has groups: ");
            for (List<ObjAtCoordinate> unitGroup : block.getUnitGroups().keySet()) {
                groups++;
                String text = "";
                for (ObjAtCoordinate objAtCoordinate : unitGroup) {
                    text += objAtCoordinate.getType().getName() + "; ";
                    units++;
                    //TODO lvls?
                    power += objAtCoordinate.getType().getIntParam(PARAMS.POWER);
                }
                main.system.auxiliary.log.LogMaster.log(1, block.getUnitGroups().get(unitGroup) +
                 ": " + text + "\n");

            }

        }
        //        System.out.format(
        main.system.auxiliary.log.LogMaster.log(1,
         "\n\n\n SPAWNED: \n Total of " +
          groups +
          "groups with " +
          units +
          " units and " +
          power + " power " + "\n");
    }


    private void spawnMandatory() {

        for (UNIT_GROUP_TYPE groupType : MANDATORY_SPAWN_GROUPS) {
            blocks:
            for (LevelBlock block : level.getBlocks()) {
                if (!isBlockForGroup(block, groupType)) {
                    continue blocks;
                }
                if (block.getUnitGroups().size() > getMaxGroupsForBlock(block))
                    for (UNIT_GROUP_TYPE group_type : block.getUnitGroups().values()) {
                        if (group_type == groupType)
                            continue blocks;
                    }
                if (calculatePowerFill(block) > getMaxPowerFill(block))
                    continue blocks;
                float powerCoef = getPowerCoef(block, groupType);
                UNIT_GROUP group = getUnitGroup(level.getLocationType(), block.getZone(), groupType);
                spawnForGroup(block, groupType, group, powerCoef);
            }
        }
    }

    private float getMaxPowerFill(LevelBlock block) {
        switch (block.getRoomType()) {
            case DEATH_ROOM:
            case GUARD_ROOM:
                return 1.25f;
            case THRONE_ROOM:
                return 1.5f;
        }
        return 1;
    }

    private int getMaxGroupsForBlock(LevelBlock block) {
        switch (block.getRoomType()) {
            case THRONE_ROOM:
                return 2;
        }
        return 1;
    }

    private void spawnForSymbols() {
        for (LevelBlock block : level.getBlocks()) {
            Map<Coordinates, ROOM_CELL> map = block.getTileMap().getMap();

            List<Coordinates> filledCells = block.getTileMap().getMap().keySet().stream().filter(
             c -> isSpawnSymbol(map.get(c))).collect(Collectors.toList());
            filledCells.forEach(c -> {
                UNIT_GROUP_TYPE type = new EnumMaster<UNIT_GROUP_TYPE>()
                 .retrieveEnumConst(UNIT_GROUP_TYPE.class,
                  map.get(c).name());
                List<ObjType> units = getUnitsForGroup(getPowerCoef(block, type), type,
                 getUnitGroup(level.getLocationType(), block.getZone(), type), 3, 1);
                //will space them out in-game already
                units.forEach(unit -> addUnit(new ObjAtCoordinate(unit, c), block));

            });


        }
    }

    private boolean isSpawnSymbol(ROOM_CELL cell) {
        if (cell != null)
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

    private void spawnAdditional() {
        //control power level

        // control fill level

        //N per groupType, or? at least preferred, min/max
        zones:
        for (LevelZone zone : level.getSubParts()) {
            for (UNIT_GROUP_TYPE groupType : ADDITIONAL_SPAWN_GROUPS) {
                List<LevelBlock> blocks = getBlocksForSpawn(groupType, zone);
                if (blocks.isEmpty())
                    continue;
                float requiredFill = 0.8f;
                float current = calculatePowerFill(zone);
                UNIT_GROUP group = getUnitGroup(level.getLocationType(), zone, groupType);
                for (LevelBlock block : blocks) {
                    if ((requiredFill < current)) {
                        continue zones;
                    }
                    current = calculatePowerFill(zone);
                    float powerCoef = getPowerCoef(block, groupType);
                    spawnForGroup(block, groupType, group, powerCoef);
                    if (checkDone()) {
                        return;
                    }
                }
            }
        }
    }

    private boolean checkDone() {
        return calculatePowerFill() >
         POWER_FILL_COEF *
          data.getFloatValue(LEVEL_VALUES.POWER_PER_SQUARE_MAX_MOD) / 100;
    }

    private List<LevelBlock> getBlocksForSpawn(UNIT_GROUP_TYPE groupType, LevelZone zone) {
        return
         level.getBlocks().stream()
          .filter(block -> block.getZone() == zone)
          .filter(block -> isBlockForGroup(block, groupType)).sorted(
          new SortMaster<LevelBlock>().getSorterByExpression_(block ->
           (int) (-100 * calculatePowerFill(block) + RandomWizard.getRandomIntBetween(0, 5))))
          .collect(Collectors.toList());
    }

    private float calculatePowerFill() {
        int n = 0;
        for (LevelZone levelZone : level.getZones()) {
            n += calculatePowerFill(levelZone);
        }
        n = n / level.getZones().size();
        return n;
    }

    private float calculatePowerFill(LevelZone zone) {
        int n = 0;
        for (LevelBlock block : zone.getSubParts()) {
            n += calculatePowerFill(block);
        }
        n = n / (1 + zone.getSubParts().size());
        return n;
    }

    private float calculatePowerFill(LevelBlock block) {
        //power per square max
        float coef = (float) Math.sqrt(block.getWidth() * block.getHeight()) / 10
         + getPowerFillCoef(block.getRoomType());
        List<ObjAtCoordinate> units = block.getUnits();
        int powerSum = units.stream().mapToInt(unit ->
         unit.getType().getIntParam(PARAMS.POWER)).sum();

        return powerSum / coef / level.getPowerLevel();
    }

    private float getPowerFillCoef(ROOM_TYPE roomType) {
        switch (roomType) {
            case THRONE_ROOM:
                return 3.25f;
            case GUARD_ROOM:
            case DEATH_ROOM:
                return 2.4f;
        }
        return 1.5f;
    }

    private float getPowerCoef(LevelBlock block, UNIT_GROUP_TYPE groupType) {
        float coef = 2f;
        switch (block.getRoomType()) {
            case THRONE_ROOM:
                coef = 3f;
                break;
            case DEATH_ROOM:
            case GUARD_ROOM:
                coef = 2.5f;
                break;
            case CORRIDOR:
                break;
            case ENTRANCE_ROOM:
            case EXIT_ROOM:
                coef = 1.75f;
                break;
        }
        switch (groupType) {
            case STALKER:
            case PATROL:
                coef *= 1.25f;
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

    private int getLimit(UNIT_GROUP_TYPE group, LevelZone zone, int size) {
        int max =
         data.getIntValue(LEVEL_VALUES.valueOf("SPAWN_GROUP_COEF_" + group.name())) *
          size / 100;
        return RandomWizard.getRandomIntBetween(max / 2, max * 3 / 2) + 1;
    }

    private boolean isBlockForGroup(LevelBlock block, UNIT_GROUP_TYPE group) {
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
                return block.getRoomType() == ROOM_TYPE.COMMON_ROOM
                 || block.getRoomType() == ROOM_TYPE.EXIT_ROOM
                 || block.getRoomType() == ROOM_TYPE.ENTRANCE_ROOM;
            case STALKER:
                return block.getRoomType() == ROOM_TYPE.CORRIDOR
                 || block.getRoomType() == ROOM_TYPE.SECRET_ROOM;
            case BOSS:
                return block.getRoomType() == ROOM_TYPE.THRONE_ROOM;
        }
        return false;
    }

    private void spawnForGroup(LevelBlock levelBlock, UNIT_GROUP_TYPE groupType, UNIT_GROUP group, float powerCoef) {
        spawnForGroup(levelBlock, groupType, group, getMaxUnits(group, groupType, powerCoef),
         getMinUnits(group, groupType, powerCoef), powerCoef);
    }


    private int getMinUnits(UNIT_GROUP group, UNIT_GROUP_TYPE groupType, float powerCoef) {
        return getMinMaxUnits(true, group, groupType, powerCoef);
    }

    private int getMaxUnits(UNIT_GROUP group, UNIT_GROUP_TYPE groupType, float powerCoef) {
        return getMinMaxUnits(false, group, groupType, powerCoef);
    }

    private int getMinMaxUnits(boolean min, UNIT_GROUP group, UNIT_GROUP_TYPE groupType, float powerCoef) {
        float base = min ? getBaseMin(group) : getBaseMax(group);
        switch (groupType) {
            case GUARDS:
            case PATROL:
            case AMBUSH:
                break;
            case CROWD:
            case IDLERS:
                base *= 1.5f;
                break;
            case STALKER:
            case BOSS:
                base *= 0.75f;
                break;
        }
        if (min)
            return 1 + Math.round(base * powerCoef);
        return Math.round(base * powerCoef);
    }

    private float getBaseMax(UNIT_GROUP group) {
        return 6;
    }

    private float getBaseMin(UNIT_GROUP group) {
        return 3;
    }

    private List<ObjType> getUnitsForGroup(float powerCoef,
                                           UNIT_GROUP_TYPE groupType,
                                           UNIT_GROUP group, int max, int minPreferred) {
        int powerLevel = Math.round(level.getPowerLevel() * powerCoef);

        List<ObjType> units = new ArrayList<>();
        Loop loop = new Loop(50 + minPreferred * 10);
        boolean oneFromAboveRank = isOneFromAboveRank(groupType, max);
        WeightMap<String> map = RngUnitProvider.getWeightMap(group, groupType, false);
        WeightMap<String> altMap = RngUnitProvider.getWeightMap(group, groupType, true);
        boolean oneOfAType = isOneOfAType(groupType, map, minPreferred);
        while (true) {
            if (map.isEmpty())
                map = RngUnitProvider.getWeightMap(group, groupType, false);
            if (loop.ended())
                break;
            String unit = (oneFromAboveRank ? altMap : map).getRandomByWeight();
            ObjType type = DataManager.getType(unit, DC_TYPE.UNITS);

            int pouvoir = type.getIntParam(PARAMS.POWER);
            boolean add = pouvoir <= powerLevel;
            if (!oneFromAboveRank)
                if (add) {
                    if (minPreferred > 0) {
                        if (pouvoir > powerLevel / minPreferred)
                            if (RandomWizard.chance(80))
                                add = false; // won't fill the minimum at this rate, don't do that so often
                    } else  //if (max < 0) {
                        if (pouvoir < powerLevel / max) {
                            if (RandomWizard.chance(80))
                                add = false;//will be exceeding the max at this rate, don't do that so often
                        }

                } else {
                    if (units.isEmpty()) {
                        if (pouvoir <= powerLevel * SINGLE_UNIT_BONUS_COEF)
                            if (RandomWizard.chance(40))
                                add = true;
                    }
                }
            if (add) {
                powerLevel -= pouvoir;
                units.add(type);
                //                max--;
                //                minPreferred--;
                if (oneOfAType)
                    map.remove(type.getName());
            }
            oneFromAboveRank = false;

        }
        return units;
    }

    private boolean isOneOfAType(UNIT_GROUP_TYPE groupType, WeightMap<String> map, int minPreferred) {
        switch (groupType) {
            case PATROL:
            case AMBUSH:
            case STALKER:
                if (minPreferred <= map.size())
                    return true;
            case BOSS:
                return true;
        }
        return false;
    }

    private boolean isOneFromAboveRank(UNIT_GROUP_TYPE group, int max) {
        switch (group) {
            case GUARDS:
                return RandomWizard.chance(20 + max * 10);
            case PATROL:
                return RandomWizard.chance(25 + max * 12);
            case AMBUSH:
                return RandomWizard.chance(10 + max * 15);
        }
        return false;
    }

    private void spawnForGroup(LevelBlock levelBlock,
                               UNIT_GROUP_TYPE groupType,
                               UNIT_GROUP group, int max, int minPreferred, float powerCoef) {
        List<ObjType> units = getUnitsForGroup(powerCoef, groupType, group,
         max, minPreferred);

        List<ObjAtCoordinate> unitsAtCoordinates = spawnUnits(levelBlock, units);

        levelBlock.getUnitGroups().put(unitsAtCoordinates, groupType);
        //            level.getAiMap().put(c, aiType)
        //            filter weight map?

        log(1, groupType + " spawned: "
         + ContainerUtils.toStringContainer(unitsAtCoordinates));
    }


    private List<ObjAtCoordinate> spawnUnits(LevelBlock levelBlock, List<ObjType> units) {
        //TODO
        int maxStack = 2;
        AbstractCoordinates center = new AbstractCoordinates(levelBlock.getWidth() / 2, levelBlock.getHeight() / 2);
        List<Coordinates> emptyCells = levelBlock.getTileMap().getMap().keySet().stream().filter(
         c ->
          levelBlock.getTileMap().getMap().get(c) == ROOM_CELL.FLOOR).
         sorted(new SortMaster<Coordinates>().getSorterByExpression_(c ->
          -c.dst(center)+RandomWizard.getRandomInt(5))).limit(units.size() * maxStack).
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

        log(1, at + " spawned for " + levelBlock.getRoomType());
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
    public enum UNIT_GROUP_TYPE {
        //this is gonna be used for fill probably too
        GUARDS(0.05f),
        PATROL(1f),
        AMBUSH(0.15f),
        CROWD(0.3f),
        IDLERS(0.2f),
        STALKER(1.25f),
        BOSS(0.1f),;
        //determines what? Except AI behavior -
        // N preference, power level, placement,

        private float speedMod = 0;

        UNIT_GROUP_TYPE(float speedMod) {
            this.speedMod = speedMod;
        }

        public float getSpeedMod() {
            return speedMod;
        }
    }


}

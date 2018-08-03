package eidolons.game.module.dungeoncrawl.generator.init;

import eidolons.content.PARAMS;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.init.RngLevelPopulator.BOSS_SPAWN_TYPE;
import eidolons.game.module.dungeoncrawl.generator.model.AbstractCoordinates;
import main.content.DC_TYPE;
import main.content.enums.entity.UnitEnums.UNIT_GROUP;
import main.data.DataManager;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.SortMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.datatypes.WeightMap;

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

    public static UNIT_GROUP getUnitGroup(ROOM_TYPE roomType, LevelZone zone, SPAWN_GROUP_TYPE boss) {
        return UNIT_GROUP.RAVENGUARD;
    }

    public void spawn(DungeonLevel level) {
        //via groups/encounters?
        this.level = level;
        this.data = level.getModel().getData();
        //some meta data to take from?
        spawnBosses();
        spawnObjective();
        spawnGroups();

    }

    private void spawnGroups() {
        for (SPAWN_GROUP_TYPE group : SPAWN_GROUP_TYPE.values()) {
            int limit = getLimit(group);
            List<LevelBlock> blocks = level.getModel().getBlocks().values().stream()
             .filter(block -> isBlockForGroup(block, group)).sorted(
              (o1, o2) -> RandomWizard.getRandomInt(10)).limit(limit).collect(Collectors.toList());

            for (LevelBlock block : blocks) {
                spawnForGroup(block, group);
            }
            //            level.getAiMap().put(c, aiType)
            //            filter weight map?
        }
    }

    private int getLimit(SPAWN_GROUP_TYPE group) {
        return RandomWizard.getRandomIntBetween(5, 10);
    }

    private boolean isBlockForGroup(LevelBlock block, SPAWN_GROUP_TYPE group) {
        return true;
    }

    private void spawnBosses() {
        //find the throne room?
        BOSS_SPAWN_TYPE spawnType = BOSS_SPAWN_TYPE.RANDOM_MANY;
        switch (spawnType) {
        }
        for (LevelBlock levelBlock : level.getModel().getBlocks().values()) {
            ROOM_TYPE type = levelBlock.getRoomType();
            if (type == ROOM_TYPE.THRONE_ROOM) {
                spawnBoss(levelBlock);
            }
        }
    }
    private void spawnBoss(LevelBlock levelBlock) {
        spawnForGroup(levelBlock, SPAWN_GROUP_TYPE.BOSS);
    }

    private void spawnForGroup(LevelBlock levelBlock, SPAWN_GROUP_TYPE groupType) {
        UNIT_GROUP group = getUnitGroup(levelBlock.getRoomType(), levelBlock.getZone(),
         SPAWN_GROUP_TYPE.BOSS);
        int powerLevel = level.getPowerLevel();
        WeightMap<String> map = RngUnitProvider.getBossWeightMap(group);
        List<ObjType> units = new ArrayList<>();
        Loop loop = new Loop(50);
        while (true) {
            if (loop.ended())
                break;
            String unit = map.getRandomByWeight();
            ObjType type = DataManager.getType(unit, DC_TYPE.UNITS);

            int pouvoir = type.getIntParam(PARAMS.POWER);
            if (pouvoir <= powerLevel) {
                powerLevel -= pouvoir;
                units.add(type);
            } else loop.continues();
        }
        spawnGroup(levelBlock, units);
    }


    private void spawnGroup(LevelBlock levelBlock, List<ObjType> units) {
        //TODO
        int maxStack = 2;
        AbstractCoordinates center = new AbstractCoordinates(levelBlock.getWidth() / 2, levelBlock.getHeight() / 2);
        List<Coordinates> emptyCells = levelBlock.getCoordinatesList().stream().filter(c ->
         levelBlock.getTileMap().getMap().get(c) == ROOM_CELL.FLOOR).
         sorted(new SortMaster<Coordinates>().getSorterByExpression_(c ->
          c.dst(center))).limit(units.size() * maxStack).
         collect(Collectors.toList());

        Iterator<Coordinates> iterator = emptyCells.listIterator();
        for (ObjType unit : units) {
            if (!iterator.hasNext())
                iterator = emptyCells.listIterator();
            Coordinates c = iterator.next();
            levelBlock.getUnits().add(new ObjAtCoordinate(unit, c));

        }
    }

    private void spawnObjective() {
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

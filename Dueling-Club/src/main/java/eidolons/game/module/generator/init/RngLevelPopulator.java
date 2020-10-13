package eidolons.game.module.generator.init;

import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.battlecraft.logic.dungeon.universal.UnitGroupMaster;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.generator.model.AbstractCoordinates;
import eidolons.game.module.generator.model.LevelModel;
import eidolons.game.module.generator.tilemap.TileMap;
import main.content.DC_TYPE;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.enums.entity.UnitEnums.UNIT_GROUPS;
import main.data.DataManager;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.SortMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import static main.content.enums.entity.UnitEnums.UNIT_GROUPS.*;

/**
 * Created by JustMe on 7/20/2018.
 */
public class RngLevelPopulator  {

    private static final float BLOCK_FILL_COEF = 3;
    LevelModel model;
    TileMap tileMap;
    private float requiredFill;
    int powerLevel;

    public static void populate(DungeonLevel level ) {
        int powerLevel=100;
        new RngMainSpawner().spawn(level);
//        new RngLevelPopulator(level.getModel(), level.getTileMap(), powerLevel).populate();
    }

    public RngLevelPopulator(LevelModel model, TileMap tileMap, int powerLevel) {
        this.model = model;
        this.tileMap = tileMap;
        this.powerLevel = powerLevel;
    }

    public void populate() {
        //via groups/encounters?
        fillOut();
    }

    private void fillOut() {
          requiredFill = 0.8f;
        float current = calculateFill();
        float dif;
        while (!checkDone() && ((dif = requiredFill - current) > 0.05f)) {
            Stack<LevelBlock> prioritySpawnLocations = createPrioritySpawnLocations();
            fill(prioritySpawnLocations, dif);
            current = calculateFill();
        }
    }

    private Stack<LevelBlock> createPrioritySpawnLocations() {
        Stack<LevelBlock> stack=new Stack<>();
        List<LevelZone> zones = new ArrayList<>(model.getZones());

        SortMaster.sortByExpression(zones,
         zone -> (int) (-100 * calculateFill((LevelZone) zone)));
        for (LevelZone zone : zones) {
            List<LevelBlock> blocks = new ArrayList<>(zone.getSubParts());
            blocks.removeIf(block -> calculateFill(block) > requiredFill);

            SortMaster.sortByExpression(blocks,
             block -> (int) (-100 * calculateFill((LevelBlock) block)));

            stack.addAll(blocks);
        }

        return stack;
    }

    private boolean checkDone() {
        return false;
    }

    private void fill(Stack<LevelBlock> prioritySpawnLocations, float maxFillIncrement) {
        //group
        LevelBlock block = prioritySpawnLocations.pop();

        if (maxFillIncrement > 100 / model.getOccupiedCells().size()) {
            UNIT_GROUPS group = chooseGroup(block, maxFillIncrement);
            //gen groups from encounter types?
            // use wave assembler?
            spawnUnitGroup(group, block, getLevel(block, maxFillIncrement, group));
        } else {
            //single unit?
        }

    }

    private int getLevel(LevelBlock block, float maxFillIncrement, UNIT_GROUPS group) {

        return 0;
    }
    private UNIT_GROUPS chooseGroup(LevelBlock block, float maxFillIncrement) {
        boolean elite = isElite(block.getRoomType(), maxFillIncrement);
        List<UNIT_GROUPS> basePool = new ArrayList<>(Arrays.asList(getPool(model.getData().getLocationType(), elite)));
       return  new RandomWizard<UNIT_GROUPS>().getRandomListItem(basePool);
    }

    private boolean isElite(ROOM_TYPE roomType, float maxFillIncrement) {
        switch (roomType) {
            case TREASURE_ROOM:
            case SECRET_ROOM:
            case GUARD_ROOM:
                return RandomWizard.random();
            case THRONE_ROOM:
            case DEATH_ROOM:
            case EXIT_ROOM:
                return true;
        }
        return false;
    }

    private UNIT_GROUPS[] getPool(LOCATION_TYPE subdungeonType, boolean elite) {
        switch (subdungeonType) {
            case CAVE:
                return elite ? new UNIT_GROUPS[]{DUNGEON, CRITTERS, }
                : new UNIT_GROUPS[]{MUTANTS, DUNGEON, };
            case HIVE:
            case HOUSE:
            case RUIN:
            case BARROW:
            case DEN:
            case CRYPT:
            case TOWER:
            case ASTRAL:
            case HELL:
            case SEWER:
            case CASTLE:
            case DUNGEON:
                break;
        }
        return null;
    }

    private void spawnUnitGroup(UNIT_GROUPS group, LevelBlock block, int level) {
        Coordinates at = CoordinatesMaster.getCenterCoordinate(block.getCoordinatesSet());

        String data = UnitGroupMaster.getUnitGroupData(StringMaster.format(group.name()), level);
        for (String unitData : data.split(UnitGroupMaster.PAIR_SEPARATOR)) {
            String[] parts = unitData.split(UnitGroupMaster.UNIT_SEPARATOR);
            Coordinates offset = new AbstractCoordinates(parts[0]);
            ObjType type = DataManager.getType(parts[1], DC_TYPE.UNITS);
            Coordinates coordinates = offset.getOffset(at);
            ObjAtCoordinate unit = new ObjAtCoordinate(type, coordinates);
            //relative or abs?
            block.getUnits().add(0, unit);

            //fitting units exactly will be done when?
        }
    }

    private float calculateFill() {
        int n = 0;
        for (LevelZone levelZone : model.getZones()) {
            n += calculateFill(levelZone);
        }
        n = n / model.getZones().size();
        return n;
    }

    private float calculateFill(LevelZone zone) {
        int n = 0;
        for (LevelBlock block : zone.getSubParts()) {
            n += calculateFill(block);
        }
        n = n / zone.getSubParts().size();
        return n;
    }

    private float calculateFill(LevelBlock block) {
        int square = block.getWidth() * block.getHeight();
        List<ObjAtCoordinate> units = block.getUnits();
        return BLOCK_FILL_COEF * units.size() / square;
    }


    public enum BOSS_SPAWN_TYPE {
        SINGLE,
        PER_ZONE,
        RANDOM_ONE,
        RANDOM_MANY,
        NONE,
    }


}

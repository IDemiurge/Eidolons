package eidolons.game.module.dungeoncrawl.generator;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.LEVEL_VALUES;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.LevelDataMaker.LEVEL_REQUIREMENTS;
import eidolons.game.module.dungeoncrawl.generator.graph.LevelGraph;
import eidolons.game.module.dungeoncrawl.generator.model.LevelModel;
import eidolons.game.module.dungeoncrawl.generator.model.Room;
import eidolons.game.module.dungeoncrawl.generator.model.Traverser;
import eidolons.game.module.dungeoncrawl.generator.test.GenerationStats;
import eidolons.game.module.dungeoncrawl.generator.test.LevelStats;
import eidolons.game.module.dungeoncrawl.generator.test.LevelStats.LEVEL_STAT;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMap;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMapper;
import main.entity.EntityCheckMaster;
import main.entity.type.ObjAtCoordinate;
import main.game.bf.Coordinates;
import main.system.auxiliary.ContainerUtils;
import main.system.data.DataUnit;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 7/27/2018.
 */
public class LevelValidator {
    LevelStats stats;
    boolean logFail = true;
    private LevelModel model;
    private int minRooms;
    private float minFillRatio;
    private DungeonLevel level;
    private float minDimensionRatio;
    private float maxSquare;
    private float maxDimension;
    private DataUnit<LEVEL_REQUIREMENTS> reqs;
    private Traverser traverser;

    public LevelValidator() {
    }

    public LevelValidator(boolean logFail) {
        this.logFail = logFail;
    }


    public static boolean validateForTester(GenerationStats stats, DungeonLevel level) {
        LevelValidator instance = new LevelValidator();
        boolean valid = instance.isLevelValid(level);
        //        valid = new Traverser().test(level);
        return valid;
    }

    private void initRequirements(LevelData data, LevelModel model) {
        this.model = model;
        this.reqs = data.getReqs();
        minFillRatio = reqs.getFloatValue(LEVEL_REQUIREMENTS.minFillRatio);
        minRooms = reqs.getIntValue(LEVEL_REQUIREMENTS.minRooms);
        maxDimension = reqs.getIntValue(LEVEL_REQUIREMENTS.maxDimension);
        maxSquare = reqs.getIntValue(LEVEL_REQUIREMENTS.maxSquare);
        minDimensionRatio = reqs.getFloatValue(LEVEL_REQUIREMENTS.minDimensionRatio);

    }

    public boolean validateModel(LevelGraph graph, LevelModel model) {
        initRequirements(model.getData(), model);
        if (!checkModel())
            return false;
        return true;
    }

    public LevelStats getStats() {
        return stats;
    }

    public void setStats(LevelStats stats) {
        this.stats = stats;
    }

    public boolean isLevelValid(DungeonLevel level) {
        this.level = level;
        initRequirements(level.getData(), level.getModel());
        if (stats == null)
            stats = new LevelStats(level);
        main.system.auxiliary.log.LogMaster.log(1, "Validating stats: " + stats);
        if (!checkExit())
            return fail(RNG_FAIL.NO_EXIT);
        if (!checkEntrace())
            return fail(RNG_FAIL.NO_ENTRACE);
        if (traverser != null) {
            if (!traverser.test()) {
                return fail(RNG_FAIL.CANNOT_TRAVERSE , traverser.getFailArgs());
            }
        }
        if (!checkModel())
            return false;
        if (!checkZones())
            return fail(RNG_FAIL.ZONES);
        if (!checkBlocks())
            return false;
        if (!checkObjects())
            return false;
        if (!checkTileMap())
            return false;

        //check population
        return true;
    }

    private boolean checkTileMap() {
        TileMap map = TileMapper.createTileMap(model);
        List<Coordinates> toClear = map.getMap().keySet().stream().filter(c -> {
            ROOM_CELL cell = map.getMap().get(c);
            if (cell == ROOM_CELL.DOOR) {
                for (Coordinates c1 : c.getAdjacentCoordinates()) {
                    if (map.getMap().get(c1) == ROOM_CELL.DOOR)
                        return true;
                }
            }
            if (cell == ROOM_CELL.ENTRANCE) {

            }
            if (cell == ROOM_CELL.ROOM_EXIT) {

            }
            return false;
        }).collect(Collectors.toList());

        if (!toClear.isEmpty())
            return false;
        return true;
    }

    private boolean checkModel() {
        if (!checkSize())
            return fail(RNG_FAIL.SIZE);
        if (!checkFillRatio())
            return fail(RNG_FAIL.FILL_RATIO);
        if (!checkRooms())
            return fail(RNG_FAIL.ROOMS);
        return true;
    }

    private boolean checkObjects() {
        for (ObjAtCoordinate objAtCoordinate : level.getObjects()) {
            if (EntityCheckMaster.isWall(objAtCoordinate.getType()))
                if (level.getObjects().stream().filter(
                 obj -> obj.getCoordinates().equals(objAtCoordinate.getCoordinates())
                ).count() > 2)
                    return false;
        }
        return true;
    }

    private boolean checkBlocks() {
        //        for (LevelBlock block : level.getBlocks()) {
        //            block.getObjects()
        //        }
        return true;
    }

    private boolean fail(RNG_FAIL fail, Object... args) {
        if (stats != null) {
            stats.setValue(LEVEL_STAT.FAIL_REASON, fail
             +": "
             + ContainerUtils.joinArray(" ", args));
        }
        if (logFail)
            main.system.auxiliary.log.LogMaster.log(1, "VALIDATION OF: \n" + model + "\n FAILED ON " + fail);
        return false;
    }

    private boolean checkExit() {
        Room room = model.getRoomMap().values().stream().filter(
         r -> r.getType() == ROOM_TYPE.EXIT_ROOM).findFirst().orElse(null);

        if (room == null )
            return false;

        if (!TileMapper.createTileMap(room).getMap().values().stream().anyMatch(c -> c == ROOM_CELL.EXIT)) {
            return false;
        }
        return true;
    }

    private boolean checkEntrace() {
        return model.getRoomMap().values().stream().filter(
         room -> room.getType() == ROOM_TYPE.ENTRANCE_ROOM).count() > 0;
    }

    private boolean checkZones() {
        for (Room room : model.getRoomMap().values()) {
            if (room.getZone() == null)
                return false;
        }
        for (LevelZone levelZone : model.getZones()) {
            if (levelZone.getSubParts().size() < 1)
                return false;

        }
        return model.getZones().size() >= model.getData().getIntValue(LEVEL_VALUES.ZONES);
    }

    private boolean checkRooms() {
        if (model.getRoomMap().size() < minRooms)
            return false;
        return true;
    }

    private boolean checkSize() {
        float w = model.getCurrentWidth();
        float h = model.getCurrentHeight();

        float sizeGap = 0.2f;
        if (h / maxDimension - 1 >= sizeGap)
            return false;
        if (w / maxDimension - 1 >= sizeGap)
            return false;
        if (maxSquare <= h * w)
            return false;
        if (minDimensionRatio >= Math.min(w / h, h / w)) {
            return false;
        }
        return true;
    }

    private boolean checkFillRatio() {
        try {
            float fillRatio = new Float(model.getOccupiedCells().size()) / (model.getCurrentWidth() * model.getCurrentHeight());
            return fillRatio > minFillRatio;
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return false;
    }

    public Traverser getTraverser() {
        return traverser;
    }

    public void setTraverser(Traverser traverser) {
        this.traverser = traverser;
    }

    public enum RNG_FAIL {
        NO_EXIT,
        SIZE,
        FILL,
        ROOMS,
        ZONES, FILL_RATIO, NO_ENTRACE,
        CANNOT_TRAVERSE,
        ERROR_MODEL,
        LOW_RATING,
    }

}

package eidolons.game.module.dungeoncrawl.generator;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.LEVEL_VALUES;
import eidolons.game.module.dungeoncrawl.generator.LevelDataMaker.LEVEL_REQUIREMENTS;
import eidolons.game.module.dungeoncrawl.generator.graph.LevelGraph;
import eidolons.game.module.dungeoncrawl.generator.model.LevelModel;
import eidolons.game.module.dungeoncrawl.generator.model.Room;
import eidolons.game.module.dungeoncrawl.generator.test.GenerationStats;
import main.entity.EntityCheckMaster;
import main.entity.type.ObjAtCoordinate;
import main.system.data.DataUnit;

/**
 * Created by JustMe on 7/27/2018.
 */
public class LevelValidator {
    GenerationStats stats;
    boolean logFail = true;
    private LevelModel model;
    private int minRooms;
    private float minFillRatio;
    private DungeonLevel level;
    private float minDimensionRatio;
    private float maxSquare;
    private float maxDimension;
    private DataUnit<LEVEL_REQUIREMENTS> reqs;

    public LevelValidator() {
    }

    public LevelValidator(boolean logFail) {
        this.logFail = logFail;
    }

    public LevelValidator(GenerationStats stats) {
        this.stats = stats;
    }

    public static boolean validateForTester(GenerationStats stats, DungeonLevel level) {
        LevelValidator instance = new LevelValidator(stats);
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

    public boolean isLevelValid(DungeonLevel level) {
        this.level = level;
        initRequirements(level.getData(), level.getModel());

        if (!checkExit())
            return fail(RNG_FAIL.NO_EXIT);
        if (!checkModel())
            return false;
        if (!checkBlocks())
            return false;
        if (!checkObjects())
            return false;
        //check population
        return true;
    }

    private boolean checkModel() {
        if (!checkSize())
            return fail(RNG_FAIL.SIZE);
        if (!checkFillRatio())
            return fail(RNG_FAIL.FILL_RATIO);
        if (!checkRooms())
            return fail(RNG_FAIL.ROOMS);
        if (!checkZones())
            return fail(RNG_FAIL.ZONES);
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

    private boolean fail(RNG_FAIL fail) {
        if (stats != null) {
            //            stats.addFail(fail);
        }
        if (logFail)
            main.system.auxiliary.log.LogMaster.log(1, "VALIDATION OF: \n" + model + "\n FAILED ON " + fail);
        return false;
    }

    private boolean checkExit() {
        return model.getRoomMap().values().stream().filter(
         room -> room.getType() == ROOM_TYPE.EXIT_ROOM).count() > 0;
    }


    private boolean checkZones() {
        for (Room room : model.getRoomMap().values()) {
            if (room.getZone() == null)
                return false;
        }
        for (LevelZone levelZone : model.getZones()) {
            if (levelZone.getSubParts().size() < 3)
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
        float fillRatio = new Float(model.getOccupiedCells().size()) / (model.getCurrentWidth() * model.getCurrentHeight());
        return fillRatio > minFillRatio;
    }

    public enum RNG_FAIL {
        NO_EXIT,
        SIZE,
        FILL,
        ROOMS,
        ZONES, FILL_RATIO,

    }

}

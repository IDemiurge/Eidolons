package eidolons.game.module.dungeoncrawl.generator;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.generator.model.LevelModel;
import eidolons.game.module.dungeoncrawl.generator.test.GenerationStats;

/**
 * Created by JustMe on 7/27/2018.
 */
public class LevelValidator {
    private DungeonLevel level;
    private LevelModel model;
    private int minRooms;
    private float minFillRatio;
    GenerationStats stats;

    public LevelValidator() {
    }

    public LevelValidator(GenerationStats stats) {
        this.stats = stats;
    }

    public static void validateForTester(GenerationStats stats, DungeonLevel level) {
        LevelValidator instance = new LevelValidator(stats);
        boolean valid = instance.isLevelValid(level);


    }
    private void initRequirements(DungeonLevel level) {
        this.level =  level;
        this.model = level.getModel();

        minFillRatio= 0.65f;

        switch (level.getModel().getData().getSublevelType()) {
            case COMMON:
                minRooms=12;
                break;
            case PRE_BOSS:
                minRooms=6;
                break;
            case BOSS:
                minRooms=4;
                break;
        }
    }
    public enum RNG_FAIL{
        NO_EXIT,
        SIZE,
        FILL,
        ROOMS,
        ZONES,

    }
    public  boolean isLevelValid(DungeonLevel level){
        initRequirements(level);

        if (!checkExit())
            return fail(RNG_FAIL.NO_EXIT);
        if (!checkModel())
            return false;
        if (!checkSize())
            return false;
        if (!checkRooms())
            return false;
        if (!checkZones())
            return false;
        //check population
        return true;
    }

    private boolean fail(RNG_FAIL fail) {
        if (stats != null) {
//            stats.addFail(fail);
        }
        return false;
    }

    private boolean checkExit() {
        return level.getModel().getRoomMap().values().stream().filter(
         room -> room.getType()== ROOM_TYPE.EXIT_ROOM).count()>0;
    }


    private boolean checkModel() {
        return true;
    }

    private boolean checkZones() {
        return false;
    }

    private boolean checkRooms() {
        if (level.getModel().getRoomMap().size()< minRooms)
            return false;
        return true;
    }

    private boolean checkSize() {
        float fillRation = model.getOccupiedCells().size() / (model.getCurrentWidth() * model.getCurrentHeight());
        return fillRation>minFillRatio;
    }

}

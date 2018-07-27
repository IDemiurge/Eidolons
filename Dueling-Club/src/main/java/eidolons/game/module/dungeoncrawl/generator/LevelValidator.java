package eidolons.game.module.dungeoncrawl.generator;

import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.generator.model.LevelModel;

/**
 * Created by JustMe on 7/27/2018.
 */
public class LevelValidator {
    private DungeonLevel level;
    private LevelModel model;
    private int minRooms;


    private void initRequirements(DungeonLevel level) {
        this.level =  level;
        this.model = level.getModel();
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
    public  boolean isLevelValid(DungeonLevel level){
        initRequirements(level);

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


    private boolean checkModel() {
        return false;
    }

    private boolean checkZones() {
        return false;
    }

    private boolean checkRooms() {
        if (level.getModel().getRoomMap().size()< minRooms)
        return false;
        return false;
    }

    private boolean checkSize() {

        return false;
    }
}

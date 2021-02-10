package eidolons.game.battlecraft.logic.dungeon.universal;

import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.struct.LevelStruct;
import main.content.enums.DungeonEnums;
import main.entity.EntityWrapper;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;

/**
 * Created by JustMe on 5/10/2017.
 */
public class FloorWrapper extends LevelStruct<Module, Module> implements EntityWrapper<Floor> {
    protected DungeonMaster master;
    protected Floor floor;

    public FloorWrapper(Floor entity, DungeonMaster master) {
        floor = entity;
        this.master = master;
    }

    @Override
    public LevelStruct getParent() {
        if (getGame().getMissionMaster() == null) {
            return null;
        }
        return getGame().getMissionMaster().getMission(); //campaign?
    }

    @Override
    public DC_Game getGame() {
        return floor.getGame();
    }

    @Override
    public Floor getEntity() {
        return floor;
    }

    public Coordinates getDefaultPlayerSpawnCoordinates() {
            return Coordinates.getMiddleCoordinate(FACING_DIRECTION.NONE);
    }

    public DungeonMaster getDungeonMaster() {
        return master;
    }

    public Floor getFloor() {
        return floor;
    }

    public String getMapBackground() {
        return floor.getMapBackground();
    }

    public boolean isBoss() {
        return floor.isBoss();
    }

    public Integer getCellsX() {
        return floor.getCellsX();
    }

    public Integer getCellsY() {
        return floor.getCellsY();
    }


    public boolean isSurface() {
        return floor.isSurface();
    }

    public int getSquare() {
        return floor.getSquare();
    }

    public DungeonEnums.LOCATION_TYPE getLocationType() {
        return (DungeonEnums.LOCATION_TYPE) getData().getEnum(LevelStructure.FLOOR_VALUES.location_type,
                DungeonEnums.LOCATION_TYPE.class);
    }

    public Module getModule() {
        return getChildren().iterator().next();
    }
    public void setLevelFilePath(String s) {
        floor.setLevelFilePath(s);
    }
}

package eidolons.game.battlecraft.logic.dungeon.universal;

import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.module.dungeoncrawl.dungeon.LevelStruct;
import main.entity.EntityWrapper;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;

/**
 * Created by JustMe on 5/10/2017.
 */
public class DungeonWrapper  extends LevelStruct<Module, Module> implements EntityWrapper<Dungeon> {
    protected DungeonMaster master;
    protected Dungeon dungeon;

    public DungeonWrapper(Dungeon entity, DungeonMaster master) {
        dungeon = entity;
        this.master = master;
        dungeon.setWrapper(this);
    }

    @Override
    protected LevelStruct getParent() {
        return null; //campaign?
    }
    @Override
    public Dungeon getEntity() {
        return dungeon;
    }

    public Coordinates getDefaultPlayerSpawnCoordinates() {
            return Coordinates.getMiddleCoordinate(FACING_DIRECTION.NONE);
    }

    public DungeonMaster getDungeonMaster() {
        return master;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public String getMapBackground() {
        return dungeon.getMapBackground();
    }

    public boolean isBoss() {
        return dungeon.isBoss();
    }

    public Integer getCellsX() {
        return dungeon.getCellsX();
    }

    public Integer getCellsY() {
        return dungeon.getCellsY();
    }


    public boolean isSurface() {
        return dungeon.isSurface();
    }

    public int getSquare() {
        return dungeon.getSquare();
    }

    public Integer getGlobalIllumination() {
        return dungeon.getGlobalIllumination();
    }

    public boolean isRandomized() {
        return dungeon.isRandomized();
    }

    public String getLevelFilePath() {
        return dungeon.getLevelFilePath();
    }

    public void setLevelFilePath(String levelFilePath) {
        dungeon.setLevelFilePath(levelFilePath);
    }


}

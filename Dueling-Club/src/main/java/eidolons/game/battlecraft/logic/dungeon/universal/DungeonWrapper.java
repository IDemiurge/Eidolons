package eidolons.game.battlecraft.logic.dungeon.universal;

import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.module.dungeoncrawl.dungeon.LevelStruct;
import main.content.CONTENT_CONSTS;
import main.content.enums.DungeonEnums.DUNGEON_TYPE;
import main.entity.EntityWrapper;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.FACING_DIRECTION;

import java.util.Map;

/**
 * Created by JustMe on 5/10/2017.
 */
public class DungeonWrapper  extends LevelStruct<Module, Module> implements EntityWrapper<Dungeon> {
    protected DungeonMaster master;
    protected Dungeon dungeon;
    private Map<String, CONTENT_CONSTS.FLIP> flipMap;
    private Map<String, DIRECTION> directionMap;

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
    public Coordinates getPlayerSpawnCoordinates() {
        String prop = getProperty(PROPS.ENTRANCE_COORDINATES);
        if (prop.isEmpty()) {
            return Coordinates.getMiddleCoordinate(FACING_DIRECTION.NONE);
        }
        return Coordinates.get(prop);
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

    public DUNGEON_TYPE getDungeonType() {
        return dungeon.getDungeonType();
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

    public void setFlipMap(Map<String, CONTENT_CONSTS.FLIP> flipMap) {
        this.flipMap = flipMap;
    }

    public Map<String, CONTENT_CONSTS.FLIP> getFlipMap() {
        return flipMap;
    }

    public void setDirectionMap(Map<String, DIRECTION> directionMap) {
        this.directionMap = directionMap;
    }

    public Map<String, DIRECTION> getDirectionMap() {
        return directionMap;
    }

}

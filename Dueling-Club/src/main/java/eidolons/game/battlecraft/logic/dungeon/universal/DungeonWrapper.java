package eidolons.game.battlecraft.logic.dungeon.universal;

import eidolons.content.PROPS;
import main.content.CONTENT_CONSTS;
import main.content.CONTENT_CONSTS.COLOR_THEME;
import main.content.enums.DungeonEnums.DUNGEON_TYPE;
import main.entity.EntityWrapper;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.FACING_DIRECTION;

import java.util.Map;

/**
 * Created by JustMe on 5/10/2017.
 */
public class DungeonWrapper<E extends DungeonWrapper> extends EntityWrapper<Dungeon> {
    protected DungeonMaster<E> master;
    protected Dungeon dungeon;
    private Map<String, CONTENT_CONSTS.FLIP> flipMap;
    private Map<String, DIRECTION> directionMap;


    public DungeonWrapper(Dungeon entity, DungeonMaster<E> master) {
        super(entity);
        dungeon = entity;
        this.master = master;
    }

    public Coordinates getPlayerSpawnCoordinates() {
        String prop = getProperty(PROPS.ENTRANCE_COORDINATES);
        if (prop.isEmpty()) {
            return Coordinates.getMiddleCoordinate(FACING_DIRECTION.NONE);
        }
        return Coordinates.get(prop);
    }

    public DungeonMaster<E> getDungeonMaster() {
        return master;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public String getMapBackground() {
        return dungeon.getMapBackground();
    }

    public COLOR_THEME getColorTheme() {
        return dungeon.getColorTheme();
    }

    public void setColorTheme(COLOR_THEME colorTheme) {
        dungeon.setColorTheme(colorTheme);
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

    public Integer getWidth() {
        return dungeon.getWidth();
    }

    public Integer getCellsY() {
        return dungeon.getCellsY();
    }

    public Integer getHeight() {
        return dungeon.getHeight();
    }

    public int getZ() {
        return dungeon.getZ();
    }

    public void setZ(int i) {
        dungeon.setZ(i);
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

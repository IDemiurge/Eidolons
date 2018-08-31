package eidolons.game.battlecraft.logic.dungeon.universal;

import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.DUNGEON_TEMPLATES;
import main.content.CONTENT_CONSTS.COLOR_THEME;
import main.content.enums.DungeonEnums.DUNGEON_TYPE;
import main.entity.EntityWrapper;
import main.game.bf.Coordinates;

/**
 * Created by JustMe on 5/10/2017.
 */
public class DungeonWrapper<E extends DungeonWrapper> extends EntityWrapper<Dungeon> {
    protected DungeonMaster<E> master;
    protected Dungeon dungeon;


    public DungeonWrapper(Dungeon entity, DungeonMaster<E> master) {
        super(entity);
        dungeon = entity;
        this.master = master;
    }

    public Coordinates getPlayerSpawnCoordinates() {
        String prop = getProperty(PROPS.PARTY_SPAWN_COORDINATES);
        if (prop.isEmpty()) {
            return Coordinates.getMiddleCoordinate(main.game.bf.directions.FACING_DIRECTION.NONE);
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

    public DUNGEON_TEMPLATES getTemplate() {
        return dungeon.getTemplate();
    }

    public void setTemplate(DUNGEON_TEMPLATES template) {
        dungeon.setTemplate(template);
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

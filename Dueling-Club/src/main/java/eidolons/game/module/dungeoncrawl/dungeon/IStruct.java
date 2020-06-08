package eidolons.game.module.dungeoncrawl.dungeon;

import eidolons.game.battlecraft.logic.dungeon.location.struct.StructureData;
import eidolons.libgdx.bf.decor.CellDecorLayer;
import eidolons.libgdx.particles.ambi.AmbienceDataSource;
import eidolons.system.audio.MusicMaster;
import main.content.CONTENT_CONSTS;
import main.content.enums.DungeonEnums;
import main.game.bf.Coordinates;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface IStruct {
    Collection getChildren();

    String getPropagatedValue(String valueName);

    String toXml();

    LevelStruct getParent();

    Coordinates getOrigin();

    void setOrigin(Coordinates origin);

    int getWidth();

    void setWidth(int width);

    int getHeight();

    void setHeight(int height);

    Set<Coordinates> getCoordinatesSet();

    Set<Coordinates> initCoordinateSet(boolean buffer);

    int getEffectiveHeight();

    int getEffectiveWidth();

    void setCoordinatesSet(Set<Coordinates> coordinatesSet);

    String getIllumination();

    void setIllumination(int globalIllumination);

    String getWallType();

    void setWallType(String wallType);

    String getWallTypeAlt();

    void setWallTypeAlt(String wallType);

    DungeonEnums.CELL_IMAGE getCellType();

    void setCellType(DungeonEnums.CELL_IMAGE cellType);

    void setValue(String value, String string);

    DungeonEnums.DUNGEON_STYLE getStyle();

    AmbienceDataSource.VFX_TEMPLATE getVfx();

    MusicMaster.AMBIENCE getAmbience();

    CONTENT_CONSTS.COLOR_THEME getColorTheme();

    CellDecorLayer.CELL_PATTERN getCellPattern();

    CONTENT_CONSTS.COLOR_THEME getAltColorTheme();

    String getName();

    int getX();

    int getY();

    int getX2();

    int getY2();

    void setName(String name);

    StructureData getData();

    void setData(StructureData data);

    void setStyle(DungeonEnums.DUNGEON_STYLE style);

    void setAltColorTheme(CONTENT_CONSTS.COLOR_THEME c);

    void setColorTheme(CONTENT_CONSTS.COLOR_THEME c);

    int getId();

    Coordinates getCenterCoordinate();

    Map<Coordinates, Integer> getPatternMap();

    void setPatternMap(Map<Coordinates, Integer> patternMap);
}

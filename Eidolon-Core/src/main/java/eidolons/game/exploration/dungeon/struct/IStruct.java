package eidolons.game.exploration.dungeon.struct;

import eidolons.content.consts.VisualEnums;
import eidolons.game.battlecraft.logic.dungeon.location.struct.StructureData;
import eidolons.system.audio.MusicEnums;
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

    DungeonEnums.CELL_SET getCellSet();

    void setValue(String value, String string);

    DungeonEnums.DUNGEON_STYLE getStyle();

    VisualEnums.VFX_TEMPLATE getVfx();

    MusicEnums.AMBIENCE getAmbience();

    CONTENT_CONSTS.COLOR_THEME getColorTheme();

    VisualEnums.CELL_PATTERN getCellPattern();

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

    int getCellSetVariant();
    int getCellSetVariantAlt();

    int getId();

    Coordinates getCenterCoordinate();

    Map<Coordinates, Integer> getPatternMap();

    void setPatternMap(Map<Coordinates, Integer> patternMap);
}

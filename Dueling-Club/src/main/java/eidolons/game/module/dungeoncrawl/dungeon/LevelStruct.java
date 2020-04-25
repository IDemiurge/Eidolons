package eidolons.game.module.dungeoncrawl.dungeon;

import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.location.struct.StructureData;
import eidolons.libgdx.particles.ambi.AmbienceDataSource;
import eidolons.system.audio.MusicMaster.AMBIENCE;
import main.content.CONTENT_CONSTS.COLOR_THEME;
import main.content.enums.DungeonEnums;
import main.data.tree.LayeredData;
import main.game.bf.Coordinates;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;

import java.util.*;

import static main.content.enums.DungeonEnums.DUNGEON_STYLE;

/**
 * Created by JustMe on 7/20/2018.
 */
public abstract class LevelStruct<T, S> implements LayeredData<S> {
    protected String name;
    protected Coordinates origin;
    protected int width;
    protected int height;
    protected Set<Coordinates> coordinatesSet;
    protected List<T> subParts = new ArrayList<>();
    protected StructureData data;

    public LevelStruct() {
    }

    @Override
    public Collection<S> getChildren() {
        return (Collection<S>) getSubParts();
    }

    public String getPropagatedValue(String valueName) {
        String value = "";
        if (getData() != null) value = getData().getValue(valueName);
        if (StringMaster.isEmpty(value)) {
            if (getParent() == null) {
                return "";
            }
           return getParent().getPropagatedValue(valueName);
        }
        return value;
    }

    public String toXml() {
        return "";
    }

    protected abstract LevelStruct getParent();

    public Coordinates getOrigin() {
        return origin;
    }

    public void setOrigin(Coordinates origin) {
        this.origin = origin;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
        if (getData() != null) {
            getData().setValue("width", width+"");
        }
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
        if (getData() != null) {
            getData().setValue("height", height+"");
        }
    }

    public Set<Coordinates> getCoordinatesSet() {
        if (!ListMaster.isNotEmpty(coordinatesSet)) {
            coordinatesSet =   initCoordinateSet(true);
        }
        return coordinatesSet;
    }

    public Set<Coordinates> initCoordinateSet(boolean buffer) {
        Set<Coordinates> coordinatesSet = new LinkedHashSet<>();
        for (T subPart : getSubParts()) {
            if (subPart instanceof LevelStruct) {
                coordinatesSet.addAll(((LevelStruct) subPart).getCoordinatesSet());
            }
        }
        if (coordinatesSet.isEmpty()) {
            if (getOrigin() != null)
                coordinatesSet.addAll(CoordinatesMaster.getCoordinatesBetweenInclusive(
                                getOrigin(), getOrigin().getOffset(getEffectiveWidth(), getEffectiveHeight())));
        }
        return coordinatesSet;
    }

    protected int getEffectiveHeight() {
        return getHeight();
    }

    protected int getEffectiveWidth() {
        return getWidth();
    }

    public void setCoordinatesSet(Set<Coordinates> coordinatesSet) {
        this.coordinatesSet = coordinatesSet;
    }

    public void setSubParts(List<T> subParts) {
        this.subParts = subParts;
    }

    public Collection<T> getSubParts() {
        return subParts;
    }

    public String getIllumination() {
        return getPropagatedValue("illumination");
    }

    public void setIllumination(int globalIllumination) {
        getData().setValue("illumination", "" + globalIllumination);
    }


    public String getWallType() {
        return getPropagatedValue("wall_type");
    }

    public void setWallType(String wallType) {
        getData().setValue("wall_type", wallType);
    }

    public String getWallTypeAlt() {
        return getPropagatedValue("alt_wall_type");
    }

    public void setWallTypeAlt(String wallType) {
        getData().setValue("alt_wall_type", wallType);
    }


    public DungeonEnums.CELL_IMAGE getCellType() {
        return new EnumMaster<DungeonEnums.CELL_IMAGE>().retrieveEnumConst(DungeonEnums.CELL_IMAGE.class,
                getPropagatedValue("cell_type"));
    }

    public void setCellType(DungeonEnums.CELL_IMAGE cellType) {
        setValue("cell_type", cellType.toString());
    }

    public void setValue(String value, String string) {
        getData().setValue(value, string);
    }

    public DUNGEON_STYLE getStyle() {
        return new EnumMaster<DUNGEON_STYLE>().retrieveEnumConst(DUNGEON_STYLE.class,
                getPropagatedValue("style"));
    }

    public AmbienceDataSource.AMBIENCE_TEMPLATE getVfx() {
        return new EnumMaster<AmbienceDataSource.AMBIENCE_TEMPLATE>().retrieveEnumConst(AmbienceDataSource.AMBIENCE_TEMPLATE.class,
                getPropagatedValue("vfx_template"));
    }

    public AMBIENCE getAmbience() {
//        zone.setStyle( new EnumMaster<DungeonEnums.DUNGEON_STYLE>().retrieveEnumConst(DungeonEnums.DUNGEON_STYLE.class,
//                getValue(ZoneData.ZONE_VALUE.style)));
        return new EnumMaster<AMBIENCE>().retrieveEnumConst(AMBIENCE.class,
                getPropagatedValue("AMBIENCE"));
    }

    public COLOR_THEME getColorTheme() {
        return new EnumMaster<COLOR_THEME>().retrieveEnumConst(COLOR_THEME.class,
                getPropagatedValue("COLOR_THEME"));
    }

    public COLOR_THEME getAltColorTheme() {
        return new EnumMaster<COLOR_THEME>().retrieveEnumConst(COLOR_THEME.class,
                getPropagatedValue("ALT_COLOR_THEME"));
    }

    public String getName() {
        if (name == null) {
            name = getData().getValue("name");
        }
        return name;
    }

    public int getX() {
        return origin.x;
    }

    public int getY() {
        return origin.y;
    }

    public int getX2() {
        return getX() + getWidth();
    }

    public int getY2() {
        return getY() + getHeight();
    }

    public void setName(String name) {
        this.name = name;
        if (getData() != null) {
            getData().setValue("name", name);
        }
    }

    public StructureData getData() {
//        if (data == null) {
//            data = createData();
//        }
        return data;
    }

    public void setData(StructureData data) {
        this.data = data;
    }

    public void setStyle(DUNGEON_STYLE style) {
        getData().setValue("style", style.toString());
    }

    public void setAltColorTheme(COLOR_THEME c) {
        getData().setValue("alt_color_theme", c.toString());
    }

    public void setColorTheme(COLOR_THEME c) {
        getData().setValue("color_theme", c.toString());
    }

    public int getId() {
        return getData().getIntValue("id");
    }
}

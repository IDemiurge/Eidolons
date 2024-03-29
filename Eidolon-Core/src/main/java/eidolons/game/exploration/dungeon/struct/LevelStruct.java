package eidolons.game.exploration.dungeon.struct;

import eidolons.content.data.StructAmbiData;
import eidolons.content.consts.VisualEnums.SHARD_TYPE;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.location.struct.StructureData;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.system.audio.MusicEnums.AMBIENCE;
import eidolons.system.audio.MusicEnums.MUSIC_THEME;
import main.content.CONTENT_CONSTS.COLOR_THEME;
import main.content.enums.DungeonEnums;
import main.data.tree.LayeredData;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.sound.AudioEnums;

import java.util.*;

import static eidolons.content.consts.VisualEnums.*;
import static main.content.enums.DungeonEnums.DUNGEON_STYLE;

/**
 * Created by JustMe on 7/20/2018.
 */
public abstract class LevelStruct<T, S> implements LayeredData<S>, IStruct {
    protected String name;
    protected Coordinates origin;
    protected int width;
    protected int height;
    protected Set<Coordinates> coordinatesSet;
    protected List<T> subParts = new ArrayList<>();
    protected StructureData data;
    private Map<Coordinates, Integer> patternMap;
    private StructAmbiData ambiData;
    private Coordinates center;

    public LevelStruct() {
        ambiData = new StructAmbiData(this);
    }

    @Override
    public Collection<S> getChildren() {
        return (Collection<S>) getSubParts();
    }

    @Override
    public String getPropagatedValue(String valueName) {
        String value = "";
        if (getData() != null) value = getData().getValue(valueName.toLowerCase());
        if (StringMaster.isEmpty(value)) {
            if (getParent() == null) {
                return "";
            }
           return getParent().getPropagatedValue(valueName);
        }
        return value;
    }

    @Override
    public String toXml() {
        return "";
    }

    @Override
    public Coordinates getOrigin() {
        return origin;
    }

    @Override
    public void setOrigin(Coordinates origin) {
        this.origin = origin;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
        if (getData() != null) {
            getData().setValue("width", width+"");
        }
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
        if (getData() != null) {
            getData().setValue("height", height+"");
        }
    }

    @Override
    public Set<Coordinates> getCoordinatesSet() {
        if (!ListMaster.isNotEmpty(coordinatesSet)) {
            coordinatesSet =   initCoordinateSet(true);
        }
        return coordinatesSet;
    }

    @Override
    public Set<Coordinates> initCoordinateSet(boolean buffer) {
        Set<Coordinates> coordinatesSet = new LinkedHashSet<>();
        for (T subPart : getSubParts()) {
            if (subPart instanceof LevelStruct) {
                coordinatesSet.addAll(((LevelStruct) subPart).getCoordinatesSet());
            }
        }
        if (coordinatesSet.isEmpty()) {
            if (getOrigin() != null)
                coordinatesSet.addAll(CoordinatesMaster.getCoordinatesBetween(
                                getOrigin(), getOrigin().getOffset(getEffectiveWidth(), getEffectiveHeight())));
        }
        if (isDiagonal()){
            coordinatesSet= CoordinatesMaster.squareToDiamondArea(coordinatesSet);
        }
        return coordinatesSet;
    }

    private boolean isDiagonal() {
        return getData().getBooleanValue("diagonal");
    }

    @Override
    public int getEffectiveHeight() {
        return getHeight();
    }

    @Override
    public int getEffectiveWidth() {
        return getWidth();
    }

    @Override
    public void setCoordinatesSet(Set<Coordinates> coordinatesSet) {
        this.coordinatesSet = coordinatesSet;
    }

    public void setSubParts(List<T> subParts) {
        this.subParts = subParts;
    }

    public Collection<T> getSubParts() {
        return subParts;
    }

    @Override
    public String getIllumination() {
        return getPropagatedValue("illumination");
    }

    public Integer getIlluminationValue() {
        String illumination = getPropagatedValue("illumination");
        if (StringMaster.isEmpty(illumination)) {
            return 0;
        }
        return NumberUtils.getInt(illumination);
    }
    @Override
    public void setIllumination(int globalIllumination) {
        getData().setValue("illumination", "" + globalIllumination);
    }


    @Override
    public String getWallType() {
        return getPropagatedValue("wall_type");
    }

    @Override
    public void setWallType(String wallType) {
        getData().setValue("wall_type", wallType);
    }

    @Override
    public String getWallTypeAlt() {
        return getPropagatedValue("alt_wall_type");
    }

    @Override
    public void setWallTypeAlt(String wallType) {
        getData().setValue("alt_wall_type", wallType);
    }


    @Override
    public DungeonEnums.CELL_SET getCellSet() {
        return new EnumMaster<DungeonEnums.CELL_SET>().retrieveEnumConst(DungeonEnums.CELL_SET.class,
                getPropagatedValue("cell_set"));
    }

    @Override
    public int getCellSetVariant() {
        Integer cell_variant = NumberUtils.getInt(getPropagatedValue("cell_variant"));
        if (cell_variant==0) {
            return 1;
        }
        return cell_variant;
    }
    @Override
    public int getCellSetVariantAlt() {
        Integer cell_variant = NumberUtils.getInt(getPropagatedValue("cell_variant_alt"));
        if (cell_variant==0) {
            return 1;
        }
        return cell_variant;
    }

    @Override
    public void setValue(String value, String string) {
        getData().setValue(value, string);
    }

    @Override
    public DUNGEON_STYLE getStyle() {
        return new EnumMaster<DUNGEON_STYLE>().retrieveEnumConst(DUNGEON_STYLE.class,
                getPropagatedValue("style"));
    }
    public SHARD_TYPE getShardTypeAlt() {
        return new EnumMaster<SHARD_TYPE>().retrieveEnumConst(SHARD_TYPE.class,
                getPropagatedValue("shard_type_alt"));
    }

    public SHARD_TYPE  getShardType() {
        return new EnumMaster<SHARD_TYPE>().retrieveEnumConst(SHARD_TYPE.class,
                getPropagatedValue("shard_type"));
    }
    @Override
    public VFX_TEMPLATE getVfx() {
        // if (CoreEngine.TEST_LAUNCH) {
        //     return new EnumMaster<VFX_TEMPLATE>().getRandomEnumConst(VFX_TEMPLATE.class);
        // }
        return new EnumMaster<VFX_TEMPLATE>().retrieveEnumConst(VFX_TEMPLATE.class,
                getPropagatedValue("vfx_template"));
    }

    @Override
    public AMBIENCE getAmbience() {
//        zone.setStyle( new EnumMaster<DungeonEnums.DUNGEON_STYLE>().retrieveEnumConst(DungeonEnums.DUNGEON_STYLE.class,
//                getValue(ZoneData.ZONE_VALUE.style)));
        return new EnumMaster<AMBIENCE>().retrieveEnumConst(AMBIENCE.class,
                getPropagatedValue("AMBIENCE"));
    }

    @Override
    public COLOR_THEME getColorTheme() {
        return new EnumMaster<COLOR_THEME>().retrieveEnumConst(COLOR_THEME.class,
                getPropagatedValue("COLOR_THEME"));
    }
    @Override
    public CELL_PATTERN getCellPattern() {
        return new EnumMaster<CELL_PATTERN>().retrieveEnumConst(CELL_PATTERN.class,
                getPropagatedValue("CELL_PATTERN"));
    }

    @Override
    public COLOR_THEME getAltColorTheme() {
        return new EnumMaster<COLOR_THEME>().retrieveEnumConst(COLOR_THEME.class,
                getPropagatedValue("ALT_COLOR_THEME"));
    }
    public MUSIC_THEME getMusicTheme() {
        return new EnumMaster<MUSIC_THEME>().retrieveEnumConst(MUSIC_THEME.class,
                getPropagatedValue("MUSIC_THEME"));
    }
    public AudioEnums.SOUNDSCAPE getSoundscape() {
        return new EnumMaster<AudioEnums.SOUNDSCAPE>().retrieveEnumConst(AudioEnums.SOUNDSCAPE.class,
                getPropagatedValue("SOUNDSCAPE"));
    }

    public DungeonEnums.WALL_SET getWallSetAlt() {
        return new EnumMaster<DungeonEnums.WALL_SET>().retrieveEnumConst(DungeonEnums.WALL_SET.class,
                getWallTypeAlt());
    }
    public DungeonEnums.WALL_SET getWallSet() {
        return new EnumMaster<DungeonEnums.WALL_SET>().retrieveEnumConst(DungeonEnums.WALL_SET.class,
                getWallType());
    }
    @Override
    public String getName() {
        if (name == null) {
            name = getData().getValue("name");
        }
        return name;
    }

    @Override
    public int getX() {
        return origin.x;
    }

    @Override
    public int getY() {
        return origin.y;
    }

    @Override
    public int getX2() {
        return getX() + getEffectiveWidth();
    }

    @Override
    public int getY2() {
        return getY() + getEffectiveHeight();
    }

    @Override
    public void setName(String name) {
        this.name = name;
        if (getData() != null) {
            getData().setValue("name", name);
        }
    }

    @Override
    public StructureData getData() {
//        if (data == null) {
//            data = createData();
//        }
        return data;
    }

    @Override
    public void setData(StructureData data) {
        this.data = data;
    }

    @Override
    public void setStyle(DUNGEON_STYLE style) {
        getData().setValue("style", style.toString());
    }

    @Override
    public void setAltColorTheme(COLOR_THEME c) {
        getData().setValue("alt_color_theme", c.toString());
    }

    @Override
    public void setColorTheme(COLOR_THEME c) {
        getData().setValue("color_theme", c.toString());
    }

    @Override
    public int getId() {
        return getData().getIntValue("id");
    }

    @Override
    public Coordinates getCenterCoordinate() {
        return center==null ? center = CoordinatesMaster.getCenterCoordinate(getCoordinatesSet()) : center;
    }

    @Override
    public Map<Coordinates, Integer> getPatternMap() {
        return patternMap;
    }

    @Override
    public void setPatternMap(Map<Coordinates, Integer> patternMap) {
        this.patternMap = patternMap;
    }

    public Module getModule() {
        return null;
    }

    public StructAmbiData getAmbiData() {
        return ambiData;
    }

    public void setAmbiData(StructAmbiData ambiData) {
        this.ambiData = ambiData;
    }

    public Coordinates getEdge(DIRECTION d) {
        return CoordinatesMaster.getFarmostCoordinateInDirection( (d),
                  new ArrayList<>(   getCoordinatesSet()));
    }
}

package eidolons.game.battlecraft.logic.dungeon.universal;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.battlefield.vision.IlluminationMaster;
import eidolons.game.battlecraft.logic.meta.scenario.script.ScriptSyntax;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.generator.GeneratorEnums.ZONE_TYPE;
import eidolons.game.module.generator.level.ZoneCreator;
import main.content.CONTENT_CONSTS;
import main.content.DC_TYPE;
import main.content.enums.DungeonEnums;
import main.content.enums.DungeonEnums.DUNGEON_STYLE;
import main.content.enums.DungeonEnums.DUNGEON_TAGS;
import main.content.enums.DungeonEnums.DUNGEON_TYPE;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.LightweightEntity;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.auxiliary.*;
import main.system.graphics.GuiManager;
import main.system.images.ImageManager;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class Dungeon extends LightweightEntity {
    private DUNGEON_TYPE dungeonType;
    private String levelFilePath;
    private LOCATION_TYPE dungeonSubtype;
    private Map<String, String> customDataMap;
    private Collection<Coordinates> voidCells = new LinkedList<>();
    private DungeonWrapper wrapper;

    /*
     * Encounters Levels Rewards Loot
     * Atmo and background
     */
    public Dungeon(ObjType type) {
        this(type, false);
    }

    public Dungeon(ObjType type, boolean sublevel) {
        super(type);
        setRef(new Ref());
    }

    public Dungeon(String typeName, boolean sublevel) {
        this(DataManager.getType(typeName, DC_TYPE.DUNGEONS), sublevel);
    }

    public CONTENT_CONSTS.COLOR_THEME getColorTheme() {
        return wrapper.getColorTheme();
    }
    public CONTENT_CONSTS.COLOR_THEME getAltColorTheme() {
        return wrapper.getAltColorTheme();
    }

    public String getMapBackground() {
        return type.getProperty(PROPS.MAP_BACKGROUND);
    }

    public LOCATION_TYPE getDungeonSubtype() {
        if (dungeonSubtype == null) {
            dungeonSubtype = new EnumMaster<LOCATION_TYPE>().retrieveEnumConst(LOCATION_TYPE.class,
                    getProperty(PROPS.SUBDUNGEON_TYPE));
        }
        return dungeonSubtype;
    }

    public boolean isBoss() {
        return false;
    }

    public Integer getCellsX() {
        return getWidth();
    }

    public Integer getWidth() {
        return getGame().getDungeonMaster().getDungeonWrapper().getWidth();
    }

    public Integer getCellsY() {
        return getHeight();
    }

    public Integer getHeight() {
        return getGame().getDungeonMaster().getDungeonWrapper().getHeight();
    }


    public DC_Game getGame() {
        return (DC_Game) super.getGame();
    }

    public boolean isSurface() {
        return checkProperty(PROPS.DUNGEON_TAGS, DUNGEON_TAGS.SURFACE + "");
    }

    public boolean isNight() {
        return checkProperty(PROPS.DUNGEON_TAGS, DUNGEON_TAGS.NIGHT + "");
    }

    public boolean isPermanentDusk() {
        return checkProperty(PROPS.DUNGEON_TAGS, DUNGEON_TAGS.PERMA_DUSK + "");
    }

    public int getSquare() {
        return getCellsX() * getCellsY();
    }

    @Override
    public void setName(String name) {
        this.name = name;
        setProperty(G_PROPS.NAME, name, true);
        name = StringMaster.formatDisplayedName(name);
        setProperty(G_PROPS.DISPLAYED_NAME, name, true);
    }


    public Integer getGlobalIllumination() {
        if (isSurface()) {
            if (checkParam(PARAMS.GLOBAL_ILLUMINATION)) {
                return getIntParam(PARAMS.GLOBAL_ILLUMINATION);
            }// day/night
            else {
                if (isPermanentDusk())
                    return (IlluminationMaster.DEFAULT_GLOBAL_ILLUMINATION_DAY
                            + IlluminationMaster.DEFAULT_GLOBAL_ILLUMINATION_NIGHT) / 2;
                if (isDaytime())
                    return IlluminationMaster.DEFAULT_GLOBAL_ILLUMINATION_DAY;
                return IlluminationMaster.DEFAULT_GLOBAL_ILLUMINATION_NIGHT;
            }
        }

        return IlluminationMaster.DEFAULT_GLOBAL_ILLUMINATION_UNDERGROUND;

    }
    @Deprecated
    public Coordinates getPoint(Integer index) {
        return getPoint("" + (index + 1));
    }
@Deprecated
    public Coordinates getPoint(String arg) {
        Coordinates c = null;
        if (arg.contains(ScriptSyntax.SPAWN_POINT) || NumberUtils.isInteger(arg)) {
            arg = arg.replace(ScriptSyntax.SPAWN_POINT, "");
            Integer i = NumberUtils.getInteger(arg) - 1;
//            List<String> spawnPoints = ContainerUtils.openContainer(
//                    getProperty(PROPS.COORDINATE_POINTS));
//            c = Coordinates.get(spawnPoints.get(i));
        } else {
            Map<String, String> map =getCustomDataMap();
            String string = map.get(arg);
            if (string == null) {
                //find
                Object key = new SearchMaster<>().findClosest(arg, map.keySet());
                string = map.get(key);
            }
            return Coordinates.get(string);
        }
        return c;
//        getProperty(PROPS.ENCOUNTER_SPAWN_POINTS)
    }

    private boolean isDaytime() {
        return !isNight();
//        return getGame().getState().getRound()/roundsPerCycle%2==0;
    }

    public boolean isRandomized() {
        // TODO
        return false;
    }

    public String getLevelFilePath() {
        return levelFilePath;
    }

    public void setLevelFilePath(String levelFilePath) {
        this.levelFilePath = levelFilePath;
    }

    public DUNGEON_STYLE getStyle() {
        return ZoneCreator.getStyle(ZONE_TYPE.OUTSKIRTS, getDungeonSubtype());
    }


    public int getCellVariant(int i, int j) {
        return getGame().getDungeonMaster().getDungeonLevel().getCellVariant(i, j);
    }
    public DungeonEnums.CELL_IMAGE getCellType(int i, int j) {
        return getGame().getDungeonMaster().getDungeonLevel().getCellType(i, j);
    }

    public String getCellImagePath(int i, int j) {
        if (!getProperty(G_PROPS.DUNGEON_GROUP).isEmpty()) {
            DungeonEnums.DUNGEON_GROUP group = new EnumMaster<DungeonEnums.DUNGEON_GROUP>().retrieveEnumConst(DungeonEnums.DUNGEON_GROUP.class,
                   getProperty(G_PROPS.DUNGEON_GROUP));
            if (group != null)
            return StrPathBuilder.build(PathFinder.getCellImagesPath(),
                        getCellImgForGroup(group)+ ".png");

        }
        
        if (getGame().getDungeonMaster().getDungeonLevel() == null) {
            if (isSurface()) {

            }
        } else {
            return getGame().getDungeonMaster().getDungeonLevel().getCellImgPath(i, j);

        }
        return ImageManager.getEmptyCellPath(GuiManager.getBfCellsVersion());
    }

    private String getCellImgForGroup(DungeonEnums.DUNGEON_GROUP group) {
        switch(group){
            case UNDERWORLD:
                return DungeonEnums.CELL_IMAGE.octagonal.toString();
            case ARCANE:
                return DungeonEnums.CELL_IMAGE.star.toString();
            case UNDEAD:
                return DungeonEnums.CELL_IMAGE.circle.toString();
            case HUMAN:
                return DungeonEnums.CELL_IMAGE.cross.toString();
            case MISC:
                return DungeonEnums.CELL_IMAGE.natural.toString();
        }
        return null;
    }

@Deprecated
    public Map<String, String> getCustomDataMap() {
        if (customDataMap == null) {
            return new LinkedHashMap<>();
//      TODO       customDataMap= new DataUnitFactory(true).
//                    deconstructDataString(getProperty(PROPS.COORDINATE_SCRIPTS));
        }
        return customDataMap;
    }

    public Coordinates getCoordinateByName(String value) {
        for (String s : getCustomDataMap().keySet()) {
            if (getCustomDataMap().get(s).trim().equalsIgnoreCase(value)) {
                return Coordinates.get(s);
            }
        }
        return null;
    }

    public Collection<Coordinates> getVoidCells() {
        return voidCells;
    }

    public void setWrapper(DungeonWrapper wrapper) {
        this.wrapper = wrapper;
    }

    public DungeonWrapper getWrapper() {
        return wrapper;
    }


    public enum POINTS {
        CENTER_SPAWN,
        REAR_SPAWN,
        SCOUTS_SPAWN,

    }

}

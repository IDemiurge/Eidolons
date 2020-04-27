package eidolons.game.battlecraft.logic.dungeon.universal;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.battlefield.vision.IlluminationMaster;
import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.meta.scenario.script.CellScriptData;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.generator.GeneratorEnums.ZONE_TYPE;
import eidolons.game.module.generator.level.ZoneCreator;
import main.content.CONTENT_CONSTS;
import main.content.enums.DungeonEnums;
import main.content.enums.DungeonEnums.DUNGEON_STYLE;
import main.content.enums.DungeonEnums.DUNGEON_TAGS;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.values.properties.G_PROPS;
import main.data.filesys.PathFinder;
import main.entity.LightweightEntity;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Dungeon extends LightweightEntity {
    private String levelFilePath;
    private LOCATION_TYPE dungeonSubtype;
    private Collection<Coordinates> voidCells = new LinkedList<>();
    private Location location;

    public Dungeon(ObjType type ) {
        super(type);
        setRef(new Ref());
    }


    public CONTENT_CONSTS.COLOR_THEME getColorTheme() {
        return location.getColorTheme();
    }
    public CONTENT_CONSTS.COLOR_THEME getAltColorTheme() {
        return location.getAltColorTheme();
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
        return getGame().getDungeonMaster().getLocation().getWidth();
    }

    public Integer getCellsY() {
        return getHeight();
    }

    public Integer getHeight() {
        return getGame().getDungeonMaster().getLocation().getHeight();
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

    private boolean isDaytime() {
        return !isNight();
//        return getGame().getState().getRound()/roundsPerCycle%2==0;
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
        return getGame().getDungeonMaster().getStructureMaster().getCellVariant(i, j);
    }
    public DungeonEnums.CELL_IMAGE getCellType(int i, int j) {
        return  DungeonEnums.CELL_IMAGE.tiles;
    }

    private DungeonEnums.CELL_IMAGE getCellImageType(DUNGEON_STYLE style) {
        switch (style) {
            case Somber:
                if (RandomWizard.chance(56))
                    return DungeonEnums.CELL_IMAGE.star;
                if (RandomWizard.chance(66))
                    return DungeonEnums.CELL_IMAGE.octagonal;
            case DWARF:
                if (RandomWizard.chance(66))
                    return DungeonEnums.CELL_IMAGE.diamond;
                return DungeonEnums.CELL_IMAGE.octagonal;
            case SPIDER:
            case Stony:
            case Pagan:
                return DungeonEnums.CELL_IMAGE.natural;
            case ROGUE:
                if (RandomWizard.chance(66))
                    return DungeonEnums.CELL_IMAGE.tiles;
                return DungeonEnums.CELL_IMAGE.cross;

            case Knightly:
            case Holy:
                if (RandomWizard.chance(66)) {
                    return DungeonEnums.CELL_IMAGE.cross;
                }
                return DungeonEnums.CELL_IMAGE.diamond;
            case DarkElegance:
            case PureEvil:
                if (RandomWizard.chance(66))
                    return DungeonEnums.CELL_IMAGE.octagonal;
            case Brimstone:
//                if (RandomWizard.chance(66))
//                    return CELL_IMAGE.circle;
            case Grimy:
                if (RandomWizard.chance(66))
                    return DungeonEnums.CELL_IMAGE.tiles;
            case Cold:
            case Arcane:
                if (RandomWizard.chance(66))
                    return DungeonEnums.CELL_IMAGE.star;
        }
        return DungeonEnums.CELL_IMAGE.tiles;
    }
    public String getCellImagePath(int i, int j) {
        if (!getProperty(G_PROPS.DUNGEON_GROUP).isEmpty()) {
            DungeonEnums.DUNGEON_GROUP group = new EnumMaster<DungeonEnums.DUNGEON_GROUP>().retrieveEnumConst(DungeonEnums.DUNGEON_GROUP.class,
                   getProperty(G_PROPS.DUNGEON_GROUP));
            if (group != null)
            return StrPathBuilder.build(PathFinder.getCellImagesPath(),
                        getCellImgForGroup(group)+ ".png");

        }
        
        return ImageManager.getEmptyCellPath(1);
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

    public Map<String, String> getCustomDataMap(CellScriptData.CELL_SCRIPT_VALUE value) {
        return new HashMap<>(); //TODO
    }
    public Map<Coordinates, CellScriptData> getCustomDataMap() {
        return getLocation().getTextDataMap();
    }

    public Coordinates getCoordinateByName(String value) {
        Map<String, String> map = getCustomDataMap(CellScriptData.CELL_SCRIPT_VALUE.named_point);
        for (String s : map.keySet()) {
            if (map.get(s).trim().equalsIgnoreCase(value)) {
                return Coordinates.get(s);
            }
        }
        return null;
    }

    public Collection<Coordinates> getVoidCells() {
        return voidCells;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }


    public enum POINTS {
        CENTER_SPAWN,
        REAR_SPAWN,
        SCOUTS_SPAWN,

    }

}

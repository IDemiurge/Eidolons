package eidolons.game.module.dungeoncrawl.dungeon;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.battlecraft.logic.dungeon.location.struct.BlockData;
import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel.CELL_IMAGE;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.init.RngMainSpawner.UNIT_GROUP_TYPE;
import eidolons.game.module.dungeoncrawl.generator.init.RngXmlMaster;
import eidolons.game.module.dungeoncrawl.generator.model.RoomModel;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMap;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMapper;
import main.content.enums.DungeonEnums.DUNGEON_STYLE;
import main.data.XLinkedMap;
import main.data.xml.XML_Converter;
import main.entity.type.ObjAtCoordinate;
import main.game.bf.Coordinates;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.data.MapMaster;

import java.util.*;

/**
 * Created by JustMe on 7/20/2018.
 */
public class LevelBlock extends LevelLayer<LevelBlock> {
    private Coordinates origin;
    private ROOM_TYPE roomType;
    private int width;
    private int height;
    private Set<Coordinates> coordinatesList;
    private TileMap tileMap;
    private TileMap originalTileMap;
    private LevelZone zone;
    private Map<Coordinates, Coordinates> boundCells;
    private Map<List<ObjAtCoordinate>, UNIT_GROUP_TYPE> unitGroups;
    private RoomModel model;
    private boolean template;
    private BlockData data;
    private int zoneIndex;

    public LevelBlock(Coordinates coordinates, LevelZone zone, ROOM_TYPE roomType, int width, int height, TileMap tileMap) {
        this.roomType = roomType;
        this.width = width;
        this.height = height;
        this.tileMap = tileMap;
        this.originalTileMap = new TileMap(
                new MapMaster<Coordinates, ROOM_CELL>().cloneHashMap(
                        tileMap.getMap()));
        this.origin = coordinates;
        this.zone = zone;

    }

    public LevelBlock(LevelZone zone) {
        this.zone = zone;
    }

    public LevelBlock(RoomModel blockTemplate, LevelZone zone) {
        this(zone);
        unitGroups = new LinkedHashMap<>();
        template = true;
        this.model = blockTemplate;
        setTileMap(TileMapper.createTileMap(blockTemplate.getCells()));
    }

    public ROOM_TYPE getRoomType() {
        return roomType;
    }

    public void setRoomType(ROOM_TYPE roomType) {
        this.roomType = roomType;
    }

    @Override
    public String toString() {
        if (tileMap == null) {
            return getRoomType() + " block of " + zone;
        }
        return getRoomType() + " block of " + zone
                + "\n" + tileMap.toString();
    }

    @Override
    public String toXml() {
        String xml = "";
        if (roomType != null) {
            xml += XML_Converter.wrap(RngXmlMaster.BLOCK_ROOM_TYPE_NODE, roomType.name());
        }

//     TODO    if (isCustomCoordinateList())
        xml += XML_Converter.wrap(RngXmlMaster.COORDINATES_NODE, ContainerUtils.
                toStringContainer(getCoordinatesSet(), RngXmlMaster.SEPARATOR));


//        xml += XML_Converter.wrap(RngXmlMaster.COLOR_THEME, colorTheme.toString());
//        xml += XML_Converter.wrap(RngXmlMaster.COLOR_THEME_ALT, altColorTheme.toString());
        return xml;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Deprecated
    public List<ObjAtCoordinate> getUnits() {
        return new ArrayList<>();
    }

    @Deprecated
    public List<ObjAtCoordinate> getObjects() {
        return new ArrayList<>();
    }

    public Set<Coordinates> getCoordinatesSet() {
        if (coordinatesList == null) {
            if (tileMap == null) {
                return new LinkedHashSet<>();
            }
            coordinatesList = new LinkedHashSet<>(
                    tileMap.getMap().keySet()
//              .stream().map(c ->
//              c.getOffset(getCoordinates())).collect(Collectors.toSet())
            );
        }
        return coordinatesList;
    }

    public void setCoordinatesList(List<Coordinates> coordinatesList) {
        coordinatesList.removeIf(c -> c == null);
        this.coordinatesList = new LinkedHashSet<>(coordinatesList);

    }

    public DUNGEON_STYLE getStyle() {
        return zone.getStyle();
    }

    public TileMap getTileMap() {
        return tileMap;
    }

    public void setTileMap(TileMap tileMap) {
        this.tileMap = tileMap;
    }

    public Coordinates getOrigin() {
        if (origin == null) {
            return Coordinates.get(0, 0);
        }
        return origin;
    }

    public LevelZone getZone() {
        if (zone == null) {
            zone = DC_Game.game.getMetaMaster().getDungeonMaster().
                    getDungeonLevel().getZoneById(getZoneIndex());
        }
        return zone;
    }

    public Map<Coordinates, Coordinates> getBoundCells() {
        if (boundCells == null) {
            boundCells = new XLinkedMap<>();
        }
        return boundCells;
    }


    public void offsetCoordinates() {
        offsetCoordinates(getOrigin());
    }

    public void offsetCoordinates(Coordinates offset) {
        coordinatesList.forEach(c -> c.offset(offset));
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setOrigin(Coordinates coordinates) {
        this.origin = coordinates;
    }

    public int getSquare() {
        return getWidth() * getHeight();
    }

    public TileMap getOriginalTileMap() {
        return originalTileMap;
    }

    public Map<List<ObjAtCoordinate>, UNIT_GROUP_TYPE> getUnitGroups() {
        if (unitGroups == null) {
            unitGroups = new XLinkedMap<>();
        }
        return unitGroups;
    }

    public Coordinates getCenterCoordinate() {
        return origin.getOffsetByX(getWidth() / 2).getOffsetByY(getHeight() / 2);
    }

    public void setZone(LevelZone zone) {
        this.zone = zone;
    }

    public boolean isTemplate() {
        return template;
    }

    public void setTemplate(boolean template) {
        this.template = template;
    }

    public RoomModel getModel() {
        return model;
    }

    public CELL_IMAGE getCellType() {
        if (getData() == null) {
            return null;
        }
        return new EnumMaster<CELL_IMAGE>().retrieveEnumConst(CELL_IMAGE.class,
                getData().getValue(LevelStructure.BLOCK_VALUE.cell_type));
    }

    public void setCellType(CELL_IMAGE cellType) {
        getData().setValue(LevelStructure.BLOCK_VALUE.cell_type, cellType.toString());
    }

    public String getWallType() {
        if (getData() == null) {
            return null;
        }
        return getData().getValue(LevelStructure.BLOCK_VALUE.wall_type);
    }

    public void setWallType(String wallType) {
        getData().setValue(LevelStructure.BLOCK_VALUE.wall_type, wallType);
    }


    public void setData(BlockData data) {
        this.data = data;
    }

    public BlockData getData() {
        return data;
    }

    public void setZoneIndex(int zoneIndex) {
        this.zoneIndex = zoneIndex;
    }

    public int getZoneIndex() {
        return zoneIndex;
    }
}

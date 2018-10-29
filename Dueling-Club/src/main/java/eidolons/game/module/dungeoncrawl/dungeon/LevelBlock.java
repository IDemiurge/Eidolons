package eidolons.game.module.dungeoncrawl.dungeon;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.init.RngMainSpawner.UNIT_GROUP_TYPE;
import eidolons.game.module.dungeoncrawl.generator.init.RngXmlMaster;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileConverter.DUNGEON_STYLE;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMap;
import main.data.XLinkedMap;
import main.data.xml.XML_Converter;
import main.entity.type.ObjAtCoordinate;
import main.game.bf.Coordinates;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.MapMaster;

import java.util.*;

/**
 * Created by JustMe on 7/20/2018.
 */
public class LevelBlock extends LevelLayer<LevelBlock> {
    Coordinates coordinates;
    private ROOM_TYPE roomType;
    private int width;
    private int height;
    private List<ObjAtCoordinate> units = new ArrayList<>();
    private List<ObjAtCoordinate> objects = new ArrayList<>();
    private Set<Coordinates> coordinatesList;
    private TileMap tileMap;
    private TileMap originalTileMap;
    private LevelZone zone;
    private Map<Coordinates, Coordinates> boundCells;
    private boolean customCoordinateList;
    private Map<List<ObjAtCoordinate>, UNIT_GROUP_TYPE> unitGroups;
    private Coordinates centerCoordinate;

    public LevelBlock(Coordinates coordinates, LevelZone zone, ROOM_TYPE roomType, int width, int height, TileMap tileMap) {
        this.roomType = roomType;
        this.width = width;
        this.height = height;
        this.tileMap = tileMap;
        this.originalTileMap = new TileMap(
         new MapMaster<Coordinates, ROOM_CELL>().cloneHashMap(
         tileMap.getMap()));
        this.coordinates = coordinates;
        this.zone = zone;
        if (RandomWizard.chance(75)) {
            setColorTheme(zone.getColorTheme());
            setAltColorTheme(zone.getAltColorTheme());
        } else {
            setColorTheme(zone.getAltColorTheme());
            setAltColorTheme(zone.getColorTheme());
        }
    }

    public LevelBlock(LevelZone zone) {
        this.zone = zone;
    }

    public ROOM_TYPE getRoomType() {
        return roomType;
    }

    public void setRoomType(ROOM_TYPE roomType) {
        this.roomType = roomType;
    }

    @Override
    public String toString() {
        return getRoomType() + " block of " + zone
         + "\n" + tileMap.toString();
    }

    @Override
    public String toXml() {
        String xml = "";
        xml += XML_Converter.wrap(RngXmlMaster.BLOCK_ROOM_TYPE_NODE, roomType.name());

//     TODO    if (isCustomCoordinateList())
            xml += XML_Converter.wrap(RngXmlMaster.COORDINATES_NODE, ContainerUtils.
             toStringContainer(getCoordinatesList(), RngXmlMaster.SEPARATOR));

        xml += XML_Converter.wrap(RngXmlMaster.UNITS_NODE, ContainerUtils.
         toStringContainer(units, RngXmlMaster.SEPARATOR));
        xml += XML_Converter.wrap(RngXmlMaster.OBJECTS_NODE, ContainerUtils.
         toStringContainer(objects, RngXmlMaster.SEPARATOR));


        xml += XML_Converter.wrap(RngXmlMaster.COLOR_THEME, colorTheme.toString());
        xml += XML_Converter.wrap(RngXmlMaster.COLOR_THEME_ALT, altColorTheme.toString());
        return xml;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<ObjAtCoordinate> getUnits() {
        return units;
    }

    public List<ObjAtCoordinate> getObjects() {
        return objects;
    }

    public Set<Coordinates> getCoordinatesList() {
        if (coordinatesList == null)
            coordinatesList = new LinkedHashSet<>(
             tileMap.getMap().keySet()
//              .stream().map(c ->
//              c.getOffset(getCoordinates())).collect(Collectors.toSet())
            );
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

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public LevelZone getZone() {
        return zone;
    }

    public Map<Coordinates, Coordinates> getBoundCells() {
        if (boundCells == null) {
            boundCells = new XLinkedMap<>();
        }
        return boundCells;
    }


    public void offsetCoordinates() {
        offsetCoordinates(getCoordinates());
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

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public int getSquare() {
        return getWidth() *  getHeight();
    }

    public TileMap getOriginalTileMap() {
        return originalTileMap;
    }

    public boolean isCustomCoordinateList() {
        return customCoordinateList;
    }

    public void setCustomCoordinateList(boolean customCoordinateList) {
        this.customCoordinateList = customCoordinateList;
    }

    public Map<List<ObjAtCoordinate>, UNIT_GROUP_TYPE> getUnitGroups() {
        if (unitGroups == null) {
            unitGroups = new XLinkedMap<>();
        }
        return unitGroups;
    }


    public Coordinates getCenterCoordinate() {
        if (centerCoordinate == null) {
        centerCoordinate = coordinates.getOffsetByX(getWidth() / 2).getOffsetByY(getHeight() / 2);
    }
        return centerCoordinate;
    }

}

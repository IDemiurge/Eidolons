package eidolons.game.module.dungeoncrawl.dungeon;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.battlecraft.logic.dungeon.location.struct.BlockData;
import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.game.battlecraft.logic.dungeon.location.struct.wrapper.ObjNode;
import eidolons.game.battlecraft.logic.dungeon.location.struct.wrapper.ObjsNode;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.generator.init.RngXmlMaster;
import eidolons.game.module.generator.model.RoomModel;
import eidolons.game.module.generator.tilemap.TileMap;
import eidolons.game.module.generator.tilemap.TileMapper;
import main.content.DC_TYPE;
import main.content.enums.EncounterEnums.UNIT_GROUP_TYPE;
import main.data.XLinkedMap;
import main.data.tree.LayeredData;
import main.data.xml.XML_Converter;
import main.entity.type.ObjAtCoordinate;
import main.game.bf.Coordinates;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.data.MapMaster;
import main.system.launch.CoreEngine;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 7/20/2018.
 */
public class LevelBlock extends LevelStruct<LevelBlock, Object> {

    private ROOM_TYPE roomType;
    private RoomModel model;
    private TileMap tileMap;
    private TileMap originalTileMap;
    private Map<Coordinates, Coordinates> boundCells;
    private Map<List<ObjAtCoordinate>, UNIT_GROUP_TYPE> unitGroups;
    private boolean template;
    private LevelZone zone;
    private int id;
    private static Integer ID = 0;

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
        id = ID++;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Collection  getChildren() {
        if (!CoreEngine.isLevelEditor()) {
            return new ArrayList<>();
        }
            LinkedHashSet<LayeredData> objs = DC_Game.game.getBfObjects().stream().filter(
                    obj -> isWithinBlock(obj) && obj.getOBJ_TYPE_ENUM() == DC_TYPE.ENCOUNTERS).map(
                    obj -> new ObjNode(obj)).collect(Collectors.toCollection(LinkedHashSet::new));
            objs.add(new ObjsNode(this));
            return objs;
    }
    private boolean isWithinBlock(BattleFieldObject obj) {
        return  getCoordinatesSet().contains(obj.getCoordinates());
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

    @Override
    protected LevelStruct getParent() {
        return zone;
    }

    public ROOM_TYPE getRoomType() {
        return roomType;
    }

    public void setRoomType(ROOM_TYPE roomType) {
        this.roomType = roomType;
        getData().setValue(LevelStructure.BLOCK_VALUE.room_type, roomType.toString());
    }

    @Override
    public String toString() {
        if (tileMap == null) {
            return getRoomType() + " block N" +id;
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
        xml += XML_Converter.wrap(RngXmlMaster.COORDINATES_NODE, ContainerUtils.
                toStringContainer(getCoordinatesSet(), RngXmlMaster.SEPARATOR));
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
        if (coordinatesSet == null) {
            coordinatesSet = new LinkedHashSet<>();
            for (int x = 0; x < getWidth(); x++) {
                for (int y = 0; y < getHeight(); y++) {
                    coordinatesSet.add( Coordinates.get(x, y).getOffset(getOrigin()));
                }
            }
        }
        return coordinatesSet;
    }

    public void setCoordinates(Collection<Coordinates> coordinatesList) {
        coordinatesList.removeIf(c -> c == null);
        this.coordinatesSet = new LinkedHashSet<>(coordinatesList);

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
        return zone;
    }

    public Map<Coordinates, Coordinates> getBoundCells() {
        if (boundCells == null) {
            boundCells = new XLinkedMap<>();
        }
        return boundCells;
    }


    public void offsetCoordinates(Coordinates offset) {
        coordinatesSet.forEach(c -> c.offset(offset));
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



    public void setData(BlockData data) {
        this.data = data;
    }

    public BlockData getData() {
        return (BlockData) data;
    }

    public void setModel(RoomModel blockTemplate) {
        model = blockTemplate;
        if (model != null) {
            setWidth(model.getWidth());
            setHeight(model.getHeight());
        }
    }
}

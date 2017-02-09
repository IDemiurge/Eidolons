package main.game.logic.dungeon.building;

import main.content.parameters.G_PARAMS;
import main.data.XLinkedMap;
import main.data.xml.XML_Converter;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.game.DC_Game;
import main.game.battlefield.Coordinates;
import main.game.battlefield.ZCoordinates;
import main.game.logic.dungeon.building.DungeonBuilder.BLOCK_TYPE;
import main.game.logic.dungeon.building.DungeonBuilder.ROOM_TYPE;
import main.game.logic.macro.utils.CoordinatesMaster;
import main.system.auxiliary.ListMaster;
import main.system.auxiliary.StringMaster;
import main.system.datatypes.DequeImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MapBlock {
    int id;
    BLOCK_TYPE type;
    DequeImpl<Obj> objects;
    List<Coordinates> coordinates;
    Map<Coordinates, Obj> map = new HashMap<Coordinates, Obj>();
    Map<MapBlock, List<Coordinates>> connectedBlocks = new HashMap<MapBlock, List<Coordinates>>();
    int spawningPriority;
    int treasureValue;
    private MapZone zone;
    private ROOM_TYPE roomType;
    private Coordinates keyCoordinate;
    private String xml;

    public MapBlock(int id, BLOCK_TYPE b, MapZone zone, DungeonPlan plan,
                    List<Coordinates> coordinates) {
        this.type = b;
        this.coordinates = coordinates;
        this.zone = zone;
        this.id = id;
        if (zone != null) {
            zone.addBlock(this);
        }
        if (plan != null) {
            plan.addBlock(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MapBlock) {
            MapBlock mapBlock = (MapBlock) obj;
            if (!mapBlock.getCoordinates().equals(getCoordinates())) {
                return false;
            }
            return true;
        }
        return false;
    }

    public void link(MapBlock lastBlock, Coordinates... coordinates) {
        List<Coordinates> list = new ListMaster<Coordinates>().getList(coordinates);
        connectedBlocks.put(lastBlock, list);
        main.system.auxiliary.LogMaster.log(1, this + " linked to " + lastBlock.toString() + " on "
                + list);
    }

    public String toString() {
        String string = getShortName();
        return string + "; Zone # " + zone.getI() + "; "
                + CoordinatesMaster.getBoundsFromCoordinates(coordinates);
    }

    public String getXml() {
        resetObjects();
        xml = "";
        xml += XML_Converter.openXmlFormatted(getShortName());
        xml += XML_Converter.wrapLeaf(DungeonBuilder.BLOCK_TYPE_NODE, type.getName());
        if (roomType != null) {
            xml += XML_Converter.wrapLeaf(DungeonBuilder.ROOM_TYPE_NODE, roomType.toString());

        }
        xml += XML_Converter.openXmlFormatted(DungeonBuilder.COORDINATES_NODE);
        for (Coordinates c : coordinates) {
            xml += c.toString() + ";";
        }
        xml += XML_Converter.closeXmlFormatted(DungeonBuilder.COORDINATES_NODE);

        xml += XML_Converter.openXmlFormatted(DungeonBuilder.OBJ_NODE);

        for (Coordinates c : map.keySet()) {
            Obj obj = map.get(c);
            String name = obj.getName();
            Integer chance = obj.getIntParam(G_PARAMS.CHANCE);
            if (chance > 0) {
                name += StringMaster.wrapInParenthesis(chance + "%");
            }

            xml += c + "=" + name + ",";
        }
        // ++ random objects
        xml += XML_Converter.closeXmlFormatted(DungeonBuilder.OBJ_NODE);
        xml += XML_Converter.closeXmlFormatted(getShortName());
        // objects?
        return xml;
    }

    public void resetObjects() {
        setObjects(getObjectsByCoordinates());
        map.clear();
        for (Coordinates c : getCoordinates()) {
            for (DC_HeroObj obj : DC_Game.game.getObjectsOnCoordinate(c)) {
                if (map.containsKey(c)) {
                    ZCoordinates coordinates = new ZCoordinates(c.x, c.y, new Random().nextInt());
                    map.put(coordinates, obj);
                } else {
                    map.put(c, obj);
                }
            }
            for (DC_HeroObj obj : DC_Game.game.getOverlayingObjects(c)) {
                if (map.containsKey(c)) {
                    ZCoordinates coordinates = new ZCoordinates(c.x, c.y, new Random().nextInt());
                    map.put(coordinates, obj);
                } else {
                    map.put(c, obj);
                }
            }
        }
    }

    public DequeImpl<Obj> getObjectsByCoordinates() {
        DequeImpl<Obj> objects = new DequeImpl<>(DC_Game.game.getUnitsForCoordinates(coordinates
                .toArray(new Coordinates[coordinates.size()])));

        return objects;
    }

    public DequeImpl<Obj> getObjects() {
        if (objects == null) {
            objects = new DequeImpl<>();
        }
        return objects;
    }

    public void setObjects(DequeImpl<Obj> objects) {
        this.objects = objects;
    }

    public String getShortName() {
        String name = type.getName();
        if (roomType != null) {
            name = StringMaster.getWellFormattedString(roomType + "");
        }
        return name + "-" + id;
    }

    public BLOCK_TYPE getType() {
        return type;
    }

    public void setType(BLOCK_TYPE type2) {
        type = type2;
    }

    public int getSquare() {
        return coordinates.size();
    }

    public List<Coordinates> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Coordinates> coordinates) {
        this.coordinates = coordinates;
    }

    public Map<MapBlock, List<Coordinates>> getConnectedBlocks() {
        if (connectedBlocks == null) {
            connectedBlocks = new XLinkedMap();
        }
        return connectedBlocks;
    }

    public int getSpawningPriority() {
        return spawningPriority;
    }

    public void setSpawningPriority(int spawningPriority) {
        this.spawningPriority = spawningPriority;
    }

    public int getTreasureValue() {
        return treasureValue;
    }

    public void setTreasureValue(int treasureValue) {
        this.treasureValue = treasureValue;
    }

    public int getWidth() {
        return CoordinatesMaster.getRangeX(coordinates);
    }

    public int getHeight() {
        return CoordinatesMaster.getRangeY(coordinates);
    }

    // ai priority , ...
    public ROOM_TYPE getRoomType() {
        return roomType;
    }

    // if (block.isEntrance())
    // block.isTreasureRoom()
    public void setRoomType(ROOM_TYPE roomType) {
        this.roomType = roomType;
    }

    public void addObject(DC_HeroObj obj, Coordinates c) {

        if (map.containsKey(c)) {
            ZCoordinates coordinates = new ZCoordinates(c.x, c.y, new Random().nextInt());
            map.put(coordinates, obj);
        } else {
            getMap().put(c, obj);
        }
        getObjects().add(obj);
    }

    public boolean removeObject(DC_Obj obj, Coordinates c) {
        getMap().remove(c);
        return !getObjects().remove(obj);
    }

    public Coordinates getKeyCoordinate() {
        return keyCoordinate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MapZone getZone() {
        return zone;
    }

    public void setZone(MapZone zone) {
        this.zone = zone;

    }

    public void addCoordinate(Coordinates coordinate) {
        if (!this.coordinates.contains(coordinate)) {
            coordinates.add(coordinate);
        }
    }

    public Map<Coordinates, Obj> getMap() {
        return map;
    }

}

package eidolons.game.module.dungeoncrawl.generator.model;

import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.LevelGenerator;
import eidolons.game.module.dungeoncrawl.generator.graph.LevelGraph;
import eidolons.game.module.dungeoncrawl.generator.graph.LevelGraphNode;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMapper;
import main.data.XLinkedMap;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.Loop;
import main.system.auxiliary.data.MapMaster;

import java.util.*;

import static main.system.auxiliary.log.LogMaster.log;

/**
 * Created by JustMe on 2/13/2018.
 * <p>
 * from this, it must be possible to create a real level with specific objects
 */
public class LevelModel {
    ROOM_CELL[][] cells; //enum?
    List<LevelZone> zones;
    Map<Room, LevelBlock> blocks = new LinkedHashMap<>();
    Map<Room, Room> merged = new LinkedHashMap<>();
    Map<Coordinates, Room> roomMap = new LinkedHashMap<>();
    LevelData data;
    private Set<Coordinates> occupiedCells = new LinkedHashSet<>();
    private int leftMost;
    private int rightMost;
    private int topMost;
    private int bottomMost;
    private LevelModelBuilder builder;
    private Map<Coordinates, ROOM_CELL> additionalCells = new XLinkedMap<>();
    private LevelGraph graph;
    private Map<Room, List<Room>> roomLinkMap;

    public LevelModel(LevelData data, LevelModelBuilder builder) {
        this.data = data;
        this.builder = builder;
    }


    @Override
    public String toString() {

        return "LevelModel: \n" + "; total rooms=" + roomMap.size()
         + "; " +
         toASCII_Map();
        //                 new ArrayMaster<ROOM_CELL>().getCellsString(cells);

    }

    private void addCapes(Coordinates p, RoomModel roomModel) {
        int x = p.x;
        int x1 = p.x + roomModel.getWidth();
        int y = p.y;
        int y1 = p.y + roomModel.getHeight();
        if (roomMap.isEmpty() || x < leftMost)
            leftMost = x;
        if (roomMap.isEmpty()|| x1 > rightMost)
            rightMost = x1;
        if (roomMap.isEmpty() || y < topMost)
            topMost = y;
        if (roomMap.isEmpty() || y1 > bottomMost)
            bottomMost = y1;

    }

    public int getCurrentWidth() {
        return Math.abs(leftMost - rightMost) + 1;
    }

    public int getCurrentHeight() {
        return Math.abs(topMost - bottomMost) + 1;
    }


    public void addRoom(Room room) {
        Coordinates p = room.getCoordinates();

        addCapes(p, room);
        roomMap.put(p, room);
        addOccupied(p, room);
        rebuildCells();
        if (!LevelGenerator.LOGGING_OFF)
            log(1, "Placed " + room + " at " +
             p.x + " " + p.y + "; " + toString());

    }

    public void rebuildCells() {
        setCells(new TileMapper(this, data).build(this));
    }


    private void addOccupied(Coordinates p, RoomModel roomModel) {
        occupied(false, p, roomModel);
    }

    private void occupied(boolean remove, Coordinates p, RoomModel roomModel) { //check wrap?
        for (int x = p.x; x < p.x + roomModel.getWidth(); x++) {
            for (int y = p.y; y < p.y + roomModel.getHeight(); y++) {
                Coordinates point = new AbstractCoordinates(x, y);
                if (remove)
                    occupiedCells.remove(point);
                else
                    occupiedCells.add(point);
            }
        }
    }

    //TODO ambitious...
    public void merge(Room room, Room room2) {
        //        model = new RoomModel(cells, ROOM_TYPE.MERGED, EXIT_TEMPLATE.CROSSROAD);
        //        Room merged = new Room(p, model, room.getEntrance());

        // just make them close and mark as merged for Blocks

        room2.setZone(room.getZone());

        FACING_DIRECTION side = room2.getEntrance().flip();
        shearWallsFromSide(room, side);
        side = room2.getEntrance();
        shearWallsFromSide(room2, side);

        merged.put(room, room2);
        merged.put(room2, room);
    }

    public Map<Room, Room> getMerged() {
        return merged;
    }

    public void offset(Room room, FACING_DIRECTION to) {
        shearWallsFromSide(room, to, true);
    }

    public void shearWallsFromSide(Room room, FACING_DIRECTION entrance) {
        shearWallsFromSide(room, entrance, false);
    }

    public void shearWallsFromSide(Room room, FACING_DIRECTION entrance, boolean offsetOnly) {
        remove(room);
        Coordinates newCoordinates = room.shearWallsFromSide(entrance, offsetOnly);
        room.setCoordinates(newCoordinates);
        //TODO can lead to path blocking!!!
        addRoom(room);
    }

    public void remove(Room room) {
        roomMap.remove(room.getCoordinates());
        occupied(true, room.getCoordinates(), room);
        rebuildCells();
        if (!LevelGenerator.LOGGING_OFF)
            log(1, "Removed " + room + "\n" + toString());
    }

    public Set<Coordinates> getOccupiedCells() {
        return occupiedCells;
    }

    public ROOM_CELL[][] getCells() {
        return cells;
    }

    public void setCells(ROOM_CELL[][] cells) {
        this.cells = cells;
    }

    public Map<Coordinates, Room> getRoomMap() {
        return roomMap;
    }

    public Integer getLeftMost() {
        return leftMost;
    }

    public Integer getRightMost() {
        return rightMost;
    }

    public Integer getTopMost() {
        return topMost;
    }

    public Integer getBottomMost() {
        return bottomMost;
    }

    public LevelData getData() {
        return data;
    }

    public List<LevelZone> getZones() {
        return zones;
    }

    public void setZones(List<LevelZone> zones) {
        this.zones = zones;
    }

    public Map<Room, LevelBlock> getBlocks() {
        return blocks;
    }

    public String toASCII_Map() {
        return toASCII_Map(false);
    }

    public String toASCII_Map(boolean nullToX) {
        return TileMapper.toASCII_String(cells, nullToX);

    }

    public LevelZone getZone(LevelGraphNode node) {
        return
         zones.toArray(new LevelZone[0])[node.getZoneIndex()];
    }

    public Room getRoom(LevelBlock block) {
        return (Room) MapMaster.getKeyForValue_(blocks, block);
    }

    public LevelModelBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(LevelModelBuilder builder) {
        this.builder = builder;
    }

    public void offsetCoordinates() {
        if (!(leftMost != 0 || topMost != 0))
         return;
        AbstractCoordinates offset = new AbstractCoordinates(
         -getLeftMost(),
         -getTopMost());
        main.system.auxiliary.log.LogMaster.log(1,toASCII_Map()+ "\n Model is Offset by " +offset);
        LinkedHashSet<Coordinates> set = new LinkedHashSet<>(roomMap.keySet());
        Map<Coordinates, Room> newMap = new LinkedHashMap<>();
        for (Coordinates coordinates : set) {
            Room room = roomMap.get(coordinates);
            room.getCoordinates().offset(offset);
            newMap.put(room.getCoordinates(), room);
        }
        getAdditionalCells().keySet().forEach(c-> c.offset(offset));
        roomMap = newMap;

        occupiedCells.forEach(c -> c.offset(offset));
        leftMost = CoordinatesMaster. getMinX(occupiedCells);
        rightMost = CoordinatesMaster. getMaxX(occupiedCells);
        topMost = CoordinatesMaster. getMinY(occupiedCells);
        bottomMost = CoordinatesMaster. getMaxY(occupiedCells);
        while (leftMost != 0 || topMost != 0 && new Loop(5).continues()) {
            offsetCoordinates();
        }
        main.system.auxiliary.log.LogMaster.log(1,toASCII_Map()+ "\n Model after Offset by " +offset);

    }

    public void placeCell(Coordinates coordinates, LevelBlock block, ROOM_CELL filler) {
        Room room = (Room) MapMaster.getKeyForValue_(getBlocks(), block);
        Coordinates relative = room.relative(coordinates);
        if (room.getCells()[relative .x]
         [relative.y].equals(ROOM_CELL.EXIT.getSymbol()))
            return;
        if (room.getCells()[relative .x]
         [relative.y].equals(ROOM_CELL.ENTRANCE.getSymbol()))
            return;
        room.getCells()[relative .x]
         [relative.y] = filler.getSymbol();
        block.getTileMap().put(coordinates, filler);

    }

    public Map<Coordinates, ROOM_CELL> getAdditionalCells() {
        return additionalCells;
    }

    public void setAdditionalCells(Map<Coordinates, ROOM_CELL> additionalCells) {
        this.additionalCells = additionalCells;
    }

    public void setGraph(LevelGraph graph) {
        this.graph = graph;
    }

    public LevelGraph getGraph() {
        return graph;
    }

    public void setRoomLinkMap(Map<Room, List<Room>> roomLinkMap) {
        this.roomLinkMap = roomLinkMap;
    }

    public Map<Room, List<Room>> getRoomLinkMap() {
        return roomLinkMap;
    }
}

package eidolons.game.module.dungeoncrawl.generator.model;

import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.LevelGenerator;
import eidolons.game.module.dungeoncrawl.generator.graph.LevelGraphNode;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMapper;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
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
    private Integer leftMost;
    private Integer rightMost;
    private Integer topMost;
    private Integer bottomMost;
    private LevelModelBuilder builder;

    public LevelModel(LevelData data, LevelModelBuilder builder) {
        this.data = data;
        this.builder = builder;
    }


    @Override
    public String toString() {

        return "LevelModel: \n" + "; total rooms=" + roomMap.size() + "; occupied cells= " + occupiedCells
         + "; " +
         toASCII_Map();
        //                 new ArrayMaster<ROOM_CELL>().getCellsString(cells);

    }

    private void addCapes(Coordinates p, RoomModel roomModel) {
        int x = p.x;
        int x1 = p.x + roomModel.getWidth();
        int y = p.y;
        int y1 = p.y + roomModel.getHeight();
        if (leftMost == null || x < leftMost)
            leftMost = x;
        if (rightMost == null || x1 > rightMost)
            rightMost = x1;
        if (topMost == null || y < topMost)
            topMost = y;
        if (bottomMost == null || y1 > bottomMost)
            bottomMost = y1;

    }

    public int getCurrentWidth() {

        if (leftMost == null)
            return data.getX();
        if (rightMost == null)
            return data.getX();
        return Math.abs(leftMost - rightMost) + 1;
    }

    public int getCurrentHeight() {
        if (bottomMost == null)
            return data.getY();
        if (topMost == null)
            return data.getY();
        return Math.abs(topMost - bottomMost) + 1;
    }


    public void addRoom(Room room) {
        Coordinates p = room.getCoordinates();

        roomMap.put(p, room);
        addOccupied(p, room);
        addCapes(p, room);
        setCells(new TileMapper(this, data).build(this));
        if (!LevelGenerator.LOGGING_OFF)
            log(1, "Placed " + room + " at " +
             p.x + " " + p.y + "; " + toString());

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
         zones.toArray(new LevelZone[zones.size()])[node.getZoneIndex()];
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
}

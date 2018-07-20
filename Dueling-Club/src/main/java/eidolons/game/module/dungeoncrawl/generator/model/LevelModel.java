package eidolons.game.module.dungeoncrawl.generator.model;

import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import main.swing.PointX;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 2/13/2018.
 *
 * from this, it must be possible to create a real level with specific objects
 *
 *
 */
public class LevelModel {
    ROOM_CELL[][] cells; //enum?
    List<LevelZone> zones;
    Map<Room, LevelBlock> blocks;
    Map<Point, Room > roomMap = new LinkedHashMap<>();
    private List<Point> occupiedCells=     new ArrayList<>() ;
    private Integer leftMost;
    private Integer rightMost;
    private Integer topMost;
    private Integer bottomMost;
    LevelData data;

    public LevelModel(LevelData data) {
        this.data = data;
    }
    @Override
    public String toString() {
        return "LevelModel: \n"+roomMap.size()+" rooms: "+roomMap.values();
//         + "\n"+ " cells: "  +
//         new ArrayMaster<ROOM_CELL>().getCellsString(cells);

    }
    private void addCapes(Point p, RoomModel roomModel) {
        int x = p.x;
        int x1 = p.x+roomModel.getWidth();
        int y = p.y;
        int y1 = p.y+roomModel.getHeight();
        if (leftMost==null ||  x<leftMost )
            leftMost = x;
        if (rightMost==null ||  x1>rightMost )
            rightMost =  x1;
        if (topMost==null ||  y<topMost )
            topMost =  y;
        if (bottomMost==null ||  y1>bottomMost )
            bottomMost =  y1;

    }

    public void setCells(ROOM_CELL[][] cells) {
        this.cells = cells;
    }

    public int getCurrentWidth() {

        if (leftMost==null)
            return data.getX();
        if (rightMost==null)
            return data.getX();
        return Math.abs(leftMost  - rightMost )+1;
    }
    public int getCurrentHeight() {
        if (bottomMost==null)
            return data.getY();
        if (topMost==null)
            return data.getY();
        return Math.abs(topMost  - bottomMost )+1;
    }
    public Room addRoom(Point point, RoomModel roomModel) {
        return addRoom(point.x, point.y, roomModel);
    }
        public Room addRoom(int x, int y, RoomModel roomModel) {
        Point p=new PointX(x,y);
        if (!canPlace(roomModel, p))
        {
//            main.system.auxiliary.log.LogMaster.log(1,"Cannot place " +roomModel + " at " +
//             x +
//             " "+y);
            return null ;
        }
            Room room = new Room(roomModel);
            room.setPoint(p);
        roomMap.put(p, room);
        addOccupied(p, room);
        addCapes(p, room);
            main.system.auxiliary.log.LogMaster.log(1,"Placed " +roomModel + " at " +
             x +
             " "+y+ "; total rooms=" + roomMap.size()+ "; total cells=" +occupiedCells.size());
            return room;
    }



    public boolean canPlace(
     RoomModel roomModel, Point p) {
        for (int x = p.x; x < p.x + roomModel.getWidth(); x++) {
            for (int y = p.y; y < p.y + roomModel.getHeight(); y++) {
                if (p.x<0)
                    return false;
                if (p.y<0)
                    return false;
                if (p.x>=data.getX())
                    return false;
                if (p.y>=data.getY())
                    return false;
                if (getOccupiedCells().contains(new PointX(x, y)))
                    return false;
            }
        }
        return true;
    }
    private void addOccupied(Point p, RoomModel roomModel) {
        for (int x = p.x; x < p.x + roomModel.getWidth(); x++) {
            for (int y = p.y; y < p.y + roomModel.getHeight(); y++) {
                Point point = new PointX(x, y);
                if (occupiedCells.contains(point)){
                    throw new RuntimeException();
                }
                occupiedCells.add(point);
            }
        }
    }

    public List<Point> getOccupiedCells() {
        return occupiedCells;
    }

    public ROOM_CELL[][] getCells() {
        return cells;
    }

    public Map<Point, Room > getRoomMap() {
        return roomMap;
    }

    public LevelModel(Map<Room, LevelBlock> blocks) {
        this.blocks = blocks;
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

    public Map<Room, LevelBlock> getBlocks() {
        return blocks;
    }
}

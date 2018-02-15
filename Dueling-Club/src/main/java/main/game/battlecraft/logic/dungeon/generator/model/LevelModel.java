package main.game.battlecraft.logic.dungeon.generator.model;

import main.game.battlecraft.logic.dungeon.generator.GeneratorEnums.ROOM_CELL;

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
    //meta data layer?
//    LevelZone[] zones;
//    LevelBlock[] blocks;
    Map<Point, RoomModel> modelMap = new LinkedHashMap<>();
    private List<Point> occupiedCells=     new ArrayList<>() ;
    private Point leftMost;
    private Point rightMost;
    private Point topMost;
    private Point bottomMost;

    private void addCapes(Point p, RoomModel roomModel) {
        if (leftMost==null ||  p.x<leftMost.x)
            leftMost = p;
        if (rightMost==null ||  p.x>rightMost.x)
            rightMost = p;
        if (bottomMost==null ||  p.y>bottomMost.y)
            bottomMost = p;
        if (topMost==null ||  p.y<topMost.y)
            topMost = p;

    }

    public void setCells(ROOM_CELL[][] cells) {
        this.cells = cells;
    }

    public int getCurrentWidth() {
        return Math.abs(leftMost.x - rightMost.x);
    }
    public int getCurrentHeight() {
        return Math.abs(topMost.y - bottomMost.y);
    }
    public boolean addRoom(Point point, RoomModel roomModel) {
        return addRoom(point.x, point.y, roomModel);
    }
        public boolean addRoom(int x, int y, RoomModel roomModel) {
        Point p=new Point(x,y);
        if (!canPlace(roomModel, p))
        {
            main.system.auxiliary.log.LogMaster.log(1,"Cannot place " +roomModel + " at " +
             x +
             " "+y);
            return false;
        }
        roomModel.setPoint(p);
        modelMap.put(p, roomModel);
        addOccupied(p, roomModel);
        addCapes(p, roomModel);
            main.system.auxiliary.log.LogMaster.log(1,"Placed " +roomModel + " at " +
             x +
             " "+y);
            return true;
    }

    public boolean canPlace(
     RoomModel roomModel, Point p) {
        for (int x = p.x; x < p.x + roomModel.getWidth(); x++) {
            for (int y = p.y; y < p.y + roomModel.getHeight(); y++) {
                if (getOccupiedCells().contains(new Point(x, y)))
                    return false;
            }
        }
        return true;
    }
    private void addOccupied(Point p, RoomModel roomModel) {
        for (int x = p.x; x < p.x + roomModel.getWidth(); x++) {
            for (int y = p.y; y < p.y + roomModel.getHeight(); y++) {
                Point point = new Point(x, y);
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

    public Map<Point, RoomModel> getModelMap() {
        return modelMap;
    }

    public Point getLeftMost() {
        return leftMost;
    }

    public Point getRightMost() {
        return rightMost;
    }

    public Point getTopMost() {
        return topMost;
    }

    public Point getBottomMost() {
        return bottomMost;
    }
}

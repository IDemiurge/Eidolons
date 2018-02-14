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
        if (p.x<leftMost.x)
            leftMost = p;
        if (p.x>rightMost.x)
            rightMost = p;
        if (p.y>bottomMost.y)
            bottomMost = p;
        if (p.y<topMost.y)
            topMost = p;

    }
    public int getCurrentWidth() {
        return Math.abs(leftMost.x - rightMost.x);
    }
    public int getCurrentHeight() {
        return Math.abs(topMost.y - bottomMost.y);
    }
    public void addRoom(int x, int y, RoomModel roomModel) {
        Point p=new Point(x,y);
        roomModel.setPoint(p);
        modelMap.put(p, roomModel);
        addOccupied(p, roomModel);
        addCapes(p, roomModel);
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

}

package main.game.battlecraft.logic.dungeon.generator.model;

import main.game.battlecraft.logic.battlefield.FacingMaster;
import main.game.battlecraft.logic.dungeon.generator.LevelData;
import main.game.battlecraft.logic.dungeon.generator.graph.LevelGraph;
import main.game.battlecraft.logic.dungeon.generator.graph.LevelGraphEdge;
import main.game.battlecraft.logic.dungeon.generator.graph.LevelGraphNode;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.system.auxiliary.data.ArrayMaster;

import java.awt.*;
import java.util.List;
import java.util.Set;

/**
 * Created by JustMe on 2/13/2018.
 */
public class RoomAttacher {

    private final LevelData data;
    private final LevelModel model;

    public RoomAttacher(LevelData data, LevelModel model) {
        this.data = data;
        this.model = model;
    }


    public FACING_DIRECTION[] getExits(LevelGraphNode node, LevelGraph graph,
                                       Set<LevelGraphEdge> links,
                                       List<LevelGraphNode> linkedNodes) {
        //can we have duplicates in linkedNodes?
        FacingMaster.getRandomFacing();
        //rotate? mirror?
        new ArrayMaster<FACING_DIRECTION>().getArray(FACING_DIRECTION.)
        return new FACING_DIRECTION[0];
    }

    public FACING_DIRECTION[] getExits(LevelGraphNode node, LevelGraph graph) {
        return new FACING_DIRECTION[0];
    }

    public Point getAttachPoint(RoomModel parent, RoomModel model, FACING_DIRECTION side
    ) {
        int x = parent.getPoint().x + (parent.getWidth() - model.getWidth()) / 2;
        int y = parent.getPoint().y + (parent.getHeight() - model.getHeight()) / 2;
        if (side == FACING_DIRECTION.SOUTH) {
            y += parent.getHeight();
        } else if (side == FACING_DIRECTION.EAST) {
            x += parent.getWidth();
        }
        return new Point(x, y);
    }

    public boolean canPlace(
     RoomModel roomModel, Point p) {
        List<Point> points = model.getOccupiedCells();
        for (int x = p.x; x < p.x + roomModel.getWidth(); x++) {
            for (int y = p.y; y < p.y + roomModel.getHeight(); y++) {
                if (points.contains(new Point(x, y))) return false;
            }
        }
        return true;
    }


    public void attach(RoomModel roomModel, RoomModel link, FACING_DIRECTION side) {
    }
}

package eidolons.game.exploration.dungeon.generator.model;

import eidolons.game.exploration.dungeon.generator.GeneratorEnums;
import eidolons.game.exploration.dungeon.generator.graph.LevelGraph;
import eidolons.game.exploration.dungeon.generator.graph.LevelGraphEdge;
import eidolons.game.exploration.dungeon.generator.graph.LevelGraphNode;
import eidolons.game.exploration.dungeon.generator.tilemap.TileMap;
import eidolons.game.exploration.dungeon.generator.tilemap.TileMapper;
import eidolons.game.exploration.dungeon.generator.tilemap.TilesMaster;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.FACING_DIRECTION;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 7/30/2018.
 */
public class Traverser {
    private static final boolean ALLOW_NULL = true;
    private static final boolean SIMPLE = true;
    private final Map<LevelGraphEdge, Room> edgeMap;
    private final Map<LevelGraphNode, Room> map;
    private final LevelModel model;
    private final LevelGraph graph;
    private List<LevelGraphEdge> failed;

    public Traverser(Map<LevelGraphNode, Room> map, LevelModel model, LevelGraph graph, Map<LevelGraphEdge, Room> edgeMap) {
        this.map = map;
        this.model = model;
        this.graph = graph;
        this.edgeMap = edgeMap;
    }

    public static int getExitsOffset(Room room, Room room2) {
        if (room2.getEntrance() == null)
            return 0;
        boolean xOrY = room2.getEntrance().isVertical();
        Coordinates c = ExitMaster.findExit(room, room2.getEntrance().flip());
        if (c == null)
            return 0;
        c = c.getOffset(room.getCoordinates());
        Coordinates c2 = room2.getEntranceCoordinates().getOffset(room2.getCoordinates());
        //        if (xOrY) { this seems wrong
        //            return dif - (room.getWidth() / 2 - room2.getWidth() / 2);
        //        }
        //        return dif - (room.getHeight() / 2 - room2.getHeight() / 2);
        return c.getXorY(xOrY) - c2.getXorY(xOrY);
    }

    public static boolean simpleCheck(Room room, Room room1, Room link) {
        return checkEntrancesPassable(room, room1, link);
    }

    public static boolean checkEntrancesPassable(Room room, Room room1, Room link) {
        if (!checkEntrancesPassable(room)) {
            System.out.println("CANNOT TRAVERSE " + room );
            return false;
        }
        if (!checkEntrancesPassable(room1)) {
            System.out.println("CANNOT TRAVERSE " + room1);
            return false;
        }
        if (link != null) {
            if (!checkEntrancesPassable(link)) {
                System.out.println("CANNOT TRAVERSE " + "\n" + link + "\n");
                return false;
            }
            if (!checkExitsAligned(room, link)) {
                System.out.println("NOT ALIGNED" + "\n" + link + "\n" + room);
                return false;
            }
            if (!checkExitsAligned(link, room1)) {
                System.out.println("NOT ALIGNED" + "\n" + link + "\n" + room1);
                return false;
            }
        } else {
//            if (!checkExitsAligned(room, room1)) {
//                System.out.println("NOT ALIGNED" + "\n" + room + "\n" + room1);
//                return false;
//            }
        }
        //entrances are aligned

        return true;
    }

    //    public String getFailReason() {
    //        String reason = "Cannot traverse: \n";
    //        failedEdges = getFailedEdges();
    //        return reason;
    //    }

    public static boolean checkExitsAligned(Room room, Room room2) {
        return getExitsOffset(room, room2) == 0;
    }

    public static boolean checkEntrancesPassable(Room room) {
        return checkEntrancesPassable(room, room.getEntrance());
    }

    public static boolean checkEntrancesPassable(Room room, FACING_DIRECTION entrance) {
        Coordinates coordinates =
//         room.getEntranceCoordinates() !=
//          null ? room.getEntranceCoordinates() :
          RoomAttacher.adjust(new AbstractCoordinates(0, 0), entrance, room, true);
        if (room.getType() == ROOM_TYPE.ENTRANCE_ROOM)
            return true;
        TileMap tileMap = TileMapper.createTileMap(room);
        List<Coordinates> blocked = TileMapper.createTileMap(room).getMap().keySet().stream()
         .filter(c -> TilesMaster.isEntranceCell(room.relative(c), room, coordinates, entrance))
         .filter(c -> !TilesMaster.isPassable(tileMap.getMap().get(c))).collect(Collectors.toList());
        if (blocked.size()>0)
        {
            List<Coordinates> entrances = TileMapper.createTileMap(room).getMap().keySet().stream()
             .filter(c -> TilesMaster.isEntranceCell(room.relative(c), room, coordinates, entrance)).collect(Collectors.toList());
            return false;
        }

        return true;
    }

    public boolean test() {
        failed = getFailedEdges();
        return failed.isEmpty();
    }

    public List<LevelGraphEdge> getFailedEdges() {
        List<LevelGraphEdge> failed = new ArrayList<>();
        for (LevelGraphEdge edge : graph.getEdges()) {
            if (!checkCanPass(edge.getNodeOne(),
             edge.getNodeTwo(), edgeMap.get(edge))) {
                failed.add(edge);

            }
        }
        return failed;
    }

    private boolean checkCanPass(LevelGraphNode nodeOne, LevelGraphNode nodeTwo, Room link) {
        return canPass(map.get(nodeOne), map.get(nodeTwo), link);

    }

    private void canPass(Coordinates start, Coordinates end) {

    }

    public boolean canPass(Room room, Room room1, Room link) {
        if (room == null)
            return ALLOW_NULL;
        if (room1 == null)
            return ALLOW_NULL;
        if (SIMPLE) {
            try {
                return simpleCheck(room, room1, link);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                return false;
            }
        }
        Coordinates start =
         CoordinatesMaster.getClosestTo(room1.getCoordinates(),
          room.getExitCoordinates().stream().map(
           c -> c.getOffset(room.getCoordinates())).collect(Collectors.toList()))
          .getOffset(room.getCoordinates());
        Coordinates end = room1.getEntranceCoordinates().getOffset(room1.getCoordinates());

        Coordinates topLeft = new AbstractCoordinates(Math.min(start.x, end.x),
         Math.min(start.y, end.y));
        Coordinates bottomRight = new AbstractCoordinates(Math.max(start.x, end.x),
         Math.max(start.y, end.y));

        GeneratorEnums.ROOM_CELL[][] cells = TileMapper.toCellArray(link.getCells());

        //         new ROOM_CELL[bottomRight.x - topLeft.x+1][bottomRight.y - topLeft.y+1];
        int x1 = 0;
        int y1 = 0;
        for (int x = topLeft.getX() - 1; x <= bottomRight.getX(); x++) {
            for (int y = topLeft.getY(); y <= bottomRight.getY(); y++) {
                cells[x1][y1++] = model.getCells()[x - model.getLeftMost()][y - model.getTopMost()];
            }
            x1++;
            y1 = 0;
        }
        start = start.getOffset(new AbstractCoordinates(room.getCoordinates().x -
         topLeft.x, room.getCoordinates().y -
         topLeft.y));
        end = end.getOffset(new AbstractCoordinates(room1.getCoordinates().x -
         topLeft.x, room.getCoordinates().y -
         topLeft.y));
        //        return new LevelPathFinder().findPath(start, end, cells);
        /*
        #o#o
        ##o#
        oooo

         */
        //there must be orthogonal adjacency of passable cells in each row/column
        return false;
    }

/*
Fail example:
#  #  #  #  #  #  #  .  -
.  .  .  .  e  .  .  .  .  -
.  .  .  .  e  .  .  .  .  -
.  #  #  #  C  #  @  #  .  -
.  #  O  @  c  #  O  #  .  -
.  %  O  C  c  C  O  c  .  -
.  #  O  @  c  #  O  #  .  -
.  #  #  %  c  L  #  #  .
 */

    //write a test for it?!
    public boolean checkTraversable(Room parent, FACING_DIRECTION parentExit) {
        return checkTraversable(parent.getCoordinates(), TileMapper.createTileMap(parent), parentExit
         , parent.getWidth(), parent.getHeight(), new AbstractCoordinates(0, 0));
    }

    public boolean checkTraversable(Room parent, FACING_DIRECTION parentExit, Coordinates offset) {
        return checkTraversable(parent.getCoordinates(), TileMapper.createTileMap(parent), parentExit
         , parent.getWidth(), parent.getHeight(), offset);
    }

    public boolean checkTraversable(Coordinates roomCoordinates, TileMap tileMap,
                                    FACING_DIRECTION parentExit, int w, int h, Coordinates offset) {
        //not only on default centered exits...

        Coordinates exitOffset = getExitOffset(parentExit);
        Coordinates key = roomCoordinates;
        key = RoomAttacher.adjust(key, parentExit, w, h, true, false);
        key.offset(offset);
        final Coordinates key_ = new AbstractCoordinates(key.x, key.y).offset(exitOffset);

        Map<Coordinates, GeneratorEnums.ROOM_CELL> checkCells =
         tileMap.getMap().keySet().stream().filter(
          c -> Math.abs(key_.x - c.x) < 2 && Math.abs(key_.y - c.y) < 2  //we need to take the 3x3 around 'one after the exit'
         ).collect(Collectors.toMap(Function.identity(),
          c -> tileMap.getMap().get((c).getOffset(roomCoordinates.negative())
          )));

        offset = CoordinatesMaster.getFarmostCoordinateInDirection(
         DIRECTION.UP_LEFT, new ArrayList<>(checkCells.keySet()), true);

        key = exitOffset.negative().offset(offset); //?
        if (!TilesMaster.isPassable(checkCells.get(key)))
            return false;

        for (Coordinates coordinates : key.getAdjacenctNoDiags()) {
            GeneratorEnums.ROOM_CELL val = checkCells.get(coordinates);
            if (val == GeneratorEnums.ROOM_CELL.ROOM_EXIT)
                continue; //?
            if (TilesMaster.isPassable(checkCells.get(coordinates)))
                return true;

        }
        return false;
    }

    private Coordinates getExitOffset(FACING_DIRECTION parentExit) {
        switch (parentExit) {
            case NORTH:
                return new AbstractCoordinates(0, 1);
            case WEST:
                return new AbstractCoordinates(1, 0);
            case SOUTH:
                return new AbstractCoordinates(0, -1);
            case EAST:
                return new AbstractCoordinates(-1, 0);
        }
        return null;
    }

    public String[] getFailArgs() {
        String[] array = new String[failed.size()];
        for (int i = 0; i < failed.size(); i++) {
            array[i] = failed.get(i).toString();
        }

        return array;
    }
}

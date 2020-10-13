package eidolons.game.module.generator.model;

import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.generator.GeneratorEnums.EXIT_TEMPLATE;
import eidolons.game.module.generator.GeneratorEnums.GRAPH_NODE_ATTRIBUTE;
import eidolons.game.module.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.generator.LevelData;
import eidolons.game.module.generator.LevelDataMaker.LEVEL_REQUIREMENTS;
import eidolons.game.module.generator.LevelValidator;
import eidolons.game.module.generator.graph.GraphPath;
import eidolons.game.module.generator.graph.LevelGraphNode;
import eidolons.game.module.generator.level.ZoneCreator;
import eidolons.game.module.generator.pregeneration.Pregenerator;
import eidolons.game.module.generator.tilemap.TileMapper;
import eidolons.game.module.generator.tilemap.TilesMaster;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.SortMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.datatypes.WeightMap;
import main.system.math.PositionMaster;

import java.util.*;
import java.util.stream.Collectors;

import static main.system.auxiliary.log.LogMaster.log;

/**
 * Created by JustMe on 7/24/2018.
 * <p>
 * <p>
 * Additional ‘rounding’ of the model
 * Identify walls that can be attached to;
 * those would be our treasure rooms and secret rooms…
 */
public class ModelFinalizer {

    RoomTemplateMaster templateMaster;
    RoomAttacher attacher;
    LevelModel model;
    private final LevelModelBuilder builder;
    private int maxRooms;

    public ModelFinalizer(RoomTemplateMaster templateMaster, RoomAttacher attacher, LevelModelBuilder builder) {
        this.templateMaster = templateMaster;
        this.attacher = attacher;
        this.builder = builder;
    }

    public static void randomizeEdges(LevelModel model) {
        model.offsetCoordinates();
        Map<Coordinates, ROOM_CELL> modelMap = TileMapper.createTileMap(model).getMapModifiable();
        for (Room room : model.getRoomMap().values()) {
            LevelBlock block = model.getBlocks().get(room);
            Map<Coordinates, ROOM_CELL> map = TileMapper.createTileMap(room).getMap();
            List<Coordinates> edgeCells = map.keySet().stream()
             .filter(c -> map.get(c) == ROOM_CELL.INDESTRUCTIBLE)
             .filter(c -> TilesMaster.getAdjacentCount(modelMap, c, ROOM_CELL.VOID) > 2)
             .collect(Collectors.toList());
            int last = 0;
            for (Coordinates edgeCell : edgeCells) {

                List<Coordinates> voidCells = TilesMaster.getAdjacentCells(modelMap, false, edgeCell, ROOM_CELL.VOID);

                if (RandomWizard.chance(50 + last)) {
                    last -= 25;
                    Coordinates c = new RandomWizard<Coordinates>().getRandomListItem(voidCells);
                    model.getBlocks().get(room).getCoordinatesSet().add(c);
                    block.getTileMap().getMapModifiable().put(c, ROOM_CELL.INDESTRUCTIBLE);

                    if (edgeCell.x == c.x || edgeCell.y == c.y)
                        if (RandomWizard.chance(85)) {
                            block.getTileMap().getMapModifiable().put(edgeCell, ROOM_CELL.FLOOR);
                            c = room.relative(edgeCell);
                            room.getCells()[c.x][c.y] = ROOM_CELL.FLOOR.getSymbol();

                        }
                } else last += 25;
                //                    for (Coordinates cell : voidCells) {
                //                        if (RandomWizard.chance(89)) {
                //                            continue;
                //                        }
                //                        model.getAdditionalCells().put(cell, ROOM_CELL.INDESTRUCTIBLE);
                //                        modelMap.put(cell, ROOM_CELL.INDESTRUCTIBLE);
                //                        if (edgeCell.x == cell.x || edgeCell.y == cell.y) {
                //                            Coordinates c = room.relative(edgeCell);
                //                            room.getCells()[c.x][c.y] = ROOM_CELL.FLOOR.getSymbol();
                //                        }
                //                    }

            }
        }
    }

    public void substituteRoomModels(LevelModel model) {
        //alternative - remove rooms if their linkcount fails to be met
        for (Room room : model.getRoomMap().values()) {
            if (room.getUsedExits().size() == ExitMaster.getExitCount(room.getExitTemplate()))
                continue;
            EXIT_TEMPLATE template = ExitMaster.getExitTemplateToLinks(room.getUsedExits().size(),
             room.getEntrance(), room.getType());

            if (template == EXIT_TEMPLATE.ANGLE || template == EXIT_TEMPLATE.THROUGH) {
                if (room.getUsedExits().get(0) == room.getEntrance().flip()) {
                    template = EXIT_TEMPLATE.THROUGH;
                } else template = EXIT_TEMPLATE.ANGLE;
            }

            if (Pregenerator.TEST_MODE) log(1, "Substituting cells for " + room + " with "
             + template);

            RoomModel roomModel = templateMaster.getRandomModelToSubstitute(room.getWidth(),
             room.getHeight(), template, room.getType(), room.getEntrance(
             ), room.getZone().getTemplateGroup(),
             room.getUsedExits());

            if (roomModel == null)
                continue;

            room.setCells(roomModel.getCells());
            room.setRotationsOnly(roomModel.getRotations());
            room.resetExitCells();
            room.setExitTemplate(template);
            room.setExits(RotationMaster.getRotatedExits( //TODO this is wrong
             roomModel.getRotations(), room.getExits()));


            if (Pregenerator.TEST_MODE) log(1, "New cells for " + room);
            if (ZoneCreator.TEST_MODE) {
                room.setZone(new LevelZone(9));
            }
            //TODO block?
        }

    }

    public void loopBack(LevelModel model) {
        //ensure graph paths
        //        for (GraphPath path : builder.graph.getPaths()) {
        //            Room room = getLastBuiltRoom(path);
        //            if (room == builder.nodeModelMap.getVar(path.getEndNode())) {
        //                continue;
        //            }
        //            Room room2 = chooseAltRoom(path, room, model); //closest?
        //            connect(room, room2);
        //            // choose randomly adjacent rooms to connect
        //        }

        //geometrically - try to find low-exit rooms that have a lot of void adjacent in between

        int maxNewPath = (int) (model.getRoomMap().size() * 0.2f);
        int remaining = maxNewPath;
        Loop loop = new Loop(model.getRoomMap().keySet().size() * 10 * maxNewPath);
        List<Room> rooms = new ArrayList<>(model.getRoomMap().values());
        //            rooms.removeIf(r -> r.getType() == ROOM_TYPE.CORRIDOR);
        rooms.sort(new SortMaster<Room>().getSorterByExpression_(r ->
                r.getExits().length - r.getUsedExits().size()));
        Map<Room, List<Room>> failed = new HashMap<>();

        loop:
        while (remaining > 0 && loop.continues()) {

            for (Room room : new ArrayList<>(rooms)) {
                for (Room room2 : new ArrayList<>(rooms)) {
                    if (room == room2)
                        continue;
                    if (failed.get(room) != null)
                        if (failed.get(room).contains(room2))
                            continue;
                    if (checkLoopBack(room, room2, model)) {
                        if (connect(room, room2)) {
                            remaining--;
                            rooms.remove(room);
                            rooms.remove(room2);
                            continue loop;
                        }
                    }
                    MapMaster.addToListMap(failed, room, room2);
                    MapMaster.addToListMap(failed, room2, room);

                }
            }
        }
        model.rebuildCells();
        if (Pregenerator.TEST_MODE) log(1,"Made " +
         (maxNewPath - remaining) +
         "additional connections for " +
         model);
        model.rebuildCells();
    }

    private boolean tryConnectViaLink(Room room, Room room2, FACING_DIRECTION side) {
        Room link = builder.findFitting(room, EXIT_TEMPLATE.THROUGH, ROOM_TYPE.CORRIDOR, side, true);
        {
            if (link != null) {
                if (Pregenerator.TEST_MODE) log(1,"LINKED VIA " + link);
                builder.makeExits(side, side, room, link, room2, false, false);
                return true;
            }
        }


        return false;
    }

    private boolean checkLoopBack(Room room, Room room2, LevelModel model) {
        //rooms must be close enough and have nothing between them

        //we could instead *search* for closest room...

        //        if (!(checkAligned(room, room2, true) || checkAligned(room, room2, false))) {
        //            return false;
        //        }

        if (builder.roomLinkMap.containsKey(room))
            if (builder.roomLinkMap.get(room).contains(room2)) {
                return false;
            }
        if (builder.roomLinkMap.containsKey(room2))
            if (builder.roomLinkMap.get(room2).contains(room)) {
                return false;
            }
        List<Object> types = ListMaster.toList(room.getType(), room2.getType());
        if (types.contains(ROOM_TYPE.ENTRANCE_ROOM) &&
         types.contains(ROOM_TYPE.EXIT_ROOM)) {
            return false;
        }
        Coordinates c = room.getCoordinates();
        Coordinates c2 = room2.getCoordinates();
        int diffX = (c.x < c2.x ? room : room2).getWidth() +
         Math.abs(c.x - c2.x);
        int diffY = (c.y < c2.y ? room : room2).getHeight() +
         Math.abs(c.y - c2.y);
        boolean xOrY = diffX < diffY;

        float dst =
         CoordinatesMaster.getMinDistanceBetweenGroups(room.getCoordinatesList(),
          room2.getCoordinatesList(), 5);
        if (dst > 5) {
            return false;
        }
        return !(
         model.getRoomMap().values().stream().anyMatch(r -> {
             if (r == room || r == room2)
                 return false;
             return ModelMaster.isBetween(r, room, room2, xOrY);
         }));

    }

    private Set<Integer> getAligned(Room room, Room room2, boolean onXorY) {
        //from the smaller room, getVar the farthest aligned line -1 and see if it is close enough to center
        Map<Coordinates, ROOM_CELL> map = TileMapper.createTileMap(room).getMap();
        Map<Coordinates, ROOM_CELL> map2 = TileMapper.createTileMap(room2).getMap();
        List<Integer> coordinates = map.keySet().stream().map(c -> c.getXorY(onXorY)).collect(Collectors.toList());
        List<Integer> coordinates2 = map2.keySet().stream().map(c -> c.getXorY(onXorY)).collect(Collectors.toList());

        return coordinates.stream().filter(coordinates2::contains).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private boolean connect(Room room, Room room2) {
        //try place room? or just a line?
        //        List<Coordinates> toClear; //
        FACING_DIRECTION side;
        Set<Integer> alignedX = getAligned(room, room2, true);
        Set<Integer> alignedY = getAligned(room, room2, false);
        List<Integer> aligned;
        boolean onXorY;
        if (alignedX.size() > alignedY.size()) {
            aligned = new ArrayList<>(alignedX);
            onXorY = true;
            side = room.getCoordinates().y < room2.getCoordinates().y ? FACING_DIRECTION.SOUTH : FACING_DIRECTION.NORTH;
        } else {
            aligned = new ArrayList<>(alignedY);
            onXorY = false;
            side = room.getCoordinates().x < room2.getCoordinates().x ? FACING_DIRECTION.EAST : FACING_DIRECTION.WEST;

        }
        if (aligned.size() < 3)
            return false;

        if (Pregenerator.TEST_MODE) log(1,"CONNECTING " +
         room + " with " + room2 + " on " + side
         + "\n\n\n" + model);
        //        if (tryConnectViaLink(room, room2, side)) {
        //            return true;
        //        }
        //        if (Pregenerator.TEST_MODE) log(1,"MANUAL LINK FOR " +
        //         room + " and " + room2);
        List<Coordinates> list = room.getCoordinatesList();
        Coordinates c = CoordinatesMaster.getFarmostCoordinateInDirection(
         side.getDirection(), list);
        //        list.addAll(room2.getCoordinatesList());

        boolean middle = !RandomWizard.chance(aligned.size() * 9);
        Integer line;
        if (middle) {
            line = aligned.get(aligned.size() / 2);
        } else line = (Integer) RandomWizard.getRandomListObject(aligned);
        int n = side.isCloserToZero() ? -1 : 1;
        int start = c.getXorY(!onXorY) - n;
        c = CoordinatesMaster.getFarmostCoordinateInDirection(
         side.getDirection().flip(), room2.getCoordinatesList());
        int end = c.getXorY(!onXorY) + n;

        n = start;
        start = Math.min(start, end);
        end = Math.max(n, end);

        List<Coordinates> path = new ArrayList<>();
        for (int i = start + 1; i <= end; i++) {
            c = new AbstractCoordinates(i, line);
            if (onXorY)
                c.swap();
            path.add(c);
        }

        for (Coordinates pathCoordinate : path) {
            c = room.relative(pathCoordinate);
            ROOM_CELL cell = ROOM_CELL.FLOOR;

            boolean assigned = false;
            if (CoordinatesMaster.isWithinBounds(c, 0, room.getWidth() - 1, 0, room.getHeight() - 1)) {
                room.getCells()[c.x][c.y] = cell.getSymbol();
                if (Pregenerator.TEST_MODE) log(1,  "changed " +
                 room + " on " + c);
                assigned = true;
            }
            c = room2.relative(pathCoordinate);
            if (CoordinatesMaster.isWithinBounds(c, 0, room2.getWidth() - 1, 0, room2.getHeight() - 1)) {
                room2.getCells()[c.x][c.y] = cell.getSymbol();
                if (Pregenerator.TEST_MODE) log(1,"changed " +
                 room2 + " on " + c);
                assigned = true;
            }
            if (assigned)
                continue;
            model.getAdditionalCells().put(pathCoordinate, cell);
            if (Pregenerator.TEST_MODE) log(1,"between " + c);
        }
        room.getUsedExits().add(side);
        room2.getUsedExits().add(side.flip());

        //if it fails t0 Traverse, it's not critical...
        //        Coordinates c = link.getCoordinates();
        //        Coordinates offset = new AbstractCoordinates(0, 0);
        //        while (true) {
        //            //           TODO  if (new Traverser().checkTraversable(link, side, offset))
        //            //                if (new Traverser().checkTraversable(room, side, offset)) {
        //            //                    builder.addRoom(link, room);
        //            //                    break;
        //            //                }
        //            int x = side.isVertical() ? 0 : 1;
        //            int y = !side.isVertical() ? 0 : 1;
        //            offset.offset(new AbstractCoordinates(x, y));
        //            link.setCoordinates(c.getOffset(offset));
        //        }

        //make exits

        return true;
    }

    private Room chooseAltRoom(GraphPath path, Room room, LevelModel model) {
        List<Coordinates> sortedCandidates = model.getRoomMap().keySet().stream().sorted(new SortMaster<Coordinates>().getSorterByExpression_(
         c -> c.dst(room.getCoordinates())
        )).collect(Collectors.toList());
        for (Coordinates c : sortedCandidates) {
            //TODO if
            return model.getRoomMap().get(c);
        }
        return room;
    }

    private Room getLastBuiltRoom(GraphPath path) {
        Room lastRoom = null;
        for (Integer n : path.getNodes().keySet()) {
            LevelGraphNode node = path.getNodes().get(n);
            if ((builder.nodeModelMap.get(node)) == null)
                break;
            lastRoom = builder.nodeModelMap.get(node);
        }
        return lastRoom;
    }

    public void finalize(LevelModel model) {
        this.model = model;
        if (Pregenerator.TEST_MODE) log(1, "FINALIZING: \n" + model);
        maxRooms =
         //model.getRoomMap().size() + 4;
         model.getData().getReqs().getIntValue(LEVEL_REQUIREMENTS.maxRooms);
        tryBuildUnbuiltGraphNodes(builder, model);

        tryAdditionalBuild(model);

        PositionMaster.initDistancesCache(null , model.getCurrentWidth(), model.getCurrentHeight());

        if (model.getData().isLoopBackAllowed())
            try {
                loopBack(model);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        if (model.getData().isSubstituteRoomsAllowed())
            try {
                substituteRoomModels(model);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }

        //      now after fill()  if (model.getData().getLocationType().getGroup() == LOCATION_TYPE_GROUP.NATURAL
        //         ) {            randomizeEdges(model);        }
    }

    private boolean checkBuildDone(LevelModel model, LevelData data) {
        return model.getRoomMap().size() > maxRooms;
    }

    private void tryAdditionalBuild(LevelModel model) {
        List<Room> edgeRooms = ModelMaster.getEdgeRooms(model);
        LevelValidator validator = new LevelValidator(false);
        if (Pregenerator.TEST_MODE) log(1, "tryAdditionalBuild: edgeRooms=" + edgeRooms);
        Loop loop = new Loop(500);
        //        (!validator.validateModel(builder.graph, model) TODO
        //         || !checkBuildDone(model, model.getData()))
        //         &&
        while (loop.continues()) {

            if (checkBuildDone(model, model.getData()))
                break;
            Room room = new RandomWizard<Room>().getRandomListItem(edgeRooms);

            FACING_DIRECTION roomExit = room.getSortedUnusedExit(new SortMaster<FACING_DIRECTION>().getSorterByExpression_(exit ->
             ModelMaster.getExitSortValue(exit, room, model)));
            LevelGraphNode node = createAdditionalNode(model, model.getData());
            if (Pregenerator.TEST_MODE) log(1, "additional Build for" + room +
             "\n; node= " + node);
            Room newRoom = builder.findFittingAndAttach(room,
             getTemplate(room, node), node.getRoomType(),
             roomExit, room.getZone());
            if (newRoom != null) {
                //                builder.graph.addNode()
                edgeRooms.remove(room);
                builder.makeExits(roomExit, null, room, null, newRoom, false);
                if (Pregenerator.TEST_MODE) log(1, "ADDITIONAL ROOM: " + newRoom +
                 "\n; attached to " + room + "\n" + model);
            }
        }


    }

    private LevelGraphNode createAdditionalNode(LevelModel model, LevelData data) {
        WeightMap<ROOM_TYPE> map = new WeightMap<>();
        for (ROOM_TYPE roomType : ROOM_TYPE.mainRoomTypes) {
            map.put(roomType, data.getRoomCoeF(roomType));
        }
        ROOM_TYPE type = map.getRandomByWeight();
        GRAPH_NODE_ATTRIBUTE attrs = null;
        return new LevelGraphNode(type, attrs);
    }

    private void tryBuildUnbuiltGraphNodes(LevelModelBuilder builder, LevelModel model) {
        List<Room> edgeRooms = ModelMaster.getEdgeRooms(model);

        List<LevelGraphNode> unbuiltNodes = builder.graph.getNodes().stream().filter(
         node -> !builder.nodeModelMap.containsKey(node)
        ).collect(Collectors.toList());

        if (Pregenerator.TEST_MODE) log(1, "tryBuildUnbuiltGraphNodes: edgeRooms=" + edgeRooms +
         "\n; nodes= " + unbuiltNodes);

        buildNodes(model, edgeRooms, unbuiltNodes);

    }

    public Integer getAttachRoomSortValue(Room room, LevelGraphNode node, LevelModel model) {
        int value = RandomWizard.getRandomInt(20);
        value += ModelMaster.getSorterEdgeValue(room, model);
        for (FACING_DIRECTION exit : room.getExits()) {
            if (room.getUsedExits().contains(exit))
                continue;
            value += ModelMaster.getExitSortValue(exit, room, model) / room.getExits().length;
        }

        if (builder.graph.getAdjList().get(node).iterator().next().getOtherNode(node).getRoomType() ==
         room.getType())
            value += 10000;

        return value;
    }

    private void buildNodes(LevelModel model, List<Room> edgeRooms, List<LevelGraphNode> unbuiltNodes
    ) {
        Boolean N_S = false;
        Boolean W_E = false;
        //identify where there is the most empty space... to sort beans on each loop
        for (LevelGraphNode node : unbuiltNodes) {

            edgeRooms.sort(new SortMaster<Room>()
                    .getSorterByExpression_(room -> getAttachRoomSortValue(room, node, model)));

            Room room = new RandomWizard<Room>().getRandomListItem(edgeRooms);
            edgeRooms = ModelMaster.getEdgeRooms(model);

            //            Coordinates p = room.getCoordinates();
            //            FACING_DIRECTION roomExit = BooleanMaster.isTrue(N_S) ? FACING_DIRECTION.NORTH : FACING_DIRECTION.SOUTH;
            //            if (N_S == null)
            //                roomExit = BooleanMaster.isTrue(W_E) ? FACING_DIRECTION.WEST : FACING_DIRECTION.EAST;

            FACING_DIRECTION roomExit = room.getRandomUnusedExit();
            Room newRoom = builder.findFittingAndAttach(room,
             getTemplate(room, node), node.getRoomType(),
             roomExit, room.getZone());
            if (newRoom != null) {
                builder.makeExits(roomExit, null, room, null, newRoom, false);
                if (Pregenerator.TEST_MODE) log(1, "UNBUILT ADDITIONAL ROOM: " + newRoom +
                 "\n; attached to " + room);
            }

        }
        //            int n = ModelMaster.getAdjacentToVoid(model, room, roomExit);
        //            List<Coordinates> exits = ModelMaster.getPossibleExits(roomExit, room);
    }

    private EXIT_TEMPLATE getTemplate(Room room, LevelGraphNode node) {
        return EXIT_TEMPLATE.CROSSROAD;
    }

}

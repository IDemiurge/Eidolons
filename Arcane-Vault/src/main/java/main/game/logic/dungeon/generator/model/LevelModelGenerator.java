package main.game.logic.dungeon.generator.model;

import main.game.battlecraft.logic.battlefield.FacingMaster;
import main.game.battlecraft.logic.dungeon.building.DungeonBuilder.ROOM_TYPE;
import main.game.bf.Coordinates.DIRECTION;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.game.bf.DirectionMaster;
import main.game.logic.dungeon.generator.GeneratorEnums.EXIT_TEMPLATE;
import main.game.logic.dungeon.generator.LevelData;
import main.game.logic.dungeon.generator.graph.LevelGraph;
import main.game.logic.dungeon.generator.graph.LevelGraphEdge;
import main.game.logic.dungeon.generator.graph.LevelGraphNode;
import main.swing.PointX;
import main.system.auxiliary.Loop;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by JustMe on 2/13/2018.
 */
public class LevelModelGenerator {

    LevelData data;
    List<LevelGraphNode> builtNodes = new ArrayList<>();
    private LevelModel model;
    private LevelGraph graph;
    private RoomAttacher attacher;
    private RoomTemplateMaster templateMaster;
    private Map<LevelGraphNode, Room> nodeModelMap = new HashMap<>();

    public LevelModelGenerator(LevelData data) {
        this.data = data;
    }

    public LevelModel buildModel(LevelGraph graph) {
        model = new LevelModel(data);
        this.graph = graph;
        this.attacher = new RoomAttacher(data, model);
        templateMaster = new RoomTemplateMaster(data, model);
        build();
        return model;
    }

    public void build() {
        build(graph.getNodeById(0), getBasePoint(), null);
    }

    private boolean isMergeLinks(LevelGraphNode node, Set<LevelGraphEdge> links,
                                 List<LevelGraphNode> linkedNodes) {
        //TODO  check edges are uniform
        return false;
    }

    private EXIT_TEMPLATE getExitTemplate(boolean mergeLinks, LevelGraphNode node, Set<LevelGraphEdge> links, List<LevelGraphNode> linkedNodes, FACING_DIRECTION entrance) {
        int toLink = linkedNodes.size();
        if (!mergeLinks) {
            if (toLink == 0)
                return EXIT_TEMPLATE.CUL_DE_SAC;
            if (toLink == 2)
                return EXIT_TEMPLATE.FORK;
            if (toLink >= 3)
                return EXIT_TEMPLATE.CROSSROAD;
        }
        switch (node.getRoomType()) {
            case THRONE_ROOM:
                return EXIT_TEMPLATE.THROUGH;
            //TODO
        }
        return getRandomSingleExitTemplate();
    }

    private void buildLinked(LevelGraphNode sub, Room link, FACING_DIRECTION side) {
        build(sub, attacher.getExitPoint(link, side), side);

    }

    public Room build(LevelGraphNode node, Point entrancePoint, FACING_DIRECTION entrance) { //'parent' in args? point?
        Room room = nodeModelMap.get(node);
        if (room != null)
            return room;
        Set<LevelGraphEdge> links = graph.getAdjList().get(node);
        //this is actually two-side, so crop built nodes!
//        links.removeIf(link->{
//            if (builtNodes.contains(link.getNodeTwo()))
//                if (builtNodes.contains(link.getNodeOne()))
//                    return true;
//            return false;
//        });

        //sort by relevance?
        List<LevelGraphNode> linkedNodes = new ArrayList<>();
        for (LevelGraphEdge sub : links) {
            linkedNodes.add(sub.getNodeTwo());
        }
        int x = entrancePoint.x;
        int y = entrancePoint.y;
        boolean mergeLinks = isMergeLinks(node, links, linkedNodes);
        EXIT_TEMPLATE exitTemplate = getExitTemplate(mergeLinks, node, links, linkedNodes, entrance);
        EXIT_TEMPLATE roomExitTemplate = !mergeLinks ? exitTemplate :
         getRandomSingleExitTemplate();

        room = findFittingAndAttach(entrancePoint, roomExitTemplate, node.getRoomType()
         , entrance);
        if (room == null)
        {
            return null; //don't break the chain!
        }
        nodeModelMap.put(node, room);
        room.setExitTemplate(roomExitTemplate);
        //recursive build - will it ensure that Main Paths are built?
        if (!mergeLinks) {
            buildLinks(entrancePoint, entrance, room, links);
        } else {
            FACING_DIRECTION side = getExit(roomExitTemplate, entrance);//link entrance
            room.makeExit(side);
            Room link = findFittingAndAttach
             (new PointX(x, y), exitTemplate,
              ROOM_TYPE.CORRIDOR, side);

            side = getExit(exitTemplate, side); //now for node entrance
            attacher.attach(room, link, side);
//            for (LevelGraphNode sub : linkedNodes) {
//                side = getExit(exitTemplate, side); //TODO ???
//                attach( sub,link,  side);
//            }
            //BFS?
            int i = 0;
            for (FACING_DIRECTION sub : link.getExits()) {
                DIRECTION dir = entrance.getDirection();
                sub = FacingMaster.getFacingFromDirection(DirectionMaster.rotate90(dir, true));
                buildLinked(linkedNodes.get(i), link, sub);
            }

        }
//        // TODO direct attach w/o corridor?!
        return room;
        //check if already built
//        if (models.contains(linkedNodes.get(i))) nodes!
    }

    public void buildLinks(Point entrancePoint, FACING_DIRECTION entrance, Room room, Set<LevelGraphEdge> links) {
        int i = 0;
        for (LevelGraphEdge edge : links) {
            if (room.getExits().length <= i) {
                break;
            }
            FACING_DIRECTION side = room.getExits()[i++];// getAttachSide(roomModel, edge);
            room.makeExit(side);
            if (entrance == null)
                entrancePoint = attacher.adjust(entrancePoint, side, room, true);
            Room link = findFittingAndAttach(entrancePoint, getRandomSingleExitTemplate(),
             ROOM_TYPE.CORRIDOR, side);
            if (link == null) {
                //link-less attach
                entrancePoint = attacher.getExitPoint(room, side);
//                    entrancePoint = attacher.getAttachPoint()
            } else {
                entrancePoint = attacher.getExitPoint(link, side);
            }
            Room newRoom = build(edge.getNodeTwo(), entrancePoint, FacingMaster.rotate180(side));
            if (newRoom==null ) {
                for (int n = 0; n < 4; n++) {
            side =FacingMaster.rotate(side, true);
                buildLinks(entrancePoint, entrance, room, links);
            }
            }
        }
    }

    private Room findFittingAndAttach(Point entrancePoint, EXIT_TEMPLATE roomExitTemplate,
                                      ROOM_TYPE roomType, FACING_DIRECTION parentEntrance) {
        Loop loop = new Loop(50);
        RoomModel roomModel = null;
        Room room = null;
        Point roomPoint;  while (true) {
            roomModel = templateMaster.getRandomModel(roomType,
             roomExitTemplate
             , parentEntrance);
              roomPoint = entrancePoint;
            if (parentEntrance != null) {
                roomPoint = attacher.getRoomPoint(entrancePoint,// FacingMaster.rotate180
                 (parentEntrance), roomModel);
            }
            room = model.addRoom(roomPoint, roomModel);
            if (room != null) {
                break;
            }
            if (loop.ended())
                return null;
        }
        if (parentEntrance != null)
        {
            Point newPoint = room.setNewEntrance(parentEntrance);
            model.getRoomMap().remove(roomPoint);
            model.getRoomMap().put(newPoint, room);
        }
        return room;
    }


    private void attach(LevelGraphNode to, RoomModel from, FACING_DIRECTION side) {
/*       RoomModel roomModel = build(to, attacher.getAttachPoint(from, side), side);
*/
//attacher.attach(roomModel, from, side);// give real points
    }

    private FACING_DIRECTION getAttachSide(RoomModel roomModel, LevelGraphEdge edge) {

        return null;
    }

    private FACING_DIRECTION getExit(EXIT_TEMPLATE roomExitTemplate,
                                     FACING_DIRECTION entrance) {
        //mirrored
        if (roomExitTemplate == EXIT_TEMPLATE.THROUGH)
            return entrance;
        DIRECTION dir = entrance.getDirection();
        return FacingMaster.getFacingFromDirection(DirectionMaster.rotate90(dir, true));
    }

    private EXIT_TEMPLATE getRandomSingleExitTemplate() {
        return
         //RandomWizard.random() ? EXIT_TEMPLATE.ANGLE :
         EXIT_TEMPLATE.THROUGH;
    }


    private Point getBasePoint() {
        FACING_DIRECTION side =
//         RandomWizard.random() ?
         FACING_DIRECTION.NONE
//          :FacingMaster.getRandomFacing()
         ;
        switch (side) {
            case NONE:
                return new PointX(
                 data.getX() / 2,
                 data.getY() / 2);
        }
        return null;
    }

    private void attach(RoomModel nodeTwo, RoomModel link, FACING_DIRECTION side) {
    }


}

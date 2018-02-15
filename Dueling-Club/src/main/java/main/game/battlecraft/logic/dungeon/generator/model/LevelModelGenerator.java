package main.game.battlecraft.logic.dungeon.generator.model;

import main.game.battlecraft.logic.battlefield.FacingMaster;
import main.game.battlecraft.logic.dungeon.building.DungeonBuilder.ROOM_TYPE;
import main.game.battlecraft.logic.dungeon.generator.GeneratorEnums.EXIT_TEMPLATE;
import main.game.battlecraft.logic.dungeon.generator.LevelData;
import main.game.battlecraft.logic.dungeon.generator.graph.LevelGraph;
import main.game.battlecraft.logic.dungeon.generator.graph.LevelGraphEdge;
import main.game.battlecraft.logic.dungeon.generator.graph.LevelGraphNode;
import main.game.bf.Coordinates.DIRECTION;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.game.bf.DirectionMaster;
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
    private Map<LevelGraphNode, RoomModel> nodeModelMap = new HashMap<>();

    public LevelModelGenerator(LevelData data) {
        this.data = data;
    }

    public LevelModel buildModel(LevelGraph graph) {
        model = new LevelModel();
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

    private void buildLinked(LevelGraphNode sub, RoomModel link, FACING_DIRECTION side) {
        build(sub, attacher.getExitPoint(link, side), side);

    }

    public RoomModel build(LevelGraphNode node, Point entrancePoint, FACING_DIRECTION entrance) { //'parent' in args? point?
        RoomModel roomModel = nodeModelMap.get(node);
        if (roomModel != null)
            return roomModel;
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

        roomModel = findFittingAndAttach(entrancePoint, roomExitTemplate, node.getRoomType()
         , entrance);
        if (roomModel == null) return null; //don't break the chain!
        nodeModelMap.put(node, roomModel);
        //recursive build - will it ensure that Main Paths are built?
        if (!mergeLinks) {
            int i = 0;
            for (LevelGraphEdge edge : links) {
                if (roomModel.getExits().length <= i) {
                    break;
                }
                FACING_DIRECTION side = roomModel.getExits()[i++];// getAttachSide(roomModel, edge);
                RoomModel link = findFittingAndAttach(entrancePoint, getRandomSingleExitTemplate(),
                 ROOM_TYPE.CORRIDOR, side);
                if (link == null) {
                    //link-less attach
                    entrancePoint = attacher.getExitPoint(roomModel, side);
                } else {
                    entrancePoint = attacher.getExitPoint(link, side);
                }
                build(edge.getNodeTwo(), entrancePoint, side);
            }
        } else {
            FACING_DIRECTION side = getExit(roomExitTemplate, entrance); //link entrance
            RoomModel link = findFittingAndAttach
             (new Point(x, y), exitTemplate,
              ROOM_TYPE.CORRIDOR, side);


            side = getExit(exitTemplate, side); //now for node entrance
            attacher.attach(roomModel, link, side);
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
        return roomModel;
        //check if already built
//        if (models.contains(linkedNodes.get(i))) nodes!
    }

    private RoomModel findFittingAndAttach(Point entrancePoint, EXIT_TEMPLATE roomExitTemplate,
                                           ROOM_TYPE roomType, FACING_DIRECTION entrance) {
        Loop loop = new Loop(50);
        RoomModel roomModel = null;
        while (true) {
            roomModel = templateMaster.getRandomModel(roomType,
             roomExitTemplate
             , entrance);
            Point roomPoint = entrancePoint;
            if (entrance != null) {
                roomPoint = attacher.getRoomPoint(entrancePoint, entrance, roomModel);
            }
            if (model.addRoom(roomPoint, roomModel)) {
                break;
            }
            if (loop.ended())
                return null;
        }
        return roomModel;
    }


    private void attach(LevelGraphNode to, RoomModel from, FACING_DIRECTION side) {
//        RoomModel roomModel = build(to, attacher.getAttachPoint(from, side), side);
//        attacher.attach(roomModel, from, side);// give real points
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
                return new Point(
                 data.getX() / 2,
                 data.getY() / 2);
        }
        return null;
    }

    private void attach(RoomModel nodeTwo, RoomModel link, FACING_DIRECTION side) {
    }


}

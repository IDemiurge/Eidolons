package main.game.battlecraft.logic.dungeon.generator.model;

import main.game.battlecraft.logic.dungeon.building.DungeonBuilder.ROOM_TYPE;
import main.game.battlecraft.logic.dungeon.generator.GeneratorEnums.EXIT_TEMPLATE;
import main.game.battlecraft.logic.dungeon.generator.LevelData;
import main.game.battlecraft.logic.dungeon.generator.graph.LevelGraph;
import main.game.battlecraft.logic.dungeon.generator.graph.LevelGraphEdge;
import main.game.battlecraft.logic.dungeon.generator.graph.LevelGraphNode;
import main.game.bf.Coordinates.FACING_DIRECTION;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by JustMe on 2/13/2018.
 */
public class LevelModelGenerator {

    LevelData data;
    private LevelModel model;
    private LevelGraph graph;
    private RoomAttacher attacher;
    private RoomTemplateMaster templateMaster;

    public LevelModelGenerator(LevelData data) {
        this.data = data;
    }

    public LevelModel buildModel(LevelGraph graph) {
        model = new LevelModel();
        this.graph = graph;
        this.attacher = new RoomAttacher(data, model);
        templateMaster= new RoomTemplateMaster(data, model);
        build();
        return model;
    }

    public void build() {
        build(graph.getNodeById(0), getBasePoint(), null );
    }

    private boolean isMergeLinks(LevelGraphNode node, Set<LevelGraphEdge> links,
                                 List<LevelGraphNode> linkedNodes) {
        //check edges are uniform
    }

    private EXIT_TEMPLATE getExitTemplate(boolean mergeLinks, LevelGraphNode node, Set<LevelGraphEdge> links, List<LevelGraphNode> linkedNodes, FACING_DIRECTION entrance) {
    int toLink = linkedNodes.size();
        if (mergeLinks) {

        }
        switch (node.getRoomType()) {

        }
        return EXIT_TEMPLATE.THROUGH;
    }
    public RoomModel build(LevelGraphNode node, Point point, FACING_DIRECTION entrance) { //'parent' in args? point?
        Set<LevelGraphEdge> links = graph.getAdjList().get(node);
        List<LevelGraphNode> linkedNodes = new ArrayList<>();
        for (LevelGraphEdge sub : links) {
            linkedNodes.add(sub.getNodeTwo());
        }
        int x = point.x;
        int y = point.y;

        boolean mergeLinks = isMergeLinks(node, links, linkedNodes);
        EXIT_TEMPLATE exitTemplate = getExitTemplate(mergeLinks, node, links, linkedNodes, entrance);
        FACING_DIRECTION[] exits = getExits(mergeLinks, linkedNodes, exitTemplate);
        RoomModel roomModel = chooseTemplate(node.getRoomType(), exits);
        model.addRoom(x, y, roomModel);
        if (mergeLinks) {
            RoomModel link =templateMaster. chooseTemplate(ROOM_TYPE.CORRIDOR, exits);
            model.addRoom(x, y, link);
            attach(roomModel, link, side);
            for (LevelGraphNode sub : linkedNodes) {
                attach(sub, link, side);
            }
        } else {
            for (LevelGraphEdge edge : links) {
                side = getAttachSide(roomModel, edge);
                RoomModel link =templateMaster. chooseTemplate(ROOM_TYPE.CORRIDOR, exits);
                if (link == null) {
                    attach(edge.getNodeTwo(),roomModel,   side);
                } else {
                model.addRoom(x, y, link);
                attach(roomModel, link, side);
                attach(edge.getNodeTwo(), link, side);
                }
            }
        }
//        // TODO direct attach w/o corridor?!
        return roomModel;
        //check if already built
//        if (models.contains()) nodes!
    }


    private void attach(LevelGraphNode to, RoomModel from, FACING_DIRECTION side) {
        RoomModel roomModel = build(to, attacher.getAttachPoint(from, side), side);
        attacher.attach(roomModel, from, side);// give real points

    }

    private FACING_DIRECTION[] getExits(boolean mergeLinks, LevelGraphEdge edge,
                                        Set<LevelGraphEdge> links, FACING_DIRECTION... sides) {
        return new FACING_DIRECTION[0];
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

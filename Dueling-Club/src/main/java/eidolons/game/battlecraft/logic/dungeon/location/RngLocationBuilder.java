package eidolons.game.battlecraft.logic.dungeon.location;

import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.battlefield.DC_ObjInitializer;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.init.RngXmlMaster;
import main.content.CONTENT_CONSTS.FLIP;
import main.content.DC_TYPE;
import main.data.xml.XML_Converter;
import main.entity.type.ObjAtCoordinate;
import main.game.bf.directions.DIRECTION;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.launch.TypeBuilder;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 8/11/2018.
 */
public class RngLocationBuilder extends LocationBuilder {
    public void loadLevel(DungeonLevel level, DC_Game game) {

        for (ObjAtCoordinate at : level.getObjects()) {
            game.createUnit(at.getType(), at.getCoordinates().x, at.getCoordinates().y, DC_Player.NEUTRAL);
        }
        for (ObjAtCoordinate at : level.getUnits()) {
            game.createUnit(at.getType(), at.getCoordinates().x, at.getCoordinates().y,
             game.getPlayer(true));
        }


        if (level.getDirectionMap() != null) {
            try {
                DC_ObjInitializer.initDirectionMap(0, level.getDirectionMap());
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        if (level.getFlipMap() != null) {
            try {
                DC_ObjInitializer.initFlipMap(0, level.getFlipMap());
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
    }

    protected LevelBlock createBlock(Node node, LevelZone zone) {
        LevelBlock b = new LevelBlock(zone);

        for (Node subNode : XML_Converter.getNodeList(node)) {
            if (StringMaster.compareByChar(subNode.getNodeName(), COORDINATES_NODE)) {
               b.setCoordinatesList(CoordinatesMaster.getCoordinatesFromString(subNode.getTextContent()));
            } else if (StringMaster.compareByChar(subNode.getNodeName(), OBJ_NODE)) {
                for (String s : ContainerUtils.open(subNode.getTextContent())) {
                    b.getObjects().add(
                     new ObjAtCoordinate(s.split("=")[0], s.split("=")[1], DC_TYPE.BF_OBJ));
                }


                //                 DC_ObjInitializer.initMapBlockObjects(dungeon, b, subNode.getTextContent());
            }
        }
        return b;
    }

    public DungeonLevel loadLevel(String path) {
        String xml = FileManager.readFile(path);
        List<LevelZone> zones = new ArrayList<>();
        int n =0;
        for (Node node : XML_Converter.getNodeList(XML_Converter.getDoc(xml))) {

            List<Node> subNodes = XML_Converter.getNodeList(node);
            Node blocksNode = null;
            for (Node subNode : subNodes) {
                if (subNode.getNodeName().equalsIgnoreCase(RngXmlMaster.BLOCKS_NODE)) {
                    blocksNode = subNode;
                } else {
                    // other
                }
            }
            LevelZone zone = new LevelZone(n++);
            subNodes = XML_Converter.getNodeList(blocksNode);
            for (Node subNode : subNodes) {
                zone.getSubParts().add(createBlock(subNode, zone));
            }


            zones.add(zone);
        }

//        style, ambi, color, ill, id
        DungeonLevel level = new RestoredDungeonLevel(zones);
        //spawn(); TODO so lvls are really all except units
        return level;
    }

    protected void processNode(Node n, Location dungeon, DungeonLevel level) {

        if (StringMaster.compareByChar(n.getNodeName(), (FLIP_MAP_NODE))) {
            level.setFlipMap(new RandomWizard<FLIP>().constructStringWeightMapInversed(n
             .getTextContent(), FLIP.class));

        } else if (StringMaster.compareByChar(n.getNodeName(), (DIRECTION_MAP_NODE))) {
            level.setDirectionMap(new RandomWizard<DIRECTION>()
             .constructStringWeightMapInversed(n.getTextContent(), DIRECTION.class));

        } else if (StringMaster.compareByChar(n.getNodeName(), (CUSTOM_PARAMS_NODE))) {
            TypeBuilder.setParams(dungeon.getDungeon() , n);
        } else if (StringMaster.compareByChar(n.getNodeName(), (CUSTOM_PROPS_NODE))) {
            TypeBuilder.setProps(dungeon.getDungeon(), n);
        }
    }

    @Override
    public Location buildDungeon(String data, List<Node> nodeList) {
        return super.buildDungeon(data, nodeList);
    }
}

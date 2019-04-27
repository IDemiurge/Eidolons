package eidolons.game.battlecraft.logic.meta.igg.xml;

import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.location.RngLocationBuilder;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.generator.init.RngLevelInitializer;
import eidolons.game.module.dungeoncrawl.generator.init.RngMainSpawner;
import eidolons.game.module.dungeoncrawl.generator.init.RngXmlMaster;
import main.content.DC_TYPE;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.entity.type.ObjAtCoordinate;
import main.system.auxiliary.data.FileManager;
import main.system.launch.CoreEngine;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * what if I wanna add some units in LE? WHICH I WILL!!!
 * <p>
 * But to RNG? Really? Maybe it's more about replacing them?
 * How do AiGroups work?
 * <p>
 * but we don't have any system for this yet. Ha!
 */
public class IGG_XmlMaster {

    private String getMetaPath() {
        return PathFinder.getDungeonLevelFolder() + "/meta/" + getLevelName() + ".xml";
    }

    private String getRawPath() {
        return PathFinder.getDungeonLevelFolder() + "/" + getLevelName() + ".xml";
    }
    private String getLE_Path() {
        return PathFinder.getDungeonLevelFolder() + "/crawl/" +
                getLevelName() + ".xml";
    }

    private String getLevelName() {
        return "Underdark";
    }

    private String getRngPath() {
        return PathFinder.getRandomLevelPath() + "/castle/CASTLE BOSS - 1.xml";
    }

    private String getMergedPath() {
        return PathFinder.getRandomLevelPath() + "/merged/" + getLevelName();
    }


    @Before
    public void testLevelFill() {
        CoreEngine.systemInit();
        DC_Engine.dataInit();
        CoreEngine.setjUnit(true);
        String path = getRngPath();
        DungeonLevel level = RngLocationBuilder.loadLevel(path);
        initAndWriteLevel(level);

    }

    @Test
    public void mergeData() {
        String le_data = FileManager.readFile(getRawPath());
        String rng = FileManager.readFile(getRngPath());
        String metaData = FileManager.readFile(getMetaPath());
        /**
         * so, how will I update the RNG ?
         *
         * create object list first then.
         * for same coordinate - replace
         * for new - no ai, just stand still
         */

        String objNode = XML_Converter.findNode(le_data, RngXmlMaster.OBJECTS_NODE).getTextContent();
        List<ObjAtCoordinate> objects =validateObjects(objNode);
        if (objects==null) {
            return;
        }
        String aiNode = XML_Converter.findNode(metaData, RngXmlMaster.AI_GROUPS_NODE).getTextContent();

        Map<List<ObjAtCoordinate>, RngMainSpawner.UNIT_GROUP_TYPE> groups = RngLocationBuilder.initAiData(aiNode, null);
    //TODO remove if missing!
        for (ObjAtCoordinate object : objects) {
            if (object.getType().getOBJ_TYPE_ENUM() == DC_TYPE.BF_OBJ) {
                //void? we could indeed use those void objects in a custom way!
                //what if we already had a DL to work with?
            } else {
                // ASSUMES ONLY 1 UNIT PER CELL
                for (List<ObjAtCoordinate> list : groups.keySet()) {
                    for (ObjAtCoordinate obj : list) {
                        if (obj.getCoordinates().equals(object.getCoordinates())) {
                            if (!obj.getType().equals(object.getType()))
                                obj.setType(object.getType());
                        }
                    }
                }

            }

        }
//        rng = updateVoid(objects)

        objNode=XML_Converter.wrap(RngXmlMaster.OBJECTS_NODE, objNode, false);
        aiNode=XML_Converter.wrap(RngXmlMaster.AI_GROUPS_NODE, aiNode, false);

        rng = XML_Converter.unwrap(rng);
        rng += objNode;
        rng += aiNode;
        rng = XML_Converter.wrap("Level", rng, false);

        FileManager.write(rng, getMergedPath());

    }


    /**
     * @param level TODO VOID
     *              TODO anti-void
     */
    public void initAndWriteLevel(DungeonLevel level) {
        new RngLevelInitializer().init(level);
        level.setPowerLevel(getPowerLevel());
        new RngMainSpawner().spawn(level);
        String xml = level.getObjDataXml();
        xml = xml.replace(";", ",");

        String blockData = "<Block_Type>Room</Block_Type>\n";
        blockData += XML_Converter.wrap("Coordinates",
                CoordinatesMaster.getStringFromCoordinates(
                        CoordinatesMaster.getCoordinatesWithin(-1, level.getTileMap().getWidth(), -1, level.getTileMap().getHeight())));
        xml += blockData + "\n";
        xml = XML_Converter.wrap("Block", xml);
        xml = XML_Converter.wrap(LocationBuilder.BLOCKS_NODE, xml);
        xml = XML_Converter.wrap("Zone1", xml);
        xml = XML_Converter.wrap(LocationBuilder.ZONES_NODE, xml);
        String copy_xml = FileManager.readFile(getLE_Path());
        String w = "<Bf_Width>" +
                level.getTileMap().getWidth() +
                "</Bf_Width>";
        String h = "<Bf_Height>" +
                level.getTileMap().getHeight() +
                "</Bf_Height>";
        copy_xml = copy_xml.replaceFirst(
                XML_Converter.findNodeText(copy_xml, "Bf_Width"), w);
        copy_xml = copy_xml.replaceFirst(
                XML_Converter.findNodeText(copy_xml, "Bf_Height"), h);

        int from = copy_xml.indexOf(XML_Converter.openXml(LocationBuilder.ZONES_NODE));
        int to = copy_xml.lastIndexOf(XML_Converter.closeXml(LocationBuilder.ZONES_NODE));

        String zoneNode = copy_xml.substring(from, to) + XML_Converter.closeXml(LocationBuilder.ZONES_NODE);
        copy_xml = copy_xml.replaceFirst(zoneNode, xml);
        FileManager.write(copy_xml, getRawPath());
        String meta= level.getAiData();
        FileManager.write(meta, getMetaPath());


    }

    private int getPowerLevel() {
        return 900;
    }

    private List<ObjAtCoordinate> validateObjects(String objNode) {
        List<ObjAtCoordinate> objects = new ArrayList<>();
        for (String s : objNode.split(",")) {
            ObjAtCoordinate obj = null;
            if (!(obj = new ObjAtCoordinate(s, DC_TYPE.BF_OBJ)).isValid())
                if (!(obj = new ObjAtCoordinate(s, DC_TYPE.UNITS)).isValid())
                    return null;

            objects.add(obj);
        }
        return objects;
    }


}

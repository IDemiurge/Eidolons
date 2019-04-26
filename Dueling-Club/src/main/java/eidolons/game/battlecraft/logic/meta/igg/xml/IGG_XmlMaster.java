package eidolons.game.battlecraft.logic.meta.igg.xml;

import eidolons.game.battlecraft.DC_Engine;
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
 *
 * But to RNG? Really? Maybe it's more about replacing them?
 * How do AiGroups work?
 *
 * but we don't have any system for this yet. Ha!
 */
public class IGG_XmlMaster {

    @Before
    public void testLevelFill(){
        CoreEngine.systemInit();
        DC_Engine.dataInit();
        String path= getRngPath();
        DungeonLevel level = RngLocationBuilder.loadLevel(path);
        initAndWriteLevel(level);

    }

    private String getRngPath() {
        return PathFinder.getRandomLevelPath() +"/castle/CASTLE BOSS - 1.xml";
    }

    private String getMergedPath() {
        return PathFinder.getRandomLevelPath() +"/merged/"+getLevelName();
    }
    @Test
    public void mergeData(){
        String data = FileManager.readFile(getLE_Path());
        String rng = FileManager.readFile(getRngPath());
        String metaData = FileManager.readFile(getMetaPath());
        /**
         * so, how will I update the RNG ?
         *
         * create object list first then.
         *
         *
         * for same coordinate - replace
         * for new - no ai, just stand still
         */

        String objNode =XML_Converter.findNode(data, RngXmlMaster.OBJECTS_NODE).getTextContent();
        if (!validateObjects(objNode)) {
            return;
        }
        String aiNode = XML_Converter.findNode(metaData, RngXmlMaster.AI_GROUPS_NODE).getTextContent();

        Map<List<ObjAtCoordinate>, RngMainSpawner.UNIT_GROUP_TYPE> groups = RngLocationBuilder.initAiData(aiNode, null);
        List<ObjAtCoordinate> objects=     new ArrayList<>() ;

        for (ObjAtCoordinate object : objects) {
            if (object.getType().getOBJ_TYPE_ENUM() == DC_TYPE.BF_OBJ) {

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
        XML_Converter.wrap(objNode, RngXmlMaster.OBJECTS_NODE);
        XML_Converter.wrap(aiNode, RngXmlMaster.AI_GROUPS_NODE);
        String xml = objNode;
        xml+=aiNode;

        rng = XML_Converter.layerDown(rng);
        rng+= xml;
        rng = XML_Converter.wrap(rng, "Level");

        FileManager.write(rng, getMergedPath());
        
    }

    private boolean validateObjects(String objNode) {
        for (String s : objNode.split(",")) {
            if (!new ObjAtCoordinate(s, DC_TYPE.BF_OBJ).isValid())
                if (!new ObjAtCoordinate(s, DC_TYPE.UNITS).isValid())
                    return false;

        }
        return true;
    }


    public void createLE_Meta(DungeonLevel level) {
/**
 * ai groups
 * blocks and zones
 *
 */
    }
    public void initAndWriteLevel(DungeonLevel level) {
        new RngLevelInitializer().init(level);
        level.setPowerLevel(150);
        new RngMainSpawner().spawn(level);
//copy some other dungeon's xml for this
//        XML_Converter.getNodeList()
        String xml =level.getObjDataXml();
//to LE format
xml= XML_Converter.wrap(LocationBuilder.OBJ_NODE, xml);
xml= XML_Converter.wrap("Block", xml);
xml= XML_Converter.wrap(LocationBuilder.BLOCKS_NODE, xml);
xml= XML_Converter.wrap("Zone1", xml);
xml= XML_Converter.wrap(LocationBuilder.ZONES_NODE, xml);
        String name = getLevelName();
        String copy_xml = FileManager.readFile(getLE_Path());
        int from = copy_xml.indexOf(XML_Converter.openXml(LocationBuilder.ZONES_NODE));
        int to = copy_xml.lastIndexOf(XML_Converter.closeXml(LocationBuilder.ZONES_NODE));
        String zoneNode = copy_xml.substring(from, to)+XML_Converter.closeXml(LocationBuilder.ZONES_NODE) ;
        copy_xml=copy_xml.replaceFirst(zoneNode, xml);

        FileManager.write(copy_xml, getMetaPath());


    }

    private String getMetaPath() {
        return PathFinder.getDungeonLevelFolder() + "/test/" + getLevelName() + ".xml";
    }

    private String getLE_Path() {
        return PathFinder.getDungeonLevelFolder() + "/crawl/" +
                getLevelName()+ ".xml";
    }

    private String getLevelName() {
        return "Academy Dungeon";
    }
}

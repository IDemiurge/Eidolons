package eidolons.game.battlecraft.logic.meta.igg.xml;

import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.location.RngLocationBuilder;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.generator.init.RngLevelInitializer;
import eidolons.game.module.dungeoncrawl.generator.init.RngMainSpawner;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.system.auxiliary.data.FileManager;
import main.system.launch.CoreEngine;
import org.junit.Test;

/**
 * what if I wanna add some units in LE? WHICH I WILL!!!
 *
 * But to RNG? Really? Maybe it's more about replacing them?
 * How do AiGroups work?
 *
 * but we don't have any system for this yet. Ha!
 */
public class IGG_XmlMaster {

    @Test
    public void testLevelFill(){
        CoreEngine.systemInit();
        DC_Engine.dataInit();
        String path= PathFinder.getRandomLevelPath() +"/castle/CASTLE BOSS - 1.xml";
        DungeonLevel level = RngLocationBuilder.loadLevel(path);
        initAndWriteLevel(level);

    }

    public static void createLE_Meta(DungeonLevel level) {
/**
 * ai groups
 * blocks and zones
 *
 */
    }
    public static void initAndWriteLevel(DungeonLevel level) {
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
        String name = "Underdark";
        String copy_xml = FileManager.readFile(PathFinder.getDungeonLevelFolder() + "/crawl/" +
                name+ ".xml");
        int from = copy_xml.indexOf(XML_Converter.openXml(LocationBuilder.ZONES_NODE));
        int to = copy_xml.lastIndexOf(XML_Converter.closeXml(LocationBuilder.ZONES_NODE));
        String zoneNode = copy_xml.substring(from, to)+XML_Converter.closeXml(LocationBuilder.ZONES_NODE) ;
        copy_xml=copy_xml.replaceFirst(zoneNode, xml);

        FileManager.write(copy_xml, PathFinder.getDungeonLevelFolder() + "/test/"+name+".xml");


    }
}

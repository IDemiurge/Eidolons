package eidolons.game.battlecraft.logic.meta.igg.xml;

import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.logic.battle.test.TestMetaMaster;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonBuilder;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.generator.init.RngXmlMaster;
import eidolons.system.options.OptionsMaster;
import main.content.DC_TYPE;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.entity.type.ObjAtCoordinate;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.PathUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.launch.CoreEngine;
import org.junit.Test;

import java.util.Map;

public class XmlLevelTools {
    @Test
    public void test() {
        XmlLevelTools.insertModule(null, "sublevels/Ashen Path modular.xml",
                "sublevels/Module Maze.xml", 0, 0);
    }

    public static void main(String[] args) {
        expandLevel(null, "sublevels/duncan module.xml", 13, 11, 15, 15, DIRECTION.UP);
    }
public static void extractLevel(DC_Game game,int w, int h,
                                int x, int y, String levelPath, String moduleName){
    String emptyLvl = "empty " + w + " " + h +".xml";



}
    public static void expandLevel(DC_Game game, String modulePath, int w, int h,
                                   int newW, int newH, DIRECTION placeAt) {
        int x = 0;
        int y = 0;
        String emptyLvl = "empty " + newW + " " + newH;
        if (placeAt == null) {
            x = (newW - w) / 2;
            y = (newH - h) / 2;
        } else {
            switch (placeAt) {
                case UP:
                    x = (newW - w) / 2;
                    y = 0;
            }
        }
        String newXml = insertModule(game, emptyLvl+".xml", modulePath, x, y);
        String name = StringMaster.cropFormat(PathUtils.getLastPathSegment(modulePath));

        newXml = newXml.replace("<Name>" +
                emptyLvl +
                "</Name>", "<Name>" +
                name +
                "</Name>");

        newXml = newXml.replace("<Displayed_Name>" +
                emptyLvl + "</Displayed_Name>", "<Displayed_Name>" +
                name + "</Displayed_Name>");
        FileManager.write(newXml, StringMaster.getAppendedFile(modulePath, newW + " " + newH));

    }

    public static String insertModule(DC_Game game, String levelPath, String modulePath, int x, int y) {
        /**
         * we could build both levels and try using normal object methods?
         * do we have level=>xml write?
         * overlaying
         * meta-data
         */
        initXmlTool(game);

        Coordinates offset = new Coordinates(x, y);
        DungeonBuilder builder = game.getDungeonMaster().getBuilder();
        builder.getMaster().getInitializer().setDungeonPath(modulePath);
        builder.getMaster().init();
//        builder.buildDungeon(modulePath);
        DungeonLevel level = builder.getMaster().getDungeonLevel();

        for (ObjAtCoordinate object : level.getObjects()) {
            object.setCoordinates(object.getCoordinates().getOffset(offset));
        }

//        String xml = FileManager.readFile(modulePath);

        String xml = level.getObjDataXml();
        xml = xml.replace(";", ",");

        String blockData = "<Block_Type>Room</Block_Type>\n";
        blockData += XML_Converter.wrap("Coordinates",
                CoordinatesMaster.getStringFromCoordinates(
                        CoordinatesMaster.getCoordinatesWithin(x, game.getDungeon().getWidth(), y, game.getDungeon().getHeight())));
        xml += blockData + "\n";
        xml = XML_Converter.wrap("Block", xml);
        xml = XML_Converter.wrap(LocationBuilder.BLOCKS_NODE, xml);
        xml = XML_Converter.wrap("Zone1", xml);
//        xml = XML_Converter.wrap(LocationBuilder.ZONES_NODE, xml);

        String mainXml = FileManager.readFile(PathFinder.getDungeonLevelFolder() + levelPath);
        mainXml = XML_Converter.appendToNode(LocationBuilder.ZONES_NODE, mainXml, xml);

//        String zones = LevelXmlMaster.getZoneNode(xml);
//        String objData = LevelXmlMaster.getObjectsNode(zones);
//        String s = objData;
//        s = XML_Converter.wrap("blockX", s);
//        String newZone = XML_Converter.wrap("zoneX", s);
//        xml = FileManager.readFile(levelPath);
//        zones = LevelXmlMaster.getZoneNode(xml);
//
//        zones = XML_Converter.wrap(zoneName,
//                XML_Converter.unwrap((zones)) + newZone);

//        location.getPlan().getObjMap().put(type, c);


        Map<String, DIRECTION> directionMap = level.getDirectionMap();
        String directionData = "";
        for (String s : directionMap.keySet()) {
            DIRECTION d = directionMap.get(s);
            ObjAtCoordinate c = new ObjAtCoordinate(s, DC_TYPE.BF_OBJ);
            c.setCoordinates(c.getCoordinates().getOffset(offset));
            directionData += c.toString() + StringMaster.wrapInParenthesis(d.toString()) + ";";
        }

        xml = XML_Converter.appendToNode(RngXmlMaster.DIRECTION_MAP_NODE_OLD, mainXml, directionData);
//        level.setDirectionMap(new RandomWizard<DIRECTION>()
//                .constructStringWeightMapInversed(n.getTextContent(), DIRECTION.class));

        String metaData = "";
        for (String s :
                game.getDungeon().getCustomDataMap().keySet()) {
            metaData += new Coordinates(s).getOffset(offset) + "=" + game.getDungeon().getCustomDataMap().get(s)
                    + ";";
        }
        xml = XML_Converter.appendToNode(LocationBuilder.META_DATA_NODE, xml, metaData);

        FileManager.write(xml, PathFinder.getDungeonLevelFolder() +
                StringMaster.getAppendedFile(levelPath, " merged"));
        /**
         * we can of course just copy from IGG to replace OBJ NODE manually
         *
         * each module oughtta be a ZONE
         */

        return xml;
    }

    private static void initXmlTool(DC_Game game) {
        CoreEngine.systemInit();
        CoreEngine.setDungeonTool(true);
        DC_Engine.dataInit();
        OptionsMaster.init();
        if (game == null) {
            game = new TestMetaMaster().init();
            game.battleInit();
        }
    }
}

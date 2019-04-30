package eidolons.game.battlecraft.logic.meta.igg.xml;

import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.location.RngLocationBuilder;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums;
import eidolons.game.module.dungeoncrawl.generator.init.RngLevelInitializer;
import eidolons.game.module.dungeoncrawl.generator.init.RngMainSpawner;
import eidolons.game.module.dungeoncrawl.generator.init.RngXmlMaster;
import eidolons.game.module.dungeoncrawl.generator.level.ZoneCreator;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileConverter;
import main.content.DC_TYPE;
import main.content.enums.DungeonEnums;
import main.content.enums.entity.UnitEnums;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.entity.type.ObjAtCoordinate;
import main.system.SortMaster;
import main.system.auxiliary.data.FileManager;
import main.system.launch.CoreEngine;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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

    public static final String FOLDER = "to gen";
    private final UnitEnums.UNIT_GROUP presetUnitGroup=null ;
    private final UnitEnums.UNIT_GROUP[] groups={
            UnitEnums.UNIT_GROUP.DWARVES,
            UnitEnums.UNIT_GROUP.HUMANS_BANDITS,
            UnitEnums.UNIT_GROUP.CRITTERS_SPIDERS
    };
    public static final  DungeonEnums.DUNGEON_STYLE presetStyle= DungeonEnums.DUNGEON_STYLE.Grimy;
    private String folder;
    private String name;
    private int powerLevel = 500;
    private DungeonEnums.LOCATION_TYPE dungeonType;

    public IGG_XmlMaster(String folder, String name) {
        this.folder = folder;
        this.name = name;
        if (!folder.isEmpty()) {
        setDungeonType(DungeonEnums.LOCATION_TYPE.valueOf(folder.toUpperCase()));
        }
        RngMainSpawner.setPresetUnitGroup(presetUnitGroup);
        RngMainSpawner.setGroupFilter(Arrays.asList(groups));
    }

    public static final boolean MERGE = true;

    public static void main(String[] args) {
        CoreEngine.setToolIsRunning(true);
        CoreEngine.systemInit();
        DC_Engine.dataInit();
        if (MERGE) {
            new IGG_XmlMaster("", "Underworld.xml").mergeData();
        } else
        for (File subfolder : FileManager.getFilesFromDirectory(
                PathFinder.getDungeonLevelFolder() +
                        FOLDER, true)) {
            for (File file : FileManager.getFilesFromDirectory(subfolder.getPath(), false)) {
                IGG_XmlMaster master = new IGG_XmlMaster(subfolder.getName(), file.getName());
                master.testLevelFill();
//                master.mergeData();
            }
        }
    }

    private String getMetaPath() {
        if (MERGE)
            return PathFinder.getDungeonLevelFolder()   +
                    "/meta/dungeon boss - 7.xml"  ;
        return PathFinder.getDungeonLevelFolder() + "/meta/" + getLevelName();
    }

    private String getRawPath() {
        return PathFinder.getDungeonLevelFolder() + "/" + getLevelName();
    }

    private String getLE_Path() {
        return PathFinder.getDungeonLevelFolder() + "/crawl/Shadow Keep.xml";
    }

    private String getLevelName() {
        return name;
    }

    private String getRngPath() {
        if (MERGE)
            return PathFinder.getRandomLevelPath()   +
                   "/dungeon/dungeon boss - 7.xml"  ;
        return PathFinder.getDungeonLevelFolder() + "/" + FOLDER + "/" +
                folder + "/" + name;
    }

    private String getMergedPath() {
        return PathFinder.getDungeonLevelFolder()+ getLevelName();
    }


    public void testLevelFill() {
        CoreEngine.systemInit();
        DC_Engine.dataInit();
        CoreEngine.setjUnit(true);
        String path = getRngPath();
        DungeonLevel level = RngLocationBuilder.loadLevel(path);
        initAndWriteLevel(level);

    }

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
        List<ObjAtCoordinate> objects = validateObjects(objNode);
        if (objects == null) {
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
//        rng = updateVoid(objects);

        objNode = XML_Converter.wrap(RngXmlMaster.OBJECTS_NODE, objNode, false);
        aiNode = XML_Converter.wrap(RngXmlMaster.AI_GROUPS_NODE, aiNode, false);

        rng = rng.replaceFirst("<AI_GROUPS></AI_GROUPS>", "");
        rng = XML_Converter.unwrap(rng);
        rng += objNode;
        rng += aiNode;
        rng = XML_Converter.wrap("Level", rng, false);
        FileManager.write(rng, getMergedPath());

    }

    private String updateVoid(DungeonLevel level, String xml, List<ObjAtCoordinate> objects) {
//        level.getBlocks().stream().sorted(new SortMaster< LevelBlock>()
//                .getSorterByExpression_(b-> b.getCenterCoordinate()))

//obj

        return xml;
    }


    /**
     * @param level TODO VOID
     *              TODO anti-void
     */
    public void initAndWriteLevel(DungeonLevel level) {
        for (LevelZone zone : level.getZones()) {
            GeneratorEnums.ZONE_TYPE type = GeneratorEnums.ZONE_TYPE.MAIN_AREA;
            zone.setStyle(
                    presetStyle!=null ? presetStyle :
//                    ZoneCreator.getStyle(type, getDungeonType())
                    TileConverter.getStyle(getDungeonType(), false)
            );
        }
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
        String meta = level.getAiData();
        FileManager.write(meta, getMetaPath());


    }

    private int getPowerLevel() {
        return powerLevel;
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


    public DungeonEnums.LOCATION_TYPE getDungeonType() {
        return dungeonType;
    }

    public void setDungeonType(DungeonEnums.LOCATION_TYPE dungeonType) {
        this.dungeonType = dungeonType;
    }
}

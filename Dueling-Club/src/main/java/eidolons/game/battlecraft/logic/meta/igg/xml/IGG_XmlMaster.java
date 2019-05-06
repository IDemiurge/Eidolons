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
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileConverter;
import eidolons.system.text.NameMaster;
import main.content.DC_TYPE;
import main.content.enums.DungeonEnums;
import main.content.enums.DungeonEnums.DUNGEON_STYLE;
import main.content.enums.entity.UnitEnums;
import main.content.values.properties.G_PROPS;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.entity.type.ObjAtCoordinate;
import main.system.PathUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.datatypes.WeightMap;
import main.system.launch.CoreEngine;

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
    private static final boolean PREGEN = true;
    private final UnitEnums.UNIT_GROUP presetUnitGroup = null;
    private final UnitEnums.UNIT_GROUP[] groups = null;
    //    {
//            UnitEnums.UNIT_GROUP.DWARVES,
//            UnitEnums.UNIT_GROUP.HUMANS_BANDITS,
//            UnitEnums.UNIT_GROUP.CRITTERS_SPIDERS
//    };
    public static final DUNGEON_STYLE mainStyle = DUNGEON_STYLE.DWARF;
    public static final DUNGEON_STYLE enterStyle = DUNGEON_STYLE.ROGUE;
    public static final DUNGEON_STYLE altStyle = DUNGEON_STYLE.SPIDER;
    //start and finish !

    WeightMap<DUNGEON_STYLE> styleMap = new WeightMap<>(DUNGEON_STYLE.class)
            .chain(DUNGEON_STYLE.DWARF, 10)
            .chain(DUNGEON_STYLE.ROGUE, 4)
            .chain(DUNGEON_STYLE.SPIDER, 6);
    //how about we make synthetic zones? just for style?
    public static final String LEVEL_NAME = "Underworld.xml";
    private String folder;
    private String name;
    private int powerLevel = 300;
    private DungeonEnums.LOCATION_TYPE dungeonType;
    private boolean syntheticZones = true;
    private String rngFilePath = "dungeon/dungeon boss - 13.xml"; //into custom property!

    public IGG_XmlMaster(String folder, String name) {
        this.folder = folder;
        this.name = name;
        if (!folder.isEmpty()) {
            setDungeonType(DungeonEnums.LOCATION_TYPE.valueOf(folder.toUpperCase()));
        }
        RngMainSpawner.setPresetUnitGroup(presetUnitGroup);
        if (groups != null) {
            RngMainSpawner.setGroupFilter(Arrays.asList(groups));
        }
    }

    public static boolean MERGE = false;

    public static void main(String[] args) {
        if (args.length > 0)
            MERGE = true;

        CoreEngine.setToolIsRunning(true);
        CoreEngine.systemInit();
        DC_Engine.dataInit();
        if (MERGE) {
            new IGG_XmlMaster("", LEVEL_NAME).mergeData();
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
            return PathFinder.getDungeonLevelFolder() +
                    "/meta/" +
                    rngFilePath;
        return PathFinder.getDungeonLevelFolder() + "/meta/" + rngFilePath; //getLevelName();
    }

    private String getOutputPath() {
        return PathFinder.getDungeonLevelFolder() + "to edit/";
    }

    //just the background and some useless data
    private String getLE_Path() {
        return PathFinder.getDungeonLevelFolder() + "/crawl/Underworld.xml";
    }

    private String getLevelName() {
        return name;
    }

    private String getRngPath() {
        if (MERGE)
            if (PREGEN)
                return PathFinder.getRandomLevelPath() +
                        "/" + "pregenerated" + "/" + rngFilePath;
            else
                return PathFinder.getRandomLevelPath() +
                        "/" + rngFilePath;
        return PathFinder.getDungeonLevelFolder() + "/" + FOLDER + "/" +
                folder + "/" + name;
    }

    private String getMergedPath() {
        return PathFinder.getDungeonLevelFolder() + getLevelName();
    }


    public void testLevelFill() {
        CoreEngine.systemInit();
        DC_Engine.dataInit();
        CoreEngine.setjUnit(true);
        String path = getRngPath();

        initAndWriteLevel(path, 10);

    }

    private void initAndWriteLevel(String path, int times) {
        for (int i = 0; i < times; i++) {
            DungeonLevel level = RngLocationBuilder.loadLevel(path);
            //TODO reset lvl
            List<String> segments = PathUtils.getPathSegments(path);
            rngFilePath = segments.get(segments.size() - 2) + "/" + segments.get(segments.size() - 1);
            initAndWriteLevel(level);
        }
    }

    public void mergeData() {
        List<File> files = FileManager.findFiles(new File(getOutputPath()
                //+"/"+getLevelName()
        ), getLevelName(), true, false);
        for (File file : files) {
            mergeData(file.getName());
        }
    }

    public void mergeData(String levelName) {
        this.name = levelName;
        String le_data = FileManager.readFile(getOutputPath() + getLevelName());

        rngFilePath = XML_Converter.unwrap(XML_Converter.findNodeText(le_data, "Dungeon_Level"));
        String rng = FileManager.readFile(getRngPath());

        DungeonLevel level = RngLocationBuilder.loadLevel(getRngPath());
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
        String nonVoid = "";
        for (ObjAtCoordinate object : objects) {
            if (level.getBlockForCoordinate(object.getCoordinates()) == null) {
                nonVoid += object.getCoordinates() + ";";
            }
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
                            {
//                                obj.setType(object.getType());
                                aiNode=   StringMaster.replaceFirst(aiNode, obj.toString(), object.toString());

                            }
                        }
                    }
                }

            }

        }
//        rng = updateVoid(objects);


        objNode = XML_Converter.wrap(RngXmlMaster.OBJECTS_NODE, objNode, false);
        aiNode = XML_Converter.wrap(RngXmlMaster.AI_GROUPS_NODE, aiNode, false);
        nonVoid = XML_Converter.wrap(RngXmlMaster.NON_VOID_NODE, nonVoid, false);

        rng = rng.replaceFirst("<AI_GROUPS></AI_GROUPS>", "");
        String directionMapData =
                XML_Converter.findNode(le_data, RngXmlMaster.DIRECTION_MAP_NODE).getTextContent();

        rng = rng.replaceFirst("<DIRECTION_MAP></DIRECTION_MAP>", XML_Converter.
                wrap("DIRECTION_MAP", directionMapData));
        rng = XML_Converter.unwrap(rng);
        rng += objNode + "\n";
        rng += aiNode + "\n";
        rng += nonVoid + "\n";
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
        if (syntheticZones) {
            int i = 0;
            for (LevelBlock block : level.getBlocks()) {
                LevelZone zone = new LevelZone(i++);
                DUNGEON_STYLE style = chooseStyleForBlock(block, i);

                zone.setStyle(style);
                block.setZone(zone);
            }
        }
        new RngLevelInitializer().init(level);
        level.setPowerLevel(getPowerLevel());
        new RngMainSpawner().spawn(level);
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


        String directionMapData = level.getDirectionMapData();

        copy_xml =
                StringMaster.replaceFirst(copy_xml,
                        XML_Converter.unwrap(XML_Converter.findNodeText(copy_xml, RngXmlMaster.DIRECTION_MAP_NODE)), directionMapData);

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
        copy_xml = StringMaster.replaceFirst(copy_xml, zoneNode, xml);

        from = copy_xml.lastIndexOf(XML_Converter.openXml("Custom_Params"));
        to = copy_xml.lastIndexOf(XML_Converter.closeXml("Custom_Params"));
        String props = copy_xml.substring(from, to);
        String rpgProp =
                XML_Converter.wrap(G_PROPS.DUNGEON_LEVEL.toString(),
                        rngFilePath) +
                        XML_Converter.wrap(G_PROPS.DUNGEON_TYPE.toString(),
                                rngFilePath) +
                        XML_Converter.wrap(G_PROPS.DUNGEON_GROUP.toString(),
                                rngFilePath) +
                        XML_Converter.wrap(G_PROPS.GROUP.toString(),
                                rngFilePath);
        copy_xml = StringMaster.replaceFirst(copy_xml,
                props, props + rpgProp);

        String name = NameMaster.getUniqueVersionedFileName(getLevelName(), getOutputPath());
        FileManager.write(copy_xml, getOutputPath() + "/" + name);
        String meta = level.getAiData();
        FileManager.write(meta, getMetaPath());


    }

    private DUNGEON_STYLE chooseStyleForBlock(LevelBlock block, int i) {
        switch (block.getRoomType()) {
            case THRONE_ROOM:
                return mainStyle;
            case GUARD_ROOM:
            case TREASURE_ROOM:
                return RandomWizard.random() ? mainStyle : altStyle;
            case ENTRANCE_ROOM:
                return enterStyle;
            case EXIT_ROOM:
                return mainStyle;
            case SECRET_ROOM:
                return altStyle;
            case DEATH_ROOM:
                return RandomWizard.random() ? enterStyle : altStyle;
        }
        return styleMap.getRandomByWeight();
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

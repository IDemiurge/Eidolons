package eidolons.game.battlecraft.logic.meta.igg.xml;

import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.location.RngLocationBuilder;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.init.RngLevelInitializer;
import eidolons.game.module.dungeoncrawl.generator.init.RngMainSpawner;
import eidolons.game.module.dungeoncrawl.generator.init.RngXmlMaster;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    protected static final boolean PREGEN = true;
    protected final UnitEnums.UNIT_GROUP presetUnitGroup = null;
    protected final UnitEnums.UNIT_GROUP[] groups = null;

    //DWARF
//    public static final DUNGEON_STYLE mainStyle = DUNGEON_STYLE.DWARF;
//    public static final DUNGEON_STYLE enterStyle = DUNGEON_STYLE.ROGUE;
//    public static final DUNGEON_STYLE altStyle = DUNGEON_STYLE.SPIDER;
//    WeightMap<DUNGEON_STYLE> styleMap = new WeightMap<>(DUNGEON_STYLE.class)
//            .chain(DUNGEON_STYLE.DWARF, 10)
//            .chain(DUNGEON_STYLE.ROGUE, 4)
//            .chain(DUNGEON_STYLE.SPIDER, 6);

    //CAVERN
    public static final DUNGEON_STYLE mainStyle = DUNGEON_STYLE.CAVE;
    public static final DUNGEON_STYLE enterStyle = DUNGEON_STYLE.CAVE;
    public static final DUNGEON_STYLE exitStyle = DUNGEON_STYLE.TELRAZI;
    public static final DUNGEON_STYLE altStyle = DUNGEON_STYLE.TELRAZI;

    //BASTION2
//    public static final DUNGEON_STYLE mainStyle = DUNGEON_STYLE.PRISON;
//    public static final DUNGEON_STYLE enterStyle = DUNGEON_STYLE.BASTION;
//    public static final DUNGEON_STYLE altStyle = DUNGEON_STYLE.CRYPTS;
    //BASTION
//    public static final DUNGEON_STYLE mainStyle = DUNGEON_STYLE.BASTION;
//    public static final DUNGEON_STYLE enterStyle = DUNGEON_STYLE.CRYPTS;
//    public static final DUNGEON_STYLE exitStyle = DUNGEON_STYLE.PRISON;
//    public static final DUNGEON_STYLE altStyle = DUNGEON_STYLE.PRISON;
    WeightMap<DUNGEON_STYLE> styleMap = new WeightMap<>(DUNGEON_STYLE.class)
            .chain(DUNGEON_STYLE.PRISON, 10)
            .chain(DUNGEON_STYLE.BASTION, 24)
            .chain(DUNGEON_STYLE.CRYPTS, 6);
    //how about we make synthetic zones? just for style?
    public static final String LEVEL_NAME = "Underworld.xml";
    public static final String[] MERGE_LEVEL_NAMES = {
            "Underworld.xml", "Bastion.xml"
    };
    protected String folder;
    protected String name;
    protected int powerLevel = 300;
    protected DungeonEnums.LOCATION_TYPE dungeonType;
    protected boolean syntheticZones = true;
    protected String rngFilePath = "dungeon/overfill dungeon boss - 2.xml"; //into custom property!
    protected String TEMPLATE = "/crawl/Underworld.xml";
    protected int timeToSpawn = 0;
    protected boolean initRequired = true;
//    protected String TEMPLATE="/crawl/Vampire Abode.xml";

    protected String getLE_Path() {
//        return PathFinder.getDungeonLevelFolder() + "/crawl/Underworld.xml";
        return PathFinder.getDungeonLevelFolder() + TEMPLATE;
    }

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
            for (String mergeLevelName : MERGE_LEVEL_NAMES) {
                new IGG_XmlMaster("", mergeLevelName).mergeData();
            }
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

    protected String getMetaPath() {
        if (MERGE)
            return PathFinder.getDungeonLevelFolder() +
                    "/meta/" +
                    rngFilePath;
        return PathFinder.getDungeonLevelFolder() + "/meta/" + rngFilePath; //getLevelName();
    }

    protected String getOutputPath() {
        return PathFinder.getDungeonLevelFolder() + "to edit/";
    }

    //just the background and some useless data

    protected String getLevelName() {
        return name;
    }

    protected String getRngPath() {
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

    protected String getMergedPath() {
        return PathFinder.getDungeonLevelFolder() + getLevelName();
    }


    public void testLevelFill() {
        CoreEngine.systemInit();
        DC_Engine.dataInit();
        CoreEngine.setjUnit(true);
        String path = getRngPath();

        clean();
        initAndWriteLevel(path, 10);

    }

    protected void clean() {
//        Path target;
//        Path src= Paths.get(path, newFolder);
//        Path target= Paths.get(path, newFolder);
//        Files.move(src, target);
        List<File> files = FileManager.getFilesFromDirectory(getOutputPath(), false);
        for (File file : files) {
            if (!file.getName().contains("overfill"))
                continue;
            try {
                Files.delete(Paths.get(file.getPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        newFolder = NameMaster.getUniqueVersionedFileName()
    }

    protected void initAndWriteLevel(String path, int times) {
        for (int i = 0; i < times; i++) {
            DungeonLevel level = RngLocationBuilder.loadLevelFromPath(path);
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

    public static String getDoorKeyData(String levelName) {
        levelName = PathUtils.getLastPathSegment(levelName).toLowerCase();
        switch (levelName) {
            case "ashen path.xml":
                return "Bone Door Enchanted(Mystery solution);Whispering Door()";
            case "underworld.xml":
                return "Iron Bars(Iron Key);Iron Door(Iron Key);Dwarven Door(Iron Key);" +
                        "Crimson Door(Bone Key);Bone Door Enchanted(Ghost Key);" +
                        "Dwarven Rune Door(Golden Key);Sinister Door(Dark Key)";
            case "gates.xml":
                return
                        "Crimson Door(Master Key);Dwarven Rune Door(Master Key)";
            case "vault.xml":
                return
                        "Crimson Door(Master Key);Bone Door Enchanted(Master Key)";

        }
        return "No door key data for " + levelName;
    }

    public static String getDialogueData(String levelName) {
        return "sentries=aggro;";
    }
        public static String getEntrancesData(String levelName) {
        if (EidolonsGame.BOSS_FIGHT) { //TODO boss fix
            return "Dark Winding Upward Stairs(11-7);Dark Winding Upward Stairs(11-1)";
        }
//        if (CoreEngine.isLiteLaunch()) { //TODO boss fix
//            entranceData = "Dark Winding Upward Stairs(15-12);Dark Winding Upward Stairs(0-0)";
//        }
        if (EidolonsGame.BRIDGE) {
//            return "Ash Vault(48-21);The Light(9-1)";
            return "Ash Vault(23-26);The Light(9-1)";
//            return "Blackness(0-15);The Light(20-1)";
        } else if (EidolonsGame.TUTORIAL_MISSION) {
            return "Dark Winding Upward Stairs(15-12);Dark Winding Upward Stairs(0-0)";
        }
        switch (PathUtils.getLastPathSegment(levelName.toLowerCase())) {
            case "underworld.xml":
                return "13-18=Upward Stairs;0-19=Dark Winding Downward Stairs";
            case "gates.xml":
                return "Winding Downward Stairs(12-19);Downward Stairs(12-0);";
            case "vault.xml":
                return
                        "Wide Downward Stairs(7-11);Wide Downward Stairs(7-0)";
        }
        return levelName;
    }

    /**
     * takes an LE-edited level and transforms it
     * writes out RNG format level
     */
    public void mergeData(String levelName) {
        this.name = levelName;
        String le_data = FileManager.readFile(getOutputPath() + getLevelName());

        rngFilePath = XML_Converter.unwrap(XML_Converter.findNodeText(le_data, "Dungeon_Level"));
        String rng = FileManager.readFile(getRngPath());

        DungeonLevel level = RngLocationBuilder.loadLevelFromPath(getRngPath());
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
                            if (!obj.getType().equals(object.getType())) {
//                                obj.setType(object.getType());
                                aiNode = StringMaster.replaceFirst(aiNode, obj.toString(), object.toString());

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

        rng = rng.replaceFirst("<DIRECTION_MAP></DIRECTION_MAP>", "");
        rng +=
                XML_Converter.
                        wrap("DIRECTION_MAP", directionMapData);
        rng = XML_Converter.unwrap(rng);
        rng += objNode + "\n";
        rng += aiNode + "\n";
        rng += nonVoid + "\n";
        rng = XML_Converter.wrap("Level", rng, false);

        FileManager.write(rng, getMergedPath());

    }

    protected String updateVoid(DungeonLevel level, String xml, List<ObjAtCoordinate> objects) {
//        level.getBlocks().stream().sorted(new SortMaster< LevelBlock>()
//                .getSorterByExpression_(b-> b.getCenterCoordinate()))

//obj

        return xml;
    }


    /**
     * randomizes an RNG template and writes it out as LE format level
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
        if (initRequired)
            new RngLevelInitializer().init(level);
        level.setPowerLevel(getPowerLevel());
        for (int i = 0; i < timeToSpawn; i++) {
            new RngMainSpawner().spawn(level);
        }

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

    protected DUNGEON_STYLE chooseStyleForBlock(LevelBlock block, int i) {
        switch (block.getRoomType()) {
            case THRONE_ROOM:
                return mainStyle;
            case GUARD_ROOM:
            case TREASURE_ROOM:
                return altStyle;
            case ENTRANCE_ROOM:
                return enterStyle;
            case EXIT_ROOM:
                return exitStyle;
            case SECRET_ROOM:
                return altStyle;
            case DEATH_ROOM:
                return RandomWizard.random() ? mainStyle : altStyle;
        }
        return mainStyle;
//        return styleMap.getRandomByWeight();
    }

    protected int getPowerLevel() {
        return powerLevel;
    }

    protected List<ObjAtCoordinate> validateObjects(String objNode) {
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

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSyntheticZones(boolean syntheticZones) {
        this.syntheticZones = syntheticZones;
    }

    public void setInitRequired(boolean initRequired) {
        this.initRequired = initRequired;
    }
}

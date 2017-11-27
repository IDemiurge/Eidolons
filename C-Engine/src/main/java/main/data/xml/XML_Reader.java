package main.data.xml;

import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.entity.type.ObjType;
import main.game.core.game.Game;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;
import main.system.launch.TypeBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.*;

/**
 * contains methods for reading Types' xml files, constructing ObjType's and putting them into maps
 * also managed dynamic reload of hero types to avoid data overwriting between HC and AV
 */
public class XML_Reader {
    // private static final Logger = Logger.getLogger(XML_Reader.class);

    private static Map<String, String> xmlMap = new HashMap<>();
    private static Map<String, Set<String>> tabGroupMap = new HashMap<>();
    private static Map<String, Set<String>> treeSubGroupMap = new HashMap<>();

    private static Map<String, Set<String>> macroTabGroupMap = new HashMap<>();
    private static Map<String, Set<String>> macroTreeSubGroupMap = new HashMap<>();

    private static Map<String, Map<String, ObjType>> typeMaps = new HashMap<>();
    private static Map<String, ObjType> bufferCharTypeMap = new HashMap<>(20);
    private static boolean macro;

    private static boolean concurrentReadingOn = true;
    private static Map<String, XML_File> heroFiles = new HashMap<>();
    private static Map<String, XML_File> partyFiles = new HashMap<>();
    private static DequeImpl<XML_File> files = new DequeImpl<>();

    private static Map<String, ObjType> originalCharTypeMap;

    private static String customTypesPath;

    private static boolean brokenXml;

    private static void constructTypeMap(Document doc, String key,
                                         Map<String, Set<String>> tabGroupMap,
                                         Map<String, Set<String>> treeSubGroupMap
    ) {
        key = key.replace("_", " ").toLowerCase();
        LogMaster.log(LogMaster.DATA_DEBUG, "type map: " + key);

        Map<String, ObjType> typeMap =
                typeMaps.computeIfAbsent(key, k -> new XLinkedMap<>());

        NodeList nl = doc.getFirstChild().getChildNodes();
        Set<String> groupSet = new LinkedHashSet<>();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            NodeList nl1 = node.getChildNodes();
            String aspect = node.getNodeName();
            PROPERTY groupingKey = DataManager.getGroupingKey(key);
            PROPERTY subGroupingKey = DataManager.getSubGroupingKey(key);

            for (int a = 0; a < nl1.getLength(); a++) {
                Node typeNode = nl1.item(a);
                String name = typeNode.getNodeName();
                if ("#text".equals(name)) {
                    continue;
                }
                ObjType type = TypeBuilder.buildType(typeNode, key);
                if (type != null) {
                    name = type.getName();
                    // TAB GROUPS
                    if (type.getProperty(groupingKey) == null) {
                        type.setProperty(G_PROPS.ASPECT, aspect);
                    }
                    groupSet.add(type.getProperty(groupingKey));
                    aspect = type.getProperty(groupingKey);
                    // TREE SUB GROUPS
                    String subKey = type.getProperty(subGroupingKey);
                    treeSubGroupMap.computeIfAbsent(aspect, k -> new HashSet<>()).add(subKey);

                    typeMap.put(name, type);
                    LogMaster.log(LogMaster.DATA_DEBUG, typeNode.getNodeName()
                            + " has been put into map as " + type);
                }
            }
        }

        if (tabGroupMap.get(key) == null) {
            tabGroupMap.put(key, groupSet);
        } else {
            tabGroupMap.get(key).addAll(groupSet);
        }
        // if (key.equals(StringS.ABILS.getName())) {
        // Err.info(set + "");
        // }

    }

    public static void loadXml(String path) {
        File folder = new File(path);

        final File[] files = folder.listFiles();
        if (files != null) {
            //DO NOT FOREACH - its slow on arrays
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (checkFile(file)) {
                    try {
                        XML_File xmlFile = readFile(file);
                        if (xmlFile==null )
                            continue;
                        getFiles().add(xmlFile);
                        Document doc = XML_Converter.getDoc(xmlFile.contents);
                        loadMap(xmlFile.type == null ? xmlFile.name : xmlFile.type.getName(), doc);
                    } catch (Exception e) {
                        brokenXml = true;
                        main.system.auxiliary.log.LogMaster.log(1,file+" is broken!" );
                        e.printStackTrace();
                    }
                }
            }
/*            Arrays.stream(files)
                    .filter(XML_Reader::checkFile)
                    .forEach(el -> {
                        XML_File xmlFile = safeReadFile(el);
                        if (xmlFile != null) {
                            getFiles().add(xmlFile);
                            final Document doc = safeParseDoc(xmlFile);
                            if (doc != null) {
                                safeLoadMap(xmlFile, doc);
                            }
                        }
                    });*/
        }
    }

    private static boolean checkFile(File file) {
        if (!file.isFile()) {
            return false;
        }
        return CoreEngine.checkReadNecessary(file.getName());

    }

    public static XML_File readFile(File file) {
        String text = FileManager.readFile(file);
        if (FileManager.readFile(file).length()<15)
        {
            main.system.auxiliary.log.LogMaster.log(1,"empty xml file " +file.getName());
            return null ;
        }
        final String name = file.getName();
        String fileName = name.substring(0, name.length() - ".xml".length());

        if (fileName.contains(DC_TYPE.CHARS.getName())) {
            XML_File heroFile = new XML_File(DC_TYPE.CHARS, fileName, "", macro, text);
            heroFile.setFile(file);
            heroFiles.put(fileName, heroFile);
        }

        XML_File xmlFile;

        String xmlName = fileName, group = null;

        if (fileName.contains("-")) {
            final int indexOf = fileName.indexOf("-");
            xmlName = fileName.substring(0, indexOf).trim();
            group = fileName.substring(indexOf + 1, fileName.length());
        }

        xmlMap.put(xmlName, "");
        xmlFile = new XML_File(DC_TYPE.getType(xmlName), xmlName, group, macro, text);
        return xmlFile;
    }


    public static void readCustomTypeFile(File file, OBJ_TYPE TYPE, Game game) {
        String xml = FileManager.readFile(file);
        createCustomTypeList(xml, TYPE, game);
    }

    private static List<ObjType> createCustomTypeList(String xml, OBJ_TYPE TYPE, Game game) {
        return createCustomTypeList(xml, TYPE, game, true, false, false);
    }

    public static List<ObjType> createCustomTypeList(String xml, OBJ_TYPE TYPE, Game game,
                                                     boolean wrap) {
        return createCustomTypeList(xml, TYPE, game, true, false, wrap);

    }

    public static List<ObjType> createCustomTypeList(String xml, OBJ_TYPE TYPE, Game game,
                                                     boolean overwrite, boolean incompleteTypes) {
        return createCustomTypeList(xml, TYPE, game, overwrite, incompleteTypes, false);
    }

    public static List<ObjType> createCustomTypeList(String xml, OBJ_TYPE TYPE, Game game,
                                                     boolean overwrite, boolean incompleteTypes, boolean wrap) {
        List<ObjType> types = new ArrayList<>();
        if (wrap) {
            xml = XML_Converter.wrap("wrapped", xml);
        }
        Document doc = XML_Converter.getDoc(xml);

        List<Node> nodes = XML_Converter.getNodeList(XML_Converter.getNodeList(doc).get(0));
        for (Node node : nodes) {
            // typeName = node.getNodeName();
            ObjType type = TypeBuilder.buildType(node, TYPE.toString());
            game.initType(type);

            if (incompleteTypes) {
                ObjType parent = DataManager.getType(type.getProperty(G_PROPS.PARENT_TYPE), type
                        .getOBJ_TYPE_ENUM());
                if (parent != null) {
                    type.setType(parent);
                    for (PROPERTY prop : parent.getPropMap().keySet()) {
                        if (type.getProperty(prop).isEmpty()) {
                            type.setProperty(prop, parent.getProperty(prop));
                        }
                    }
                    for (PARAMETER param : parent.getParamMap().keySet()) {
                        if (type.getParam(param).isEmpty()) {
                            type.setParam(param, parent.getParam(param));
                        }
                    }
                }
            }

            if (overwrite) {
                DataManager.overwriteType(type);
            }
            types.add(type);
        }
        return types;
    }

    public static boolean isConcurrentReadingOn() {
        return concurrentReadingOn;
    }

    public static void setConcurrentReadingOn(boolean concurrentReadingOn) {
        XML_Reader.concurrentReadingOn = concurrentReadingOn;
    }

    public static boolean isBrokenXml() {
        return brokenXml;
    }

    public static void loadXml(boolean macro) {
        if (macro) {
            loadXml(PathFinder.getMACRO_TYPES_PATH());
        } else {
            if (customTypesPath != null) {
                loadXml(customTypesPath);
            } else {
                loadXml(PathFinder.getTYPES_PATH());
            }
        }

    }

    static public void readTypes(boolean macro) {
        readTypes(macro, isConcurrentReadingOn());
    }

    static public void readTypes(boolean macro, boolean concurrentReadingOn) {
        setMacro(macro);
        setConcurrentReadingOn(concurrentReadingOn);

        if (CoreEngine.isArcaneVault()) {
            loadXml(isMacro());
        } else {
            loadXml(false);
            if (macro) {
                loadXml(true);
            }
        }
    }

    private static void loadMap(String name, Document doc) {
        Chronos.mark("TYPE MAPPING " + name);
        Map<String, Set<String>> tabGroupMap = XML_Reader.macroTabGroupMap;
        Map<String, Set<String>> treeSubGroupMap = XML_Reader.macroTreeSubGroupMap;

        if (DC_TYPE.isOBJ_TYPE(name)) {
            tabGroupMap = XML_Reader.tabGroupMap;
            treeSubGroupMap = XML_Reader.treeSubGroupMap;
        }

        constructTypeMap(doc, name, tabGroupMap, treeSubGroupMap);

        LogMaster.getInstance().log(LogMaster.INFO,
                "" + Chronos.getTimeElapsedForMark("TYPE MAPPING " + name));
    }

    public static void loadMap(String name, String text) {
        final Document doc = XML_Converter.getDoc(text);
        loadMap(name, doc);
    }

    public static Map<String, String> getXmlMap() {
        return xmlMap;
    }


    public static Map<String, Map<String, ObjType>> getTypeMaps() {
        return typeMaps;
    }

    public static Set<String> getSubGroups(OBJ_TYPE TYPE) {
        boolean buffer;
        if (TYPE instanceof DC_TYPE) {
            buffer = macro;
            macro = false;
        } else {
            return getSubGroups(TYPE.getName());
        }
        Set<String> set = getSubGroups(TYPE.getName());
        macro = buffer;
        return set;
    }

    public static Set<String> getSubGroups(String strings) {

        Set<String> groups;
        groups = getTabGroupMap().get(strings);
        // Err.info(key + " SUB GROUP SET" + groups);
        if (groups == null) {
            groups = getTabGroupMap(!macro).get(strings);
        }
        if (groups == null) {
            LogMaster.log(1, "NO SUB GROUP SET!" + strings);
        }
        return groups;
    }

    /**
     * @return the tabGroupMap
     */
    public static Map<String, Set<String>> getTabGroupMap(boolean macro) {
        if (macro) {
            return macroTabGroupMap;
        }
        return tabGroupMap;
    }

    /**
     * //     * @param tabGroupMap
     * the tabGroupMap to set
     */

    public static Map<String, Set<String>> getTabGroupMap() {
        if (macro) {
            return macroTabGroupMap;
        }
        return tabGroupMap;
    }

    /**
     * @return the treeSubGroupMap
     */
    public static Map<String, Set<String>> getTreeSubGroupMap(boolean macro) {
        if (macro) {
            return macroTreeSubGroupMap;
        }
        return treeSubGroupMap;
    }

    public static Map<String, Set<String>> getTreeSubGroupMap() {
        if (macro) {
            return macroTreeSubGroupMap;
        }
        return treeSubGroupMap;
    }


    public static boolean isMacro() {
        return macro;
    }

    public static void setMacro(boolean macro) {
        XML_Reader.macro = macro;
    }

    public static void checkHeroesAdded() {
        String key = DC_TYPE.CHARS.getName();
        if (originalCharTypeMap == null) {
            originalCharTypeMap = new MapMaster<String, ObjType>().constructMap(new ArrayList<>(
                    getTypeMaps().get(key).keySet()), new ArrayList<>(getTypeMaps()
                    .get(key).values()));
        }

        bufferCharTypeMap = new MapMaster<String, ObjType>().constructMap(new ArrayList<>(
                getTypeMaps().get(key).keySet()), new ArrayList<>(getTypeMaps().get(key)
                .values()));

        try {
            reloadHeroFiles();
        } catch (Exception e) {
            e.printStackTrace();
            getTypeMaps().put(key, bufferCharTypeMap);
        }
        try {
            checkNewHeroes();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            getTypeMaps().put(key, bufferCharTypeMap);
        }

    }

    private static void reloadHeroFiles() {
        for (XML_File heroFile : heroFiles.values()) {
            LogMaster.setOff(true);
            try {
                loadMap(heroFile.getType().getName(), FileManager.readFile(heroFile.getFile()));
            } catch (Exception e) {
                LogMaster.setOff(false);
                LogMaster.log(1, "Hero File failed to load:" + heroFile);
                e.printStackTrace();
            } finally {
                LogMaster.setOff(false);
            }
            // loadTypeFile(heroFile);
        }
    }

    private static void checkNewHeroes() {
        Collection<ObjType> types = getTypeMaps().get(DC_TYPE.CHARS.getName()).values();
        for (ObjType type : types) {
            ObjType oldType = originalCharTypeMap.get(type.getName());
            if (oldType != null) {
                if (!type.getGroup().equals(oldType.getGroup())) {
                    oldType = null;
                }
            }
            if (oldType == null) {
                LogMaster.log(1, "New Hero loaded:" + type.getName());
                bufferCharTypeMap.put(type.getName(), type);
                originalCharTypeMap.put(type.getName(), type);
            }

        }
    }

    public static DequeImpl<XML_File> getFiles() {
        return files;
    }

    public static void setFiles(DequeImpl<XML_File> files) {
        XML_Reader.files = files;
    }

    public static XML_File getFile(DC_TYPE TYPE) {
        for (XML_File file : files) {
            if (file.getType().equals(TYPE)) {
                return file;
            }
        }
        return null;
    }

    public static String getCustomTypesPath() {
        return customTypesPath;
    }

    public static void setCustomTypesPath(String customTypesPath) {
        XML_Reader.customTypesPath = customTypesPath;
    }

}

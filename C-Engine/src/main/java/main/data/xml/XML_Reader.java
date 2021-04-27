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
import main.entity.type.TypeBuilder;
import main.game.core.game.Game;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;
import main.system.launch.Launch;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;

import static main.system.auxiliary.log.LogMaster.*;

/**
 * contains methods for reading Types' xml files, constructing ObjType's and putting them into maps also managed dynamic
 * reload of hero types to avoid src.main.data overwriting between HC and AV
 */
public class XML_Reader {
    // protected static final Logger = Logger.getLogger(XML_Reader.class);

    protected  XmlModel model=new XmlModel(false);
    protected  XmlModel macroModel =new XmlModel(true);
  private static final XML_Reader instance= new XML_Reader();
    
    protected static final Map<String, Set<String>> macroTabGroupMap = new HashMap<>();
    protected static final Map<String, Set<String>> macroTreeSubGroupMap = new HashMap<>();

    protected static Map<String, ObjType> bufferCharTypeMap = new HashMap<>(20);
    protected static boolean macro;

    protected static boolean concurrentReadingOn = true;
    protected static final Map<String, XML_File> heroFiles = new HashMap<>();
    protected static final Map<String, XML_File> partyFiles = new HashMap<>();

    protected static Map<String, ObjType> originalCharTypeMap;

    protected static String customTypesPath;

    protected static boolean brokenXml;
    protected static boolean macroLoaded;
    protected static boolean microLoaded;
    protected static boolean macroAndMicro;

    static Predicate<ObjType> typeChecker;

    public static void setTypeChecker(Predicate<ObjType> typeChecker) {
        XML_Reader.typeChecker = typeChecker;
    }

    public static XML_Reader getInstance() {
        return instance;
    }

    public XmlModel getModel() {
        return macro? macroModel : model;
    }

    protected static void constructTypeMap(Document doc, String key,
                                           Map<String, Set<String>> tabGroupMap,
                                           Map<String, Set<String>> treeSubGroupMap
    ) {
        key = key.replace("_", " ").toLowerCase();
        log(DATA_DEBUG, "type map: " + key);

        Map<String, ObjType> typeMap =
                getInstance().model.typeMaps.computeIfAbsent(key, k -> new XLinkedMap<>());

        NodeList nl = doc.getFirstChild().getChildNodes();
        Set<String> groupSet = new LinkedHashSet<>();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            NodeList nl1 = node.getChildNodes();
            String group = node.getNodeName();
            PROPERTY groupingKey = DataManager.getGroupingKey(key);
            PROPERTY subGroupingKey = DataManager.getSubGroupingKey(key);

            for (int a = 0; a < nl1.getLength(); a++) {
                Node typeNode = nl1.item(a);
                String name = typeNode.getNodeName();
                if (name.equals("#text")) {
                    continue;
                }
                ObjType type = TypeBuilder.buildType(typeNode, key);
                if (typeChecker != null)
                    if (!typeChecker.test(type)) {
                        continue;
                    }
                if (type != null) {
                    name = type.getName();
                    // TAB GROUPS
                    if (type.getProperty(groupingKey) == null) {
                        type.setProperty(G_PROPS.ASPECT, group);
                    }
                    groupSet.add(type.getProperty(groupingKey));
                    group = type.getProperty(groupingKey);
                    // TREE SUB GROUPS
                    String subKey = type.getProperty(subGroupingKey);
                    treeSubGroupMap.computeIfAbsent(group, k -> new HashSet<>()).add(subKey);

                    type.setInitialized(true);
                    typeMap.put(name, type);
                    log(DATA_DEBUG, typeNode.getNodeName()
                            + " has been put into map as " + type);
                }
            }
        }
        if (groupSet.isEmpty()) {
            return;
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
        Launch.START(Launch.LaunchPhase._4_xml_read);
        File folder = FileManager.getFile(path);
        important("Loading xml files from: \n " + path);
        final File[] files = folder.listFiles();
        List<XML_File> list = new ArrayList<>(files.length);
        if (files != null) {
            //DO NOT FOREACH - its slow on arrays
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isDirectory()) {
                    continue;
                }
                if (checkFile(file)) {
                    try {
                        XML_File xmlFile = readFile(file);
                        if (xmlFile == null)
                            continue;
                        list.add(xmlFile); //multi-threaded? we could do it via some Worker, but later [20-04-21]
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    }
                } else {
                    Launch.ERROR("ENGINE INIT >> not a valid xml file: \n " + file);
                }
            }
            Launch.END(Launch.LaunchPhase._4_xml_read);

            Launch.START(Launch.LaunchPhase._5_xml_init);
            for (XML_File xmlFile : list) {
                try {
                    loadFile(xmlFile);
                } catch (Exception e) {
                    brokenXml = true;
                    Launch.ERROR("ENGINE INIT >> " + xmlFile + " is broken!");
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
            Launch.END(Launch.LaunchPhase._5_xml_init);

            important("ENGINE INIT >> Done loading xml files from: \n " + path);
            important(getFiles().size() + "Xml files: \n " + getFiles());
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

    protected static void loadFile(XML_File xmlFile) {
        getFiles().add(xmlFile);
        Document doc = XML_Converter.getDoc(xmlFile.contents);
        loadMap(xmlFile.type == null ? xmlFile.name :
                xmlFile.type.getName(), doc);
    }

    protected static boolean checkFile(File file) {
        if (!file.isFile()) {
            return false;
        }
        return CoreEngine.checkReadNecessary(file.getName());

    }

    public static XML_File readFile(File file) {
        String text = FileManager.readFile(file);
        if (text.length() < 15) {
            log(1, "empty xml file " + file.getName());
            return null;
        }
        final String name = file.getName();
        String fileName = name.substring(0, name.length() - ".xml".length());

        if (fileName.contains(DC_TYPE.CHARS.getName())) {
            String version=readVersion(text);
            XML_File heroFile = new XML_File(DC_TYPE.CHARS, fileName, "", macro, text, version);
            heroFile.setFile(file);
            heroFiles.put(fileName, heroFile);
        }

        XML_File xmlFile;

        String xmlName = fileName, group = null;

        if (fileName.contains("-")) {
            final int indexOf = fileName.indexOf("-");
            xmlName = fileName.substring(0, indexOf).trim();
            group = fileName.substring(indexOf + 1);
        }
        String version = readVersion(text);
        xmlFile = new XML_File(DC_TYPE.getType(xmlName), xmlName, group, macro, text, version);
        xmlFile.setFile(file);
        return xmlFile;
    }

    protected static String readVersion(String text) {
        String version = CoreEngine.XML_BUILD;
        if (text.contains(XML_Writer.XML_VERSION_PREFIX)) {
            version = text.substring(text.lastIndexOf(XML_Writer.XML_VERSION_PREFIX), text.indexOf(XML_Writer.XML_VERSION_SEPARATOR));
        }
        return version;
    }


    public static void readCustomTypeFile(File file, OBJ_TYPE TYPE, Game game) {
        String xml = FileManager.readFile(file);
        createCustomTypeList(xml, TYPE, game);
    }

    protected static List<ObjType> createCustomTypeList(String xml, OBJ_TYPE TYPE, Game game) {
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

        List<Node> nodes = XmlNodeMaster.getNodeList(XmlNodeMaster.getNodeList(doc).get(0));
        for (Node node : nodes) {
            // typeName = node.getNodeName();

            ObjType type = TypeBuilder.buildType(node, TYPE.toString());
            if (typeChecker != null)
                if (!typeChecker.test(type)) {
                    continue;
                }
            if (game != null)
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

    public static void readTypeFile(String path, OBJ_TYPE type) {
        XML_File xml = readFile(FileManager.getFile(path));
        xml.setType(type);
        loadFile(xml);
    }

    public static void readTypeFile(boolean macro, OBJ_TYPE type) {
        String path = StrPathBuilder.build((macro ? PathFinder.getMACRO_TYPES_PATH()
                : PathFinder.getTYPES_PATH()), type.getName() + ".xml");
        readTypeFile(path, type);
    }

    public static void loadXml(boolean macro) {
        loadXml(macro, false);
    }

    public static void loadXml(boolean macro, boolean force) {
        if (!force) {
            if (macro) {
                if (macroLoaded)
                    return;
            } else if (microLoaded)
                return;
        }
        if (macro) {
            loadXml(PathFinder.getMACRO_TYPES_PATH());
        } else {
            if (customTypesPath != null) {
                loadXml(customTypesPath);
            } else {
                loadXml(PathFinder.getTYPES_PATH());
            }
        }
        if (macro)
            macroLoaded = true;
        else
            microLoaded = true;
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

    protected static void loadMap(String name, Document doc) {
        Chronos.mark("TYPE MAPPING " + name);
        Map<String, Set<String>> tabGroupMap = XML_Reader.macroTabGroupMap;
        Map<String, Set<String>> treeSubGroupMap = XML_Reader.macroTreeSubGroupMap;

        if (DC_TYPE.isOBJ_TYPE(name)) {
            tabGroupMap = getInstance().model.tabGroupMap;
            treeSubGroupMap = getInstance().model.treeSubGroupMap;
        }

        constructTypeMap(doc, name, tabGroupMap, treeSubGroupMap);

        LogMaster.verbose("TYPE MAPPING " + name + " " + Chronos.getTimeElapsedForMark("TYPE MAPPING " + name));
    }

    public static void loadMap(String name, String text) {
        final Document doc = XML_Converter.getDoc(text);
        loadMap(name, doc);
    }


    public static Map<String, Map<String, ObjType>> getTypeMaps() {
        return getInstance().model.typeMaps;
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
            log(1, "NO SUB GROUP SET!" + strings);
        }
        return groups;
    }

    /**
     * //     * @param tabGroupMap the tabGroupMap to set
     */

    public static Map<String, Set<String>> getTabGroupMap() {
        return getTabGroupMap(macro);
    }

    public static Map<String, Set<String>> getTabGroupMap(boolean macro) {
        if (macro) {
            return macroTabGroupMap;
        }
        return getInstance().model.tabGroupMap;
    }

    /**
     * @return the treeSubGroupMap
     */
    public static Map<String, Set<String>> getTreeSubGroupMap(boolean macro) {
        if (macro) {
            return macroTreeSubGroupMap;
        }
        return getInstance().model.treeSubGroupMap;
    }

    public static Map<String, Set<String>> getTreeSubGroupMap() {
        if (macro) {
            return macroTreeSubGroupMap;
        }
        return getInstance().model.treeSubGroupMap;
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
            main.system.ExceptionMaster.printStackTrace(e);
            getTypeMaps().put(key, bufferCharTypeMap);
        }
        try {
            checkNewHeroes();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            getTypeMaps().put(key, bufferCharTypeMap);
        }

    }

    protected static void reloadHeroFiles() {
        for (XML_File heroFile : heroFiles.values()) {
            setOff(true);
            try {
                loadMap(heroFile.getType().getName(), FileManager.readFile(heroFile.getFile()));
            } catch (Exception e) {
                setOff(false);
                log(1, "Hero File failed to load:" + heroFile);
                main.system.ExceptionMaster.printStackTrace(e);
            } finally {
                setOff(false);
            }
            // loadTypeFile(heroFile);
        }
    }

    protected static void checkNewHeroes() {
        Collection<ObjType> types = getTypeMaps().get(DC_TYPE.CHARS.getName()).values();
        for (ObjType type : types) {
            ObjType oldType = originalCharTypeMap.get(type.getName());
            if (oldType != null) {
                if (!type.getGroup().equals(oldType.getGroup())) {
                    oldType = null;
                }
            }
            if (oldType == null) {
                log(1, "New Hero loaded:" + type.getName());
                bufferCharTypeMap.put(type.getName(), type);
                originalCharTypeMap.put(type.getName(), type);
            }

        }
    }

    public static DequeImpl<XML_File> getFiles() {
        return getInstance().model.files;
    }

    public static void setFiles(DequeImpl<XML_File> files) {
        getInstance().model.files = files;
    }

    public static XML_File getFile(DC_TYPE TYPE) {
        for (XML_File file : getInstance().model.files) {
            if (file.getType() == null) {
                continue;
            }
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

package main.data.xml;

import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.content.parameters.PARAMETER;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.data.ConcurrentMap;
import main.data.DataManager;
import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.entity.type.ObjType;
import main.game.Game;
import main.system.auxiliary.*;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;
import main.system.launch.TypeBuilder;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

public class XML_Reader {
    final static DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    // private static final Logger = Logger.getLogger(XML_Reader.class);

    static Map<String, String> xmlMap = new ConcurrentMap<String, String>();
    static Map<String, Set<String>> tabGroupMap = new XLinkedMap<String, Set<String>>();
    static Map<String, Set<String>> treeSubGroupMap = new ConcurrentMap<String, Set<String>>();
    static Map<String, Set<String>> treeSubGroupedTypeMap = new ConcurrentMap<String, Set<String>>();

    static Map<String, Set<String>> macroTabGroupMap = new ConcurrentMap<String, Set<String>>();
    static Map<String, Set<String>> macroTreeSubGroupMap = new ConcurrentMap<String, Set<String>>();
    static Map<String, Set<String>> macroTreeSubGroupedTypeMap = new ConcurrentMap<String, Set<String>>();

    private static Map<String, Map<String, ObjType>> typeMaps =
            new ConcurrentSkipListMap
//     new XLinkedMap
                    <>(
            );
    private static Map<String, ObjType> bufferCharTypeMap = new XLinkedMap<String, ObjType>(20);
    private static boolean macro;

    private static boolean concurrentReadingOn = true;
    private static boolean superConcurrentReadingOn = true;
    private static DequeImpl<Thread> threads = new DequeImpl<Thread>();
    private static Map<String, XML_File> heroFiles = new HashMap<String, XML_File>();
    private static Map<String, XML_File> partyFiles = new HashMap<String, XML_File>();
    private static DequeImpl<XML_File> files = new DequeImpl<XML_File>();

    private static Map<String, ObjType> originalCharTypeMap;

    private static String customTypesPath;

    private static boolean brokenXml;

    public static void constructTypeMap(Document doc, String key,
                                        Map<String, Set<String>> tabGroupMap, Map<String, Set<String>> treeSubGroupMap,
                                        Map<String, Set<String>> treeSubGroupedTypeMap

    ) {

        key = key.replace("_", " ");
        main.system.auxiliary.LogMaster.log(LogMaster.DATA_DEBUG, "type map: " + key);

        Map<String, ObjType> type_map = typeMaps.get(key);
        if (type_map == null)
            type_map = new XLinkedMap<String, ObjType>();
        // else split = true;
        typeMaps.put(key, type_map);

        NodeList nl = doc.getFirstChild().getChildNodes();
        Set<String> group_set = new LinkedHashSet<String>();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            NodeList nl1 = node.getChildNodes();
            String aspect = node.getNodeName();
            PROPERTY groupingKey = DataManager.getGroupingKey(key);
            PROPERTY subGroupingKey = DataManager.getSubGroupingKey(key);
            Set<String> subSet;
            for (int a = 0; a < nl1.getLength(); a++) {
                Node typeNode = nl1.item(a);
                String name = typeNode.getNodeName();
                if (name.equals("#text"))
                    continue;
                ObjType type = TypeBuilder.buildType(nl1.item(a), key);
                name = type.getName();
                // TAB GROUPS
                if (type.getProperty(groupingKey) == null)
                    type.setProperty(G_PROPS.ASPECT, aspect);
                group_set.add(type.getProperty(groupingKey));
                aspect = type.getProperty(groupingKey);
                // TREE SUB GROUPS
                String subKey = type.getProperty(subGroupingKey);

                subSet = treeSubGroupMap.get(aspect);
                if (subSet == null) {
                    subSet = new HashSet<String>();
                    treeSubGroupMap.put(aspect, subSet);
                }
                subSet.add(subKey);

                Set<String> groupedSet = treeSubGroupedTypeMap.get(subKey);
                if (groupedSet == null) {
                    groupedSet = new HashSet<String>();
                    treeSubGroupedTypeMap.put(subKey, groupedSet);
                }
                groupedSet.add(name);

                type_map.put(name, type);
                main.system.auxiliary.LogMaster.log(LogMaster.DATA_DEBUG, nl1.item(a).getNodeName()
                        + " has been put into map as " + type);
            }
        }

        if (tabGroupMap.get(key) == null)
            tabGroupMap.put(key, group_set);
        else
            tabGroupMap.get(key).addAll(group_set);
        // if (key.equals(StringS.ABILS.getName())) {
        // Err.info(set + "");
        // }

    }

    public static void loadXml(String path) {
        File folder = new File(path);
        List<File> list = new LinkedList<>(
                Arrays.asList(folder.listFiles()));
        list.sort((p1, p2) ->
                BooleanMaster.compare(
                        p1.getTotalSpace(), (p2.getTotalSpace())
                ));
        for (File file : list) {
            file.getTotalSpace();
            if (checkFile(file)) {
                readTypeXmlFile(file, isConcurrentReadingOn());

            } else {
                // TODO create empty type file
            }
        }
        // WaitMaster.waitForCondition(isReading());
        if (isConcurrentReadingOn() && !CoreEngine.isConcurrentLaunch()) {
            WaitMaster.waitForInput(WAIT_OPERATIONS.READING_DONE);
        }
        WaitMaster.markAsComplete(WAIT_OPERATIONS.READING_DONE);
        // if (MapMaster.isNotEmpty(typeMaps))
        // for (String key : typeMaps.keySet())
        // main.system.auxiliary.LogMaster.log(1, key + " types: " +
        // typeMaps.get(key).size());
    }

    private static boolean checkFile(File file) {
        if (!file.isFile())
            return false;
        return CoreEngine.checkReadNecessary(file.getName());

    }

    public static void readTypeXmlFile(final File file, boolean concurrentReadingOn) {
        if (concurrentReadingOn) {
            if (superConcurrentReadingOn) {
                // how to split it into groups?
            }
            final String name = file.getName() + " read file thread";
            Thread t = new Thread(new Runnable() {

                public void run() {
                    Chronos.mark(name);
                    Thread t = Thread.currentThread();
                    addReadingThread(t);
                    try {
                        readFile(file);
                    } catch (Exception e) {
                        brokenXml = true;
                        e.printStackTrace();
                    }
                    removeReadingThread(t);
                    Chronos.logTimeElapsedForMark(name);
                }
            }, name) {

                @Override
                public String toString() {
                    return getName();
                }

            };
            t.start();
        } else
            readFile(file);

    }

    protected static void addReadingThread(Thread t) {
        threads.add(t);
        main.system.auxiliary.LogMaster.log(LogMaster.THREADING_DEBUG, t.getName() + " added to "
                + threads);

    }

    protected static void removeReadingThread(Thread thread) {
        threads.remove(thread);
        main.system.auxiliary.LogMaster.log(1, thread.getName() + " removed from " + threads);

        if (threads.isEmpty())
            WaitMaster.receiveInput(WAIT_OPERATIONS.READING_DONE, true);

    }

    public static String readFile(File file) {
        String text = FileManager.readFile(file);
        String fileName = file.getName().replace(".xml", "");
        if (fileName.contains(OBJ_TYPES.CHARS.getName())) {
            XML_File heroFile = new XML_File(OBJ_TYPES.CHARS, fileName, "", // TODO
                    macro, text);
            heroFile.setFile(file);
            heroFiles.put(fileName, heroFile);
        }
        XML_File xmlFile = null;
        if (fileName.contains("-")) {
            String typeName = fileName.substring(0, fileName.indexOf("-")).trim();
            String fullText = xmlMap.get(typeName);
            if (fullText == null)
                xmlMap.put(typeName, text);
            else {
                fullText += text;
                xmlMap.put(typeName, fullText);
            }

            xmlFile = new XML_File(OBJ_TYPES.getType(typeName), fileName, fileName.substring(
                    fileName.indexOf("-") + 1, fileName.length()), macro, text);

            fileName = typeName;
        } else {
            xmlFile = new XML_File(OBJ_TYPES.getType(fileName), fileName, null, macro, text);

            xmlMap.put(fileName, text);
        }

        getFiles().add(xmlFile);

        if (isConcurrentReadingOn())
            loadMap(fileName, text);

        return text;
    }

    public static void readCustomTypeFile(File file, OBJ_TYPE TYPE, Game game) {
        String xml = FileManager.readFile(file);
        createCustomTypeList(xml, TYPE, game);
    }

    public static List<ObjType> createCustomTypeList(String xml, OBJ_TYPE TYPE, Game game) {
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
        List<ObjType> types = new LinkedList<>();
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
                    for (PROPERTY prop : parent.getPropMap().keySet())
                        if (type.getProperty(prop).isEmpty())
                            type.setProperty(prop, parent.getProperty(prop));
                    for (PARAMETER param : parent.getParamMap().keySet())
                        if (type.getParam(param).isEmpty())
                            type.setParam(param, parent.getParam(param));
                }
            }

            if (overwrite)
                DataManager.overwriteType(type);
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

    public static void loadXml() {
        loadXml(isMacro());
    }

    public static void loadXml(boolean macro) {

        if (macro) {
            loadXml(PathFinder.getMACRO_TYPES_PATH());
        } else {
            if (customTypesPath != null)
                loadXml(customTypesPath);
            else
                loadXml(PathFinder.getTYPES_PATH());
        }

    }

    static public void readTypes(boolean macro) {
        readTypes(macro, concurrentReadingOn);
    }

    static public void readTypes(boolean macro, boolean concurrentReadingOn) {
        setMacro(macro);
        setConcurrentReadingOn(concurrentReadingOn);
        if (CoreEngine.isArcaneVault())
            loadXml();
        else {
            loadXml(false);
            loadXml(true);
        }
        if (!concurrentReadingOn)
            loadMaps();

    }

    public static void loadMaps() {

        for (String name : xmlMap.keySet()) {
            String text = xmlMap.get(name);
            loadMap(name, text);
        }

    }

    private static void loadMap(String name, String text) {
        if (text == null) {
            // createTypeDataFile(name, OBJ_TYPES.isOBJ_TYPE(name));
        }
        // xmlMap.put(name, text);
        Chronos.mark("TYPE MAPPING " + name);
        if (OBJ_TYPES.isOBJ_TYPE(name)) {
            try {
                constructTypeMap(XML_Converter.getDoc(text), name, tabGroupMap, treeSubGroupMap,
                        treeSubGroupedTypeMap);
            } catch (ConcurrentModificationException ex) {
                ex.printStackTrace();
                if (Err.getErrorsShown().contains(name)) {
                    Err.error("Data file could not be parsed: " + name);
                    return;
                }
                Err.getErrorsShown().add(name);
                loadMap(name, text);
            } catch (Exception e) {
                e.printStackTrace();
                LogMaster.log(1, "***Failed to load xml: " + name);

                removeReadingThread(Thread.currentThread());
            }
        } else {
            constructTypeMap(XML_Converter.getDoc(text), name, macroTabGroupMap,
                    macroTreeSubGroupMap, macroTreeSubGroupedTypeMap);
        }
        LogMaster.getInstance().log(LogMaster.INFO,
                "" + Chronos.getTimeElapsedForMark("TYPE MAPPING " + name));

    }

    public static Document getDocForGroup(String name) {
        return XML_Converter.getDoc(xmlMap.get(name));
    }

    public static Map<String, String> getXmlMap() {
        return xmlMap;
    }

    public static void setXmlMap(Map<String, String> xmlMap) {
        XML_Reader.xmlMap = xmlMap;
    }

    public static Map<String, Map<String, ObjType>> getTypeMaps() {
        return typeMaps;
    }

    public static Set<String> getSubGroups(OBJ_TYPE TYPE) {
        boolean buffer;
        if (TYPE instanceof OBJ_TYPES) {
            buffer = macro;
            macro = false;
        } else
            return getSubGroups(TYPE.getName());
        Set<String> set = getSubGroups(TYPE.getName());
        macro = buffer;
        return set;
    }

    public static Set<String> getSubGroups(String StringS) {

        Set<String> groups = null;
        groups = getTabGroupMap().get(StringS);
        // Err.info(key + " SUB GROUP SET" + groups);
        if (groups == null) {
            groups = getTabGroupMap(!macro).get(StringS);
        }
        if (groups == null) {
            main.system.auxiliary.LogMaster.log(1, "NO SUB GROUP SET!" + StringS);
        }
        return groups;
    }

    /**
     * @return the tabGroupMap
     */
    public static Map<String, Set<String>> getTabGroupMap(boolean macro) {
        if (macro)
            return macroTabGroupMap;
        return tabGroupMap;
    }

    /**
     * //     * @param tabGroupMap
     * the tabGroupMap to set
     */

    public static Map<String, Set<String>> getTabGroupMap() {
        if (macro)
            return macroTabGroupMap;
        return tabGroupMap;
    }

    /**
     * @return the treeSubGroupMap
     */
    public static Map<String, Set<String>> getTreeSubGroupMap(boolean macro) {
        if (macro)
            return macroTreeSubGroupMap;
        return treeSubGroupMap;
    }

    public static Map<String, Set<String>> getTreeSubGroupMap() {
        if (macro)
            return macroTreeSubGroupMap;
        return treeSubGroupMap;
    }

    /**
     * @param treeSubGroupMap the treeSubGroupMap to set
     */
    public static void setTreeSubGroupMap(Map<String, Set<String>> treeSubGroupMap) {
        XML_Reader.treeSubGroupMap = treeSubGroupMap;
    }

    public static Map<String, Set<String>> getTreeSubGroupedTypeMap(boolean macro) {
        if (macro)
            return macroTreeSubGroupedTypeMap;
        return treeSubGroupedTypeMap;
    }

    /**
     * @return the treeSubGroupedTypeMap
     */
    public static Map<String, Set<String>> getTreeSubGroupedTypeMap() {
        if (macro)
            return macroTreeSubGroupedTypeMap;
        return treeSubGroupedTypeMap;
    }

    /**
     * @param treeSubGroupedTypeMap the treeSubGroupedTypeMap to set
     */
    public static void setTreeSubGroupedTypeMap(Map<String, Set<String>> treeSubGroupedTypeMap) {
        XML_Reader.treeSubGroupedTypeMap = treeSubGroupedTypeMap;
    }

    public static boolean isMacro() {
        return macro;
    }

    public static void setMacro(boolean macro) {
        XML_Reader.macro = macro;
    }

    public static void checkHeroesAdded() {
        String key = OBJ_TYPES.CHARS.getName();
        if (originalCharTypeMap == null)
            originalCharTypeMap = new MapMaster<String, ObjType>().constructMap(new LinkedList<>(
                    getTypeMaps().get(key).keySet()), new LinkedList<ObjType>(getTypeMaps()
                    .get(key).values()));

        bufferCharTypeMap = new MapMaster<String, ObjType>().constructMap(new LinkedList<>(
                getTypeMaps().get(key).keySet()), new LinkedList<ObjType>(getTypeMaps().get(key)
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
                main.system.auxiliary.LogMaster.log(1, "Hero File failed to load:" + heroFile);
                e.printStackTrace();
            } finally {
                LogMaster.setOff(false);
            }
            // loadTypeFile(heroFile);
        }
    }

    private static void checkNewHeroes() {
        Collection<ObjType> types = getTypeMaps().get(OBJ_TYPES.CHARS.getName()).values();
        for (ObjType type : types) {
            ObjType oldType = originalCharTypeMap.get(type.getName());
            if (oldType != null)
                if (!type.getGroup().equals(oldType.getGroup()))
                    oldType = null;
            if (oldType == null) {
                main.system.auxiliary.LogMaster.log(1, "New Hero loaded:" + type.getName());
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

    public static XML_File getFile(OBJ_TYPES TYPE) {
        for (XML_File file : files) {
            if (file.getType().equals(TYPE))
                return file;
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

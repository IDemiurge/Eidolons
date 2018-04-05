package eidolons.system.text;

import eidolons.content.DC_ContentManager.ATTRIBUTE;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.game.battlecraft.DC_Engine;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.enums.entity.HeroEnums;
import main.content.enums.entity.HeroEnums.PRINCIPLES;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Reader;
import main.entity.type.ObjType;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.TimeMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.ListMaster;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TextMaster {
    private static final String descrHeaderSeparator = "<>";
    public static String[] props = {"Lore", "Description",};
    private static String locale = "english";
    private static Map<OBJ_TYPE, List<String>> extractedTypesMap = new XLinkedMap<>();
    private static PROPERTY[] extract_props = {G_PROPS.DESCRIPTION, G_PROPS.LORE, G_PROPS.TOOLTIP,};
    private static OBJ_TYPE[] extractedTypes = {DC_TYPE.CHARS, DC_TYPE.UNITS,
     DC_TYPE.CLASSES, DC_TYPE.SKILLS, DC_TYPE.SPELLS, DC_TYPE.DEITIES,};
    private static String[] extractedTypeGroups = {"Background"};

    public static void generateMissingDescrTemplate() {
        DC_TYPE TYPE = DC_TYPE.SKILLS;
        generateMissingDescrTemplate(TYPE, 1, null, false);
        generateMissingDescrTemplate(TYPE, 1, null, true);
        Map<String, Set<String>> tabGroupMap = XML_Reader.getTabGroupMap();
        for (String sub : tabGroupMap.get(TYPE.getName())) {
//            if (!ContentMaster.basicScope.contains(generic))
//                continue;
            generateMissingDescrTemplate(TYPE, 1, sub, false);
            generateMissingDescrTemplate(TYPE, 1, sub, true);
        }
    }

    public static void generateMissingDescrTemplate(DC_TYPE TYPE, int circle, String groups,
                                                    boolean appendLogic) {
        List<ObjType> list = new ArrayList<>();
        List<ObjType> incomplete = new ArrayList<>();
        for (ObjType type : DataManager.getTypes(TYPE)) {
            if (type.getIntParam(PARAMS.CIRCLE) > circle) {
                continue;
            }
            if (groups != null) {
                if (!groups.contains(type.getGroupingKey())) {
                    continue;
                }
            }
            String descr = type.getDescription();
            if (descr.isEmpty()) {
                list.add(type);
                continue;
            }
            if (descr.contains(".")) {
                continue;
            }
            if (descr.contains("{") && descr.contains("}")) {
                continue;
            }

            incomplete.add(type);
        }
        String text = "";
        String suffix = "";
        if (groups != null) {
            suffix = groups;
        }

        String prefix = "missing descr";
        writeDescrReqFile(list, circle, suffix, prefix, false, appendLogic);

        prefix = "incomplete descr";
        writeDescrReqFile(incomplete, circle, suffix, prefix, true, appendLogic);

    }

    private static void writeDescrReqFile(List<ObjType> list, int circle, String suffix,
                                          String prefix, boolean incomplete, boolean appendLogic) {
        String text = "";
        for (ObjType sub : list) {
            text += descrHeaderSeparator + sub.getName() + descrHeaderSeparator
             + StringMaster.NEW_LINE;
            if (incomplete) {
                text += sub.getProperty(G_PROPS.DESCRIPTION) + StringMaster.NEW_LINE;
            }

            if (appendLogic) {
                text += sub.getProperty(G_PROPS.PASSIVES) + StringMaster.NEW_LINE;
            }
        }
        if (appendLogic) {
            suffix += " with logic";
        }
        String filepath = PathFinder.getTextPath() + prefix + "" + suffix + " up to " + circle
         + " circle.txt";
        FileManager.write(text, filepath);
    }

    private static void extractTypeText() {
        DC_Engine.fullInit();
        int i = 0;
        for (OBJ_TYPE k : extractedTypes) {
            List<String> value = null;
            if (extractedTypeGroups.length > i) {
                value = StringMaster.openContainer(extractedTypeGroups[i]);
            }
            extractedTypesMap.put(k, value);
            i++;
        }

        for (PROPERTY prop : extract_props) {
            for (OBJ_TYPE t : extractedTypesMap.keySet()) {

                List<String> list = extractedTypesMap.get(t);
                if (list == null) {
                    extractTypeText(t, null, prop);
                } else {
                    for (String c : list) {
                        extractTypeText(t, c, prop);
                    }
                }
            }
        }

    }

    private static void extractTypeText(OBJ_TYPE t, String group, PROPERTY prop) {
        String content = "";
        String filepath = getPropsPath();
        for (ObjType type : DataManager.getTypes(t)) {
            if (group != null) {

            }
            content += getDescriptionOpener(type.getName()) + type.getProperty(prop)

            ;

        }
        FileManager.write(content, filepath + t.getName() + " " + prop.getName() + " merge.txt");
    }

    public static void init(String lang) {
        if (lang != null) {
            locale = lang;
        }
        // String testdata = FileManager.readFile(getCompendiumPath() +
        // "l5.odt");
        // for (String prop : props) {
        // initEntityPropText(prop);
        // }
        // initValueDescriptions();

    }

    public static void initValueDescriptionsFolder() {
        for (PARAMS p : PARAMS.values()) {
            String path = getParamsPath();
            String name = p.getName();
            if (p.isMastery()) {
                path += "mastery\\";
            }
            if (p.isAttribute()) {
                path += "attributes\\";
            }
            if (p.isDynamic()) {
                name = name.replace("c ", "");
            }
            String descr = FileManager.readFile(path + name);
            if (!descr.isEmpty()) {
                p.setDescr(descr);
            }
        }
        for (PROPS p : PROPS.values()) {
            String path = getPropsPath();
            String name = p.getName();
            if (p.isPrinciple()) {
                path += "principles\\";
            }
            String descr = FileManager.readFile(path + name);
            if (!descr.isEmpty()) {
                p.setDescr(descr);
            }
        }
    }

    public static void writeAllToFolder() {
        for (ATTRIBUTE p : ATTRIBUTE.values()) {
            FileManager.write(p.getParameter().getDescription(), getParamsPath() + "attributes\\"
             + p.toString() + ".txt");
        }
        for (PRINCIPLES p : HeroEnums.PRINCIPLES.values()) {
            FileManager.write(p.getDescription(), getPropsPath() + "principles\\" + p.toString()
             + ".txt");
        }
    }

    public static void initEntityPropText(String prop) {
        String filepath = getTextPath() + "types\\" + prop + "\\";
        for (File dir : FileManager.getFilesFromDirectory(filepath, true)) {
            if (!dir.isDirectory()) {
                continue;
            }
            DC_TYPE T = DC_TYPE.getType(dir.getName());
            for (File file : FileManager.getFilesFromDirectory(filepath + dir.getName(), false)) {

                ObjType type = DataManager.getType(file.getName(), T);
                if (type != null) {
                    type.setProperty(prop, FileManager.readFile(file));
                }

            }
        }
    }

    public static void main(String[] args) {

        generateMissingDescrTemplate();
        // processDescriptionFile(FileManager.getFile("X:\\Dropbox\\" +
        // "FocusWriting\\2016-7\\"
        // + "param descr.odt"));
        // extractTypeText();
        // merge();
    }

    private static void merge() {
        String mergeContents = "";
        for (File f : FileManager.getFilesFromDirectory("X:\\Dropbox\\"
         + "FocusWriting\\2016-Lore\\", true)) {
            mergeContents += getOdtDescriptionFilesContents(f.getPath());
        }

        FileManager.write(mergeContents, "X:\\Dropbox\\FocusWriting\\2016-Lore\\" + "descr merge "
         + TimeMaster.getFormattedTimeAlt(false) + ".txt");
    }

    public static void processDescriptionFile(File file) {

        String content = FileManager.readFile(file);
        String path = file.getPath().replace(file.getName(), "");
        for (String description : content.split(getDescriptionSeparator())) {
            String name = content.split(getNameSeparator())[0];
            description = description.replace(name, "");
            FileManager.write(description, path + "\\" + name + ".txt");
        }

    }

    private static String getDescriptionOpener(String name) {
        return getDescriptionSeparator() + name + getNameSeparator();
    }

    private static String getOdtSpecialFilesContents(String path) {
        String contents = "";
        for (File f : FileManager.getFilesFromDirectory(path, false)) {

            if (checkSpecialFileName(f.getName())) {
                contents += getNameSeparator() + f.getName() + " " + getNameSeparator() + "\n"
                 + FileManager.readFile(f);
            }

        }
        return contents;
    }

    private static String getOdtDescriptionFilesContents(String path) {
        String contents = "";
        for (File f : FileManager.getFilesFromDirectory(path, false)) {
            if (checkDescrFileName(f.getName())) {
                contents += getDescriptionFileSeparator() + f.getName() + " "
                 + getDescriptionFileSeparator() + "\n" + FileManager.readFile(f);
            }

        }
        return contents;
    }

    private static boolean checkDescrFileName(String name) {
        if (StringMaster.getStringBeforeNumeralsAndSymbols(name).equalsIgnoreCase("dscr")) {
            return true;
        }
        if (StringMaster.getStringBeforeNumeralsAndSymbols(name).equalsIgnoreCase("d")) {
            return true;
        }
        if (StringMaster.getStringBeforeNumeralsAndSymbols(name).equalsIgnoreCase("dcr")) {
            return true;
        }
        if (StringMaster.getStringBeforeNumeralsAndSymbols(name).equalsIgnoreCase("descr")) {
            return true;
        }
        return StringMaster.getStringBeforeNumeralsAndSymbols(name).equalsIgnoreCase("description");
    }

    private static boolean checkSpecialFileName(String name) {
        if (checkDescrFileName(name)) {
            return false;
        }
        String stdFileNames = "l,r,a,re,j,mr,dr,e,i,wr,m,fw,q,gr,meta,s,b,wb,mc,mm,";
        String beforeNumeralsAndSymbols = StringMaster.getStringBeforeNumeralsAndSymbols(name);
        if (beforeNumeralsAndSymbols.isEmpty()) {
            return false;
        }
        for (String substring : StringMaster.open(stdFileNames, ",")) {
            if (beforeNumeralsAndSymbols.equalsIgnoreCase(substring)) {
                return false;
            }
        }
        if (beforeNumeralsAndSymbols.contains("dev")) {
            return false;
        }
        if (beforeNumeralsAndSymbols.contains("meta")) {
            return false;
        }
        return !beforeNumeralsAndSymbols.contains("re");
    }

    public static void mergeOdtFiles(String path, String prefix, String newName, boolean recursive) {

        String mergeContents = "";
        // path= "";
        String contents = getOdtAllFileContents(path, prefix);
        mergeContents += contents;
        FileManager.write(mergeContents, path + newName + " merge " + TimeMaster.getFormattedDate()
         + ".txt");

    }

    private static String getOdtAllFileContents(String path, String prefix) {
        return getOdtAllFileContents(path, prefix, null);
    }

    private static String getOdtAllFileContents(String path, String prefix, Integer lengthLimit) {
        String contents = "";
        for (File f : FileManager.getFilesFromDirectory(path, false)) {

            if (StringMaster.isEmpty(prefix) || f.getName().startsWith(prefix)) {
                if (lengthLimit == null || f.getName().length() < lengthLimit) {
                    contents += getNameSeparator() + f.getName() + " " + getNameSeparator() + "\n"
                     + FileManager.readFile(f);
                }
            }

        }
        return contents;
    }

    private static String getDescriptionFileSeparator() {
        return "*** ";
    }

    private static String getNameSeparator() {
        return ":: ";
    }

    private static String getDescriptionSeparator() {
        return "<>";
    }

    public static void writeEntityTextToFolder(String prop) {
        String filepath = getTextPath() + "types\\" + prop + "\\";
        for (String t : XML_Reader.getTypeMaps().keySet()) {
            Map<String, ObjType> map = XML_Reader.getTypeMaps().get(t);
            for (String name : map.keySet()) {
                ObjType type = map.get(name);
                String content = type.getProperty(prop);
                FileManager.write(content, filepath + name + ".txt");
            }

        }
    }

    public static String getParamsPath() {
        return getTextPath() + "parameters\\";
    }

    public static String getPropsPath() {
        return getTextPath() + "properties\\";
    }

    public static String getCompendiumPath() {

        return getTextPath() + "compendium\\";
    }

    public static String getTextPath() {
        return PathFinder.getEnginePath() + PathFinder.getTextPath() + locale + "\\";
    }

    public static String getLocale() {
        return locale;
    }

    public static void setLocale(String locale) {
        TextMaster.locale = locale;
    }

    public static String readResource(String... parts) {
        List<String> list =
         new ListMaster<String>().getList(parts);
        list.add(0, locale);
        list.add(0, PathFinder.getTextPath());
        return FileManager.readFile(StrPathBuilder.build(list));

    }

    public static String getDisplayedName(PARAMS params) {
        switch (params) {
            case N_OF_ACTIONS:
                if (DC_Engine.isAtbMode()) return "Initiative";
            case C_INITIATIVE:
                if (DC_Engine.isAtbMode()) return "Readiness";
            case C_N_OF_COUNTERS:
            case N_OF_COUNTERS:
                return "Extra Attacks";
        }
        return params.getName();
    }
}

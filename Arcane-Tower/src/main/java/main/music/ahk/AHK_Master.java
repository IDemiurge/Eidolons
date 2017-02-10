package main.music.ahk;

import main.ArcaneTower;
import main.content.properties.G_PROPS;
import main.data.XLinkedMap;
import main.data.xml.XML_Writer;
import main.entity.type.ObjType;
import main.logic.AT_OBJ_TYPE;
import main.logic.AT_PROPS;
import main.music.MusicCore;
import main.music.gui.MusicListPanel;
import main.music.gui.MusicMouseListener;
import main.music.m3u.M3uGenerator;
import main.system.auxiliary.FileManager;
import main.system.auxiliary.GuiManager;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AHK_Master {

    public static final String qwerty = "`1234567890-= qwertyuiop[] asdfghjkl;'\\ zxcvbnm,./";
    public static final int ROWS = 4;
    public static final boolean generateLists = false;
    public static final boolean repairLists = false;
    private static final String PLAYLISTS_FOLDER = "C:\\playlists\\music core\\";
    public static final String SYSTEM_LISTS_FOLDER = PLAYLISTS_FOLDER + "system\\";
    public static final String CUSTOM_LISTS_FOLDER = PLAYLISTS_FOLDER + "custom\\";
    public static final String GENERATED_LISTS_FOLDER = PLAYLISTS_FOLDER + "generated\\";
    private static final String UI_ARCANE_TOWER_KEY_PNG = "UI\\arcane tower\\key white.png";
    private static final String PATH = "X:\\Dropbox\\soft\\";
    private static final String CORE_PATH = PATH + "AutoHotkey Core.ahk";
    private static final String TYPES_PATH = "mus-core\\types";
    public static String[] scripts = {"Rpg Lists.ahk", "Epic Lists.ahk",
            "Songs and Edalar Themes.ahk", "Atmo and Ambience.ahk", "Immersions and Edalar.ahk",
            "Mists of Ersidris, Hero's Exploits.ahk",};
    public static String[] generated_scripts = {"Rpg Lists.ahk", "Epic Lists.ahk"};
    private static int height = 264;
    private static int width = 124;
    static private Map<String, List<String>> tagMap = new XLinkedMap<>();
    static private Map<String, List<String>> groupMap = new XLinkedMap<>();
    private static MusicListPanel panel;
    private static MusicListPanel wrappingPanel;
    private String arg;

    public static void main(String[] args) {
        CoreEngine.setArcaneTower(true);
        if (args.length > 0) {
            MusicCore.initMusicListTypes = true;
        }

        CoreEngine.setSelectivelyReadTypes("Track;Music List;Script;");
        XML_Writer.getBlocked().add(AT_OBJ_TYPE.TRACK);
        ArcaneTower.genericInit(false);
        // PathFinder.init();
        // GuiManager.init();
        // FontMaster.init();
        // ImageManager.init();
        // // XML_Reader.readCustomTypeFile(file, TYPE, game);
        // XML_Reader.setCustomTypesPath(ArcaneTower.getTypesPath());
        // contentInit();
        // CoreEngine.systemInit();
        // XML_Reader.loadXml(ArcaneTower.getTypesPath());
        // for (ObjType t : DataManager.getTypes( MusicTYPE .TRACK )){
        // trackTypeMap.put(t, new Track(t));
        // }

        generateScrips();
        new MusicKeyMaster().initKeys();
        if (generateLists) {
            M3uGenerator.generateCustomM3Us();
        }
        // showMusicListPanel("Full Session Collection.ahk");
        // showMusicListPanel("Full Rpg Lists.ahk");
        showMusicListPanel(PATH + "Full Epic Lists.ahk");
        // AT_Keys.initKeys();
        MusicCore.processData();

        // MusicCore.initDates();
        XML_Writer.backUpAll();
        if (repairLists) {
            M3uGenerator.repairM3uLists();
        }
    }

    private static void generateScrips() {
        for (String name : generated_scripts) {
            generateScript(PATH + name, PATH + "Full " + name);

            removeNumericHotkeysFromScript(PATH + "Full " + name);
            splitScriptIntoMusicAndNonMusic(PATH + "Full " + name);
        }
    }

    public static void generateScript(String appendixPath, String filepath) {
        String content = "";
        for (String line : FileManager.readFileLines(appendixPath)) {
            content += line + StringMaster.NEW_LINE;
        }
        for (String line : FileManager.readFileLines(CORE_PATH)) {
            content += line + StringMaster.NEW_LINE;
        }
        FileManager.write(content, filepath);
    }

    public static Map<String, List<String>> getScriptViewMap(String script) {
        if (!script.contains(PATH)) {
            script = PATH + script;
        }
        Map<String, List<String>> map = new XLinkedMap<>();
        for (String chars : StringMaster.openContainer(AHK_Master.qwerty, " ")) {
            map.put(chars, new LinkedList<>());
        }
        for (String line : FileManager.readFileLines(script)) {
            if (!line.contains("Run C:\\playlists")) {
                continue;
            }
            // line = processComments(line);
            for (String chars : StringMaster.openContainer(AHK_Master.qwerty, " ")) {
                if (chars.contains("" + AHK_Master.getLetter(line))) {
                    map.get(chars).add(line);
                }
            }
        }
        return map;
    }

    public static void showMusicListPanel(String fileName) {

        setPanel(new MusicListPanel(fileName, getScriptViewMap(fileName)));
        panel.init();
        JFrame window = GuiManager.inNewWindow(getPanel(), fileName, GuiManager.getScreenSize());

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Image image = ImageManager.getImage(UI_ARCANE_TOWER_KEY_PNG);
        window.setIconImage(image);

    }

    private static int getY(char letter) {
        int y = 50;
        for (String chars : StringMaster.openContainer(qwerty, " ")) {
            if (chars.contains("" + letter)) {
                return y;
            }
            y += height;
        }
        return y;
    }

    private static int getX(char letter) {
        int x = 40;
        for (String chars : StringMaster.openContainer(qwerty, " ")) {
            x = 0;
            for (char c : chars.toCharArray()) {
                if (c == letter) {
                    return x;
                } else {
                    x += width;
                }
            }
        }
        return x;
    }

    public static int getLetterCode(String line, String qwerty) {
        List<String> list = StringMaster.openContainer(line, "::");
        String keyPart = list.get(0);
        // String funcPart = list.getOrCreate(1).trim();
        if (keyPart.isEmpty()) {
            return 0;
        }
        char letter = keyPart.charAt(keyPart.length() - 1);
        int letterCode = 1000;
        for (char c : qwerty.toCharArray()) {
            if (c == letter) {
                break;
            }
            letterCode += 100;
        }
        for (char c : keyPart.toCharArray()) {
            if (c == letter) {
                break;
            }
            letterCode += getModifierCode(c);

        }
        return letterCode;
    }

    public static int getModifierCode(char c) {
        switch (c) {
            case '#':
                return 1;
            case '^':
                return 2;
            case '+':
                return 4;
            case '!':
                return 8;
        }
        return 0;
    }

    public static String getModifierDescr(char c) {
        switch (c) {
            case '#':
                return "Win";
            case '^':
                return "Ctrl";
            case '+':
                return "Shift";
            case '!':
                return "Alt";
        }
        return "" + c;
    }

    public static String getKeyModifiers(String keyPart) {
        if (keyPart.isEmpty()) {
            return "[NO HOTKEY]";
        }
        String tooltip = "";
        for (char c : keyPart.toCharArray()) {
            tooltip += getModifierDescr(c) + "+";
        }
        return tooltip.substring(0, tooltip.length() - 1);
    }

    public static char getLetter(String line) {
        List<String> list = StringMaster.openContainer(line, "::");
        String keyPart = list.get(0);
        if (keyPart.isEmpty()) {
            return '0';
        }
        char letter = keyPart.charAt(keyPart.length() - 1);
        return letter;
    }

    // to store list tags/group
    // private static void setComment(String lineIdentifier, String comment,
    // boolean append) {
    // comment = "; " + comment;
    // String contents = FileManager.readFile(
    //
    // new File(CORE_PATH), StringMaster.NEW_LINE);
    // for (String line : StringMaster.openContainer(contents,
    // StringMaster.NEW_LINE)) {
    // if (!line.contains(lineIdentifier))
    // continue;
    // int i = line.lastIndexOf(";");
    // String prevComment = "";
    // if (i != -1)
    // prevComment = line.substring(i);
    // if (append)
    // comment = prevComment + comment;
    // line = line.substring(0, i);
    // line += "; " + comment;
    // break;
    // }
    // FileManager.write(contents, CORE_PATH);
    //
    // }
    //
    // private static String processComments(String line) {
    // int i = line.lastIndexOf(";");
    // String comment = "";
    // if (i != -1)
    // comment = line.substring(i);
    // i = comment.indexOf(GROUP);
    // if (i != -1) {
    // if (list == null) {
    // list = new LinkedList<>();
    // groupMap.put(getKeyPart(line), list);
    // }
    // }
    // i = comment.indexOf(TAGS);
    // if (i != -1) {
    // tags = comment.substring(arg0, arg1)
    // list = tagMap.getOrCreate(key);
    // if (list == null) {
    // list = new LinkedList<>();
    // groupMap.put(getKeyPart(line), list);
    // }
    // for(String substring: StringMaster.openContainer( tags )){
    //
    // list.add(tag);
    // }
    // }
    // return line.replace(comment, "");
    //
    // }
    //
    // private void setGroup(String arg, String lineIdentifier) {
    // comment = getTags();
    // comment += getGroupString(arg);
    // setComment(lineIdentifier, comment, false);
    //
    // }
    //
    // private void addTag(String tag, String lineIdentifier) {
    // // setComment
    // List<String> tags = tagMap.getOrCreate(lineIdentifier);
    // if (tags == null) {
    // tags = new LinkedList<>();
    // tagMap.put(lineIdentifier, tags);
    // }
    // if (tags.contains(tag))
    // tags.remove(tag);
    // else
    // tags.add(tag);
    //
    // String comment = StringMaster.joinStringList(tags, ",");
    // setComment(lineIdentifier, comment, false);
    // }

    public static void listRenamed(String path, String name) {
        for (String sub : scripts) {
            renameListInScript(sub, path, name);
        }
    }

    public static List<JButton> getButtonsAll() {
//		return new ListMaster<JButton>().join(false, getButtonsAll());
        return new LinkedList<>();
    }

    public static List<JButton> getButtonsFromActiveSubPanel() {
        int n = 0;
        while (true) {
            if (n >= getPanel().getComponentCount() - 1) {
                break;
            }
            if (MusicMouseListener.getActivePanel() == getPanel().getView().getComponent(n)) {
                break;
            }
            n++;
        }
        return getButtonLists().get(n);
    }

    public static List<List<JButton>> getButtonLists() {
        List<List<JButton>> lists = new LinkedList<>();
        for (Component sub : AHK_Master.getPanel().getView().getComponents()) {
            List<JButton> list = new LinkedList<>();
            if (sub instanceof Container) {
                Container container = (Container) sub;
                for (Component btn : container.getComponents()) {
                    if (btn instanceof JButton) {
                        list.add((JButton) btn);
                    }
                }
            }
            if (!list.isEmpty()) {
                lists.add(list);
            }
        }
        return lists;
    }

    private static void renameListInScript(String scriptPath, String path, String name) {
        List<String> list = FileManager.readFileLines(scriptPath);
        String content = "";
        for (String sub : list) {
            if (sub.contains(path)) {
                String oldName = StringMaster.getLastPathSegment(path);
                sub = StringMaster.replaceLast(sub, oldName, name);
            }
            content += sub + StringMaster.NEW_LINE;
        }
        FileManager.write(content, scriptPath);

    }

    private static void splitScriptIntoMusicAndNonMusic(String scriptPath) {
        List<String> list = FileManager.readFileLines(scriptPath);
        String content_non_music = "";
        String content_music = "";
        for (String sub : list) {
            if (sub.contains(".m3u")) {
                content_music += sub + StringMaster.NEW_LINE;
            } else {
                content_non_music += sub + StringMaster.NEW_LINE;
            }
        }
        FileManager.write(content_non_music, StringMaster.cropFormat(scriptPath) + " No Music.ahk");
        FileManager.write(content_music, StringMaster.cropFormat(scriptPath) + " Music.ahk");
    }

    public static String getScriptLineForList(ObjType type) {
        return type.getProperty(G_PROPS.HOTKEY) + "::Run " + type.getProperty(AT_PROPS.PATH);
    }

    private static void removeNumericHotkeysFromScript(String scriptPath) {
        List<String> list = FileManager.readFileLines(scriptPath);
        String content = "";
        for (String sub : list) {
            if (sub.contains("Run ")) {
                if (sub.contains(".m3u")) {
                    String string = sub.split("::")[0].trim();
                    if (Character.isDigit(string.charAt(string.length() - 1)))
                    // if (StringMaster.getFirstNumberIndex(generic)>1)
                    {
                        continue;
                    }
                }
            }
            content += sub + StringMaster.NEW_LINE;
        }
        FileManager.write(content, StringMaster.cropFormat(scriptPath) + " No Number Keys.ahk");

    }

    public static MusicListPanel getPanel() {
        return panel;
    }

    public static void setPanel(MusicListPanel panel) {
        AHK_Master.panel = panel;
    }

    public static MusicListPanel getWrappingPanel() {
        if (wrappingPanel == null) {
            return panel;
        }
        return wrappingPanel;
    }

    public static void setWrappingPanel(MusicListPanel wrappingPanel) {
        AHK_Master.wrappingPanel = wrappingPanel;
    }

}

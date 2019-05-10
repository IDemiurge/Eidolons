package eidolons.test;

import eidolons.game.battlecraft.logic.battlefield.DC_ObjInitializer;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.launch.PresetLauncher;
import eidolons.swing.generic.services.dialog.DialogMaster;
import eidolons.test.Preset.PRESET_DATA;
import eidolons.test.Preset.PRESET_OPTION;
import eidolons.test.Preset.PRESET_TYPE;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Writer;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.swing.generic.components.editors.FileChooser;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.system.PathUtils;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.TimeMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.secondary.Bools;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static eidolons.test.Preset.PRESET_DATA.*;

public class PresetMaster {
    private static final String LAST = "last";
    private static final String AUTO = "auto";
    static List<Preset> presets;
    static PRESET_DATA[] edit_values = {OPTIONS, PLAYER_PARTY,
     ENEMY_PARTY, DUNGEONS, FIRST_DUNGEON,
     PRESET_TYPE};
    static String[] options = {"Autosave", "Save", "Save As", "New", "Edit", "Load",};
    private static Preset preset;

    public static void handlePreset(boolean alt) {
        if (alt) {
            updatePreset();
            savePreset(preset, false, true);
            return;
        }
        int option = DialogMaster.optionChoice("What to do?",// ,
         options);
        if (option == -1) {
            return;
        }
        switch (options[option]) {
            case "Autosave":
                updatePreset();
                savePreset(preset, true, true);
                savePreset(preset, null, true);
                break;
            case "Save":
                updatePreset();
                savePreset(preset, false);
                break;
            case "Save As":
                setName();
                updatePreset();
                savePreset(preset, false, true);
                break;
            case "Load":
                PresetLauncher.launchPresetDynamically();
                // loadPreset();
                break;
            case "New":
                newPreset(); // so we can autosave later
                break;
            case "Edit":
                editPreset();
                break;
        }
    }

    private static void editPreset() {
        PRESET_DATA item = new EnumMaster<PRESET_DATA>()
         .retrieveEnumConst(PRESET_DATA.class, ListChooser.chooseEnum(SELECTION_MODE.SINGLE,
          ListMaster.toStringList(edit_values)));
        if (item != null) {
            editPresetItem(item, getPreset());
        }
    }

    public static void newPreset() {
        String path = new FileChooser(getPresetFolderPath()).launch("", "");
        Preset preset;
        if (path != null) {
            File file = FileManager.getFile(path);
            String data = FileManager.readFile(file);
            String name = DialogMaster.inputText("", file.getName());
            if (name.equals(file.getName())) {
                name = file.getName() + " " + TimeMaster.getFormattedDate(false);
                // Calendar.getInstance().getTime().getMonth()
            }
            preset = new Preset(name, path, data);
        } else {
            // TODO
            preset = new Preset(generateName(), generatePath());
        }
        // initParams(preset); ???
        savePreset(preset);
    }

    private static void setName() {
        String path = generatePath() + generateName();
        path = DialogMaster.inputText("Enter a name...", path);
        String newName = PathUtils.getLastPathSegment(path);
        path = path.replace(newName, "");
        getPreset().setName(newName);
        getPreset().setPath(path);

    }

    private static String generatePath() {
        return generatePath(preset.getValue(PLAYER_PARTY), preset
         .getValue(ENEMY_PARTY), preset.getValue(FIRST_DUNGEON));
    }

    private static String generateName() {
        return generateName(preset.getValue(PLAYER_PARTY), preset
         .getValue(ENEMY_PARTY), preset.getValue(FIRST_DUNGEON));
    }

    private static boolean editPresetItem(PRESET_DATA item, Preset preset) {
        String value = inputValue(item);
        if (value == null) {
            return false;
        }
        preset.setValue(item, value);
        return true;
    }

    private static String inputValue(PRESET_DATA item) {
        String value = null;
        ListChooser lc;
        List<String> listData;
        List<String> listData2;
        switch (item) {

            case PRESET_TYPE:
                return ListChooser.chooseEnum(PRESET_TYPE.class);
            case DUNGEONS:
                break;

//            case ENEMY_PARTY:
//                return FAST_DC.chooseEnemies(null);
            case FIRST_DUNGEON:
                break;

            case OPTIONS:
                lc = new ListChooser(SELECTION_MODE.MULTIPLE, PRESET_OPTION.class);
                listData = ListMaster.toStringList(PRESET_OPTION.values());
                lc.setListData(listData);
                listData2 = ContainerUtils.openContainer(getPreset().getValue(OPTIONS));
                lc.setSecondListData(listData2);
                return lc.choose();
//            case PLAYER_PARTY:
//                return FAST_DC.choosePlayerUnits(null);

            case PRESET_OPTION_PARAMS:
                break;

        }
        return value;
    }

    public static Preset createNetPreset(String data) {

        Preset newPreset = new Preset(null, "net/", data);
        // newPreset.setNet(true);

        // swapped players
        String name = generateName(newPreset.getEnemies(), newPreset.getAllies(), newPreset
         .getLevelPath());
        newPreset.setName(name);
        return newPreset;
    }

    public static Preset createPreset(String PLAYER_PARTY, String ENEMY_PARTY,
                                      String levelFilePath, Boolean auto) {
        String name = generateName(PLAYER_PARTY, ENEMY_PARTY, levelFilePath);
        if (Bools.isFalse(auto)) {
            name = DialogMaster.inputText("Input preset name!", name);
            if (name == null) {
                name = generateName(PLAYER_PARTY, ENEMY_PARTY, levelFilePath);
            }
        }
        Preset newPreset = (new Preset(name, generatePath(PLAYER_PARTY, ENEMY_PARTY, levelFilePath)));
        newPreset.setValue(PRESET_DATA.PLAYER_PARTY, PLAYER_PARTY);
        newPreset.setValue(PRESET_DATA.ENEMY_PARTY, ENEMY_PARTY);
        newPreset.setValue(FIRST_DUNGEON, levelFilePath);
        newPreset.setValue(DUNGEONS, levelFilePath);
        // TODO unit(pos) update !
        return newPreset;
    }

    private static String generatePath(String party, String enemies, String levelFilePath) {
        // preCheck multi-dungeon; preCheck party-type, preCheck encounter
        return "test/";
    }

    private static String generateName(String party, String enemies, String levelFilePath) {
        String partyName = StringMaster.getFirstItem(party);
        // if (DC_Game.game.getMainHero() != null) {
        // partyName = DC_Game.game.getMainHero().getName();
        // }
        String enemyName = StringMaster.getFirstItem(enemies);
        return partyName + " vs " + enemyName + " on "
         + getRawDungeonName(levelFilePath);
    }

    private static String getRawDungeonName(String levelFilePath) {
        if (levelFilePath == null) {
            return "null dungeon";
        }
        String name = PathUtils.getLastPathSegment(levelFilePath);
        name = StringMaster.cropFormat(name);
        name = StringMaster.cropVersion(name);
        return name;
    }

    public static boolean choosePreset() {
        int i = DialogMaster.optionChoice("Choose a preset to launch", getPresets().toArray());
        if (i == -1) {
            return false;
        }
        setPreset(getPresets().get(i));
        return true;
    }

    public static void savePreset(Preset preset) {
        savePreset(preset, false);
    }

    public static void savePreset(Preset preset, Boolean auto) {
        savePreset(preset, auto, null);
    }

    public static void savePreset(Preset preset, Boolean auto, Boolean forceOption) {
        String name = preset.getName();
        String path = getPresetFolderPath() + preset.getPath();
        if (auto == null) {
            name = LAST;
            path = getPresetFolderPath();
        } else if (auto) {
            name = getAutosaveName();
            path = getAutoPresetPath();
        } else {
            if (name.equals(LAST)) {
                name = getAutosaveName();
                path = getAutoPresetPath();
            }
        }

        // TODO autosaves should be further sorted by type/...
        if (Bools.isFalse(auto)) {
            File file = FileManager.getFile(path + name + getFormat());
            if (file.isFile()) {
                Boolean result;
                if (forceOption != null) {
                    result = forceOption;
                } else {
                    result = DialogMaster.askAndWait("File exists, what to do?", "Overwrite",
                     "Version", "New");
                }
                if (result == null) {
                    // TODO path???
                    name = DialogMaster.inputText("Enter a name...", name);
                    preset.setName(name);
                } else if (!result) {
                    name = StringMaster.cropFormat(FileManager.getUniqueVersion(file));
                    preset.setName(name);
                }
            }
        }
        XML_Writer.write(preset.getXml(), path, name + getFormat());
    }

    private static String getAutosaveName() {
        return TimeMaster.getFormattedTimeAlt(false) + " - " + generateName();
    }

    private static String getAutoPresetPath() {
        return getPresetFolderPath() + AUTO + "/" + TimeMaster.getMonthString() + "/"
         + TimeMaster.getDayString() + "/";
    }

    private static String getFormat() {
        return StringMaster.DATA_FORMAT;
    }

    public static void updatePreset() {
        String PLAYER_PARTY = "";
        String PLAYER_UNITS = "";
        String ENEMY_PARTY = "";
        String ENEMIES = "";
        String levelFilePath = DC_Game.game.getDungeonMaster().getDungeonWrapper().getLevelFilePath();
        String dungeons = "";
        boolean partyType = false;
        ObjType encounterType = DataManager.getType(preset.getValue(PRESET_DATA.ENEMY_PARTY),
         DC_TYPE.ENCOUNTERS);
        // String enemyUnits = preset.getValue(PRESET_DATA.ENEMY_PARTY);
        if (encounterType != null) {
            ENEMY_PARTY = encounterType.getName();
            // enemyUnits = encounterType.getProperty(PROPS.UNIT_TYPES);// TODO
        }

//      TODO   if (DC_Game.game.getParty() != null) {
//            partyType = true;
//            PLAYER_PARTY = DC_Game.game.getParty().getName();
//        }

        for (Obj obj : DC_Game.game.getPlayer(true).collectControlledUnits()) {
            if (!partyType) {
                PLAYER_PARTY += obj.getName() + ";";
            }
            // if
            // (!preset.getValue(PRESET_DATA.PLAYER_PARTY).contains(obj.getName()))
            // ALL UNITS SAVED - BUT THEN CHOOSE IF CUSTOM INIT OR PARTY
            PLAYER_UNITS += DC_ObjInitializer.getObjStringAlt(obj) + ";";
            // custom hacks - spells, skills, items..
        }
        for (Obj obj : DC_Game.game.getPlayer(false).collectControlledUnits()) {
            if (encounterType == null) {
                ENEMY_PARTY += obj.getName() + ";";
            }

            // if (!enemyUnits.contains(obj.getName()))
            ENEMIES += DC_ObjInitializer.getObjStringAlt(obj) + ";";
            // custom hacks - spells, skills, items..
        }
//     TODO    for (Dungeon d : DungeonMaster.getDungeons()) {
//            dungeons += d.getLevelFilePath() + ";";
//            if (levelFilePath.isEmpty()) {
//                levelFilePath = d.getLevelFilePath();
//            }
//        }

        getPreset().setValue(PRESET_DATA.PLAYER_PARTY, PLAYER_PARTY);
        getPreset().setValue(PRESET_DATA.ENEMY_PARTY, ENEMY_PARTY);
        getPreset().setValue(PRESET_DATA.ENEMIES, ENEMIES);
        getPreset().setValue(PRESET_DATA.PLAYER_UNITS, PLAYER_UNITS);
        getPreset().setValue(FIRST_DUNGEON, levelFilePath);
        getPreset().setValue(DUNGEONS, dungeons);

        savePreset(preset, true, true);
        savePreset(preset, null, true);
    }

    public static List<Preset> getRecentPresets() {
        int maximum = 10;
        String autoFolderPath = getPresetFolderPath() + AUTO + "/";
        File root = FileManager.getFile(autoFolderPath);
        List<File> list =
         // new ArrayList<File>(Arrays.asList(root.listFiles()));
         getPresetFiles(root, true);

        Collections.sort(list, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.lastModified() > o2.lastModified()) {
                    return -1;
                }
                if (o1.lastModified() < o2.lastModified()) {
                    return 1;
                }
                return 0;
            }
        });
        List<File> recent = list.subList(0, maximum);
        // new ListMaster<>().

        presets = new ArrayList<>();
        // getPresets()
        int n = 0;
        for (File file : list) {
            Preset preset = loadPreset(file);
            if (preset == null) {
                continue;
            }

            if (StringMaster.isEmpty(preset.getAllies())) {
                continue;
            }
            if (StringMaster.isEmpty(preset.getFirstDungeonName())) {
                continue;
            }
            presets.add(preset);
            if (n > maximum) {
                break;
            }
            n++;
        }
        return presets;

    }

    public static List<Preset> getPresets() {
        String presetFolderPath = getPresetFolderPath();
        return getPresetsFromFolder(false, presetFolderPath);
    }

    public static List<Preset> getPresetsFromFolder(Boolean autoIncluded, String presetFolderPath) {
        if (presets == null) {
            presets = new ArrayList<>();
            File root = FileManager.getFile(presetFolderPath);
            // sort();
            for (File file : root.listFiles()) {
                // directory = FileManager.getFile(path);
                presets.addAll(loadPresets(file, autoIncluded));
            }

        }
        return presets;
    }

    private static List<Preset> loadPresets(File file) {
        return loadPresets(file, false);
    }

    private static List<File> getPresetFiles(File file, Boolean auto) {
        List<File> list = new ArrayList<>();
        if (file == null) {
            return list;
        }
        if (file.isDirectory()) {
            for (File sub : file.listFiles()) {
                list.addAll(getPresetFiles(sub, auto));
            }
        } else if (file.isFile()) {
            if (Bools.isTrue(auto) || !file.getPath().contains(AUTO + "/")) {
                list.add((file));
            }
        }

        return list;
    }

    private static List<Preset> loadPresets(File file, Boolean auto) {
        List<Preset> list = new ArrayList<>();
        if (file == null) {
            return list;
        }
        if (file.isDirectory()) {
            for (File sub : file.listFiles()) {
                list.addAll(loadPresets(sub, auto));
            }
        } else if (file.isFile()) {
            if (Bools.isTrue(auto) || !file.getPath().contains(AUTO + "/")) {
                list.add(loadPreset(file));
            }
        }

        return list;
    }

    public static Preset findPreset(String result) {
        for (Preset p : getPresets()) {
            if (StringMaster.compare(p.toString(), (result))) {
                return p;
            }
        }
        return null;
    }

    public static Preset loadLastPreset() {
        return loadPreset(FileManager.getFile(getPresetFolderPath() + LAST + getFormat()));
    }

    public static Preset loadPreset(String relativePath) {
        return loadPreset(FileManager.getFile(getPresetFolderPath() + relativePath));
    }

    public static Preset loadPreset(File file) {
        String data = FileManager.readFile(file);
        String path = file.getPath().replace(getPresetFolderPath(), "").replace(file.getName(), "");
        String name = StringMaster.cropFormat(file.getName());
        Preset preset = new Preset(name, path, data);
        // for (Node node:
        // XML_Converter.getNodeList(XML_Converter.getDoc(data)){
        // if (node.getNodeName().equals("data")){
        // preset.setData(data, getFormat());
        // }
        // }

        return preset;
    }

    private static String getPresetFolderPath() {
        return PathFinder.getXML_PATH() + "presets/";
    }

    public static Preset getPreset() {
        return preset;
    }

    public static void setPreset(Preset preset) {
        PresetMaster.preset = preset;
    }

}
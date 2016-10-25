package main.test;

import main.ability.UnitMaster;
import main.client.cc.logic.items.ItemGenerator;
import main.content.OBJ_TYPES;
import main.content.PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.DC_Game;
import main.game.battlefield.UnitGroupMaster;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.test.Preset.PRESET_DATA;
import main.test.Preset.PRESET_OPTION;
import main.test.debug.DebugMaster;
import main.test.debug.DebugMaster.DEBUG_FUNCTIONS;
import main.test.frontend.FAST_DC;

import java.util.LinkedList;
import java.util.List;

import static main.test.Preset.PRESET_DATA.*;
import static main.test.Preset.PRESET_OPTION.*;

public class PresetLauncher {
    public   static int PRESET_OPTION=-1;
    public final static String[] LAUNCH_OPTIONS = {"Last","Graphics Test", "Recent", "New", "Load", "Fast",
            "Superfast"

    };

    public static Boolean chooseLaunchOption() {
        int i =PRESET_OPTION;
        if (i==-1)
        i=DialogMaster.optionChoice("", LAUNCH_OPTIONS);
        switch (LAUNCH_OPTIONS[i]) {
            case "Last":
                Preset lastPreset = PresetMaster.loadLastPreset();
                UnitGroupMaster.setFactionMode(false);
                UnitMaster.setRandom(false);
                PresetMaster.setPreset(lastPreset);
                break;
            case "Graphics Test":
                FAST_DC.getGameLauncher().DUMMY_MODE = true;
                FAST_DC.getGameLauncher().DUMMY_PP = true;
                Preset p = PresetMaster.loadPreset("Graphics Test.xml");
                PresetMaster.setPreset(p);
                return true;
            case "Recent":
                chooseRecentPreset();
                break;
            case "New":
                FAST_DC.getGameLauncher().DUMMY_MODE = false;
                FAST_DC.getGameLauncher().DUMMY_PP = false;
                UnitGroupMaster.setFactionMode(DialogMaster.confirm("Faction Mode?"));
                return null;
            case "Fast":
                return true;
            case "Superfast":
                FAST_DC.getGameLauncher().DUMMY_MODE = true;
                FAST_DC.getGameLauncher().DUMMY_PP = true;
                return false;
            case "Load":
                // if (choosePreset()==null)
                choosePreset();
                break;

        }
        return null;
    }

    public static Preset chooseRecentPreset() {
        List<Preset> recent = PresetMaster.getRecentPresets();
        return choosePreset(recent);
    }

    public static Preset choosePreset() {
        List<Preset> presets = PresetMaster.getPresets();
        return choosePreset(presets);
    }

    public static Preset choosePreset(List<Preset> presets) {
        List<String> list = new LinkedList<>();
        for (Preset s : presets) {
            list.add(s.toString());
        }
        String result = new ListChooser(SELECTION_MODE.SINGLE, list, false).choose();
        if (result == null)
            return null;
        Preset preset = PresetMaster.findPreset(result);
        if (preset != null)
            PresetMaster.setPreset(preset);
        return preset;
    }

    public static void launchPresetDynamically() {
        Preset profile = choosePreset();
        boolean newDungeon = false;
        String oldDungeon = PresetMaster.getPreset().getValue(FIRST_DUNGEON);
        if (!profile.getValue(FIRST_DUNGEON).equals(oldDungeon))
            newDungeon = true; // check after launch?
        launchPreset(profile);
        if (newDungeon)
            DC_Game.game.getDungeonMaster().initDungeonLevelChoice();
        DC_Game.game.getDebugMaster().executeDebugFunctionNewThread(DEBUG_FUNCTIONS.CLEAR);
        // check new dungeon

    }

    public static void launchPreset() {
        launchPreset(PresetMaster.getPreset());
    }

    public static void launchPreset(Preset profile) {
        PresetMaster.setPreset(profile);
        for (PRESET_DATA item : PRESET_DATA.values() ) {

            String value = profile.getValue(item);
            if (value!=null )
            switch (item) {
                case CONTENT_SCOPE:
                    initContentScope(value);
                    break;
                case OPTIONS:
                    initOptions(value);
                    break;
                case PRESET_OPTION_PARAMS:
                    initOptionParams(value);
                    break;
                case ENEMY_PARTY:
                    initDefaultParty(value, false);
                    break;
                case ENEMIES:
                    initEnemies(value, false);
                    break;
                case PLAYER_UNITS:
                    initPlayerUnits(value, false);
                    break;
                case PLAYER_PARTY:
                    initDefaultParty(value, true);
                    break;
                case DUNGEONS:
                    initDungeonsList(value);
                    break;
                case FIRST_DUNGEON:
                    initFirstDungeon(value);
                    break;
            }
        }
        /*
		 * set dungeon, layer, encounter, party, options, ... 
		 */
    }

    private static void initOptions(String value) {
        for (String optionString : StringMaster.openContainer(value)) {
            PRESET_OPTION option = new EnumMaster<PRESET_OPTION>().retrieveEnumConst(
                    PRESET_OPTION.class, optionString);
            switch (option) {
                case DEBUG:
                    DC_Game.game.setDebugMode(true);
                    break;
                case ITEM_GENERATION_OFF:
                    ItemGenerator.setGenerationOn(false);
                    break;
                case OMNIVISION:
                    DebugMaster.setOmnivisionOn(true);
                    break;
                default:
                    break;

            }
        }
    }

    private static void initOptionParams(String value) {
        // DebugMaster.setOmnivisionOn(VISION_HACK);
        // game.setDebugMode(FAST_MODE || SUPER_FAST_MODE);
        // CoreEngine.setTEST_MODE(true);

    }

    private static void initDefaultParty(String value, boolean me) {
        ObjType type = null;
        if (!value.contains(StringMaster.getSeparator())) {
            type = DataManager.getType(value, OBJ_TYPES.PARTY);
        }
        if (type == null) {
            if (me)
                DC_Game.game.setPlayerParty(value);
            else
                DC_Game.game.setEnemyParty(value);
        } else if (me)
            DC_Game.game.setPlayerParty(type.getProperty(PROPS.MEMBERS));
        else
            DC_Game.game.setEnemyParty(type.getProperty(PROPS.MEMBERS));
    }

    private static void initPlayerUnits(String value, boolean b) {
        DC_Game.game.getData().setPlayerUnitData(value);
        DC_Game.game.setPlayerParty(value);

    }

    private static void initEnemies(String value, boolean b) {
        DC_Game.game.getData().setPlayer2UnitData(value);
        DC_Game.game.setEnemyParty(value);
    }

    private static void initFirstDungeon(String name) {
        DC_Game.game.getDungeonMaster().setChooseLevel(false);
        DC_Game.game.getDungeonMaster().setDungeonPath(name);
    }

    private static void initDungeonsList(String value) {
        for (String name : StringMaster.openContainer(value)) {
            // else TODO
            // DungeonMaster.getPendingDungeons().add(name);

        }

    }

    private static void initContentScope(String value) {
        // TODO Auto-generated method stub

    }
}

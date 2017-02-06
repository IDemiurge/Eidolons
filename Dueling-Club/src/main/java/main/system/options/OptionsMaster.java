package main.system.options;

import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.swing.components.menus.OptionsPanel;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.FileManager;
import main.system.auxiliary.ListMaster;
import main.system.auxiliary.StringMaster;
import main.system.options.AnimationOptions.ANIMATION_OPTION;
import main.system.options.SoundOptions.SOUND_OPTION;

import java.util.Map;

public class OptionsMaster {
    static Map<OPTIONS_GROUP, Options> optionsMap;

    public static String promptSetOption() {
        String name = ListChooser.chooseEnum(OPTIONS_GROUP.class);
        if (name == null) {
            return null;
        }
        OPTIONS_GROUP group = new EnumMaster<OPTIONS_GROUP>().retrieveEnumConst(
                OPTIONS_GROUP.class, name);
        Options options = getOptions(group);

        if (options == null) {
            return null;
        }

        String optionName = ListChooser.chooseString(ListMaster.toStringList(options.getValues()
                .keySet().toArray()));
        String value = DialogMaster.inputText("", options.getValue(optionName));
        if (value == null) {
            options.removeValue(optionName);
        } else {
            options.setValue(optionName, value);
        }
        return value;
    }

    // Useful Options Gui
    //
    // Sound
    // Master Volume
    // Voice Volume
    // Combat Volume
    // Misc Volume
    //
    // Graphics
    // PhaseAnimation speed
    // PhaseAnimation details
    //
    // Stacked Units as Thumbnails
    // Values displayed as: Orbs, Bars
    //
    //
    // Camera centering
    //
    // Usability
    // Right Click
    //
    // Std Hotkeys
    //
    //
    // Gameplay
    // Log details
    // Quick-Movement
    // Auto-Attack
    // Tooltips
    //
    //
    //
    // AI
    // Speed
    //

    public static void init() {
        optionsMap = new XLinkedMap<>();
        for (OPTIONS_GROUP group : OPTIONS_GROUP.values()) {
            optionsMap.put(group, generateOptions(group));
        }
        String data = FileManager.readFile(PathFinder.getEnginePath() + "options.txt");
        for (String str : StringMaster.openContainer(data)) {

        }
    }

    private static Class<?> getOptionGroupEnumClass(OPTIONS_GROUP group) {
        switch (group) {
            case ANIMATION:
                return ANIMATION_OPTION.class;
            case GRAPHICS:
                // return GRAPHICS_OPTION.class;
                // case GAMEPLAY:
                // return GAMEPLAY_OPTION.class;
            case SOUND:
                return SOUND_OPTION.class;
            case TUTORIAL:
                // return ANIMATION_OPTION.class;

        }
        return null;
    }

    public static Options getAnimOptions() {
        OPTIONS_GROUP animation = OPTIONS_GROUP.ANIMATION;
        return getOptions(animation);
    }

    public static Options getOptions(OPTIONS_GROUP animation) {
        return optionsMap.get(animation);
    }

    private static Options generateOptions(OPTIONS_GROUP group) {
        Options options = null;
        switch (group) {
            case ANIMATION:
                options = new AnimationOptions();
                break;
        }
        if (options == null) {
            return null;
        }
        Class<?> clazz = getOptionGroupEnumClass(group);
        for (Object c : clazz.getEnumConstants()) {
            OptionsPanel.OPTION option = (OptionsPanel.OPTION) c;
            if (option.getDefaultValue() == null) {
                continue;
            }
            String value = option.getDefaultValue().toString();
            options.setValue(c.toString(), value);
        }
        return options;
    }

    public static int getAnimPhasePeriod() {
        return optionsMap.get(OPTIONS_GROUP.ANIMATION).getIntValue(ANIMATION_OPTION.PHASE_TIME);
    }

    public enum OPTIONS_GROUP {
        ANIMATION, SOUND, GRAPHICS, TUTORIAL, GAMEPLAY,
    }

}

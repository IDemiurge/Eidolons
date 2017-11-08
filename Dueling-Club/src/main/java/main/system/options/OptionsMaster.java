package main.system.options;

import com.badlogic.gdx.Gdx;
import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.game.battlecraft.logic.battlefield.vision.OutlineMaster;
import main.game.battlecraft.rules.RuleMaster;
import main.game.battlecraft.rules.RuleMaster.RULE_SCOPE;
import main.game.core.Eidolons;
import main.libgdx.GdxMaster;
import main.libgdx.anims.AnimMaster;
import main.libgdx.anims.particles.ParticleManager;
import main.libgdx.bf.SuperActor;
import main.libgdx.bf.light.ShadeLightCell;
import main.libgdx.bf.light.ShadowMap;
import main.libgdx.screens.DungeonScreen;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.audio.MusicMaster;
import main.system.audio.MusicMaster.MUSIC_VARIANT;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.graphics.FontMaster;
import main.system.graphics.GuiManager;
import main.system.options.AnimationOptions.ANIMATION_OPTION;
import main.system.options.GameplayOptions.GAMEPLAY_OPTION;
import main.system.options.GraphicsOptions.GRAPHIC_OPTION;
import main.system.options.Options.OPTION;
import main.system.options.SoundOptions.SOUND_OPTION;
import main.system.sound.SoundMaster;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

public class OptionsMaster {
    private static Map<OPTIONS_GROUP, Options> optionsMap = new HashMap<>();
    private static Map<OPTIONS_GROUP, Options> cachedMap;
    private static OptionsPanel optionsPanel;
    private static boolean initialized;
    private static JFrame optionsPanelFrame;

    private static void applyAnimOptions(AnimationOptions animOptions) {

        for (Object sub : animOptions.getValues().keySet()) {
            new EnumMaster<ANIMATION_OPTION>().
             retrieveEnumConst(ANIMATION_OPTION.class,
              animOptions.getValues().get(sub).toString());
            ANIMATION_OPTION key = animOptions.getKey((sub.toString()));
            String value = animOptions.getValue(key);
            if (StringMaster.isInteger(value)) {
                Integer intValue = StringMaster.getInteger(value);
                switch (key) {
                    case SPEED:
                        AnimMaster.getInstance().setAnimationSpeedFactor(
                         new Float(intValue) / 100);
                }
            } else {
                switch (key) {
                    case WAIT_FOR_ANIM:
                        break;
                    case MAX_ANIM_WAIT_TIME:
                        break;
                    case PARALLEL_DRAWING:
                        AnimMaster.getInstance().setParallelDrawing(Boolean.valueOf(value));
                        break;

                    case TEXT_DURATION:
                        break;
                    case PRECAST_ANIMATIONS:
                        break;
                    case CAST_ANIMATIONS:
                        break;
                    case AFTER_EFFECTS_ANIMATIONS:
                        break;
                }

            }


        }
    }

    private static void applyGameplayOptions(GameplayOptions gameplayOptions) {
        for (Object sub : gameplayOptions.getValues().keySet()) {
            new EnumMaster<GAMEPLAY_OPTION>().
             retrieveEnumConst(GAMEPLAY_OPTION.class,
              gameplayOptions.getValues().get(sub).toString());
            GAMEPLAY_OPTION key = gameplayOptions.getKey((sub.toString()));
            String value = gameplayOptions.getValue(key);
            if (!StringMaster.isInteger(value)) {
                switch (key) {
                    case RULES_SCOPE:
                        RuleMaster.setScope(
                         new EnumMaster<RULE_SCOPE>().
                          retrieveEnumConst(RULE_SCOPE.class,
                           gameplayOptions.getValues().get(sub).toString()
                          ));
                        break;
                    case GAME_DIFFICULTY:
                        if (Eidolons.game != null)
                            if (Eidolons.game.getBattleMaster() != null)
                                Eidolons.game.getBattleMaster().getOptionManager().difficultySet(value);
                        break;
                    case DEFAULT_ACTIONS:
                        break;
                    case MANUAL_CONTROL:
                        break;
                    case DEBUG_MODE:
                        break;
                    case INFO_DETAIL_LEVEL:
                        break;
                }
            }
        }
    }

    private static void applySoundOptions(SoundOptions soundOptions) {
        for (Object sub : soundOptions.getValues().keySet()) {
            new EnumMaster<SOUND_OPTION>().
             retrieveEnumConst(SOUND_OPTION.class,
              soundOptions.getValues().get(sub).toString());
            SOUND_OPTION key = soundOptions.getKey((sub.toString()));
            String value = soundOptions.getValue(key);
            if (!StringMaster.isInteger(value)) {
                switch (key) {
                    case SOUNDS_OFF:
                        SoundMaster.setOn(false);
//                        MusicMaster.resetSwitcher();
                        break;
                    case MUSIC_OFF:
                        MusicMaster.resetSwitcher();
                        break;
                    case MUSIC_VARIANT:
                        MusicMaster.getInstance().setVariant(
                         new EnumMaster<MUSIC_VARIANT>().retrieveEnumConst(MUSIC_VARIANT.class,
                          soundOptions.getValue(key)));
                        break;
                }
            } else {
                Integer integer = Integer.valueOf(value.toLowerCase());
                Float v = new Float(integer) / 100;
                switch (key) {
                    case MASTER_VOLUME:
                        SoundMaster.setMasterVolume(integer);
                        MusicMaster.resetVolume();
                        break;
                    case MUSIC_VOLUME:
                        MusicMaster.resetVolume();
                        //auto
                        break;
                }
            }
        }
    }

    //OR LET THOSE CLASSES GET() OPTIONS?
    public static void applyGraphicsOptions(GraphicsOptions graphicsOptions) {
        for (Object sub : graphicsOptions.getValues().keySet()) {
            new EnumMaster<GRAPHIC_OPTION>().
             retrieveEnumConst(GRAPHIC_OPTION.class,
              graphicsOptions.getValues().get(sub).toString());
            GRAPHIC_OPTION key = graphicsOptions.getKey((sub.toString()));
            if (key == null)
                continue;
            String value = graphicsOptions.getValue(key);
            boolean bool = Boolean.valueOf(value.toLowerCase());
            switch (key) {
                case AUTO_CAMERA:
                    DungeonScreen.setCameraAutoCenteringOn(bool);
                    break;
                case AMBIENCE:
                    ParticleManager.setAmbienceOn(bool);
                    break;
                case ANIMATED_UI:
                    SuperActor.setAlphaFluctuationOn(bool);
                    break;
                case ANIMATED_SHADOWMAP:
                    ShadeLightCell.setAlphaFluctuation(bool);
                    break;
                case SHADOWMAP:
                    ShadowMap.setOn(bool);
                    break;
                case AMBIENCE_MOVE_SUPPORTED:
                    ParticleManager.setAmbienceMoveOn(
                     bool);
                    break;
                case OPTIMIZATION_ON:
                    SuperActor.setCullingOff(!bool);
                    break;
                case OUTLINES:
                    OutlineMaster.setOutlinesOn(bool);
                    break;
            }
        }
    }

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

    public static void applyOptions() {
        applyGraphicsOptions(getGraphicsOptions());
        applySoundOptions(getSoundOptions());
        applyGameplayOptions(getGameplayOptions());

        if (!GdxMaster.isGuiReady())
            return;
        if (AnimMaster.getInstance() == null)
            return;
        if (GdxMaster.isLwjglThread()) {
            applyAnimOptions(getAnimOptions());
        } else
            Gdx.app.postRunnable(() ->
             applyAnimOptions(getAnimOptions()));
    }


    public static void saveOptions() {
        StringBuilder content = new StringBuilder();
        content.append(XML_Converter.openXml("Options" + StringMaster.NEW_LINE));
        for (OPTIONS_GROUP sub : optionsMap.keySet()) {
            content.append(XML_Converter.openXml(sub.toString()) + StringMaster.NEW_LINE);
            //OR PUT UNID-DATA-STRING there
            for (Object option : optionsMap.get(sub).getValues().keySet()) {
                content.append(XML_Converter.wrap(option.toString(),
                 optionsMap.get(sub).getValues().get(option).toString()) + StringMaster.NEW_LINE);
            }
            content.append(XML_Converter.closeXml(sub.toString()) + StringMaster.NEW_LINE);
        }
        content.append(XML_Converter.closeXml("Options"));
        FileManager.write(content.toString(), getOptionsPath());
    }

    private static String getOptionsPath() {
        return PathFinder.getXML_PATH() + "options.xml";
    }

    public static void main(String[] args) {
        FontMaster.init();
        GuiManager.init();
        init();
        openMenu();
    }

    public static void openMenu() {
        if (optionsPanelFrame != null) {
//            optionsPanelFrame.setVisible(false);
            optionsPanelFrame.dispatchEvent(new WindowEvent(optionsPanelFrame, WindowEvent.WINDOW_CLOSING));

        }
        optionsPanel = new OptionsPanel(optionsMap);
        optionsPanelFrame = GuiManager.inNewWindow(optionsPanel,
         "Options", new Dimension(800, 600));


    }

    public static Map<OPTIONS_GROUP, Options> readOptions(String data) {
        Document doc = XML_Converter.getDoc(data);
        Map<OPTIONS_GROUP, Options> optionsMap = new XLinkedMap<>();
        for (Node sub : XML_Converter.getNodeListFromFirstChild(doc, true)) {
            OPTIONS_GROUP group = OPTIONS_GROUP.valueOf(sub.getNodeName());
            Options options = createOptions(group, sub);
            if (options != null)
                optionsMap.put(group, options);
        }

        return optionsMap;
    }

    private static Options createOptions(OPTIONS_GROUP group, Node doc) {
        Options options = createOptions(group);
        for (Node optionNode : XML_Converter.getNodeList(doc)) {
            options.setValue(optionNode.getNodeName(), optionNode.getTextContent());
        }
        return options;
    }

    public static void init() {
        if (initialized)
            return;
        String data = FileManager.readFile(getOptionsPath());
        if (data.isEmpty()) {
            optionsMap = initDefaults();
        } else {
            optionsMap = readOptions(data);
        }
        try {
            applyOptions();
            initialized = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static Map<OPTIONS_GROUP, Options> initDefaults() {
        XLinkedMap optionsMap = new XLinkedMap<>();
        for (OPTIONS_GROUP group : OPTIONS_GROUP.values()) {

            Options options = generateDefaultOptions(group);
            if (options != null)
                optionsMap.put(group, options);
        }
        return optionsMap;
    }

    private static Class<?> getOptionGroupEnumClass(OPTIONS_GROUP group) {
        switch (group) {
            case ANIMATION:
                return ANIMATION_OPTION.class;
            case GRAPHICS:
                return GRAPHIC_OPTION.class;
            case SOUND:
                return SOUND_OPTION.class;
            case TUTORIAL:
//                return TUTORIAL_OPTION.class;

            case GAMEPLAY:
                return GAMEPLAY_OPTION.class;
            case ENGINE:
//                return Engine_Options.class;
        }
        return null;
    }

    public static AnimationOptions getAnimOptions() {
        return (AnimationOptions) getOptions(OPTIONS_GROUP.ANIMATION);
    }

    public static GraphicsOptions getGraphicsOptions() {
        return (GraphicsOptions) optionsMap.get(OPTIONS_GROUP.GRAPHICS);
    }

    public static GameplayOptions getGameplayOptions() {
        return (GameplayOptions) optionsMap.get(OPTIONS_GROUP.GAMEPLAY);
    }

    public static Options getOptions(OPTION group) {
        if (group instanceof SOUND_OPTION) {
            return optionsMap.get(OPTIONS_GROUP.SOUND);
        }
        if (group instanceof GRAPHIC_OPTION) {
            return optionsMap.get(OPTIONS_GROUP.GRAPHICS);
        }
        if (group instanceof ANIMATION_OPTION) {
            return optionsMap.get(OPTIONS_GROUP.ANIMATION);
        }
        if (group instanceof GAMEPLAY_OPTION) {
            return optionsMap.get(OPTIONS_GROUP.GAMEPLAY);
        }
        return null;
    }

    public static Options getOptions(OPTIONS_GROUP group) {
        return optionsMap.get(group);
    }

    private static Options generateDefaultOptions(OPTIONS_GROUP group) {
        Options options = createOptions(group);
        if (options == null) {
            return null;
        }
        Class<?> clazz = getOptionGroupEnumClass(group);
        for (Object c : clazz.getEnumConstants()) {
            Options.OPTION option = (Options.OPTION) c;
            if (option.getDefaultValue() == null) {
                continue;
            }
            String value = option.getDefaultValue().toString();
            options.setValue(c.toString(), value);
        }
        return options;
    }

    private static Options createOptions(OPTIONS_GROUP group) {
        switch (group) {
            case ANIMATION:
                return new AnimationOptions();
            case SOUND:
                return new SoundOptions();
            case GRAPHICS:
                return new GraphicsOptions();
            case TUTORIAL:
                break;
            case GAMEPLAY:
                return new GameplayOptions();
        }
        return null;
    }

    public static int getAnimPhasePeriod() {
        return 1000;
//        return optionsMap.get(OPTIONS_GROUP.ANIMATION).getIntValue(ANIMATION_OPTION.PHASE_TIME);
    }

    public static void cacheOptions() {
        cachedMap = new MapMaster<OPTIONS_GROUP, Options>().cloneHashMap(optionsMap);
    }

    public static void resetToCached() {
        optionsMap = new MapMaster<OPTIONS_GROUP, Options>().cloneHashMap(cachedMap);
        applyOptions();
    }

    public static void resetToDefaults() {
        optionsMap = initDefaults();
        applyOptions();
    }

    public static EngineOptions getEngineOptions() {
//        return (EngineOptions) getOptions(OPTIONS_GROUP.ENGINE);
        return null;
    }

    public static SoundOptions getSoundOptions() {
        return (SoundOptions) getOptions(OPTIONS_GROUP.SOUND);
    }


    public enum OPTIONS_GROUP {
        ANIMATION, SOUND, GRAPHICS, TUTORIAL, GAMEPLAY, ENGINE,
    }

}

package main.system.options;

import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.libgdx.anims.particles.ParticleManager;
import main.libgdx.bf.UnitView;
import main.libgdx.bf.light.ShadowMap;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.graphics.FontMaster;
import main.system.graphics.GuiManager;
import main.system.options.AnimationOptions.ANIMATION_OPTION;
import main.system.options.GraphicsOptions.GRAPHIC_OPTION;
import main.system.options.SoundOptions.SOUND_OPTION;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class OptionsMaster {
    private static Map<OPTIONS_GROUP, Options> optionsMap = new HashMap<>();
    private static Map<OPTIONS_GROUP, Options> cachedMap;

    //OR LET THOSE CLASSES GET() OPTIONS?
    public static void applyGraphicsOptions(GraphicsOptions graphicsOptions) {
        for (Object sub : graphicsOptions.getValues().keySet()) {
            new EnumMaster<GRAPHIC_OPTION>().
             retrieveEnumConst(GRAPHIC_OPTION.class,
              graphicsOptions.getValues().get(sub).toString());
            GRAPHIC_OPTION key = graphicsOptions.getKey((sub.toString()));
            String value = graphicsOptions.getValue(key);
            boolean bool = Boolean.valueOf(value.toLowerCase());
            switch (key) {
                case AMBIENCE:
                    ParticleManager.setAmbienceOn(bool);
                    break;
                case PARTICLE_EFFECTS:
//                ParticleManager.setAmbienceOn(bool);
                    break;
                case ANIMATED_UI:
                    UnitView.setAlphaFluctuationOn(bool);
                    break;
                case SHADOWMAP:
                    ShadowMap.setOn(bool);
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
    }
        public static void saveOptions() {
        StringBuilder content = new StringBuilder();
        content.append(XML_Converter.openXml("Options"+ StringMaster.NEW_LINE));
        for (OPTIONS_GROUP sub : optionsMap.keySet()) {
            content.append(XML_Converter.openXml(sub.toString())+ StringMaster.NEW_LINE);
            //OR PUT UNID-DATA-STRING there
            for (Object option : optionsMap.get(sub).getValues().keySet()) {
                content.append(XML_Converter.wrap(option.toString(),
                 optionsMap.get(sub).getValues().get(option).toString())+ StringMaster.NEW_LINE);
            }
            content.append(XML_Converter.closeXml(sub.toString())+ StringMaster.NEW_LINE);
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
        new OptionsPanel(optionsMap);
        GuiManager.inNewWindow(new OptionsPanel(optionsMap), "Options", new Dimension(800, 1000));
    }

    public static void readOptions(String data) {
        Document doc = XML_Converter.getDoc(data);
        optionsMap = new XLinkedMap<>();
        for (Node sub : XML_Converter.getNodeListFromFirstChild(doc, true)) {
            OPTIONS_GROUP group=OPTIONS_GROUP.valueOf(sub.getNodeName());
            Options options = createOptions(group, sub);
            if (options != null)
                optionsMap.put(group, options);
        }

    }

    private static Options createOptions(OPTIONS_GROUP group, Node doc) {
        Options options = createOptions(group);
        for (Node optionNode : XML_Converter.getNodeList(doc)) {
            options.setValue(optionNode.getNodeName(), optionNode.getTextContent());
        }
        return options;
    }

    public static void init() {
        String data = FileManager.readFile(getOptionsPath());
        if (data.isEmpty()) {
            initDefaults();
        } else {
            readOptions(data);
        }

        applyGraphicsOptions(getGraphicsOptions());
    }

    private static void initDefaults() {
        optionsMap = new XLinkedMap<>();
        for (OPTIONS_GROUP group : OPTIONS_GROUP.values()) {

            Options options = generateDefaultOptions(group);
            if (options != null)
                optionsMap.put(group, options);
        }
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

        }
        return null;
    }

    public static AnimationOptions getAnimOptions() {
        return (AnimationOptions) getOptions(OPTIONS_GROUP.ANIMATION);
    }

    public static GraphicsOptions getGraphicsOptions() {
        return (GraphicsOptions) optionsMap.get(OPTIONS_GROUP.GRAPHICS);
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
               return  new AnimationOptions();
            case SOUND:
                break;
            case GRAPHICS:
               return  new GraphicsOptions();
            case TUTORIAL:
                break;
            case GAMEPLAY:
                break;
        }
        return null;
    }

    public static int getAnimPhasePeriod() {
        return optionsMap.get(OPTIONS_GROUP.ANIMATION).getIntValue(ANIMATION_OPTION.PHASE_TIME);
    }

    public static void cacheOptions() {
        cachedMap = new MapMaster<OPTIONS_GROUP, Options>().cloneHashMap(optionsMap);
    }

    public static void resetToCached() {
        optionsMap= new MapMaster<OPTIONS_GROUP, Options>().cloneHashMap(cachedMap);
    }

    public static EngineOptions getEngineOptions() {
//        return (EngineOptions) getOptions(OPTIONS_GROUP.ENGINE);
        return null;
    }

    public enum OPTIONS_GROUP {
        ANIMATION, SOUND, GRAPHICS, TUTORIAL, GAMEPLAY,ENGINE,
    }

}

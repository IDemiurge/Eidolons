package main.system.options;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;
import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.game.battlecraft.rules.RuleMaster;
import main.game.battlecraft.rules.RuleMaster.RULE_SCOPE;
import main.game.core.Eidolons;
import main.libgdx.GdxMaster;
import main.libgdx.anims.AnimMaster;
import main.libgdx.anims.particles.ParticleManager;
import main.libgdx.bf.mouse.InputController;
import main.libgdx.launch.GenericLauncher;
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
import java.util.HashMap;
import java.util.Map;

public class OptionsMaster {
    private static Map<OPTIONS_GROUP, Options> optionsMap = new HashMap<>();
    private static Map<OPTIONS_GROUP, Options> cachedMap;
    private static OptionsPanel optionsPanel;
    private static boolean initialized;
    private static JFrame optionsPanelFrame;
    private static JDialog modalOptionsPanelFrame;

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
                }
            }
        }
    }

    public static void applySoundOptions(SoundOptions soundOptions) {
        if (Gdx.app == null)
            return;
        if (!GdxMaster.isLwjglThread()) {
            Gdx.app.postRunnable(() ->
             applySoundOptions_(soundOptions));
        } else
            applySoundOptions_(soundOptions);
    }

    private static void applySoundOptions_(SoundOptions soundOptions) {
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

    public static void applyGraphicsOptions(GraphicsOptions graphicsOptions) {
        if (Gdx.app == null)
            return;
        if (!GdxMaster.isLwjglThread()) {
            Gdx.app.postRunnable(() ->
             applyGraphicsOptions_(graphicsOptions));
        } else
            applyGraphicsOptions_(graphicsOptions);
    }

    //OR LET THOSE CLASSES GET() OPTIONS?
    private static void applyGraphicsOptions_(GraphicsOptions graphicsOptions) {

        for (Object sub : graphicsOptions.getValues().keySet()) {
            new EnumMaster<GRAPHIC_OPTION>().
             retrieveEnumConst(GRAPHIC_OPTION.class,
              graphicsOptions.getValues().get(sub).toString());
            GRAPHIC_OPTION key = graphicsOptions.getKey((sub.toString()));
            if (key == null)
                continue;
            String value = graphicsOptions.getValue(key);
            boolean bool = Boolean.valueOf(value.toLowerCase());
//            Eidolons.getApplication().getGraphics(). setCursor();

            try {
                applyOption(key, value, bool  );
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }

        }
    }

    private static void applyOption(GRAPHIC_OPTION key, String value, boolean bool ) {
        switch (key) {
            case FRAMERATE:
                GenericLauncher launcher = Eidolons.getLauncher();
                launcher.setForegroundFPS(Integer.valueOf(value));
                break;
            case AUTO_CAMERA:
                DungeonScreen.setCameraAutoCenteringOn(bool);
                break;
            case AMBIENCE:
                ParticleManager.setAmbienceOn(bool);
                break;
            case FULLSCREEN:
                Eidolons.setFullscreen(bool);
                break;
            case AMBIENCE_MOVE_SUPPORTED:
                ParticleManager.setAmbienceMoveOn(
                 bool);
                break;
            case RESOLUTION:
                Eidolons.setResolution(value);
                break;
            case ZOOM_STEP:
                InputController.setZoomStep(Integer.valueOf(value) / new Float(100));
                break;
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
        if (true) {
            new LwjglApplication(new ApplicationAdapter() {
                Stage stage;
                Table root;
                VisWindow optionsWindow;
                Array<OptionsTab> tabs = new Array<>();

                @Override public void create () {
                    VisUI.load();
                    stage = new Stage(new ScreenViewport(), new SpriteBatch());
                    root = new Table();
                    root.setFillParent(true);
                    stage.addActor(root);
                    Gdx.input.setInputProcessor(stage);

                    init();
                    VisTextButton show = new VisTextButton("Options");
                    root.add(show);
                    show.addListener(new ChangeListener() {
                        @Override public void changed (ChangeEvent event, Actor actor) {
                            toggleOptions();
                        }
                    });
                }

                private void toggleOptions () {
                    if (optionsWindow == null) {
                        optionsWindow = new VisWindow("Options");
                        TabbedPane optionsPane = new TabbedPane();
                        // tabs
                        optionsWindow.add(optionsPane.getTable()).expandX().fillX().row();
                        // tab content
                        final Table content = new Table();
                        optionsWindow.add(content).expand().fill().row();

                        optionsPane.addListener(new TabbedPaneAdapter() {
                            @Override public void switchedTab (Tab tab) {
                                content.clear();
                                content.add(tab.getContentTable()).expand().top().left();
                            }
                        });

                        for (OPTIONS_GROUP group: optionsMap.keySet()) {
                            OptionsTab tab = new OptionsTab(group);
                            tabs.add(tab);
                            optionsPane.add(tab);
                        }

                        optionsPane.switchTab(0);

                        Table bottomMenu = new Table();
                        bottomMenu.defaults().pad(4);
                        optionsWindow.add(bottomMenu).expandX().left();
                        {
                            VisTextButton button = new VisTextButton("Ok");
                            addClickListener(button, this::ok);
                            bottomMenu.add(button);
                        }
                        {
                            VisTextButton button = new VisTextButton("Save");
                            addClickListener(button, this::save);
                            bottomMenu.add(button);
                        }
                        {
                            VisTextButton button = new VisTextButton("Apply");
                            addClickListener(button, this::apply);
                            bottomMenu.add(button);
                        }
                        {
                            VisTextButton button = new VisTextButton("Cancel");
                            addClickListener(button, this::cancel);
                            bottomMenu.add(button);
                        }
                        {
                            VisTextButton button = new VisTextButton("Defaults");
                            addClickListener(button, this::defaults);
                            bottomMenu.add(button);
                        }

                        optionsWindow.setSize(800, 600);
                    } if (optionsWindow.getParent() == null) {
                        stage.addActor(optionsWindow);
                        optionsWindow.fadeIn();
                        optionsWindow.centerWindow();
                    } else {
                        optionsWindow.fadeOut();
                    }
                }

                class OptionsTab extends Tab {
                    String title;
                    Table content;
                    ObjectMap<OPTION, OptionActor> optionActors = new ObjectMap<>();
                    public OptionsTab (OPTIONS_GROUP group) {
                        super(false, false);
                        this.title = StringMaster.getWellFormattedString(group.toString());
                        Options options = optionsMap.get(group);
                        content = new Table();
                        content.defaults().pad(4);
                        final Map values = options.getValues();
                        for (Object v : values.keySet()) {
                            final OPTION option = options.getKey(v.toString());
                            if (option == null)
                                continue;
                            VisLabel label = new VisLabel(option.getName());
                            content.add(label).left();
                            String optionType = options.getValueClass(option).getSimpleName();
                            final String optionStr = option.toString();
                            switch (optionType) {
                            case "String": { // aka combo box
                                Object[] optionValues = option.getOptions();
                                String[] strings = ListMaster.toStringList(optionValues).toArray(new String[optionValues.length]);
                                String selected = options.getValue(optionStr);

                                final VisSelectBox<String> selectBox = new VisSelectBox<>();
                                content.add(selectBox);
                                selectBox.setItems(strings);
                                selectBox.setSelected(selected);
                                selectBox.addListener(new ChangeListener() {
                                    @Override public void changed (ChangeEvent event, Actor actor) {
                                        Gdx.app.log("Options", option + " -> " + selectBox.getSelected());
                                    }
                                });
                                optionActors.put(option, new OptionActor() {
                                    @Override void apply () {
                                        options.setValue(optionStr, selectBox.getSelected());
                                    }

                                    @Override void refresh () {
                                        selectBox.setSelected(options.getValue(optionStr));
                                    }
                                });
                            }
                            break;
                            case "Integer": { // aka slider
                                int min = option.getMin();
                                int max = option.getMax();
                                int current = options.getIntValue(optionStr);
                                final VisSlider slider = new VisSlider(min, max, 1, false);
                                content.add(slider);
                                slider.setValue(current);
                                slider.addListener(new ChangeListener() {
                                    @Override public void changed (ChangeEvent event, Actor actor) {
                                        int value = (int)slider.getValue();
                                        Gdx.app.log("Options", option + " -> " + value);
                                    }
                                });
                                optionActors.put(option, new OptionActor() {
                                    @Override void apply () {
                                        int value = (int)slider.getValue();
                                        options.setValue(optionStr, String.valueOf(value));
                                    }

                                    @Override void refresh () {
                                        slider.setValue(options.getIntValue(optionStr));
                                    }
                                });
                            }
                            break;
                            case "Boolean": { // aka checkbox
                                boolean checked = options.getBooleanValue((Enum)option);
                                VisCheckBox checkBox = new VisCheckBox("", checked);
                                content.add(checkBox);
                                checkBox.addListener(new ChangeListener() {
                                    @Override public void changed (ChangeEvent event, Actor actor) {
                                        Gdx.app.log("Options", option + " -> " + checkBox.isChecked());
                                    }
                                });
                                optionActors.put(option, new OptionActor() {
                                    @Override void apply () {
                                        boolean checked = checkBox.isChecked();
                                        options.setValue(optionStr, String.valueOf(checked));
                                    }

                                    @Override void refresh () {
                                        checkBox.setChecked(options.getBooleanValue((Enum)option));
                                    }
                                });
                            }
                            break;
                            default: {
                                Gdx.app.log("Options", "unknown option type:" + optionType);
                            }
                            }
                            content.row();
                        }
                    }

                    @Override public String getTabTitle () {
                        return title;
                    }

                    @Override public Table getContentTable () {
                        return content;
                    }

                    public void apply () {
                        for (ObjectMap.Entry<OPTION, OptionActor> optionActor : optionActors) {
                            optionActor.value.apply();
                        }
                    }

                    public void refresh () {
                        for (ObjectMap.Entry<OPTION, OptionActor> optionActor : optionActors) {
                            optionActor.value.refresh();
                        }
                    }

                    abstract class OptionActor {
                        abstract void apply();
                        abstract void refresh();
                    }
                }

                private void addClickListener (VisTextButton button, Runnable run) {
                    button.addListener(new ClickListener() {
                        @Override public void clicked (InputEvent event, float x, float y) {
                            run.run();
                        }
                    });
                }

                private void ok () {
                    Gdx.app.log("Options", "ok");
                    apply();
                    optionsWindow.fadeOut();
                }

                private void save () {
                    Gdx.app.log("Options", "save");
                    apply();
                    OptionsMaster.saveOptions();
                }

                private void apply () {
                    Gdx.app.log("Options", "apply");
                    OptionsMaster.cacheOptions();
                    for (OptionsTab tab : tabs) {
                        tab.apply();
                    }
                    try {
                        OptionsMaster.applyOptions();
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    }
                }

                private void cancel () {
                    Gdx.app.log("Options", "cancel");
                    OptionsMaster.resetToCached();
                    optionsWindow.fadeOut();
                }

                private void defaults () {
                    OptionsMaster.resetToDefaults();
                    for (OptionsTab tab : tabs) {
                        tab.refresh();
                    }
                    Gdx.app.log("Options", "defaults");
                }

                @Override public void render () {
                    Gdx.gl.glClearColor(.5f, .5f, .5f, 1);
                    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                    stage.act();
                    stage.draw();
                }

                @Override public void resize (int width, int height) {
                    super.resize(width, height);
                    stage.getViewport().update(width, height);
                }

                @Override public void dispose () {
                    VisUI.dispose();
                    stage.dispose();
                    stage.getBatch().dispose();
                }
            }, "TestOptions", 900, 700);
        } else {
            FontMaster.init();
            GuiManager.init();
            init();
            tryOpenMenu();
        }
    }

    public static void tryOpenMenu() {
        try {
            if (Eidolons.isFullscreen()) {
                Eidolons.setFullscreen(false);
            }
            openMenu();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);

        }
    }

    public static void openMenu() {
        if (modalOptionsPanelFrame != null) {
//            optionsPanelFrame.setVisible(false);
//            optionsPanelFrame.dispatchEvent(new WindowEvent(optionsPanelFrame, WindowEvent.WINDOW_CLOSING));
            modalOptionsPanelFrame.setVisible(false);
        }
        optionsPanel = new OptionsPanel(optionsMap);
//        optionsPanelFrame = GuiManager.inNewWindow(optionsPanel,
//         "Options", new Dimension(800, 600));
        modalOptionsPanelFrame = GuiManager.inModalWindow(optionsPanel,
         "Options", new Dimension(800, 600));
        modalOptionsPanelFrame.setAlwaysOnTop(true);
    }

    public static boolean isMenuOpen() {
        if (modalOptionsPanelFrame != null)
            return modalOptionsPanelFrame.isVisible();
        return false;
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
            addMissingDefaults(optionsMap);
        }
        OptionsMaster.cacheOptions();
        try {
            applyOptions();
            initialized = true;
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

    }

    private static void addMissingDefaults(Map<OPTIONS_GROUP, Options> optionsMap) {

        for (OPTIONS_GROUP group : OPTIONS_GROUP.values()) {
            Options map = optionsMap.get(group);
            if (map == null) {

                continue;
            }
            Options options = generateDefaultOptions(group);
            for (Object val : options.getValues().keySet()) {
                if (map.getValues().containsKey(val))
                    continue;
                map.setValue(val.toString(), options.getValue(val.toString()));
            }
//            for (Object sub : getOptionGroupEnumClass(group).getEnumConstants()) {
//                String value = map.getValue((Enum) sub);
//            }
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

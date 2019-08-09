package eidolons.system.options;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionRule;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.battlecraft.rules.RuleKeeper.RULE_SCOPE;
import eidolons.game.core.Eidolons;
import eidolons.game.core.Eidolons.SCOPE;
import eidolons.game.module.dungeoncrawl.explore.ExplorationTimeMaster;
import eidolons.libgdx.GDX;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.Assets;
import eidolons.libgdx.anims.FloatingTextLayer;
import eidolons.libgdx.anims.anim3d.AnimMaster3d;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.anims.std.HitAnim;
import eidolons.libgdx.bf.Fluctuating;
import eidolons.libgdx.bf.light.ShadowMap;
import eidolons.libgdx.bf.mouse.BattleClickListener;
import eidolons.libgdx.bf.mouse.InputController;
import eidolons.libgdx.gui.panels.ScrollPanel;
import eidolons.libgdx.gui.panels.dc.logpanel.LogPanel;
import eidolons.libgdx.launch.GenericLauncher;
import eidolons.libgdx.particles.ambi.EmitterMap;
import eidolons.libgdx.particles.ambi.ParticleManager;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.screens.GameScreen;
import eidolons.libgdx.screens.map.layers.LightLayer;
import eidolons.libgdx.shaders.post.PostProcessController;
import eidolons.libgdx.stage.GuiVisualEffects;
import eidolons.libgdx.stage.camera.CameraMan;
import eidolons.macro.global.time.MacroTimeMaster;
import eidolons.swing.generic.services.dialog.DialogMaster;
import eidolons.system.audio.MusicMaster;
import eidolons.system.data.MetaDataUnit;
import eidolons.system.data.MetaDataUnit.META_DATA;
import eidolons.system.graphics.RESOLUTION;
import eidolons.system.options.AnimationOptions.ANIMATION_OPTION;
import eidolons.system.options.ControlOptions.CONTROL_OPTION;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.GraphicsOptions.GRAPHIC_OPTION;
import eidolons.system.options.Options.OPTION;
import eidolons.system.options.PostProcessingOptions.POST_PROCESSING_OPTIONS;
import eidolons.system.options.SoundOptions.SOUND_OPTION;
import eidolons.system.options.SystemOptions.SYSTEM_OPTION;
import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.swing.generic.components.editors.lists.ListChooser;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.FileLogManager;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.FontMaster;
import main.system.graphics.GuiManager;
import main.system.launch.CoreEngine;
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
    private static OptionsPanelSwing optionsPanel;
    private static boolean initialized;
    private static JDialog modalOptionsPanelFrame;
    private static String optionsPath;
    private static String OPTIONS_MODE;

    private static void applyAnimOptions(AnimationOptions animOptions) {

        for (Object sub : animOptions.getValues().keySet()) {
            new EnumMaster<ANIMATION_OPTION>().
                    retrieveEnumConst(ANIMATION_OPTION.class,
                            animOptions.getValues().get(sub).toString());
            ANIMATION_OPTION key = animOptions.getKey((sub.toString()));
            if (key == null) {
                continue;
            }
            String value = animOptions.getValue(key);
            boolean booleanValue = animOptions.getBooleanValue(key);
            Integer intValue = animOptions.getIntValue(key);
            float floatValue = new Float(intValue) / 100;

            switch (key) {
                case SPEED:
                    AnimMaster.getInstance().setAnimationSpeedFactor(
                            floatValue);
                    break;
                case FLOAT_TEXT_DURATION_MOD:
                    FloatingTextLayer.setDurationMod(floatValue);
                    break;

                case WEAPON_3D_ANIMS_OFF:
                    AnimMaster3d.setOff(booleanValue);
                    break;
                case BLOOD_ANIMS_OFF:
                    HitAnim.setBloodOff(booleanValue);
                    break;
                case MAX_ANIM_WAIT_TIME:
                    break;
                case PARALLEL_ANIMATIONS:
                    AnimMaster.getInstance().setParallelDrawing(Boolean.valueOf(value));
                    break;

                case PRECAST_ANIMATIONS:
                    break;
                case CAST_ANIMATIONS:
                    break;
                case AFTER_EFFECTS_ANIMATIONS:
                    break;
                case HIT_ANIM_DISPLACEMENT:
                    HitAnim.setDisplacementOn(booleanValue);
                    break;
            }

        }


    }

    public static void applyControlOptions() {
        applyControlOptions(getControlOptions());
    }

    private static void applyControlOptions(ControlOptions options) {
        for (Object sub : options.getValues().keySet()) {
            new EnumMaster<CONTROL_OPTION>().
                    retrieveEnumConst(CONTROL_OPTION.class,
                            options.getValues().get(sub).toString());
            CONTROL_OPTION key = options.getKey((sub.toString()));
            if (key == null)
                continue;
            String value = options.getValue(key);
            int intValue = options.getIntValue(key);
            boolean booleanValue = options.getBooleanValue(key);
            float floatValue = new Float(intValue) / 100;
            switch (key) {
                case SCROLL_SPEED:
                    ScrollPanel.setScrollAmount(intValue);
                    break;
                case ZOOM_STEP:
                    InputController.setZoomStep(Integer.valueOf(value) / new Float(100));
                    break;
                case UNLIMITED_ZOOM:
                    InputController.setUnlimitedZoom(booleanValue);
                    break;
                //                    case DRAG_OFF:
                //                        InputController.setDragOff(booleanValue);
                //                        break;
                case AUTO_CENTER_CAMERA_ON_HERO:
                    CameraMan.setCameraAutoCenteringOn(booleanValue);
                    break;
                case ALT_MODE_ON:
                    BattleClickListener.setAltDefault(booleanValue);
                    break;
                case CENTER_CAMERA_ON_ALLIES_ONLY:
                    CameraMan.setCenterCameraOnAlliesOnly(booleanValue);
                    break;
                case CENTER_CAMERA_AFTER_TIME:
                    if (DungeonScreen.getInstance() == null) {
                        break;
                    }
                    DungeonScreen.getInstance().getCameraMan(). setCameraTimer(intValue);
                    break;
                case CENTER_CAMERA_DISTANCE_MOD:
                    CameraMan.setCameraPanMod(floatValue);
                    break;
            }
        }
    }

    public static void applyAnimOptions() {
        applyAnimOptions(getAnimOptions());
    }

    public static void applyGameplayOptions() {
        applyGameplayOptions(getGameplayOptions());
    }

    private static void applyGameplayOptions(GameplayOptions gameplayOptions) {
        for (Object sub : gameplayOptions.getValues().keySet()) {
            new EnumMaster<GAMEPLAY_OPTION>().
                    retrieveEnumConst(GAMEPLAY_OPTION.class,
                            gameplayOptions.getValues().get(sub).toString());
            GAMEPLAY_OPTION key = gameplayOptions.getKey((sub.toString()));
            String value = gameplayOptions.getValue(key);
            switch (key) {
                case GAME_SPEED:
                    try {
                        float speed = gameplayOptions.getFloatValue(key) / 100;
                        ExplorationTimeMaster.setDefaultSpeed(speed);
                        ExplorationTimeMaster.setSpeed(speed);
                        MacroTimeMaster.getInstance().setSpeed(speed);
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    }
                    break;
                case GHOST_MODE:
                    if (!CoreEngine.isFastMode())
                        VisionRule.setPlayerUnseenMode(gameplayOptions.getBooleanValue(key));
                    break;
                case RULES_SCOPE:
                    RuleKeeper.setScope(
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
        MusicMaster master = MusicMaster.getInstance();
        if (master == null) {
            return;
        }
        for (Object sub : soundOptions.getValues().keySet()) {
            new EnumMaster<SOUND_OPTION>().
                    retrieveEnumConst(SOUND_OPTION.class,
                            soundOptions.getValues().get(sub).toString());
            SOUND_OPTION key = soundOptions.getKey((sub.toString()));
            if (key == null) {
                continue;
            }
            String value = soundOptions.getValue(key);

            if (!NumberUtils.isInteger(value)) {
                switch (key) {
                    case SOUNDS_OFF:
                        SoundMaster.setOn(!OptionsMaster.getSoundOptions().
                                getBooleanValue(SOUND_OPTION.SOUNDS_OFF));
                        //                        MusicMaster.resetSwitcher();
                        break;
                    case MUSIC_OFF:
                        MusicMaster.resetSwitcher();
                        break;
                }
            } else {
                Integer integer = Integer.valueOf(value.toLowerCase());
                switch (key) {
                    case AMBIENCE_VOLUME:
                        master.resetAmbientVolume();
                        break;
                    case MASTER_VOLUME:
                        SoundMaster.setMasterVolume(integer);
                        master.resetVolume();
                        break;
                    case MUSIC_VOLUME:
                        master.resetVolume();
                        //auto
                        break;
                }
            }
        }
    }

    public static void applyGraphicsOptions(GraphicsOptions graphicsOptions) {
        if (Gdx.app == null) {
            return;
        }
        if (!GdxMaster.isLwjglThread()) {
            Gdx.app.postRunnable(() ->
                    applyGraphicsOptions_(graphicsOptions));
        } else
            applyGraphicsOptions_(graphicsOptions);
    }

    //OR LET THOSE CLASSES GET() OPTIONS?
    private static void applyGraphicsOptions_(GraphicsOptions graphicsOptions) {

        for (Object sub : graphicsOptions.getValues().keySet()) {
            GRAPHIC_OPTION key = graphicsOptions.getKey((sub.toString()));
            if (key == null)
                continue;
            String value = graphicsOptions.getValue(key);
            boolean bool = Boolean.valueOf(value.toLowerCase());
            //            Eidolons.getApplication().getGraphics(). setCursor();

            try {
                applyOption(key, value, bool);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }

        }
    }

    private static void applySystemOptions(SystemOptions systemOptions) {

        for (Object sub : systemOptions.getValues().keySet()) {
            SYSTEM_OPTION key = systemOptions.getKey((sub.toString()));
            if (key == null)
                continue;
            String value = systemOptions.getValue(key);
            boolean bool = Boolean.valueOf(value.toLowerCase());
            //            Eidolons.getApplication().getGraphics(). setCursor();

            try {
                applySystemOption(key, value, bool);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }

        }
    }

    private static void applySystemOption(SYSTEM_OPTION key, String value, boolean bool) {
        switch (key) {
            case DEV:
                CoreEngine.setDevEnabled(bool);
                break;
            case LOGGING:
                break;
            case LOG_TO_FILE:
                FileLogManager.setLoggingOn(bool);
                break;
            case RESET_COSTS:
                break;
            case LOG_MORE_INFO:
                break;
            case LOG_DEV_INFO:
                break;
            case MESSAGES_OFF:
                break;
            case INTRO_OFF:
                break;
            case CACHE:
                break;
            case PRECONSTRUCT:
                break;
            case TESTER_VERSION:
                CoreEngine.setTesterVersion(bool);
                break;
            case LAZY:
                break;
            case ActiveTestMode:
                CoreEngine.setActiveTestMode(bool);
                break;
            case Ram_economy:
                CoreEngine.setRamEconomy(bool);
                break;
            case levelTestMode:
                CoreEngine.setLevelTestMode(bool);
                break;
            case contentTestMode:
                CoreEngine.setContentTestMode(bool);
                break;
            case reverseExit:
                CoreEngine.setReverseExit(bool);
                break;
            case KeyCheat:
                CoreEngine.setKeyCheat(bool);
                break;
        }

    }

    private static void applyOption(GRAPHIC_OPTION key, String value, boolean bool) {
        switch (key) {
            case ALT_ASSET_LOAD:
                Assets.setON(!bool);
                break;
            case AMBIENCE_DENSITY:
                EmitterMap.setGlobalShowChanceCoef(Integer.valueOf(value));
                break;
            case ADDITIVE_LIGHT:
                LightLayer.setAdditive(bool);
                break;
            case PERFORMANCE_BOOST:
                Fluctuating.fluctuatingAlphaPeriodGlobal = (Integer.valueOf(value)) / 10;
                break;
            case UI_VFX:
                GuiVisualEffects.setOff(!bool);
                break;
            case BRIGHTNESS:
                GdxMaster.setBrightness(new Float(Integer.valueOf(value) / 100));
                break;
            case FRAMERATE:
                GenericLauncher launcher = Eidolons.getLauncher();
                launcher.setForegroundFPS(Integer.valueOf(value));
                break;
            case AMBIENCE_VFX:
                ParticleManager.setAmbienceOn(bool);
                break;
            case LITE_MODE:
                CoreEngine.setLiteLaunch(bool);
                break;
            case FULLSCREEN:
                if (Eidolons.getScope() == SCOPE.MENU)
                    Eidolons.setFullscreen(bool);
                break;
            case VIDEO:
                break;
            case AMBIENCE_MOVE_SUPPORTED:
                ParticleManager.setAmbienceMoveOn(
                        bool);
                break;
            case RESOLUTION:
                Eidolons.setResolution(value);
                break;
            case VSYNC:
                break;
            case SHADOW_MAP_OFF:
                ShadowMap.setOn(!bool);
                break;
            case FONT_SIZE:
                GdxMaster.setUserFontScale(Float.valueOf(value) / 100);
                break;
            case UI_SCALE:
                GdxMaster.setUserUiScale(Float.valueOf(value) / 100);
                break;
            case SPRITE_CACHE_ON:
                break;
            case LIGHT_OVERLAYS_OFF:
                break;
            case UI_ATLAS:
                break;
            case FULL_ATLAS:
                break;
            case SHARD_VFX:
                break;
            case COLOR_TEXT_LOG:
                LogPanel.setColorText(bool);
                break;
            case NO_BACKGROUND_SPRITES:
                break;
        }
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

    public static void applyOptions() {

        applyGraphicsOptions(getGraphicsOptions());
        applySoundOptions(getSoundOptions());
        applyGameplayOptions(getGameplayOptions());
        applyControlOptions(getControlOptions());
        applySystemOptions(getSystemOptions());

        if (GdxMaster.isLwjglThread()) {
            try {
                PostProcessController.getInstance().update(getPostProcessingOptions());
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        if (!GdxMaster.isGuiReady())
            return;
        if (AnimMaster.getInstance() == null)
            return;
        if (GdxMaster.isLwjglThread()) {
            applyAnimOptions(getAnimOptions());
        } else
            Gdx.app.postRunnable(() ->
            {
                applyAnimOptions(getAnimOptions());
            });
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
        if (isLocalOptionsPreferred()) {
            FileManager.write(content.toString(), getLocalOptionsPath());
        } else {
//    TODO igg demo fix    FileManager.write("Global options are now saved at " + getGlobalOptionsPath(), getLocalOptionsPath());
            FileManager.write(content.toString(), getGlobalOptionsPath());
        }
    }

    private static String getGlobalOptionsPath() {
        if (optionsPath != null)
            return optionsPath;
        return PathFinder.OPTIONS_PATH + "options.xml";
    }

    private static String getLocalOptionsPath() {
        return PathFinder.getXML_PATH() + "options.xml";
    }

    public static void main(String[] args) {
        FontMaster.init();
        GuiManager.init();
        init();
        openVisUiMenu(new Stage(new FitViewport(800, 800), new SpriteBatch()));

    }

    public static void openVisUiMenu(Stage stage) {
        OptionsWindow.getInstance().open(optionsMap, stage);

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
        if (isVisUiMode()) {
            openVisUiMenu(Eidolons.getScreen().getGuiStage());
            return;
        }

        if (modalOptionsPanelFrame != null) {
            //            optionsPanelFrame.setVisible(false);
            //            optionsPanelFrame.dispatchEvent(new WindowEvent(optionsPanelFrame, WindowEvent.WINDOW_CLOSING));
            modalOptionsPanelFrame.setVisible(false);
        }
        optionsPanel = new OptionsPanelSwing(optionsMap);
        //        optionsPanelFrame = GuiManager.inNewWindow(optionsPanel,
        //         "Options", new Dimension(800, 600));
        modalOptionsPanelFrame = GuiManager.inModalWindow(optionsPanel,
                "Options", new Dimension(800, 600));
        modalOptionsPanelFrame.setAlwaysOnTop(true);
    }

    private static boolean isVisUiMode() {
        return true;
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

    private static boolean isLocalOptionsPreferred() {
        return //CoreEngine.isMe() &&
                !CoreEngine.isJar();
    }

    public static void init() {
        if (initialized)
            return;
        String data = readOptionsFile();
        if (data.isEmpty()) {
            optionsMap = initDefaults();
        } else {
            optionsMap = readOptions(data);
            addMissingDefaults(optionsMap);

            if (MetaDataUnit.getInstance().getIntValue(META_DATA.TIMES_LAUNCHED) < 2)
                autoAdjustOptions(OPTIONS_GROUP.GRAPHICS, optionsMap.get(OPTIONS_GROUP.GRAPHICS));
        }

        autoAdjustOptions(OPTIONS_GROUP.SYSTEM, optionsMap.get(OPTIONS_GROUP.SYSTEM));

//        if (CoreEngine.isMapPreview()) {
//            getGraphicsOptions().setValue("RESOLUTION", RESOLUTION._3840x2160.toString());
//        }
        OptionsMaster.cacheOptions();

        initFlags();
        try {
            applyOptions();
            initialized = true;
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

    }

    private static String readOptionsFile() {
        String path = getOptionsPath();
        String data = FileManager.readFile(path);
       if (OPTIONS_MODE==null )
           if (data.isEmpty() || isLocalOptionsPreferred()) {
            data = FileManager.readFile(getLocalOptionsPath());
        }
        return data;
    }

    private static void initFlags() {
        if (CoreEngine.isLiteLaunch()) {
            getGraphicsOptions().setValue(GRAPHIC_OPTION.LITE_MODE, true);
        }
    }


    private static void addMissingDefaults(Map<OPTIONS_GROUP, Options> optionsMap) {

        for (OPTIONS_GROUP group : OPTIONS_GROUP.values()) {
            Options map = optionsMap.get(group);
            Options options = generateDefaultOptions(group);
            if (map == null) {
                optionsMap.put(group, options);
            } else
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

    private static void autoAdjustOptions(OPTIONS_GROUP group, Options options) {
        switch (group) {
            case GRAPHICS:
                options.setValue(GRAPHIC_OPTION.RESOLUTION, GDX.getDisplayResolutionString());
                break;
            case SYSTEM:

                break;
        }


    }

    private static Map<OPTIONS_GROUP, Options> initDefaults() {
        SystemAnalyzer.analyze();
        Map<OPTIONS_GROUP, Options> defaults = initDefaults(false);
        SystemAnalyzer.adjustForRAM(defaults);
        return defaults;
    }

    private static Map<OPTIONS_GROUP, Options> initDefaults(boolean adjust) {
        XLinkedMap optionsMap = new XLinkedMap<>();
        for (OPTIONS_GROUP group : OPTIONS_GROUP.values()) {

            Options options = generateDefaultOptions(group);
            if (adjust)
                autoAdjustOptions(group, options);
            if (options != null)
                optionsMap.put(group, options);
        }
        return optionsMap;
    }

    private static Class<?> getOptionGroupEnumClass(OPTIONS_GROUP group) {
        switch (group) {
            case CONTROLS:
                return CONTROL_OPTION.class;
            case ANIMATION:
                return ANIMATION_OPTION.class;
            case GRAPHICS:
                return GRAPHIC_OPTION.class;
            case SOUND:
                return SOUND_OPTION.class;
            case GAMEPLAY:
                return GAMEPLAY_OPTION.class;
            case SYSTEM:
                return SYSTEM_OPTION.class;
            case POST_PROCESSING:
                return POST_PROCESSING_OPTIONS.class;
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
            case CONTROLS:
                return new ControlOptions();
            case SOUND:
                return new SoundOptions();
            case GRAPHICS:
                return new GraphicsOptions();

            case GAMEPLAY:
                return new GameplayOptions();

            case SYSTEM:
                return new SystemOptions();
            case POST_PROCESSING:
                return new PostProcessingOptions();
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

    public static SystemOptions getSystemOptions() {
        return (SystemOptions) getOptions(OPTIONS_GROUP.SYSTEM);
    }

    public static ControlOptions getControlOptions() {
        return (ControlOptions) getOptions(OPTIONS_GROUP.CONTROLS);
    }

    public static SoundOptions getSoundOptions() {
        return (SoundOptions) getOptions(OPTIONS_GROUP.SOUND);
    }

    public static Map<OPTIONS_GROUP, Options> getOptionsMap() {
        return optionsMap;
    }

    public static void setOptionsPath(String optionsPath) {
        OptionsMaster.optionsPath = optionsPath;
    }

    public static void setOptionsMode(String optionsMode) {
        OPTIONS_MODE = optionsMode;
    }

    public static String getOptionsPath() {
        if (optionsPath != null) {
            return optionsPath;
        }
        if (OPTIONS_MODE != null) {
            return (PathFinder.getXML_PATH() + "options/" + OPTIONS_MODE + ".xml");
        } else if (isLocalOptionsPreferred()) {
            return FileManager.readFile(getLocalOptionsPath());
        }
        return FileManager.readFile(getGlobalOptionsPath());
    }

    public static PostProcessingOptions getPostProcessingOptions() {
        return (PostProcessingOptions) optionsMap.get(OPTIONS_GROUP.POST_PROCESSING);
    }


    public enum OPTIONS_GROUP {
        GRAPHICS, GAMEPLAY, CONTROLS, SOUND, ANIMATION, SYSTEM, POST_PROCESSING,
        //TUTORIAL, ENGINE,
    }

}

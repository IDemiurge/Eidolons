package eidolons.system.options;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionRule;
import eidolons.game.battlecraft.rules.RuleEnums.RULE_SCOPE;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.core.Eidolons;
import eidolons.game.core.Eidolons.SCOPE;
import eidolons.game.module.dungeoncrawl.explore.ExplorationTimeMaster;
import eidolons.libgdx.GDX;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.FloatingTextLayer;
import eidolons.libgdx.anims.anim3d.AnimMaster3d;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.anims.std.HitAnim;
import eidolons.libgdx.bf.Fluctuating;
import eidolons.libgdx.bf.decor.shard.ShardVisuals;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.light.ShadowMap;
import eidolons.libgdx.bf.mouse.BattleClickListener;
import eidolons.libgdx.bf.mouse.InputController;
import eidolons.libgdx.gui.panels.ScrollPanel;
import eidolons.libgdx.gui.panels.dc.logpanel.LogPanel;
import eidolons.libgdx.particles.ParticleEffectX;
import eidolons.libgdx.particles.ambi.EmitterMap;
import eidolons.libgdx.particles.ambi.ParticleManager;
import eidolons.libgdx.screens.ScreenMaster;
import eidolons.libgdx.screens.map.layers.LightLayer;
import eidolons.libgdx.shaders.post.PostProcessController;
import eidolons.libgdx.stage.GuiVisualEffects;
import eidolons.libgdx.stage.camera.CameraOptions;
import eidolons.macro.global.time.MacroTimeMaster;
import eidolons.swing.generic.services.dialog.DialogMaster;
import eidolons.system.audio.MusicMaster;
import eidolons.system.data.MetaDataUnit;
import eidolons.system.data.MetaDataUnit.META_DATA;
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
import main.data.xml.XmlNodeMaster;
import main.swing.generic.components.editors.lists.ListChooser;
import main.system.ExceptionMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.FileLogManager;
import main.system.graphics.FontMaster;
import main.system.graphics.GuiManager;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;
import main.system.sound.SoundMaster;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class OptionsMaster {
    private static OptionsMaster instance;
    private static OptionsPanelSwing optionsPanel;
    private static JDialog modalOptionsPanelFrame;

    protected Map<OPTIONS_GROUP, Options> optionsMap = new HashMap<>();
    protected Map<OPTIONS_GROUP, Options> cachedMap;
    protected boolean initialized;
    protected String optionsPath;
    protected String OPTIONS_MODE;

    public static void init() {
        //    TODO    Gdx.app.getPreferences()
        getInstance().initialize();
    }

    protected static void applyAnimOptions(AnimationOptions animOptions) {

        for (Object sub : animOptions.getValues().keySet()) {
            new EnumMaster<ANIMATION_OPTION>().
                    retrieveEnumConst(ANIMATION_OPTION.class,
                            animOptions.getValues().get(sub));
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
                case AFTER_EFFECTS_ANIMATIONS:
                case CAST_ANIMATIONS:

                case PRECAST_ANIMATIONS:
                    break;
                case PARALLEL_ANIMATIONS:
                    AnimMaster.getInstance().setParallelDrawing(Boolean.valueOf(value));
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

    protected static void applyControlOptions(ControlOptions options) {
        CameraOptions.update(options);

        for (Object sub : options.getValues().keySet()) {
            new EnumMaster<CONTROL_OPTION>().
                    retrieveEnumConst(CONTROL_OPTION.class,
                            options.getValues().get(sub));
            CONTROL_OPTION key = options.getKey((sub.toString()));
            if (key == null)
                continue;
            String value = options.getValue(key);
            int intValue = options.getIntValue(key);
            boolean booleanValue = options.getBooleanValue(key);
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
                case ALT_MODE_ON:
                    BattleClickListener.setAltDefault(booleanValue);
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

    protected static void applyGameplayOptions(GameplayOptions gameplayOptions) {
        for (Object sub : gameplayOptions.getValues().keySet()) {
            //            new EnumMaster<GAMEPLAY_OPTION>().
            //                    retrieveEnumConst(GAMEPLAY_OPTION.class,
            //                            gameplayOptions.getValues().get(sub).toString());

            GAMEPLAY_OPTION key = gameplayOptions.getKey((sub.toString()));
            if (key == null) {
                continue;
            }
            String value = gameplayOptions.getValue(key);
            switch (key) {
                case GAME_SPEED:
                    try {
                        float speed = gameplayOptions.getFloatValue(key) / 100;
                        ExplorationTimeMaster.setDefaultSpeed(speed);
                        ExplorationTimeMaster.setSpeed(speed);
                        MacroTimeMaster.getInstance().setSpeed(speed);
                    } catch (Exception e) {
                        ExceptionMaster.printStackTrace(e);
                    }
                    break;
                case GHOST_MODE:
                    // if (!CoreEngine.isFastMode())
                    VisionRule.setPlayerUnseenMode(gameplayOptions.getBooleanValue(key));
                    break;
                case RULES_SCOPE:
                    RuleKeeper.setScope(
                            new EnumMaster<RULE_SCOPE>().
                                    retrieveEnumConst(RULE_SCOPE.class,
                                            gameplayOptions.getValues().get(sub)
                                    ));
                    break;
                case GAME_DIFFICULTY:
                    if (Eidolons.game != null)
                        if (Eidolons.game.getMissionMaster() != null)
                            Eidolons.game.getMissionMaster().getOptionManager().difficultySet(value);
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

    protected static void applySoundOptions_(SoundOptions soundOptions) {
        MusicMaster master = MusicMaster.getInstance();
        if (master == null) {
            return;
        }
        for (Object sub : soundOptions.getValues().keySet()) {
            new EnumMaster<SOUND_OPTION>().
                    retrieveEnumConst(SOUND_OPTION.class,
                            soundOptions.getValues().get(sub));
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

    public static void applyGraphicsOptions() {
        applyGraphicsOptions(getGraphicsOptions());
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
    protected static void applyGraphicsOptions_(GraphicsOptions graphicsOptions) {

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
                ExceptionMaster.printStackTrace(e);
            }

        }
    }

    protected static void applySystemOptions(SystemOptions systemOptions) {

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
                ExceptionMaster.printStackTrace(e);
            }

        }
    }

    protected static void applySystemOption(SYSTEM_OPTION key, String value, boolean bool) {
        switch (key) {
            case LITE_MODE:
                Flags.setLiteLaunch(bool);
                break;
            case SUPERLITE_MODE:
                Flags.setSuperLite(bool);
                break;
            case DEV:
                Flags.setDevEnabled(bool);
                break;
            case LOGGING:
            case LAZY:
            case PRECONSTRUCT:
            case CACHE:
            case INTRO_OFF:
            case MESSAGES_OFF:
            case LOG_DEV_INFO:
            case LOG_MORE_INFO:
            case RESET_COSTS:
                break;
            case LOG_TO_FILE:
                FileLogManager.on = bool;
                break;
            case TESTER_VERSION:
                Flags.setTesterVersion(bool);
                break;
            case ActiveTestMode:
                Flags.setActiveTestMode(bool);
                break;
            case Ram_economy:
                Flags.setRamEconomy(bool);
                break;
            case levelTestMode:
                Flags.setLevelTestMode(bool);
                break;
            case contentTestMode:
                Flags.setContentTestMode(bool);
                break;
            case reverseExit:
                Flags.setReverseExit(bool);
                break;
            case KeyCheat:
                Flags.setKeyCheat(bool);
                break;
        }

    }

    protected static void applyOption(GRAPHIC_OPTION key, String value, boolean bool) {
        switch (key) {
            //            case ALT_ASSET_LOAD:
            //                Assets.setON(!bool);
            //                break;
            case AMBIENCE_DENSITY:
                EmitterMap.setGlobalShowChanceCoef(Integer.valueOf(value));
                break;
            case ADDITIVE_LIGHT:
                LightLayer.setAdditive(bool);
                break;
            case PERFORMANCE_BOOST:
                Fluctuating.fluctuatingAlphaPeriodGlobal = (Integer.valueOf(value)) / 10;
                break;
            case GRID_VFX:
                GridPanel.setShowGridEmitters(bool);
                break;
            case UI_VFX:
                GuiVisualEffects.setOff(!bool);
                break;
            case BRIGHTNESS:
                GdxMaster.setBrightness(new Float(Integer.valueOf(value) / 100));
                break;
            case AMBIENCE_VFX:
                ParticleManager.setAmbienceOn(bool);
                break;

            case FULLSCREEN:
                if (Eidolons.getScope() == SCOPE.MENU)
                    ScreenMaster.setFullscreen(bool);
                break;

            case VIDEO:
            case BACKGROUND_SPRITES_OFF:
            case SHARD_VFX:
            case FULL_ATLAS:
            case UI_ATLAS:
            case LIGHT_OVERLAYS_OFF:
            case SPRITE_CACHE_ON:
            case VSYNC:

                break;
            case AMBIENCE_MOVE_SUPPORTED:
                ParticleManager.setAmbienceMoveOn(
                        bool);
                break;
            case RESOLUTION:
                ScreenMaster.setResolution(value);
                break;
            case SHARDS_OFF:
                ShardVisuals.setOn(!bool);
                break;
            case SHADOW_MAP_OFF:
                ShadowMap.setOn(!bool);
                break;
            case SPECIAL_EFFECTS:
                ParticleEffectX.setGlobalAlpha(Float.valueOf(value) / 100);
                break;
            case FONT_SIZE:
                GdxMaster.setUserFontScale(Float.valueOf(value) / 100);
                break;
            case UI_SCALE:
                GdxMaster.setUserUiScale(Float.valueOf(value) / 100);
                break;
            case COLOR_TEXT_LOG:
                LogPanel.setColorText(bool);
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
                ExceptionMaster.printStackTrace(e);
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

        if (CoreEngine.TEST_LAUNCH) {
            Flags.setFullFastMode(true);
            Flags.setSuperLite(true);
        }
    }

    public static void saveOptions() {
        getInstance().save();
    }

    public static OptionsMaster getInstance() {
        if (instance == null) {
            instance = new OptionsMaster();
        }
        return instance;
    }

    public static Options getOptionsByConst(OPTION option) {
        OPTIONS_GROUP group = getGroupForConst(option);
        return getOptions(group);
    }

    public static void setOption(OPTION option, Object value, boolean apply) {
        OPTIONS_GROUP group = getGroupForConst(option);
        Options options = getOptions(group);
        options.setValue(option.toString(), value.toString());
        if (apply) {
            applyOptions(group);
        }

    }

    private static void applyOptions(OPTIONS_GROUP group) {
        switch (group) {
            case GRAPHICS:
                applyGraphicsOptions();
                break;
            case GAMEPLAY:
                applyGameplayOptions();
                break;
            case CONTROLS:
                applyControlOptions();
                break;
            case SOUND:
                applySoundOptions(getSoundOptions());
                break;
            case ANIMATION:
                applyAnimOptions();
                break;
            case SYSTEM:
                applySystemOptions(getSystemOptions());
                break;
            case POST_PROCESSING:
                break;
        }
    }

    private static OPTIONS_GROUP getGroupForConst(OPTION option) {
        switch (option.getClass().getSimpleName()) {
            case "GAMEPLAY_OPTION":
                return OPTIONS_GROUP.GAMEPLAY;
            case "CONTROL_OPTION":
                return OPTIONS_GROUP.CONTROLS;
            case "ANIMATION_OPTION":
                return OPTIONS_GROUP.ANIMATION;
            case "SOUND_OPTION":
                return OPTIONS_GROUP.SOUND;
            case "GRAPHIC_OPTION":
                return OPTIONS_GROUP.GRAPHICS;
            case "SYSTEM_OPTION":
                return OPTIONS_GROUP.SYSTEM;
            case "POST_PROCESSING_OPTION":
                return OPTIONS_GROUP.POST_PROCESSING;
        }
        return null;
    }

    public void save() {
        StringBuilder content = new StringBuilder();
        content.append(XML_Converter.openXml("Options" + StringMaster.NEW_LINE));
        for (OPTIONS_GROUP sub : optionsMap.keySet()) {
            content.append(XML_Converter.openXml(sub.toString())).append(StringMaster.NEW_LINE);
            //OR PUT UNID-DATA-STRING there
            for (Object option : optionsMap.get(sub).getValues().keySet()) {
                content.append(XML_Converter.wrap(option.toString(),
                        optionsMap.get(sub).getValues().get(option).toString())).append(StringMaster.NEW_LINE);
            }
            content.append(XML_Converter.closeXml(sub.toString())).append(StringMaster.NEW_LINE);
        }
        content.append(XML_Converter.closeXml("Options"));
        String path = getSaveOptionsPath();
        FileManager.write(content.toString(), path);
        //    TODO EA check
        //     FileManager.write("Global options are now saved at " + getGlobalOptionsPath(), getLocalOptionsPath());
    }

    protected String getSaveOptionsPath() {

        if (isLocalOptionsPreferred()) {
            return getLocalOptionsPath();
        }
        return getGlobalOptionsPath();
    }

    protected String getGlobalOptionsPath() {
        if (optionsPath != null)
            return optionsPath;
        return PathFinder.OPTIONS_PATH + "options.xml";
    }

    protected String getLocalOptionsPath() {
        return PathFinder.getXML_PATH() + "options.xml";
    }

    public static void main(String[] args) {
        FontMaster.init();
        GuiManager.init();
        init();
        openVisUiMenu(new Stage(new FitViewport(800, 800), new SpriteBatch()));

    }

    public static void openVisUiMenu(Stage stage) {
        OptionsWindow.getInstance().open(getInstance().getOptionsMap(), stage);

    }

    public static void openMenu() {
        if (isVisUiMode()) {
            openVisUiMenu(ScreenMaster.getScreen().getGuiStage());
            return;
        }

        if (modalOptionsPanelFrame != null) {
            //            optionsPanelFrame.setVisible(false);
            //            optionsPanelFrame.dispatchEvent(new WindowEvent(optionsPanelFrame, WindowEvent.WINDOW_CLOSING));
            modalOptionsPanelFrame.setVisible(false);
        }
        optionsPanel = new OptionsPanelSwing(getInstance().getOptionsMap());
        //        optionsPanelFrame = GuiManager.inNewWindow(optionsPanel,
        //         "Options", new Dimension(800, 600));
        modalOptionsPanelFrame = GuiManager.inModalWindow(optionsPanel,
                "Options", new Dimension(800, 600));
        modalOptionsPanelFrame.setAlwaysOnTop(true);
    }

    protected static boolean isVisUiMode() {
        return true;
    }

    public static boolean isMenuOpen() {

        if (modalOptionsPanelFrame != null)
            return modalOptionsPanelFrame.isVisible();
        return false;
    }

    public Map<OPTIONS_GROUP, Options> readOptions(String data) {
        Document doc = XML_Converter.getDoc(data);
        Map<OPTIONS_GROUP, Options> optionsMap = new XLinkedMap<>();
        for (Node sub : XmlNodeMaster.getNodeListFromFirstChild(doc, true)) {
            OPTIONS_GROUP group = OPTIONS_GROUP.valueOf(sub.getNodeName());
            Options options = createOptions(group, sub);
            if (options != null)
                optionsMap.put(group, options);
        }

        return optionsMap;
    }

    protected Options createOptions(OPTIONS_GROUP group, Node doc) {
        Options options = createOptions(group);
        for (Node optionNode : XmlNodeMaster.getNodeList(doc)) {
            options.setValue(optionNode.getNodeName(), optionNode.getTextContent());
        }
        return options;
    }

    protected boolean isLocalOptionsPreferred() {
        //        return CoreEngine.isMe() &&
        ////                !CoreEngine.isJar();
        return false;
    }


    public void initialize() {
        if (initialized)
            return;
        String data = readOptionsFile();
        if (data.isEmpty()) {
            optionsMap = initDefaults();
        } else {
            optionsMap = readOptions(data);
            addMissingDefaults(optionsMap);

            if (!Flags.isIDE())
                if (MetaDataUnit.getInstance().getIntValue(META_DATA.TIMES_LAUNCHED) < 2)
                    autoAdjustOptions(OPTIONS_GROUP.GRAPHICS, optionsMap.get(OPTIONS_GROUP.GRAPHICS));
        }

        autoAdjustOptions(OPTIONS_GROUP.SYSTEM, optionsMap.get(OPTIONS_GROUP.SYSTEM));

        applySystemOptions(getSystemOptions());
        try {
            SystemAnalyzer.analyze();
            SystemAnalyzer.adjustForRAM(optionsMap);
        } catch (Exception e) {
            ExceptionMaster.printStackTrace(e);
        }
        //        if (CoreEngine.isMapPreview()) {
        //            getGraphicsOptions().setValue("RESOLUTION", RESOLUTION._3840x2160.toString());
        //        }
        cacheOptions();

        initFlags();
        try {
            applyOptions();
            initialized = true;
        } catch (Exception e) {
            ExceptionMaster.printStackTrace(e);
        }

    }

    protected String readOptionsFile() {
        String path = getOptionsPath();
        String data = FileManager.readFile(path);
        if (OPTIONS_MODE == null)
            if (data.isEmpty() || isLocalOptionsPreferred()) {
                data = FileManager.readFile(getLocalOptionsPath());
            }
        return data;
    }

    protected void initFlags() {
        if (Flags.isLiteLaunch()) {
            getSystemOptions().setValue(SYSTEM_OPTION.LITE_MODE, true);
        }
    }


    protected void addMissingDefaults(Map<OPTIONS_GROUP, Options> optionsMap) {

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

    protected void autoAdjustOptions(OPTIONS_GROUP group, Options options) {
        switch (group) {
            case GRAPHICS:
                options.setValue(GRAPHIC_OPTION.RESOLUTION, GDX.getDisplayResolutionString());
                break;
            case SYSTEM:

                break;
        }


    }

    protected Map<OPTIONS_GROUP, Options> initDefaults() {
        Map<OPTIONS_GROUP, Options> defaults = initDefaults(false);
        return defaults;
    }

    protected Map<OPTIONS_GROUP, Options> initDefaults(boolean adjust) {
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

    protected Class<?> getOptionGroupEnumClass(OPTIONS_GROUP group) {
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
        return (GraphicsOptions) getOptions(OPTIONS_GROUP.GRAPHICS);
    }

    public static GameplayOptions getGameplayOptions() {
        return (GameplayOptions) getOptions(OPTIONS_GROUP.GAMEPLAY);
    }

    public Options getOptions(OPTION group) {
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
        Options options = (getInstance().getOptionsMap()).get(group);
        if (options == null) {
            options = getInstance().createOptions(group);
        }
        return options;
    }

    protected Options generateDefaultOptions(OPTIONS_GROUP group) {
        Options options = createOptions(group);
        if (options == null) {
            return null;
        }
        Class<?> clazz = getOptionGroupEnumClass(group);
        return setDefaults(options, clazz);
    }

    protected Options setDefaults(Options options, Class<?> clazz) {
        for (Object c : clazz.getEnumConstants()) {
            OPTION option = (OPTION) c;
            if (option.getDefaultValue() == null) {
                continue;
            }
            String value = option.getDefaultValue().toString();
            options.setValue(c.toString(), value);
        }
        return options;
    }

    protected Options createOptions(OPTIONS_GROUP group) {
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
        //        return optionsMap.getVar(OPTIONS_GROUP.ANIMATION).getIntValue(ANIMATION_OPTION.PHASE_TIME);
    }

    public void cacheOptions() {
        cachedMap = new MapMaster<OPTIONS_GROUP, Options>().cloneHashMap(optionsMap);
    }

    public void resetToCached() {
        optionsMap = new MapMaster<OPTIONS_GROUP, Options>().cloneHashMap(cachedMap);
        applyOptions();
    }

    public void resetToDefaults() {
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
        return getInstance().optionsMap;
    }

    public static void setOptionsPath(String optionsPath) {
        getInstance().optionsPath = optionsPath;
    }

    public static void setOptionsMode(String optionsMode) {
        getInstance().OPTIONS_MODE = optionsMode;
    }

    public String getOptionsPath() {
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
        return (PostProcessingOptions) getOptions(OPTIONS_GROUP.POST_PROCESSING);
    }


    public enum OPTIONS_GROUP {
        GRAPHICS, GAMEPLAY, CONTROLS, SOUND, ANIMATION, SYSTEM, POST_PROCESSING, EDITOR
        //TUTORIAL, ENGINE,
    }

}

package libgdx.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.core.Eidolons;
import libgdx.anims.construct.AnimConstructor;
import libgdx.anims.sprite.SpriteAnimationFactory;
import libgdx.audio.SoundPlayer;
import libgdx.particles.EmitterPools;
import libgdx.particles.util.EmitterMaster;
import libgdx.screens.ScreenWithLoader;
import libgdx.texture.SmartTextureAtlas;
import libgdx.texture.Sprites;
import libgdx.texture.TextureCache;
import eidolons.system.audio.MusicEnums;
import eidolons.system.audio.MusicMaster;
import eidolons.system.options.GraphicsOptions;
import eidolons.system.options.OptionsMaster;
import main.content.enums.GenericEnums;
import main.data.filesys.PathFinder;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.PathUtils;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.LOG_CHANNEL;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 12/1/2017.
 */
public class Assets {
    private static boolean ON = true;
    private static final List<String> ktxAtlases = new ArrayList<>();
    private static final int estMemoryLoad = 0;
    private static final int memoryBuffer = 500;

    private static Assets assets;
    private final AssetManager manager;

    public static void setON(boolean ON) {
        Assets.ON = ON;
    }

    private Assets() {
        GuiEventManager.bind(GuiEventType.LOAD_SCOPE, p -> {
            preloadScope(new EnumMaster<ASSET_GROUP>().retrieveEnumConst(ASSET_GROUP.class, p.get().toString()));
        });
        GuiEventManager.bind(GuiEventType.DISPOSE_SCOPE, p -> {
            dispose(new EnumMaster<ASSET_GROUP>().retrieveEnumConst(ASSET_GROUP.class, p.get().toString()));
        });
        manager = new GdxAssetManager();
    }

    private static boolean isPreconstructAllOnInit() {
        return !CoreEngine.TEST_LAUNCH;
    }

    public static void preloadMenu() {
        //step #0 - just when we start gdx app!
        MusicMaster.preload(MusicEnums.MUSIC_SCOPE.MENU);
        // Atlases.preloadAtlas(AssetEnums.ATLAS.UI_BASE);
    }

    public static void loadAtlasesForScreen(ScreenWithLoader screen, VisualEnums.SCREEN_TYPE type) {

        switch (type) {
            case DUNGEON:
                if (TextureCache.atlasesOn) {
                    if (!Flags.isJarlike()) {
                    Atlases.preloadAtlas(AssetEnums.ATLAS.SPRITES_GRID);
                    Atlases.preloadAtlas(AssetEnums.ATLAS.UNIT_VIEW);
                    Atlases.preloadAtlas(AssetEnums.ATLAS.SPRITES_UI);
                    Atlases.preloadAtlas(AssetEnums.ATLAS.TEXTURES);
                    }
                }
                Assets.preloadDC();
                break;
        }
    }

    public static void preloadDC() {
        if (!Flags.isLiteLaunch()) {
        loadSprite(Sprites.GHOST_FIST);
        preloadScope(ASSET_GROUP.DC_COMMONS);
        }

        // Chronos.mark("preload her0es");
        // preloadHeroes();
        // Chronos.logTimeElapsedForMark("preload her0es");

        if (!Flags.isVfxOff()) {
            Chronos.mark("preload Emitters");
            preloadEmitters();
            Chronos.logTimeElapsedForMark("preload Emitters");
        }
        if (CoreEngine.FULL_LAUNCH) {
            Chronos.mark("preload Audio");
            try {
                preloadSounds();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            Chronos.logTimeElapsedForMark("preload Audio");
        }

    }


    public static void preloadModule(Module module) {
        //objects, special
    }

    public static boolean preloadObjects(DequeImpl<BattleFieldObject> objects) {
        Chronos.mark("preloadObjects");
        boolean result = false;
        if (isPreconstructAllOnInit()) {
            for (BattleFieldObject sub : objects) {
                if (!checkPreloadUnit(sub)) {
                    continue;
                }
                if (sub instanceof Unit)
                    try {
                        AnimConstructor.preconstruct((Unit) sub);
                        result = true;
                    } catch (Exception e) {
                        main.system.auxiliary.log.LogMaster.log(LOG_CHANNEL.ERROR_CRITICAL,
                                "FAILED TO CONSTRUCT ANIMS FOR " + sub);
                        main.system.ExceptionMaster.printStackTrace(e);
                    }
            }
        }

        Chronos.logTimeElapsedForMark("preloadObjects");
        return result;
    }


    private static void preloadEmitters(EmitterMaster.VFX_ATLAS... atlases) {

        EmitterPools.init(get().getManager());
        if (EmitterPools.isPreloaded()) {
            Chronos.mark("preload EmitterPools");
            for (EmitterMaster.VFX_ATLAS value : atlases) {
                switch (value) {
                    case SPELL:
                    case AMBIENCE:
                    case INVERT:
                        get().getManager().load(EmitterMaster.getVfxAtlasPathFull(value), TextureAtlas.class);
                        // get().getManager().finishLoadingAsset(EmitterMaster.getVfxAtlasPathFull(value));
                        // EmitterMaster.getAtlas(value);
                        break;
                }
            }
            Chronos.logTimeElapsedForMark("preload EmitterPools");
        }
    }


    public static void preloadHeroes() {
        loadSprite(Sprites.getHeroSpritePath(Eidolons.getMainHero().getName()));
    }

    public static void preloadSounds() {
        for (GenericEnums.SOUND_CUE value : GenericEnums.SOUND_CUE.values()) {
            try {
                SoundPlayer.preload(value.getPath());
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
    }


    public static void preloadHero(Unit hero) {
        //gdx review
        if (hero.getName().contains("Anphis")) {
            preloadEmitters(EmitterMaster.VFX_ATLAS.SPELL);
        }
        AnimConstructor.preconstruct(hero);
    }


    public static void preloadScope(ASSET_GROUP scope) {
        for (String substring : ContainerUtils.openContainer(scope.assets)) {
            loadSprite(substring);
        }
    }

    public static boolean isLoaded(String path) {
        return get().getManager().isLoaded(path);
    }


    //only for things that do not go into atlases!
    public enum ASSET_GROUP {
        DC_COMMONS(ContainerUtils.construct(";",
                VisualEnums.FULLSCREEN_ANIM.BLOOD.getSpritePath())),

        VOID_MAZE_PUZZLE(ContainerUtils.construct(";", Sprites.BG_DEFAULT,
                VisualEnums.FULLSCREEN_ANIM.GATE_FLASH.getSpritePath())),
        ;

        ASSET_GROUP(String assets) {
            this.assets = assets;
        }

        public String assets;
    }

    public enum ASSET {
        ;
        public boolean loaded;
        ASSET_GROUP[] scopes;
        String path;
        int maxMemoryLevel;
        int memoryCost;
    }


    public static void dispose(ASSET_GROUP scope) {
        String[] paths = null;
        main.system.auxiliary.log.LogMaster.important("*********** Dispose Called for: " + scope);
        List<String> toClear = //new ArrayList<>(Arrays.asList(paths));
                ContainerUtils.openContainer(scope.assets);
        toClear = toClear.stream().map(PathUtils::getLastPathSegment).collect(Collectors.toList());
        main.system.auxiliary.log.LogMaster.important("*********** To Dispose: " + toClear);
        Array<TextureAtlas> array = new Array<>();
        for (TextureAtlas textureAtlas : get().getManager().getAll(TextureAtlas.class, array)) {
            String path = ((SmartTextureAtlas) textureAtlas).getPath();
            if (textureAtlas instanceof SmartTextureAtlas)
                if (toClear.contains(PathUtils.getLastPathSegment(path))) {
                    SpriteAnimationFactory.disposed(path);
                    textureAtlas.dispose();
                    main.system.auxiliary.log.LogMaster.important("*********** Atlas disposed: " + path);
                }
        }
    }

    //gdx revamp - assets are IMPORTANT
    public static boolean checkSprite(String path) {
        if (Flags.isUtility()) {
            return true;
        }
        if (path.contains("blotch")) {
            return true;
        }
        if (path.contains("fire light")) {
            return true;
        }
        switch (PathUtils.getFirstPathSegment(PathUtils.cropFirstPathSegment(path)).toLowerCase()) {
            case "fullscreen":
            case "particles":
                if (path.contains("atlas.txt")) {
                    return true;
                }
                if (path.contains("explode bright")) {
                    return true;
                }
                if (path.contains("blood")) {
                    return true;
                }
                return !OptionsMaster.getGraphicsOptions().getBooleanValue(GraphicsOptions.GRAPHIC_OPTION.LARGE_SPRITES_OFF);
            case "bf":
            case "cells":
                return !OptionsMaster.getGraphicsOptions().getBooleanValue(GraphicsOptions.GRAPHIC_OPTION.GRID_SPRITES_OFF);
            case "unit":
                return !OptionsMaster.getGraphicsOptions().getBooleanValue(GraphicsOptions.GRAPHIC_OPTION.UNIT_SPRITES_OFF);
            case "ui":
            case "hero":
                return !OptionsMaster.getGraphicsOptions().getBooleanValue(GraphicsOptions.GRAPHIC_OPTION.UI_SPRITES_OFF);


        }
        return true;
    }

    public static void loadSprite(String path) {
        assets.getManager().load(PathFinder.getImagePath() + path, TextureAtlas.class);
    }


    private static boolean checkPreloadUnit(BattleFieldObject sub) {
        if (CoreEngine.isFullLaunch()) {
            return true;
        }
        if (sub.getActiveWeapon(false) != null) {
            return sub.getActiveWeapon(false).getName().contains("Claw"); //quick hack
        }
        //what about those that are not yet spawned?
        return false;
    }


    public static String getKtxAtlasPath(String path) {
        return PathUtils.cropLastPathSegment(path) + "ktx/" +
                StringMaster.getAppendedFile(PathUtils.getLastPathSegment(path), " ktx");
    }

    public static String getScaledAtlasPath(String path) {
        return PathUtils.cropLastPathSegment(path) + "scaled/" +
                PathUtils.getLastPathSegment(path);
    }

    public static void loadedKtxAtlas(String texturePath) {
        texturePath = TextureCache.formatTexturePath(texturePath);
        ktxAtlases.add(texturePath);
        main.system.auxiliary.log.LogMaster.devLog(">>>>> loadedKtxAtlas " + texturePath);
    }

    public static boolean isKtx(String texturePath) {
        return ktxAtlases.contains(texturePath);
    }

    public static String getKtxImgPath(String path) {
        return PathUtils.cropLastPathSegment(path) + "ktx/" +
                StringMaster.cropFormat(PathUtils.getLastPathSegment(path)) +
                ".ktx";
    }


    public AssetManager getManager() {
        return manager;
    }

    public static Assets get() {
        if (assets == null)
            assets = new Assets();
        return assets;
    }

    public static boolean isOn() {
        return ON;
    }

}

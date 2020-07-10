package eidolons.libgdx.anims;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData.Page;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.core.Eidolons;
import eidolons.game.netherflame.additional.IGG_Demo;
import eidolons.game.netherflame.main.NF_Images;
import eidolons.libgdx.GDX;
import eidolons.libgdx.anims.construct.AnimConstructor;
import eidolons.libgdx.anims.fullscreen.FullscreenAnims.FULLSCREEN_ANIM;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.audio.SoundPlayer;
import eidolons.libgdx.bf.light.ShadeLightCell;
import eidolons.libgdx.gui.panels.dc.topleft.atb.INTENT_ICON;
import eidolons.libgdx.launch.GpuTester;
import eidolons.libgdx.launch.MainLauncher;
import eidolons.libgdx.particles.EmitterPools;
import eidolons.libgdx.particles.ParticleEffectX;
import eidolons.libgdx.particles.util.EmitterMaster;
import eidolons.libgdx.texture.SmartTextureAtlas;
import eidolons.libgdx.texture.Sprites;
import eidolons.libgdx.texture.TextureCache;
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
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.LOG_CHANNEL;
import main.system.auxiliary.secondary.ReflectionMaster;
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
    static Assets assets;
    private static final List<String> ktxAtlases = new ArrayList<>();
    private static final int estMemoryLoad = 0;
    private static final int memoryBuffer = 500;
    AssetManager manager;
    private final TextureAtlas dummyAtlas = new TextureAtlas(PathFinder.getImagePath() + "sprites/ui/dummy.txt");

    public static void setON(boolean ON) {
        Assets.ON = ON;
    }

    private Assets() {
        GuiEventManager.bind(GuiEventType.LOAD_SCOPE, p -> {
            try {
                preloadScope(new EnumMaster<GAME_SCOPE>().retrieveEnumConst(GAME_SCOPE.class, p.get().toString()),
                        false);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        });
        GuiEventManager.bind(GuiEventType.DISPOSE_SCOPE, p -> {
            try {
                dispose(new EnumMaster<GAME_SCOPE>().retrieveEnumConst(GAME_SCOPE.class, p.get().toString()));
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        });
        manager = new AssetManager() {
            @Override
            public synchronized boolean update() {
                return super.update();
            }

            @Override
            public synchronized boolean isLoaded(String fileName) {
                fileName = FileManager.formatPath(fileName, true, true);
                if (isKtx(fileName)) {
                    if (super.isLoaded(getKtxAtlasPath(fileName))) {
                        return true;
                    }
                }
                return super.isLoaded(fileName);
            }

            @Override
            public synchronized boolean isLoaded(String fileName, Class type) {
                fileName = FileManager.formatPath(fileName, true, true);

                if (isKtx(fileName)) {
                    if (super.isLoaded(getKtxAtlasPath(fileName), type)) {
                        return true;
                    }
                }
                return super.isLoaded(fileName, type);
            }

            @Override
            public synchronized <T> void load(String fileName, Class<T> type, AssetLoaderParameters<T> parameter) {
                fileName = FileManager.formatPath(fileName, true, true);
                super.load(fileName, type, parameter);
            }

            @Override
            public synchronized <T> T get(String fileName, Class<T> type) {
                fileName = FileManager.formatPath(fileName, true, true);
                if (isKtx(fileName)) {
                    main.system.auxiliary.log.LogMaster.devLog(">>>>> returning KtxAtlas " + fileName);
                    return super.get(getKtxAtlasPath(fileName), type);
                }
                return super.get(fileName, type);
            }

            @Override
            public synchronized <T> T get(String fileName) {
                fileName = FileManager.formatPath(fileName, true, true);
                if (isKtx(fileName)) {
                    main.system.auxiliary.log.LogMaster.devLog(">>>>> returning KtxAtlas " + fileName);
                    return super.get(getKtxAtlasPath(fileName));
                }
                return super.get(fileName);
            }
        };
        manager.setLogger(new Logger("Atlases", Logger.DEBUG));
        manager.setErrorListener(new AssetErrorListener() {
            @Override
            public void error(AssetDescriptor asset, Throwable throwable) {
                main.system.auxiliary.log.LogMaster.log(1, "Failed to load " + asset.fileName);
            }
        });
        if (EmitterPools.isPreloaded())
            manager.setLoader(ParticleEffect.class,
                    new ParticleEffectLoader(fileName -> GDX.file(fileName)) {
                        @Override
                        public ParticleEffect load(AssetManager am, String fileName,
                                                   FileHandle file, ParticleEffectParameter param) {
                            ParticleEffectX fx = createEmitter(file.path());
//                 ParticleEffect fx=super.load(am, fileName, file, param);
                            main.system.auxiliary.log.LogMaster.important(fileName + file.path() + " loaded!");
                            return fx;
                        }

                        private ParticleEffectX createEmitter(String path) {
                            return new ParticleEffectX(path);
                        }
                    });
        manager.setLoader(TextureAtlas.class, new TextureAtlasLoader(
                fileName -> GDX.file(fileName)
        ) {
            @Override
            public TextureAtlas load(AssetManager assetManager, String fileName, FileHandle file, TextureAtlasParameter parameter) {
//               super.load()
//                return atlas;
                TextureAtlasData data = new ReflectionMaster<TextureAtlasData>()
                        .getFieldValue("data", this, TextureAtlasLoader.class);
                for (Page page : data.getPages()) {
                    Texture texture = assetManager.get(page.textureFile.path().replaceAll("//", "/"), Texture.class);
                    page.texture = texture;
                }
                SmartTextureAtlas atlas = new SmartTextureAtlas(data);
                new ReflectionMaster<TextureAtlasData>()
                        .setValue("data", null, this);
                atlas.setPath(fileName);
                main.system.auxiliary.log.LogMaster.log(1, fileName + " loaded!");
                return atlas;
            }
        });

    }

    public static SmartTextureAtlas getAtlas() {
//        manager.load();
        return null;
    }

    public static Assets get() {
        if (assets == null)
            assets = new Assets();
        return assets;
    }

    public static boolean isOn() {
        return ON;
    }

    public static void preloadMenu() {
        //TODO ?
    }

    public static boolean preloadAll(DequeImpl<BattleFieldObject> objects) {
        return preload(objects, true, true, true, true);
    }

    public static boolean preloadAdditional(DequeImpl<BattleFieldObject> objects) {
        return preload(objects, false, true, true, true);
    }

    public static boolean preloadMain(DequeImpl<BattleFieldObject> objects) {
        return preload(objects, true, true, false, true);
    }

    public static boolean preloadModule(Module module) {
/*

 */

        return false;
    }
        public static boolean preload(DequeImpl<BattleFieldObject> objects,
        boolean full, boolean ui, boolean her0es, boolean emitters) {
        boolean result = preloadObjects(objects, full);
        her0es = EidolonsGame.FOOTAGE;

        if (isOptimizationTest()){
//            loadSprite("", false, true);
            return false;
        }
        if (!Flags.isIDE()) {
        Chronos.mark("preload Audio");
        try {
            preloadAudio(full);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        Chronos.logTimeElapsedForMark("preload Audio");
        }
        if (!Flags.isVfxOff()) {
            if (emitters) {
                Chronos.mark("preload Emitters");
                preloadEmitters();
                Chronos.logTimeElapsedForMark("preload Emitters");
            }
        }
        if (ui) {
            Chronos.mark("preload ui");
            preloadUI(full);
            Chronos.logTimeElapsedForMark("preload ui");
            result = true;
        }
        if (!EidolonsGame.FOOTAGE)
            if (her0es) {
                Chronos.mark("preload her0es");
                preloadHeroes(full);
                Chronos.logTimeElapsedForMark("preload her0es");
            }

        if (MainLauncher.BG != null) {
            loadSprite(MainLauncher.BG, false, false);
        }

        return result;
    }

    private static boolean isOptimizationTest() {
        return true;
    }

    private static boolean preloadObjects(DequeImpl<BattleFieldObject> objects, boolean full) {
        Chronos.mark("preloadObjects");
        boolean result = false;
        if (AnimConstructor.isPreconstructAllOnGameInit()) {
            for (BattleFieldObject sub : objects) {
                if (!checkPreloadUnit(sub, full)) {
                    continue;
                }
                if (sub instanceof Unit)
                    try {
                        AnimConstructor.preconstruct((Unit) sub);
                    } catch (Exception e) {
                        main.system.auxiliary.log.LogMaster.log(LOG_CHANNEL.ERROR_CRITICAL, "FAILED TO CONSTRUCT ANIMS FOR " + sub);
                        main.system.ExceptionMaster.printStackTrace(e);
                    }
            }
            result = true;
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
                        get().getManager().finishLoadingAsset(EmitterMaster.getVfxAtlasPathFull(value));
                        EmitterMaster.getAtlas(value);
                        break;
                }
            }
            Chronos.logTimeElapsedForMark("preload EmitterPools");
        }
    }


    public static void preloadHeroes(boolean full) {
        loadSprite(Sprites.getHeroSpritePath(Eidolons.getMainHero().getName()), full, false);
    }

    public static void preloadAudio(boolean full) {
        if (Flags.isSuperLite()) {
            return;
        }
        for (GenericEnums.SOUND_CUE value : GenericEnums.SOUND_CUE.values()) {
            try {
                SoundPlayer.preload(value.getPath());
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        switch (getScope()) {
            case DUEL:
                MusicMaster.getInstance().getMusic(

                        MusicMaster.MUSIC_TRACK.NIGHT_OF_DEMON.getFullPath(), true);
                break;
            case INTRO:
                MusicMaster.getInstance().getMusic(MusicMaster.AMBIENCE.EVIL.getPath(), true);
                break;
            case PUZZLES:
            case DIALOGUE:
            case COMMON:
                break;
        }


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


    public static void dispose(GAME_SCOPE scope) {
        String[] paths = null;
        main.system.auxiliary.log.LogMaster.important("*********** Dispose Called for: " + scope);
//        switch (scope) {
//            case DIALOGUE:
//                paths = new String[]{
//                        Sprites.AX_FIRE, Sprites.ACID_BLADE,
//                        FULLSCREEN_ANIM.HELLFIRE.getSpritePath(),
//                        FULLSCREEN_ANIM.TUNNEL.getSpritePath(),
//                        FULLSCREEN_ANIM.WAVE.getSpritePath(),
//                        FULLSCREEN_ANIM.GATE_FLASH.getSpritePath(),
//                        Sprites.BG_DEFAULT,
//                };
//                break;
//            case DUEL:
//                break;
//            case INTRO:
//                break;
//        }
        List<String> toClear = //new ArrayList<>(Arrays.asList(paths));
                ContainerUtils.openContainer(scope.assets);
        toClear = toClear.stream().map(t -> PathUtils.getLastPathSegment(t)).collect(Collectors.toList());
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

    public static void preloadHero(Unit hero) {
        //gdx review
        if (hero.getName().contains("Anphis")) {
            preloadEmitters(EmitterMaster.VFX_ATLAS.SPELL);
        }
    }

    public enum ASSET {
        ;
        public boolean loaded;
        GAME_SCOPE[] scopes;
        String path;
        int maxMemoryLevel;
        int memoryCost;
    }

    public static void preloadScope(GAME_SCOPE scope, boolean full) {
        for (String substring : ContainerUtils.openContainer(scope.assets)) {
            boolean ktx = false;
            loadSprite(substring, full, ktx);
        }


//        for (ASSET value : ASSET.values()) {
//            if (value.loaded) {
//                boolean ktx=true;
//                if (!isKtxTest() && checkMemory(value)) {
//                    ktx=false;
//                }
//                loadSprite(value.path, full, ktx);
//                if (ktx) {
//                    estMemoryLoad += value.memoryCost*getKtxCompressionFactor();
//                } else
//                     estMemoryLoad += value.memoryCost;
//
//            }
//        }
    }

    private static float getKtxCompressionFactor() {
        return 0.6f;
    }

    private static boolean checkMemory(ASSET value) {
        return
                getEstGpuMemoryLeft() - memoryBuffer > value.memoryCost;
    }

    private static Integer getEstGpuMemoryLeft() {
        return GpuTester.getDedicatedMemory() - estMemoryLoad;
    }


    public enum GAME_SCOPE {
        DUEL(ContainerUtils.construct(";", Sprites.BIG_CLAW_ATTACK, Sprites.BIG_CLAW_IDLE, Sprites.SMALL_CLAW_IDLE,
                Sprites.GHOST_FIST,
                Sprites.CLAWS,
                Sprites.KRIS,
                Sprites.FANGS,
                Sprites.ARMOR_FIST,

                FULLSCREEN_ANIM.BLOOD.getSpritePath()
                , Sprites.REAPER_SCYTHE
        )),

        INTRO(ContainerUtils.construct(";", Sprites.ACID_BLADE, Sprites.AX_FIRE, Sprites.BLOOD_SHOWER, Sprites.BONE_WINGS,
                FULLSCREEN_ANIM.HELLFIRE.getSpritePath(),
                FULLSCREEN_ANIM.TUNNEL.getSpritePath(),
                FULLSCREEN_ANIM.WAVE.getSpritePath(),
                FULLSCREEN_ANIM.GATE_FLASH.getSpritePath(),
                Sprites.ACID_BLADE, Sprites.ACID_BLADE)),

        PUZZLES(ContainerUtils.construct(";", Sprites.GATE_LIGHTNING, Sprites.ACID_BLADE, Sprites.ACID_BLADE, Sprites.ACID_BLADE,
                Sprites.ACID_BLADE, Sprites.ACID_BLADE, Sprites.ACID_BLADE, Sprites.ACID_BLADE)),

        COMMON(ContainerUtils.construct(";", Sprites.SNOW, Sprites.FLOAT_WISP,
                Sprites.INK_BLOTCH, Sprites.BG_DEFAULT, Sprites.FIRE_LIGHT, Sprites.WATER,
                FULLSCREEN_ANIM.EXPLOSION.getSpritePath(), Sprites.GATE_LIGHTNING,
                Sprites.PORTAL_OPEN, Sprites.PORTAL_CLOSE, Sprites.PORTAL, Sprites.WHITE_TENTACLE)),

        DIALOGUE(ContainerUtils.construct(";", Sprites.ACID_BLADE, Sprites.ACID_BLADE, Sprites.ACID_BLADE, Sprites.ACID_BLADE,
                Sprites.ACID_BLADE, Sprites.ACID_BLADE, Sprites.ACID_BLADE, Sprites.ACID_BLADE)),
        DEFAULT("");

        GAME_SCOPE(String assets) {
            this.assets = assets;
        }

        public String assets;
    }

    public static void preloadUI(boolean full) {
        ShadeLightCell.getShadowMapAtlas();
//        if (CoreEngine.isSuperLite())
        {
            if (isScopeLoadingMode()) {
//                if (!EidolonsGame.PUZZLES)
//                    preloadScope(GAME_SCOPE.COMMON, full);
                preloadScope(getScope(), full);
                return;
            }
        }
        boolean ktx = true;
        if (!isKtxTest() && (!GpuTester.isMeasured() || GpuTester.getDedicatedMemory() > 3000)) {
            ktx = false;
        }
        if (!EidolonsGame.DEMO) {
            loadSprite(Sprites.FIRE_LIGHT, full, ktx);
            return;
        }
        loadSprite(Sprites.SNOW, full, ktx);
        loadSprite(Sprites.BG_DEFAULT, full, ktx);
        if (!isKtxTest() && (!GpuTester.isMeasured() || GpuTester.getDedicatedMemory() > 2000)) {
            ktx = false;
        }
        loadSprite(FULLSCREEN_ANIM.EXPLOSION.getSpritePath(), full, ktx);

//        if (!isPreloadOn()) {
//            return;
//        }
        if (full) {
            if (!isKtxTest() && (!GpuTester.isMeasured() || GpuTester.getDedicatedMemory() > 1000)) {
                ktx = false;
            }
//            loadSprite(Sprites.MIST, full, ktx);

            ktx = false;

            loadSprite(Sprites.COMMENT_KESERIM, full, ktx);
            loadSprite(Sprites.ORB, full, ktx);
            loadSprite(Sprites.RUNE_INSCRIPTION, full, ktx);
            loadSprite(Sprites.WATER, full, ktx);
            loadSprite(Sprites.FIRE_LIGHT, full, ktx);
            loadSprite(Sprites.FLOAT_WISP, full, ktx);
            loadSprite(Sprites.WHITE_TENTACLE, full, ktx);
            loadSprite(Sprites.BONE_WINGS, full, ktx);
            loadSprite(Sprites.LIGHT_VEIL, full, ktx);
            loadSprite(Sprites.BIG_CLAW_ATTACK, full, ktx);
            loadSprite(Sprites.BIG_CLAW_IDLE, full, ktx);
            loadSprite(Sprites.SMALL_CLAW_IDLE, full, ktx);
            loadSprite(Sprites.GHOST_FIST, full, ktx);
            loadSprite(FULLSCREEN_ANIM.BLOOD.getSpritePath(), full, ktx);

            if (!Flags.isSuperLite()) {
                loadSprite("sprites/weapons3d/atlas/pole arm/scythes/reaper scythe.txt", full, ktx);
            }
//            loadSprite(FullscreenAnims.FULLSCREEN_ANIM.BLOOD_SCREEN.getSpritePath(), full);

            if (EidolonsGame.DUEL_TEST) {
                loadSprite(FULLSCREEN_ANIM.WAVE.getSpritePath(), full, ktx);
                loadSprite(FULLSCREEN_ANIM.BLOOD.getSpritePath(), full, ktx);
            } else {
                loadSprite(Sprites.PORTAL_OPEN, full, ktx);
                loadSprite(Sprites.PORTAL, full, ktx);
                loadSprite(Sprites.PORTAL_CLOSE, full, ktx);

                loadSprite(FULLSCREEN_ANIM.TUNNEL.getSpritePath(), full, ktx);
                loadSprite(FULLSCREEN_ANIM.WAVE.getSpritePath(), full, ktx);

                loadSprite(Sprites.ACID_BLADE, full, ktx);
                loadSprite(Sprites.AX_FIRE, full, ktx);
                if (!CoreEngine.isMyLiteLaunch()) {
                    for (INTENT_ICON value : INTENT_ICON.values()) {
                        loadSprite(value.getPath(), full, ktx);
                    }
//                    loadSprite(FULLSCREEN_ANIM.HELLFIRE.getSpritePath(), full, ktx);
//                    loadSprite(FULLSCREEN_ANIM.GATE_FLASH.getSpritePath(), full, ktx);
                }
            }


        }


        if (EidolonsGame.BRIDGE) {
            return;
        }
//        if (!CoreEngine.isLiteLaunch())
//            loadSprite(Sprites.RADIAL, full, ktx);

        loadSprite(NF_Images.getBackground(IGG_Demo.IGG_MISSION.ACT_I_MISSION_I), full, ktx);
        //locks
        // blood
        //boss
    }

    private static GAME_SCOPE getScope() {
        return GAME_SCOPE.DEFAULT;
    }

    private static boolean isScopeLoadingMode() {
        return true;
    }

    private static boolean isKtxTest() {
        return false;
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

    public static void loadSprite(String path, boolean full, boolean ktx) {

//        if (checkReducedSprite(path)){
//            reduced =true;
//        }
        if (isAsyncLoad(full)) {
            assets.getManager().load(PathFinder.getImagePath() + path, TextureAtlas.class);
        } else {
            SpriteAnimationFactory.getSpriteAnimation(path, false, true, ktx);
        }
    }

    private static boolean isAsyncLoad(boolean full) {
        return full;//!CoreEngine.isSuperLite()&&
    }


    private static boolean checkPreloadUnit(BattleFieldObject sub, boolean full) {
        if (sub == Eidolons.getMainHero()) {
            return true;
        }
        return Eidolons.getPlayerCoordinates().dst(sub.getCoordinates()) <= (full ? 30 : 10);
    }


    public AssetManager getManager() {
        return manager;
    }
}

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
import com.badlogic.gdx.utils.Logger;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.meta.igg.IGG_Demo;
import eidolons.game.battlecraft.logic.meta.igg.IGG_Images;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueManager;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GDX;
import eidolons.libgdx.anims.construct.AnimConstructor;
import eidolons.libgdx.anims.fullscreen.FullscreenAnims;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.gui.panels.dc.atb.AtbPanel;
import eidolons.libgdx.particles.EmitterPools;
import eidolons.libgdx.particles.util.EmitterMaster;
import eidolons.libgdx.particles.util.EmitterPresetMaster;
import eidolons.libgdx.particles.ParticleEffectX;
import eidolons.libgdx.texture.SmartTextureAtlas;
import eidolons.libgdx.texture.Sprites;
import eidolons.system.audio.MusicMaster;
import main.content.enums.GenericEnums;
import main.data.filesys.PathFinder;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.LOG_CHANNEL;
import main.system.auxiliary.secondary.ReflectionMaster;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;

/**
 * Created by JustMe on 12/1/2017.
 */
public class Assets {
    private static boolean ON = true;
    static Assets assets;
    AssetManager manager;
    private TextureAtlas dummyAtlas = new TextureAtlas(PathFinder.getImagePath() + "sprites/ui/dummy.txt");

    public static void setON(boolean ON) {
        Assets.ON = ON;
    }

    private Assets() {
        manager = new AssetManager() {
            @Override
            public synchronized boolean update() {
                return super.update();
            }

            @Override
            public synchronized boolean isLoaded(String fileName) {
                if (fileName.contains(".txt"))
                    if (!fileName.contains("vfx")) {
                        if (CoreEngine.isSuperLite()) {
                            return true;
                        }
                    }
                fileName = FileManager.formatPath(fileName, true, true);
                return super.isLoaded(fileName);
            }

            @Override
            public synchronized boolean isLoaded(String fileName, Class type) {
                if (type.getName().contains("TextureAtlas")) {
                    if (CoreEngine.isSuperLite()) {
                        return true;
                    }
                }
                fileName = FileManager.formatPath(fileName, true, true);
                return super.isLoaded(fileName, type);
            }

            @Override
            public synchronized <T> void load(String fileName, Class<T> type, AssetLoaderParameters<T> parameter) {
                if (type.getName().contains("TextureAtlas")) {
                    if (CoreEngine.isSuperLite()) {
                        return;
                    }
                }
                fileName = FileManager.formatPath(fileName, true, true);
                super.load(fileName, type, parameter);
            }

            @Override
            public synchronized <T> T get(String fileName, Class<T> type) {
                if (type.getName().contains("TextureAtlas")) {
                    if (CoreEngine.isSuperLite()) {
                        return (T) dummyAtlas;
                    }
                }
                fileName = FileManager.formatPath(fileName, true, true);
                return super.get(fileName, type);
            }

            @Override
            public synchronized <T> T get(String fileName) {
                if (fileName.contains(".txt"))
                    if (!fileName.contains("vfx")) {
                        if (CoreEngine.isSuperLite()) {
                            return (T) dummyAtlas;
                        }
                    }
                fileName = FileManager.formatPath(fileName, true, true);
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
                TextureAtlas atlas = new SmartTextureAtlas(data);
                new ReflectionMaster<TextureAtlasData>()
                        .setValue("data", null, this);
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
        return preload(objects, true, true, true, true);
    }

    public static boolean preloadMain(DequeImpl<BattleFieldObject> objects) {
        return preload(objects, false, true, false, true);
    }

    public static boolean preload(DequeImpl<BattleFieldObject> objects,
                                  boolean full, boolean ui, boolean her0es, boolean emitters) {
        boolean result = preloadObjects(objects, full);

        Chronos.mark("preload Audio");
        preloadAudio(full);
        Chronos.logTimeElapsedForMark("preload Audio");

        if (emitters) {
            preloadEmitters();
        }

        if (ui) {
            Chronos.mark("preload ui");
            preloadUI(full);
            Chronos.logTimeElapsedForMark("preload ui");
            result = true;
        }

        if (her0es) {
            Chronos.mark("preload her0es");
            preloadHeroes(full);
            Chronos.logTimeElapsedForMark("preload her0es");
        }

        return result;
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
                        continue;
                    }
            }
            result = true;
        }

        Chronos.logTimeElapsedForMark("preloadObjects");
        return result;
    }

    private static void preloadEmitters() {
        EmitterPresetMaster.getInstance().init();

        EmitterPools.init(get().getManager());
        if (EmitterPools.isPreloaded()) {
            Chronos.mark("preload EmitterPools");
            for (EmitterMaster.VFX_ATLAS value : EmitterMaster.VFX_ATLAS.values()) {
                switch (value) {
                    case SPELL:
                    case AMBIENCE:
                    case INVERT:
                        get().getManager().load(EmitterMaster.getVfxAtlasPathFull(value), TextureAtlas.class);
                        break;
                }
            }
            Chronos.logTimeElapsedForMark("preload EmitterPools");
        }
    }


    public static void preloadHeroes(boolean full) {
        loadSprite(PathFinder.getSpritesPath()
                + "hero/" + Eidolons.getMainHero().getName() + ".txt", full);
    }

    public static void preloadAudio(boolean full) {
        if (DialogueManager.TEST) {
            return;
        }
        for (GenericEnums.SOUND_CUE value : GenericEnums.SOUND_CUE.values()) {
            try {
                MusicMaster.getInstance().getMusic(value.getPath(), true);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        MusicMaster.getInstance().getMusic(MusicMaster.AMBIENCE.EVIL.getPath(), true);
    }

    public static void preloadUI(boolean full) {
        if (CoreEngine.isSuperLite())
            return;
        loadSprite(Sprites.INK_BLOTCH, full);
        loadSprite(Sprites.SNOW, full);
        loadSprite(FullscreenAnims.FULLSCREEN_ANIM.EXPLOSION.getSpritePath(), full);
//        if (DialogueManager.TEST)
//            return;
        if (full) {
            loadSprite(Sprites.ORB, full);
            loadSprite(Sprites.RUNE_INSCRIPTION, full);
            loadSprite(Sprites.WATER, full);
            loadSprite(Sprites.FIRE_LIGHT, full);
            loadSprite(Sprites.FLOAT_WISP, full);
            loadSprite(Sprites.WHITE_TENTACLE, full);
            loadSprite(Sprites.BONE_WINGS, full);
            loadSprite(Sprites.LIGHT_VEIL, full);
            loadSprite("sprites/weapons3d/atlas/screen/ghost/ghost fist.txt", full);
//            loadSprite(Sprites.COMMENT_KESERIM, full);

            loadSprite(FullscreenAnims.FULLSCREEN_ANIM.BLOOD.getSpritePath(), full);
//            loadSprite(FullscreenAnims.FULLSCREEN_ANIM.BLOOD_SCREEN.getSpritePath(), full);

            if (!DialogueManager.TEST) {
                loadSprite(Sprites.BG_DEFAULT, full);
                loadSprite(Sprites.PORTAL_OPEN, full);
                loadSprite(Sprites.PORTAL, full);
                loadSprite(Sprites.PORTAL_CLOSE, full);

                loadSprite(Sprites.MIST, full);
                loadSprite(FullscreenAnims.FULLSCREEN_ANIM.TUNNEL.getSpritePath(), full);
                loadSprite(FullscreenAnims.FULLSCREEN_ANIM.WAVE.getSpritePath(), full);

                loadSprite(Sprites.ACID_BLADE, full);
                loadSprite(Sprites.AX_FIRE, full);
            }
            if (!CoreEngine.isMyLiteLaunch()) {
                for (AtbPanel.INTENT_ICON value : AtbPanel.INTENT_ICON.values()) {
                    loadSprite(value.getPath(), full);
                }
                loadSprite(FullscreenAnims.FULLSCREEN_ANIM.HELLFIRE.getSpritePath(), full);
                loadSprite(FullscreenAnims.FULLSCREEN_ANIM.GATE_FLASH.getSpritePath(), full);
            }


        }


        if (EidolonsGame.BRIDGE) {
            return;
        }
        loadSprite(Sprites.RADIAL, full);
        loadSprite(IGG_Images.getBackground(IGG_Demo.IGG_MISSION.ACT_I_MISSION_I), full);
        //locks
        // blood
        //boss
    }

    private static void loadSprite(String path, boolean full) {
//        if (full) {
//            assets.getManager().load(PathFinder.getImagePath() + spritePaths, TextureAtlas.class);
//        } else
        {
            SpriteAnimationFactory.getSpriteAnimation(path, false);
        }
    }


    private static boolean checkPreloadUnit(BattleFieldObject sub, boolean full) {
        if (sub == Eidolons.getMainHero()) {
            return true;
        }
        if (Eidolons.getMainHero().getCoordinates().dst(sub.getCoordinates()) > (full ? 30 : 10)) {
            return false;
        }
        return true;
    }


    public AssetManager getManager() {
        return manager;
    }
}

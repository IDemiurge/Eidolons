package eidolons.libgdx.anims;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData.Page;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.igg.IGG_Demo;
import eidolons.game.battlecraft.logic.meta.igg.IGG_Images;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GDX;
import eidolons.libgdx.anims.construct.AnimConstructor;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.particles.EmitterPools;
import eidolons.libgdx.particles.util.EmitterPresetMaster;
import eidolons.libgdx.particles.ParticleEffectX;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.texture.SmartTextureAtlas;
import eidolons.libgdx.texture.Sprites;
import main.data.filesys.PathFinder;
import main.system.auxiliary.log.LOG_CHANNEL;
import main.system.auxiliary.secondary.ReflectionMaster;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;

/**
 * Created by JustMe on 12/1/2017.
 */
public class Assets {
    static Assets assets;
    AssetManager manager;

    private Assets() {
        manager = new AssetManager();
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
                     main.system.auxiliary.log.LogMaster.log(1, fileName + file.path() + " loaded!");
                     return fx;
                 }

                 private ParticleEffectX createEmitter(String path) {
                    return  new ParticleEffectX(path);
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
        return true;
    }

    public static boolean preloadAll(DequeImpl<BattleFieldObject> objects) {
        boolean result = false;
        if (AnimConstructor.isPreconstructAllOnGameInit()) {
            for (BattleFieldObject sub : objects) {
                if (!checkPreloadUnit(sub)) {
                    continue;
                }
                if (sub instanceof Unit)
                    try {
                         AnimConstructor.preconstruct((Unit) sub);
                    } catch (Exception e) {
                         main.system.auxiliary.log.LogMaster.log(LOG_CHANNEL.ERROR_CRITICAL,"FAILED TO CONSTRUCT ANIMS FOR " +sub);
                        main.system.ExceptionMaster.printStackTrace(e);
                        continue;
                    }
            }
            result = true;
        }
        EmitterPresetMaster.getInstance().init();
        EmitterPools.init(get().getManager());

        if (isPreloadUI()){
            preloadUI();
        }

        if (isPreloadHeroes()){
            preloadHeroes();
        }

        return result;
    }

    public static void preloadHeroes() {
        SpriteAnimationFactory.getSpriteAnimation(PathFinder.getSpritesPathNew()
                + "unit/" + Eidolons.getMainHero().getName() + ".txt", false);
    }

    public static void preloadUI() {
        SpriteAnimationFactory.getSpriteAnimation(Sprites.RADIAL, false);
        SpriteAnimationFactory.getSpriteAnimation(Sprites.SHADOW_DEATH, false);
        SpriteAnimationFactory.getSpriteAnimation(Sprites.SHADOW_SUMMON, false);
        SpriteAnimationFactory.getSpriteAnimation(IGG_Images.getBackground(IGG_Demo.IGG_MISSION.ACT_I_MISSION_I), false);
        //locks
        // blood
        //boss
    }

    private static boolean isPreloadUI() {
        return !CoreEngine.isLiteLaunch();
    }

    private static boolean isPreloadHeroes() {
        return false;
    }

    private static boolean checkPreloadUnit(BattleFieldObject sub) {
        if (Eidolons.getMainHero().getCoordinates().dst(sub.getCoordinates())>30) {
            return false;
        }
        return true;
    }

    public AssetManager getManager() {
        return manager;
    }
}

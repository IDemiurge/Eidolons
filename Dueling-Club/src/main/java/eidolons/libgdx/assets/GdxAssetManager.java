package eidolons.libgdx.assets;

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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import eidolons.libgdx.GDX;
import eidolons.libgdx.particles.EmitterPools;
import eidolons.libgdx.particles.ParticleEffectX;
import eidolons.libgdx.texture.SmartTextureAtlas;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.log.FileLogManager;
import main.system.auxiliary.secondary.ReflectionMaster;

class GdxAssetManager extends AssetManager {
    public GdxAssetManager() {
        super(
                fileName -> GDX.file(fileName));
        setLogger(new Logger("Atlases", Logger.DEBUG));
        setErrorListener(new AssetErrorListener() {
            @Override
            public void error(AssetDescriptor asset, Throwable throwable) {
                main.system.auxiliary.log.LogMaster.log(1, "Failed to load " + asset.fileName);
            }
        });

        if (EmitterPools.isPreloaded())
            setLoader(ParticleEffect.class, createParticleEffectLoader());

        setLoader(TextureAtlas.class,
                createTextureAtlasLoader());
    }

    private ParticleEffectLoader createParticleEffectLoader() {
        return new ParticleEffectLoader(getFileHandleResolver()) {
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
        };
    }
    private TextureAtlasLoader createTextureAtlasLoader() {
        return new TextureAtlasLoader(getFileHandleResolver()
        ) {

            @Override
            public TextureAtlas load(AssetManager assetManager, String fileName, FileHandle file, TextureAtlasParameter parameter) {
                TextureAtlas.TextureAtlasData data = new ReflectionMaster<TextureAtlas.TextureAtlasData>()
                        .getFieldValue("data", this, TextureAtlasLoader.class);
                for (TextureAtlas.TextureAtlasData.Page page : data.getPages()) {
                    Texture texture = assetManager.get(page.textureFile.path().replaceAll("//", "/"), Texture.class);
                    page.texture = texture;
                }
                SmartTextureAtlas atlas = new SmartTextureAtlas(data);
                new ReflectionMaster<TextureAtlas.TextureAtlasData>()
                        .setValue("data", null, this);
                atlas.setPath(fileName);
                main.system.auxiliary.log.LogMaster.log(1, fileName + " loaded!");
                FileLogManager.stream(FileLogManager.LOG_OUTPUT.MAIN,  fileName + " loaded!");

                Atlases.loaded(fileName, atlas);
                return atlas;
            }

            @Override
            public Array<AssetDescriptor> getDependencies(String fileName, FileHandle atlasFile, TextureAtlasParameter parameter) {
                return super.getDependencies(fileName, atlasFile, parameter);
            }
        };
    }

    @Override
    public synchronized boolean update() {
        return super.update();
    }

    @Override
    public synchronized boolean isLoaded(String fileName) {
        fileName = FileManager.formatPath(fileName, true, true);
        if (Assets.isKtx(fileName)) {
            if (super.isLoaded(Assets.getKtxAtlasPath(fileName))) {
                return true;
            }
        }
        return super.isLoaded(fileName);
    }

    @Override
    public synchronized boolean isLoaded(String fileName, Class type) {
        fileName = FileManager.formatPath(fileName, true, true);

        if (Assets.isKtx(fileName)) {
            if (super.isLoaded(Assets.getKtxAtlasPath(fileName), type)) {
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
        if (Assets.isKtx(fileName)) {
            main.system.auxiliary.log.LogMaster.devLog(">>>>> returning KtxAtlas " + fileName);
            return super.get(Assets.getKtxAtlasPath(fileName), type);
        }
        return super.get(fileName, type);
    }

    @Override
    public synchronized <T> T get(String fileName) {
        fileName = FileManager.formatPath(fileName, true, true);
        if (Assets.isKtx(fileName)) {
            main.system.auxiliary.log.LogMaster.devLog(">>>>> returning KtxAtlas " + fileName);
            return super.get(Assets.getKtxAtlasPath(fileName));
        }
        return super.get(fileName);
    }
}

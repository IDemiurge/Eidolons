package main.libgdx.anims;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData.Page;
import main.libgdx.texture.SmartTextureAtlas;
import main.system.auxiliary.secondary.ReflectionMaster;

/**
 * Created by JustMe on 12/1/2017.
 */
public class Assets {
    static Assets assets;
    AssetManager manager;

    private Assets() {
        manager = new AssetManager();
        manager.setLoader(TextureAtlas.class, new TextureAtlasLoader(
         new FileHandleResolver() {
             @Override
             public FileHandle resolve(String fileName) {
                 return new FileHandle(fileName);
             }
         }
        ) {
            @Override
            public TextureAtlas load(AssetManager assetManager, String fileName, FileHandle file, TextureAtlasParameter parameter) {
//               super.load()
//                return atlas;
                TextureAtlasData data = new ReflectionMaster<TextureAtlasData>()
                 .getFieldValue("data", this, TextureAtlasLoader.class);
                for (Page page : data.getPages()) {
                    Texture texture = assetManager.get(page.textureFile.path().replaceAll("\\\\", "/"), Texture.class);
                    page.texture = texture;
                }
                TextureAtlas atlas = new SmartTextureAtlas(data);
                new ReflectionMaster<TextureAtlasData>()
                 .setValue("data", null, this);
                main.system.auxiliary.log.LogMaster.log(1, fileName + " loaded...");
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

    public AssetManager getManager() {
        return manager;
    }
}

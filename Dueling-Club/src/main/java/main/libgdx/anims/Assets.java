package main.libgdx.anims;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import main.libgdx.texture.SmartTextureAtlas;

/**
 * Created by JustMe on 12/1/2017.
 */
public class Assets {
   static Assets assets;
    AssetManager manager;

    private  Assets() {
        manager = new AssetManager();
        manager.setLoader( TextureAtlas.class, new TextureAtlasLoader(
         new FileHandleResolver() {
             @Override
             public FileHandle resolve(String fileName) {
                 return new FileHandle(fileName);
             }
         }
        ){
            @Override
            public TextureAtlas load (AssetManager assetManager, String fileName, FileHandle file, TextureAtlasParameter parameter) {
                TextureAtlas atlas = new SmartTextureAtlas(fileName);
                return atlas;
            }
        });

    }

    public static SmartTextureAtlas getAtlas() {
//        manager.load();
        return null;
    }
        public static Assets get() {
        if (assets==null )
            assets = new Assets();
        return assets;
    }

    public AssetManager getManager() {
        manager.update();
        return manager;
    }

    public static boolean isOn() {
        return false;
    }
}

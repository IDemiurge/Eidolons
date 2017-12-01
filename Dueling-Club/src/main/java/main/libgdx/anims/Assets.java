package main.libgdx.anims;

import com.badlogic.gdx.assets.AssetManager;

/**
 * Created by JustMe on 12/1/2017.
 */
public class Assets {
   static Assets assets;
    AssetManager manager;

    private  Assets() {
        manager = new AssetManager();
    }

    public static Assets get() {
        if (assets==null )
            assets = new Assets();
        return assets;
    }

    public AssetManager getManager() {
        return manager;
    }
}

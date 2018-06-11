package eidolons.libgdx.anims.particles;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.anims.particles.Emitter.EMITTER_VALS_SCALED;
import eidolons.libgdx.anims.particles.EmitterPresetMaster.EMITTER_VALUE_GROUP;
import eidolons.libgdx.texture.TextureCache;
import eidolons.system.utils.GdxUtil;
import main.content.CONTENT_CONSTS2.EMITTER_PRESET;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.launch.CoreEngine;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 3/16/2018.
 * <p>
 * <p>
 * Fullscreen emitter transform
 * <p>
 * emitter mods
 * <p>
 * move emitters (add gdx behavior?) steerableEmitter...
 */
public class EmitterMaster extends GdxUtil {
    static Map<String, String> map = new HashMap<>();
    public void applyMod(String filter, String path, EMITTER_VALS_SCALED val, float mod) {

    }

    public void applyMod(ParticleEffectX effect) {

//        EmitterPresetMaster.getInstance().getModifiedEmitter()

    }

    public static void createVfxAtlas() {
        for (EMITTER_PRESET sub : EMITTER_PRESET.values()) {
            String imagePath = EmitterPresetMaster.getInstance().findImagePath(sub.path);
            Texture t = TextureCache.getOrCreate(imagePath);

            String newPath = map.get(imagePath);
            if (newPath == null)
                newPath =getVfxAtlasPath(sub) + sub.name() + ".png";
            FileHandle handle= new FileHandle(newPath);
            map.put(imagePath, newPath);

            GdxImageMaster.writeImage(handle, t);

            String data = EmitterPresetMaster.getInstance().getModifiedData(sub.path,
             EMITTER_VALUE_GROUP.Image_Path, StringMaster.getLastPathSegment(newPath));
            FileManager.write(sub.path,data );
        }
    }


    public static void main(String[] args) {
        CoreEngine.systemInit();
        new EmitterMaster().start();
    }
    private static String getVfxAtlasPath(EMITTER_PRESET sub) {
        String name="main.atlas";
        switch (sub) {
            //TODO some cases other name
        }
        return PathFinder.getVfxPath()+"atlases"+StringMaster.getPathSeparator()+name;
    }
    @Override
    protected void execute() {
        createVfxAtlas();
    }

    public enum VFX_TEMPLATE {
        CENTER, SWIRL, FADE, FLOW,
        MISSILE, WHIRL,
    }
}

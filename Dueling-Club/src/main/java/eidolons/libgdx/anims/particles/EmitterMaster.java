package eidolons.libgdx.anims.particles;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.anims.particles.Emitter.EMITTER_VALS_SCALED;
import eidolons.libgdx.anims.particles.EmitterPresetMaster.EMITTER_VALUE_GROUP;
import eidolons.libgdx.texture.TexturePackerLaunch;
import eidolons.system.utils.GdxUtil;
import main.content.CONTENT_CONSTS2.EMITTER_PRESET;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StrPathBuilder;
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

    public static void createVfxAtlas() {
        String atlasPath = null;
        String atlasName = null;
        String imagesPath = null;
        for (EMITTER_PRESET sub : EMITTER_PRESET.values()) {
            String imagePath = EmitterPresetMaster.getInstance().findImagePath(sub.path);
            Texture t = null;
            try {
                t = new Texture(imagePath);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                continue;
            }

            String newPath = map.get(imagePath);
            imagesPath = getVfxAtlasImagesPath(sub);
            atlasPath = getVfxAtlasPath(sub);
            atlasName = getVfxAtlasName(sub);
            if (newPath == null)
                newPath = imagesPath + StringMaster.getPathSeparator()+ sub.name() + ".png";
            FileHandle handle = new FileHandle(newPath);
            map.put(imagePath, newPath);
            try {
                GdxImageMaster.writeImage(handle, t);

                String data = EmitterPresetMaster.getInstance().getModifiedData(sub.path,
                 EMITTER_VALUE_GROUP.Image_Path, StringMaster.getLastPathSegment(newPath));

                FileManager.write(data, atlasPath + StringMaster.getPathSeparator() + atlasName);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        TexturePackerLaunch.pack(imagesPath, atlasPath, atlasName);
    }

    public static void main(String[] args) {
        CoreEngine.systemInit();
        new EmitterMaster().start();
    }

    private static String getVfxAtlasImagesPath(EMITTER_PRESET sub) {
        String name = "main";
        switch (sub) {
            //TODO some cases other name
        }
        return
         StrPathBuilder.build(PathFinder.getVfxPath() + "atlases", name, "images");

    }

    private static String getVfxAtlasPathFull(EMITTER_PRESET sub) {
        String name = "main";
        switch (sub) {
            //TODO some cases other name
        }
        return StrPathBuilder.build(PathFinder.getVfxPath() + "atlases", name, name + ".atlas");
    }

    private static String getVfxAtlasPath(EMITTER_PRESET sub) {
        String name = getVfxAtlasName(sub);
        switch (sub) {
            //TODO some cases other name
        }
        return StrPathBuilder.build(PathFinder.getVfxPath() + "atlases", name);
    }

    private static String getVfxAtlasName(EMITTER_PRESET sub) {
        return "main";
    }

    @Override
    protected boolean isExitOnDone() {
        return false;
    }

    public void applyMod(String filter, String path, EMITTER_VALS_SCALED val, float mod) {

    }

    public void applyMod(ParticleEffectX effect) {

        //        EmitterPresetMaster.getInstance().getModifiedEmitter()

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

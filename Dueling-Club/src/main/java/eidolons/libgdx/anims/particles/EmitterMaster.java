package eidolons.libgdx.anims.particles;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.anims.particles.Emitter.EMITTER_VALS_SCALED;
import eidolons.libgdx.anims.particles.EmitterPresetMaster.EMITTER_VALUE_GROUP;
import eidolons.libgdx.texture.TexturePackerLaunch;
import eidolons.system.utils.GdxUtil;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.launch.CoreEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public static final String ATLAS_VFX_PREFIX = "atlas";
    static Map<String, String> map = new HashMap<>();
    static Map<String, String> broken = new HashMap<>();
    private static TextureAtlas atlas;
    private static boolean writeImage = false;
    private static boolean pack = false;
static String[] exceptions= {
 "custom",
};
    public static void createVfxAtlas() {
        String atlasPath = null;
        String atlasName = null;
        String imagesPath = null;
        for (File sub : FileManager.getFilesFromDirectory(PathFinder.getVfxPath(), false, true)) {
            String path = sub.getPath().toLowerCase().replace(PathFinder.getVfxPath().toLowerCase(), "");

            for (String s: exceptions){
                if (path.contains(s))
                    continue;
            }

            String imagePath = EmitterPresetMaster.getInstance().findImagePath(path);
            List<Texture> t = new ArrayList<>();

            String imagesData="";
            if (writeImage)
                try {
                    for (String s : StringMaster.openContainer(imagePath, "\n")) {
                        t.add(new Texture(s));
                        imagesData+= StringMaster.getLastPathSegment(s)+"\n";
                    }
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                    broken.put(path, imagePath);
                    continue;
                }

            imagesPath = getVfxAtlasImagesPath();
            atlasPath = getVfxAtlasPath();
            atlasName = getVfxAtlasName();

            try {
                if (writeImage)
                    for (String s : StringMaster.openContainer(imagePath)) {
                        Texture texture = (new Texture(s));
                        String imageName=StringMaster.getLastPathSegment(s);
                        imagesData+= imageName+"\n";

                        String newPath = map.get(imageName);
                        if (newPath == null) {
                            newPath = imagesPath + StringMaster.getPathSeparator() + imageName  ;
                            map.put(imageName, newPath);
                        }
                        FileHandle handle = new FileHandle(newPath);
                        GdxImageMaster.writeImage(handle, texture);
                    }

                String data = EmitterPresetMaster.getInstance().getModifiedData(path,
                 EMITTER_VALUE_GROUP.Image_Path,imagesData);

                FileManager.write(data, PathFinder.getVfxPath() + StringMaster.getPathSeparator() + "atlas" + StringMaster.getPathSeparator() + path);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                broken.put(path, imagePath);
                continue;
            }
        }
        main.system.auxiliary.log.LogMaster.log(1, broken.size() + " vfx imagepath broken: " + broken);

        if (pack)
            TexturePackerLaunch.pack(imagesPath, atlasPath, atlasName);
        System.exit(0);
    }

    public static void main(String[] args) {
        CoreEngine.systemInit();
        new EmitterMaster().start();
    }

    private static String getVfxAtlasImagesPath() {
        String name = "main";
        return
         StrPathBuilder.build(PathFinder.getVfxPath() + "atlases", name);

    }

    private static String getVfxAtlasPathFull() {
        String name = getVfxAtlasName();
        //        switch (sub) {
        //            //TODO some cases other name
        //        }
        return StrPathBuilder.build(PathFinder.getVfxPath() + "atlases", name, name + TexturePackerLaunch.ATLAS_EXTENSION);
    }

    private static String getVfxAtlasPath() {
        String name = getVfxAtlasName();
        return StrPathBuilder.build(PathFinder.getVfxPath() + "atlases", name);
    }

    private static String getVfxAtlasName() {
        return "main";
    }

    public static TextureAtlas getAtlas(String path) {
        if (atlas == null) {
            atlas = new TextureAtlas(getVfxAtlasPathFull()); //  Assets.get().getManager().get
        }
        return atlas;
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

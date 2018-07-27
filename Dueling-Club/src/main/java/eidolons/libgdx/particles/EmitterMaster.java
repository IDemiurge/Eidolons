package eidolons.libgdx.particles;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.particles.Emitter.EMITTER_VALS_SCALED;
import eidolons.libgdx.particles.EmitterPresetMaster.EMITTER_VALUE_GROUP;
import eidolons.libgdx.texture.TexturePackerLaunch;
import eidolons.system.utils.GdxUtil;
import main.data.filesys.PathFinder;
import main.system.PathUtils;
import main.system.auxiliary.ContainerUtils;
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
    private static boolean writeImage = true;
    private static boolean pack = true;
    private static boolean overwriteImage=false;
    private static boolean sizeImages=false;
    static String[] exceptions= {
 "custom","atlas", "atlases", "broken", "templates", "workspace", "export", "target"
};
    private static final int MAX_IMAGE_SIZE=200;

    public static void createVfxAtlas() {
        String atlasPath = null;
        String atlasName = null;
        String imagesPath = null;
       main: for (File sub : FileManager.getFilesFromDirectory(PathFinder.getVfxPath(), false, true)) {
            String path = sub.getPath().toLowerCase().replace(PathFinder.getVfxPath().toLowerCase(), "");

            for (String s: exceptions){
                if (path.contains(s))
                    continue main;
            }

            String imagePath = EmitterPresetMaster.getInstance().findImagePath(path);
            List<Texture> t = new ArrayList<>();

            if (writeImage)
                try {
                    for (String s : ContainerUtils.openContainer(imagePath, "\n")) {
                        Texture texture = new Texture(s);
                     if (sizeImages)
                         if (texture.getHeight()>MAX_IMAGE_SIZE || texture.getWidth()>MAX_IMAGE_SIZE)
                        {
//                            EmitterPresetMaster.getInstance().getGroupText(data, EMITTER_VALUE_GROUP.Scale);
                            int max = Math.max(texture.getHeight(), texture.getWidth());
                            int coef = max / MAX_IMAGE_SIZE;
                             texture = GdxImageMaster.size(s, texture.getWidth()*coef,
                              texture.getHeight()*coef, false);
                        }
                        t.add(texture);
                    }
                } catch (Exception e) {
//                    main.system.ExceptionMaster.printStackTrace(e);
                    main.system.auxiliary.log.LogMaster.log(1,
                     path + " vfx broken! "  );
                    broken.put(path, imagePath);
                    continue;
                }

            imagesPath = getVfxAtlasImagesPath();
            atlasPath = getVfxAtlasPath();
            atlasName = getVfxAtlasName();
            String imagesData="\n";

            try {
                    for (String s : ContainerUtils.openContainer(imagePath, "\n")) {
                       String imageName=StringMaster.getLastPathSegment(s);
                        imagesData+= imageName+"\n";

                        String newPath = map.get(imageName);
                        if (newPath == null) {
                            newPath = imagesPath + PathUtils.getPathSeparator() + imageName  ;
                            map.put(imageName, newPath);
                        }
                        if (writeImage)
                        {
                            Texture texture = (new Texture(s));
                        FileHandle handle = new FileHandle(newPath);
                        if (!handle.exists() || overwriteImage)
                            GdxImageMaster.writeImage(handle, texture);
                            main.system.auxiliary.log.LogMaster.log(1,
                             path + " vfx image written: " + newPath);
                        }
                    }

                String data = EmitterPresetMaster.getInstance().getModifiedData(path,
                 EMITTER_VALUE_GROUP.Image_Path,imagesData);

                FileManager.write(data, PathFinder.getVfxPath() + PathUtils.getPathSeparator() + "atlas" + PathUtils.getPathSeparator() + path);

                main.system.auxiliary.log.LogMaster.log(1,
                 path + " vfx preset written  "  );

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

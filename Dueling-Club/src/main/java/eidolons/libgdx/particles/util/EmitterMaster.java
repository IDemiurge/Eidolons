package eidolons.libgdx.particles.util;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.libgdx.GDX;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.anims.construct.AnimConstructor.ANIM_PART;
import eidolons.libgdx.particles.Emitter.EMITTER_VALS_SCALED;
import eidolons.libgdx.particles.ParticleEffectX;
import eidolons.libgdx.particles.util.EmitterPresetMaster.EMITTER_VALUE_GROUP;
import eidolons.libgdx.texture.SmartTextureAtlas;
import eidolons.libgdx.texture.TexturePackerLaunch;
import eidolons.swing.generic.services.dialog.DialogMaster;
import eidolons.system.utils.GdxUtil;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Reader;
import main.entity.type.ObjType;
import main.swing.generic.components.editors.ImageChooser;
import main.system.PathUtils;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.launch.CoreEngine;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static main.system.ExceptionMaster.printStackTrace;
import static main.system.auxiliary.log.LogMaster.log;

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
    private static final int MAX_IMAGE_SIZE = 200;
    static Map<VFX_ATLAS, Map<String, String>> maps = new HashMap<>();
    static Map<String, String> broken = new HashMap<>();
    static List<String> group1 = new ArrayList<>();
    static List<String> group2 = new ArrayList<>();
    static List<String> group3 = new ArrayList<>();
    static String[] exceptions = {
            "custom", "atlas", "atlases", "broken", "templates", "workspace", "export", "target"
    };
    private static boolean writeImage = true;
    private static boolean pack = true;
    private static boolean overwriteImage = false;
    private static boolean sizeImages = false;
    private static boolean test = true;
    private static Map<VFX_ATLAS, TextureAtlas> atlasMap = new HashMap<>();
    private static boolean manualFix;


    public static List<File> getVfxPresets(boolean removeNonAtlas) {
        List<File> files = FileManager.getFilesFromDirectory(PathFinder.getVfxPath(), false, true);
        if (removeNonAtlas)
            files.removeIf(file ->
                    getAtlasType(file.getPath().toLowerCase().replace(
                            PathFinder.getVfxPath().toLowerCase(), ""), true) == null);
        return files;
    }

    public static void createVfxAtlas() {
        String imagesPath = null;
        List<File> files = getVfxPresets(true);

        for (VFX_ATLAS type : VFX_ATLAS.values()) {
            switch (type) {
                //                case UNIT:
                case SPELL:
                    break;
                case AMBIENCE:
                case MAP:
                case MISC:
                    continue;
            }
            main:
            for (File sub : files) {
                String path = sub.getPath().toLowerCase().replace(PathFinder.getVfxPath().toLowerCase(), "");

                for (String s : exceptions) {
                    if (path.contains(s))
                        continue main;
                }

                if (type != getAtlasType(path, true))
                    continue;

                imagesPath = getVfxAtlasImagesPath(type);

                String imagesData = "";// "\n";

                if (path.contains("preset")) {
                    imagesData = "";
                }

                String imagePath = EmitterPresetMaster.getInstance().findImagePath(path);
                List<Texture> list = new ArrayList<>();

                if (writeImage)
                    try {
                        for (String s : ContainerUtils.openContainer(imagePath, "\n")) {
                            Texture texture = new Texture(s);
                            if (sizeImages)
                                if (texture.getHeight() > MAX_IMAGE_SIZE || texture.getWidth() > MAX_IMAGE_SIZE) {
                                    //                            EmitterPresetMaster.getInstance().getGroupText(data, EMITTER_VALUE_GROUP.Scale);
                                    int max = Math.max(texture.getHeight(), texture.getWidth());
                                    int coef = max / MAX_IMAGE_SIZE;
                                    texture = GdxImageMaster.size(s, texture.getWidth() * coef,
                                            texture.getHeight() * coef, false);
                                }
                            list.add(texture);
                        }
                    } catch (Exception e) {
                        printStackTrace(e);
                        log(1,
                                path + " vfx broken! ");
                        broken.put(path, imagePath);
                        if (manualFix)
                            imagePath = applyFix(path, imagePath, type);
                        else
                            imagePath = null;

                    }

                if (imagePath == null)
                    continue;
                Map<String, String> map = maps.get(type);
                if (map == null) {
                    maps.put(type, map = new HashMap<>());
                }
                imagePath = imagePath.trim();
                try {
                    for (String s : ContainerUtils.openContainer(imagePath, "\n")) {
                        s = s.trim();
                        if (s.isEmpty())
                            continue;
                        String imageName = processImageName(s);
                        imagesData += imageName + "\n";

                        String newPath = map.get(imageName);
                        if (newPath == null) {
                            newPath = imagesPath + PathUtils.getPathSeparator() +
                                    (imageName);
                            map.put(imageName, newPath);
                        }
                        if (writeImage) {
                            Texture texture = (new Texture(s));
                            if (texture.getWidth() > 1000) {
                                continue;
                            }
                            FileHandle handle = GDX.file(newPath);
                            if (!handle.exists() || overwriteImage) {
                                GdxImageMaster.writeImage(handle, texture);
                                log(1,
                                        path + " vfx image written: " + newPath);
                            }
                        }
                    }
                    Boolean p = new Boolean(EmitterPresetMaster.getInstance().getValueFromGroup(path, EMITTER_VALUE_GROUP.Options,
                            EMITTER_VALUE_GROUP.PremultipliedAlpha.name()));
                    Boolean additive = new Boolean(EmitterPresetMaster.getInstance().getValueFromGroup(path, EMITTER_VALUE_GROUP.Options,
                            "Additive"));

                    if (p) {
                        group1.add(path);
                    } else {
                        if (additive) {
                            group2.add(path);
                        } else {
                            group3.add(path);
                        }
                    }

                    String data = EmitterPresetMaster.getInstance().readAndModifyData(path,
                            EMITTER_VALUE_GROUP.Image_Path, imagesData);
                    String newPath = PathFinder.getVfxAtlasPath() + path;
                    FileManager.write(data, newPath);

                    log(1,
                            newPath + " vfx preset written  ");
                    if (test) {
                        try {
                            new ParticleEffectX(newPath);
                        } catch (Exception e) {
                            printStackTrace(e);
                            log(1, " vfx test failed: " + newPath);
                        }
                    }
                } catch (Exception e) {
                    printStackTrace(e);
                    broken.put(path, imagePath);
                    continue;
                }
            }
            if (pack) {
                String atlasName = getVfxAtlasName(type);
                String atlasPath = getVfxAtlasPath(atlasName);
                Settings settings = TexturePackerLaunch.getSettings();
                settings.combineSubdirectories = true;
                settings.jpegQuality = 0.9f;
                TexturePackerLaunch.pack(imagesPath, atlasPath, atlasName, settings);
            }

        }
        log(1, broken.size() + " vfx imagepath broken: "
                + broken);
        log(1, group1.size() + " vfx w/ premultiplied: "
                + group1);
        log(1, group2.size() + " vfx w/ additive: "
                + group2);
        log(1, group3.size() + " vfx w/o additive: "
                + group2);

        System.exit(0);
    }


    private static String applyFix(String path, String imagePath, VFX_ATLAS type) {

        int i =
                DialogMaster.optionChoice(imagePath + " on " +
                                path + " is broken. What do to?", "Disable manual fix", "Set image", "Random image", "Last image",
                        "Delete", "Pass");
        switch (i) {
            case 0:
                manualFix = false;
                break;
            case 1:
                return new ImageChooser(PathFinder.getVfxPath()).launch("", imagePath);

        }
        return null;
    }

    private static boolean isProcessImageNames() {
        return false;
    }

    private static String processImageName(String imageName) {
        boolean sprite = imageName.contains("sprites");
        String format = StringMaster.getFormat(imageName);
        imageName = PathUtils.getLastPathSegment(imageName);
        imageName = StringMaster.cropFormat(imageName);
        if (isProcessImageNames()) {
            imageName = processImageNameDeep(imageName);
        }
        if (sprite) {
            return "sprites" + "/" + imageName + format;
        }
        return imageName + format;
    }

    private static String processImageNameDeep(String imageName) {
        String last = StringMaster.getLastPart(imageName, "_");
        while (NumberUtils.isInteger(last)) {
            int n = NumberUtils.getInteger(last);
            imageName = StringMaster.cropLast(imageName, "_")
                    + n;
            last = StringMaster.getLastPart(imageName, "_");
        }
        return imageName;
    }

    public static void main(String[] args) {
        CoreEngine.systemInit();
        new EmitterMaster().start();
    }

    private static String getVfxAtlasImagesPath(VFX_ATLAS atlas) {
        String name = atlas.name().toLowerCase();
        return
                StrPathBuilder.build(PathFinder.getVfxPath() + "atlas images", name);

    }

    public static VFX_ATLAS getAtlasType(String path) {
        return getAtlasType(path, false);
    }

    public static VFX_ATLAS getAtlasType(String path, boolean noAtlasFolders) {
        path = FileManager.formatPath(path, true);
        path = path.toLowerCase().replace(FileManager.formatPath(PathFinder.getVfxPath(), true), "");
        String folder = PathUtils.getPathSegments(path).get(0).toLowerCase();
        if (folder.equalsIgnoreCase("atlas")) {
            if (noAtlasFolders) {
                return null;
            }
            folder = PathUtils.getPathSegments(path).get(1).toLowerCase();
        }
        switch (folder) {
            case "mist":
            case "black mist":
            case "snow":
            case "ambience":
            case "ambient":
            case "woods":
                return VFX_ATLAS.AMBIENCE;

            case "buffs":
            case "cast":
            case "center":
            case "circle":
            case "precast":
            case "damage":
            case "fade":
            case "flow":
            case "hit":
            case "impact":
            case "missile":
            case "spell":
                return VFX_ATLAS.SPELL;

            case "smokes":
            case "waters":
            case "moons":
                return VFX_ATLAS.MAP;
            case "unit":
            case "advanced":
                return VFX_ATLAS.UNIT;
            case "export":
            case "modified":
            case "custom":
            case "junk":
                return VFX_ATLAS.MISC;
        }
        return VFX_ATLAS.MISC;
    }

    private static String getVfxAtlasPathFull(VFX_ATLAS type) {
        String name = getVfxAtlasName(type);
        return StrPathBuilder.build(getVfxAtlasPath(getVfxAtlasName(type)), name + TexturePackerLaunch.ATLAS_EXTENSION);
    }

    private static String getVfxAtlasPath(String name) {
        return StrPathBuilder.build(PathFinder.getVfxPath() + "atlases", name);
    }

    private static String getVfxAtlasName(VFX_ATLAS type) {
        return type.name().toLowerCase();
    }

    public static TextureAtlas getAtlas(String path) {
        VFX_ATLAS type = getAtlasType(path);
        TextureAtlas atlas = atlasMap.get(type);
        if (atlas == null) {
            atlas = new SmartTextureAtlas(getVfxAtlasPathFull(type)); //  Assets.get().getManager().get
            atlasMap.put(type, atlas);
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
        //        generateVfx();
        createVfxAtlas();
    }

    private void generateVfx() {
        DC_Engine.dataInit();
        XML_Reader.readTypeFile(false, DC_TYPE.SPELLS);
        String template = PathFinder.getSpellVfxPath() + "/cast/cast template";
        String spellsPath = "Y:\\Eidolons\\art materials\\psd\\[main]\\spells";
        List<File> imagePool = FileManager.getFilesFromDirectory(spellsPath, false, true);
        imagePool.removeIf(image -> !StringMaster.getFormat(image.getName()).equalsIgnoreCase(".png"));
        ANIM_PART PART = ANIM_PART.CAST;
        for (ObjType type : DataManager.getTypes(DC_TYPE.SPELLS)) {
            String root = type.getImagePath();
            File current = FileManager.getFile(PathFinder.getImagePath() + root);
            File sameImage = imagePool.stream().filter(file ->
                    FileManager.isSameFile(file, current)).findFirst().orElse(null);
            if (sameImage == null) {
                continue;
            }

            List<File> validImages = imagePool.stream().filter(
                    image -> checkImageForSpellVfx(image, sameImage, type)).
                    //             sorted().
                            limit(5).
                            collect(Collectors.toList());


            List<String> imageNames = imagePool.stream().map(file -> "gen/" + file.getName()).collect(Collectors.toList());

            String newImages = ContainerUtils.toStringContainer(imageNames, "\n");
            String data = EmitterPresetMaster.getInstance().readAndModifyData(template,
                    EMITTER_VALUE_GROUP.Image_Paths,
                    newImages);

            String newPath = PathFinder.getVfxPath() +
                    "/atlas/gen/" + PART + "/" + type.getName();
            FileManager.write(data, newPath, true);


            //copy valid images...
            for (File validImage : validImages) {
                File output = FileManager.getFile(getVfxAtlasImagesPath(VFX_ATLAS.SPELL) +
                        "/gen/" + validImage.getName());
                try {
                    Files.copy(Paths.get(validImage.toURI()), Paths.get(output.toURI()), StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean checkImageForSpellVfx(File image, File root, ObjType type) {
        String imgName = root.getName();

        String base = processImageNameDeep(imgName);
        if (image.getName().toLowerCase().contains(base.toLowerCase()))
            return true;
        //so we'll get all the images... isn't it much?

        return false;
    }

    public enum VFX_ATLAS {
        AMBIENCE,
        SPELL,
        MAP,
        MISC,
        UNIT
    }

    public enum VFX_TEMPLATE {
        CENTER, SWIRL, FADE, FLOW,
        MISSILE, WHIRL,
    }
}

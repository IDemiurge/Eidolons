package eidolons.system.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;
import main.data.filesys.PathFinder;
import main.system.PathUtils;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 5/13/2018.
 * <p>
 * <p>
 * basic control panel via swing !!!
 */
public class IconGenerator extends GdxUtil {
    private static final String IMAGES = "images";
    private static final String UNDERLAYS = "underlays";
    private static final String BACKGROUND_OVERLAYS = "Background overlays";
    private static final String OVERLAYS = "overlays";
    private static final String OUTPUT_DEFAULT = "generated";

    private static IconGenerator generator;
    private static boolean rounded;
    private static boolean folderPerUnderlay = true;
    private static boolean folderPerImage = true;
    private static boolean applyOverlays = true;
    private static boolean applyBackgroundOverlays = true;
    private static boolean flipXisOn = true;
    private static boolean flipYisOn = true;
    private static boolean resizeImage;
    private static boolean roundedUnderlay;
    private static Integer size;
    private static boolean resizeAll;
    String root;
    String underlay;
    String output;
    private String overlays;
    private String backgroundOverlays;
    private boolean subdirs;
    private int i = 0;
    private boolean flipUnderlayisOn = true;
    private static boolean realNaming;

    public IconGenerator(String root, String underlay, String output, boolean subdirs) {
        this.root = root;
        this.underlay = underlay;
        this.output = output;
        this.subdirs = subdirs;

        overlays = StringMaster.getLastPathSegment(root).equalsIgnoreCase(IMAGES)
         ? StrPathBuilder.build(StringMaster.cropLastPathSegment(root), OVERLAYS)
         : StrPathBuilder.build(root, OVERLAYS);
        backgroundOverlays = StringMaster.getLastPathSegment(root).equalsIgnoreCase(IMAGES)
         ? StrPathBuilder.build(StringMaster.cropLastPathSegment(root), BACKGROUND_OVERLAYS)
         : StrPathBuilder.build(root, BACKGROUND_OVERLAYS);
        start();
    }

    public static void main(String[] args) {
        CoreEngine.systemInit();

        rounded = false;
        folderPerUnderlay = true;
        resizeAll = true;
        resizeImage = true;
        flipYisOn = false;
        size = 64;
        realNaming = true;
        generateByRoot("64/items/weapons", false);
    }

    private static void generateMasteries() {
        generateByRoot("mastery", false);
    }

    private static void generateRoundedSkills() {
        //        folderPerUnderlay = false;
        rounded = true;
        resizeImage = true;
        generateByRoot("large", false);
        //        generateByRoot("64\\skills\\generated", false);

    }

    private static void roundImages(String s) {
        new IconGenerator("", "", "", false);
        for (File sub : FileManager.getFilesFromDirectory(
         StrPathBuilder.build(
          PathFinder.getImagePath(),
          getGeneratorRootPath(), s), false)) {
            if (ImageManager.isImageFile(sub.getName())) {
                Gdx.app.postRunnable(() ->
                 GdxImageMaster.round(GdxImageMaster.cropImagePath(sub.getAbsolutePath()),
                  true));
            }
        }
    }

    private static void sizeImages(int size, String s) {
        new IconGenerator("", "", "", false);
        for (File sub : FileManager.getFilesFromDirectory(
         StrPathBuilder.build(
          PathFinder.getImagePath(),
          getGeneratorRootPath(), s), false)) {
            if (ImageManager.isImageFile(sub.getName())) {
                Gdx.app.postRunnable(() -> GdxImageMaster.size(GdxImageMaster.cropImagePath(sub.getAbsolutePath()),
                 size, true));
            }
        }
    }

    private static void generateJewelry() {
        generateByRoot("64\\items\\jewelry", false);
    }

    private static void generateWeapons() {
        generateByRoot("64\\items\\weapons", false);
    }

    private static void generateArmor() {
        generateByRoot("64\\items\\armor", false);
    }

    private static void generateByRoot(String rootPath, boolean subdirs) {
        String path = StrPathBuilder.build(
         getGeneratorRootPath(), rootPath);

        String imagesPath = StrPathBuilder.build(PathFinder.getImagePath(), path, "images");

        String underlaysPath = StrPathBuilder.
         build(PathFinder.getImagePath(), path, "underlays") +
         PathUtils.getPathSeparator();
        gen(path, imagesPath, underlaysPath, subdirs);
    }

    private static String getGeneratorRootPath() {
        return   "generator/" ;
    }

    private static void generateAll() {
        String path = StrPathBuilder.build(
         ImageManager.getValueIconsPath(), "generator");

        String imagesPath = StrPathBuilder.build(PathFinder.getImagePath(), path, "images");

        String underlaysPath = StrPathBuilder.
         build(PathFinder.getImagePath(), path, "underlays") +
         PathUtils.getPathSeparator();
        gen(path, imagesPath, underlaysPath, true);
    }

    private static void gen(String path, String imagesPath, String underlaysPath, boolean subdirs) {
        generator = null;

        for (File sub : FileManager.getFilesFromDirectory(underlaysPath, false, false)) {
            String output = getOutputFolder(path, sub);
            String underlay = StrPathBuilder.build(path, "underlays", sub.getName());

            if (generator == null)
                generator = new IconGenerator(imagesPath, underlay, output, subdirs);
            else
                Gdx.app.postRunnable(() -> {
                    try {
                        getGenerator().generate(imagesPath, underlay, output);
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    }
                });
        }
    }

    private static String getOutputFolder(String path, File sub) {
        return StrPathBuilder.build(
         PathFinder.getImagePath(), path, OUTPUT_DEFAULT,
         (folderPerUnderlay ? StringMaster.cropFormat(sub.getName()) : "")) +
         PathUtils.getPathSeparator();
    }

    public static IconGenerator getGenerator() {
        return generator;
    }

    private static void generateDefault() {
        String path = StrPathBuilder.build(PathFinder.getImagePath(),
         ImageManager.getValueIconsPath());
        String output = StrPathBuilder.build(path, "generated") +
         PathUtils.getPathSeparator();
        new IconGenerator(path, Images.EMPTY_RANK_SLOT, output, true);
    }

    public static void generateOverlaidIcon(Texture underlayTexture,
                                            String path, FileHandle outputHandle,
                                            boolean flipX, boolean flipY) {
        generateOverlaidIcon(underlayTexture, null, null, path, outputHandle, flipX, flipY);
    }

    public static void generateOverlaidIcon(Texture underlayTexture, String bgOverlay, String overlay,
                                            String path, FileHandle outputHandle, boolean flipX, boolean flipY) {
        Pixmap pixMap = GdxImageMaster.getPixmap(underlayTexture);
        Texture texture = rounded ?
         GdxImageMaster.round(path, false).getTexture()
         : TextureCache.getOrCreate(path);
        if (resizeImage)
            if (size != null)
                texture = GdxImageMaster.size(path, size, false);

        if (flipX || flipY) {
            texture = GdxImageMaster.flip(path, flipX, flipY, false);
        }
        if (size == null)
            size = Math.min(underlayTexture.getHeight(), underlayTexture.getWidth());


        int w = Math.min(underlayTexture.getWidth(), texture.getWidth());
        int h = Math.min(underlayTexture.getHeight(), texture.getHeight());
        int x = 0;
        int y = 0;

        if (TextureCache.isImage(bgOverlay)) {
            Texture bgOverlayTexture = TextureCache.getOrCreate(bgOverlay);

            if (resizeAll)
                bgOverlayTexture = GdxImageMaster.size(bgOverlay, size, false);

            x = (underlayTexture.getWidth() - w) / 2;
            y = (underlayTexture.getHeight() - h) / 2;
            GdxImageMaster.drawTextureRegion(x, y, bgOverlayTexture, w, h, pixMap);
        }

        x = (underlayTexture.getWidth() - w) / 2;
        y = (underlayTexture.getHeight() - h) / 2;
        GdxImageMaster.drawTextureRegion(x, y, texture, w, h, pixMap);

        if (overlay != null) {
            Texture overlayTexture = TextureCache.getOrCreate(overlay);

            if (resizeAll)
                overlayTexture = GdxImageMaster.size(overlay, size, false);
            x = (underlayTexture.getWidth() - w) / 2;
            y = (underlayTexture.getHeight() - h) / 2;
            GdxImageMaster.drawTextureRegion(x, y, overlayTexture, w, h, pixMap);
        }
        GdxImageMaster.writeImage(outputHandle, pixMap);

        //        pixMap.setBlending();
        //    pixMap.setFilter();

    }

    protected boolean isExitOnDone() {
        return true;
    }

    public void generate(String root, String underlay, String output) {


        boolean[] flipsX = flipXisOn ? new boolean[]{false, true} : new boolean[]{false};
        boolean[] flipsY = flipYisOn ? new boolean[]{false, true} : new boolean[]{false};
        boolean[] flipUnderlay = flipUnderlayisOn ? new boolean[]{false, true} : new boolean[]{false};
        for (boolean flipX : flipsX) {
            for (boolean flipY : flipsY) {
                for (boolean flippedUnderlay : flipUnderlay) {
                    Texture underlayTexture = roundedUnderlay ? TextureCache.
                     getOrCreateRoundedRegion(underlay, false).getTexture() : TextureCache.getOrCreate(underlay);

                    if (flippedUnderlay) {
                        underlayTexture = GdxImageMaster.flip(underlay, flipX, flipY, false);
                        flipX = false;
                        flipY = false;
                    }
                    if (resizeAll)
                        if (size != null)
                            underlayTexture = GdxImageMaster.size(underlay, size, false);

                    for (File sub : FileManager.getFilesFromDirectory(root, false, subdirs)) {
                        if (!FileManager.isImageFile(sub.getName()))
                            continue;
                        String underlaySuffix = StringMaster.cropFormat(StringMaster.getLastPathSegment(underlay));
                        FileHandle handle = null;
                        String path = sub.getPath().replace(PathFinder.getImagePath(), "");

                        if (FileManager.isDirectory(backgroundOverlays)) {
                            List<String> bgOverlays = FileManager.getFilesFromDirectory(backgroundOverlays, false, subdirs).
                             stream().map(file ->
                             GdxImageMaster.cropImagePath(StrPathBuilder.build(backgroundOverlays, file.getName()))).collect(Collectors.toList());;
                            bgOverlays.add(""); // none
                            for (String bgOverlayPath : bgOverlays) {

                                if (FileManager.isDirectory(overlays)) {
                                    for (File overlay : FileManager.getFilesFromDirectory(overlays, false, subdirs)) {
                                        String overlayPath = GdxImageMaster.cropImagePath(StrPathBuilder.
                                         build(overlays, overlay.getName()));

                                        handle = getOutputHandle(output, getOutputFileName(sub, underlaySuffix, bgOverlayPath, overlayPath, flipX, flipY));
                                        generateOverlaidIcon(underlayTexture, bgOverlayPath, overlayPath, path, handle, flipX, flipY);
                                    }
                                } else {
                                  if (realNaming){
                                      handle = getOutputHandle(output,
                                       getOutputFileName(sub,  bgOverlayPath, flipX, flipY));
                                  } else
                                    handle = getOutputHandle(output,
                                     getOutputFileName(sub, underlaySuffix, bgOverlayPath, flipX, flipY));

                                    generateOverlaidIcon(underlayTexture, bgOverlayPath, null, path, handle, flipX, flipY);
                                }
                            }
                        } else {
                            handle = getOutputHandle(output, getOutputFileName(sub, underlaySuffix, flipX, flipY));
                            generateOverlaidIcon(underlayTexture, path, handle, flipX, flipY);
                        }

                    }

                }

            }
        }
    }

    private String getOutputFileName(File imageFile, Object... suffixes) {
       if (realNaming){
           List<Object> list = new ArrayList<>();
           for (Object suffix : suffixes) {
               if (StringMaster.isEmpty(suffix.toString()))
                   continue;
               if (suffix instanceof String){
                   list.add(StringMaster.cropFormat(StringMaster.getLastPathSegment(((String) suffix))));
               } else {
                   if (!suffix.toString().equalsIgnoreCase("false")) {
                       list.add(suffix);
                   }
               }
           }
           String prefix =
           StrPathBuilder._build(false, list.toArray());
           return prefix + StringMaster.cropFormat(imageFile.getName());
       }
        if (folderPerUnderlay) {
            return StringMaster.cropFormat(imageFile.getName()) + (i++);
        } else {
            return StringMaster.cropFormat(imageFile.getName()) + (folderPerImage ? PathUtils.getPathSeparator() : "_") + ContainerUtils.join("_",
             Arrays.stream(suffixes).map(suffix ->
              StringMaster.cropFormat(StringMaster.getLastPathSegment(suffix.toString()))).collect(Collectors.toList()).toArray(
              new String[suffixes.length]));
        }

    }

    private FileHandle getOutputHandle(String output, String name) {
        return new FileHandle(output + (name.replace(" 64", "")
         .replace(" 128", "").replace(" 96", "")) + ".png");
    }

    @Override
    protected void execute() {
        if (!StringMaster.isEmpty(root))
            generate(root, underlay, output);
    }
}

package eidolons.system.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;

import java.io.File;

/**
 * Created by JustMe on 5/13/2018.
 * <p>
 * <p>
 * basic control panel via swing !!!
 */
public class IconGenerator extends GdxUtil {
    private static IconGenerator generator;
    String root;
    String underlay;
    String output;
    private boolean subdirs;

    public IconGenerator(String root, String underlay, String output, boolean subdirs) {
        this.root = root;
        this.underlay = underlay;
        this.output = output;
        this.subdirs = subdirs;
        start();
    }

    public static void main(String[] args) {
        CoreEngine.systemInit();
//        generateDefault();
//        generateAll();
//        generateArmor();
        generateWeapons();
//        generateJewelry();
//        sizeImages(64, PathFinder.getImagePath() +
//         "\\main\\item\\weapon\\sprites");
    }

    private static void sizeImages(int size, String s) {
        new IconGenerator("", "", "", false);
        for (File sub : FileManager.getFilesFromDirectory(s, false)) {
            if (ImageManager.isImageFile(sub.getName())) {
                Gdx.app.postRunnable(() -> GdxImageMaster.size(GdxImageMaster.cropImageImage(sub.getAbsolutePath()),
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
         ImageManager.getValueIconsPath(), "generator", rootPath);

        String imagesPath = StrPathBuilder.build(PathFinder.getImagePath(), path, "images");

        String underlaysPath = StrPathBuilder.
         build(PathFinder.getImagePath(), path, "underlays") +
         StringMaster.getPathSeparator();
        gen(path, imagesPath, underlaysPath, subdirs);
    }

    private static void generateAll() {
        String path = StrPathBuilder.build(
         ImageManager.getValueIconsPath(), "generator");

        String imagesPath = StrPathBuilder.build(PathFinder.getImagePath(), path, "images");

        String underlaysPath = StrPathBuilder.
         build(PathFinder.getImagePath(), path, "underlays") +
         StringMaster.getPathSeparator();
        gen(path, imagesPath, underlaysPath, true);
    }

    private static void gen(String path, String imagesPath, String underlaysPath, boolean subdirs) {
        generator = null;

        for (File sub : FileManager.getFilesFromDirectory(underlaysPath, false, false)) {
            String output = StrPathBuilder.build(
             PathFinder.getImagePath(), path, "generated",
             StringMaster.cropFormat(sub.getName())) +
             StringMaster.getPathSeparator();
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

    public static IconGenerator getGenerator() {
        return generator;
    }

    private static void generateDefault() {
        String path = StrPathBuilder.build(PathFinder.getImagePath(),
         ImageManager.getValueIconsPath());
        String output = StrPathBuilder.build(path, "generated") +
         StringMaster.getPathSeparator();
        new IconGenerator(path, Images.EMPTY_RANK_SLOT, output, true);
    }

    public static void generateOverlaidIcon(Texture underlayTexture, String path, FileHandle handle, boolean b) {
        Pixmap pixMap = GdxImageMaster.getPixmap(underlayTexture);
        Texture texture = TextureCache.getOrCreate(path);
        int w = Math.min(underlayTexture.getWidth(), texture.getWidth());
        int h = Math.min(underlayTexture.getHeight(), texture.getHeight());
        int x = (underlayTexture.getWidth() - w) / 2;
        int y = (underlayTexture.getHeight() - h) / 2;
        GdxImageMaster.drawTextureRegion(x, y, texture, w, h, pixMap);
        GdxImageMaster.writeImage(handle, pixMap);

//        pixMap.setBlending();
//    pixMap.setFilter();

    }

    protected boolean isExitOnDone() {
        return false;
    }

    public void generate(String root, String underlay, String output) {
        Texture underlayTexture = TextureCache.getOrCreate(underlay);
        for (File sub : FileManager.getFilesFromDirectory(root, false, subdirs)) {
            if (!FileManager.isImageFile(sub.getName()))
                continue;
            FileHandle handle =getOutputHandle(output, sub);
            String path = sub.getPath().replace(PathFinder.getImagePath(), "");
            generateOverlaidIcon(underlayTexture, path, handle, true);

        }
    }

    private FileHandle getOutputHandle(String output, File sub) {
        return new FileHandle(output + (sub.getName().replace(" 64", "")
         .replace(" 128", "").replace(" 96", "")));
    }

    @Override
    protected void execute() {
        generate(root, underlay, output);
    }
}

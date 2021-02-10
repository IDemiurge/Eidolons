package libgdx.utils.textures;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.tools.ktx.KTXProcessor;
import libgdx.assets.Assets;
import eidolons.system.utils.GdxUtil;
import main.data.filesys.PathFinder;
import main.system.auxiliary.data.FileManager;
import main.system.launch.CoreEngine;

import java.io.File;

public class KtxCompressor extends GdxUtil {
    private static final CharSequence FORMAT = ".ktx";
    private static final String DEFAULT = "ui/light/light.png;";
    private final String[] args;
    private SpriteBatch spriteBatch;

    public KtxCompressor(String... args) {
        this.args = args;
    }

    private void checkRGB(File file) {
        String contents = FileManager.readFile(file);
        if (contents.contains(".jpg")) if (contents.contains("RGBA8888"))
            FileManager.write(contents.replace("RGBA8888", "RGB888"),
                    (file.getPath()));
    }

    private void generateAtlasVersion(File file) {
        String contents = FileManager.readFile(file);
        FileManager.write(contents.replace(".jpg", FORMAT).replace(".png", FORMAT),
                Assets.getKtxAtlasPath(file.getPath()));
    }

    public static void main(String[] args) {
        CoreEngine.systemInit();
        args = new String[]{
                 "sprites/boss/knight"
        };
        new KtxCompressor(args).start();
    }

    @Override
    protected void execute() {
        for (String arg : args) {
            String path = PathFinder.getImagePath() +
                      arg;
            if (FileManager.isFile(path)) {
                convert(FileManager.getFile(path));
            } else
                for (File file : FileManager.getFilesFromDirectory(path, false, true)) {
                    convert(file);
                }

        }
    }

    private void convert(File file) {
        if (file.getName().contains(".txt")) {
            boolean checkRGB = true;
            if (checkRGB)
                checkRGB(file);
            if (!file.getName().contains("ktx")) {
                generateAtlasVersion(file);
            }
        } else {
            boolean writeKTX = true;
            if (writeKTX)
                if (!file.getName().contains(".ktx"))
                    convert(file.getPath());
        }
    }

    @Override
    protected int getWidth() {
        return 1920;
    }

    @Override
    protected int getHeight() {
        return 1080;
    }

    public void convert(String raw) {
        toKTX(raw);
    }

    private void toKTX(String path) {
        main.system.auxiliary.log.LogMaster.devLog("toKTX " + path);
        try {
            KTXProcessor.convert(path, Assets.getKtxImgPath(path), false,
                    true, false);
            main.system.auxiliary.log.LogMaster.devLog("toKTX done " + path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render() {
        super.render();

    }

}

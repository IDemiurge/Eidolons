package main.utilities.gdx;

import eidolons.content.consts.libgdx.GdxUtils;
import libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.system.utils.GdxUtil;
import main.system.auxiliary.data.FileManager;
import main.system.launch.CoreEngine;

import java.io.File;

public class OneFrameGenerator extends GdxUtil {

    public static void main(String[] args) {
        new OneFrameGenerator().start();
    }

    @Override
    protected void execute() {
        CoreEngine.TEST_LAUNCH = true;
        // FileManager.getFilesFromDirectory()
        //sort?
        for (File file : FileManager.getSpriteFilesFromDirectory("")) {
            String path = GdxUtils.cropImagePath(file.getPath());
            if (!checkPath(path)) {
                continue;
            }
            SpriteAnimationFactory.getSpriteAnimation(path);
        }
    }

    private boolean checkPath(String path) {

        if (path.contains("cells")) {
            return true;
        }
        if (path.contains("unit")) {
            return true;
        }
        if (path.contains("lock")) {
            return false;
        }
        if (path.contains("fullscreen")) {
            return false;
        }
        if (path.contains("background")) {
            return false;
        }
        if (path.contains("particles")) {
            return false;
        }
        if (path.contains("potions")) {
            return false;
        }
        return false;
    }
}

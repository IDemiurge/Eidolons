package main.utilities.gdx;

import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
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
        CoreEngine.TEST_LAUNCH=true;
        // FileManager.getFilesFromDirectory()

        for (File file : FileManager.getSpriteFilesFromDirectory("")) {
            String path = GdxImageMaster.cropImagePath(file.getPath());
            SpriteAnimationFactory.getSpriteAnimation(path);
        }
    }
}

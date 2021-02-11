package libgdx.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.content.consts.libgdx.GdxUtils;
import libgdx.GdxImageMaster;
import libgdx.anims.sprite.SpriteAnimation;
import libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.system.utils.GdxUtil;
import main.data.filesys.PathFinder;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.launch.CoreEngine;

import java.io.File;

public class SpriteCutter extends GdxUtil {
    public  static void main(String [] are){
        new SpriteCutter().start();
    }


    private FileHandle getHandle(String s, File file, int i) {
        String path = PathFinder.getSpritesPathFull() + "/cut/2/"+s+"/";
        String n= NumberUtils.getFormattedTimeString(i, 2);
        return new FileHandle(path+StringMaster.cropFormat(file.getName()) +"_"+ n + ".png");
    }

    @Override
    protected void execute() {
        CoreEngine.systemInit();
        for (File file : FileManager.getFilesFromDirectory(PathFinder.getSpritesPathFull()+"/to cut/", false, true)) {
            SpriteAnimation sprite = SpriteAnimationFactory.getSpriteAnimation(GdxUtils.cropImagePath(file.getPath()));
            int i=0;
            for (TextureRegion region : sprite.getKeyFrames()) {
                FileHandle handle= getHandle(region.getRegionWidth() + "_" +region.getRegionHeight(), file, i++);
                GdxImageMaster.writeImage(handle, region);
            }

        }
    }
}

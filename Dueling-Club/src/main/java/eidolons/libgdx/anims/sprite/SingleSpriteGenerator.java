package eidolons.libgdx.anims.sprite;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.texture.TexturePackerLaunch;
import main.system.PathUtils;
import main.system.auxiliary.StringMaster;

import java.io.File;

public class SingleSpriteGenerator {

    public static final void main(String[] args) {

    }
    public static final void gen(String  path, int maxFrames) {
        TextureAtlas atlas = new TextureAtlas(path);
        String atlasName = StringMaster.cropFormat(PathUtils.getLastPathSegment(path));
        String append="reduced sprite";
        //create img to separate local path!

        String folderPath= path+ append ;
        File folder = new File(folderPath);
        folder.mkdirs();
        int s = atlas.getRegions().size;
        for (int i = 1; i <= maxFrames; i++) {
            int n = s / (maxFrames + 1) * i;
            TextureAtlas.AtlasRegion frame = atlas.getRegions().get(n);
            FileHandle imgPath= new FileHandle(path + append+" frame "+i);
            GdxImageMaster.writeImage(imgPath, frame);
        }


        //TODO idea - pack ALL single sprites into single atlas?
        // but we want single-sprite to be fallback option..

//        TexturePackerLaunch.pack(folderPath, output, atlasName);

    }

    public static String getPath(String key) {
        return null;
    }
}

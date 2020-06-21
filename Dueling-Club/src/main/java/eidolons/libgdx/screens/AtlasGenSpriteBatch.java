package eidolons.libgdx.screens;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.utils.ObjectMap;
import eidolons.libgdx.GdxImageMaster;

import java.util.Set;

public class AtlasGenSpriteBatch extends CustomSpriteBatch {
// Gdx revamp - use this to put ALL textures in 3-4 atlases
    public enum ATLAS {
    ui,

    /*
    depending on our draw order!

    bg

    grid
    > cells (emblems, arrow, border,
    > shadows, walls,
    > Overlays (hp,
    > emitters
    > anims

    ui
    > atb views

    what else prevents us from creating 'super huge atlas'?
    > if we load ALL units, that's a bit much!
    > create atlas per level - could work at first!

    > different shard versions
    > cell img
    > emitters split


    How big an atlas can we make? If we at least create the freaking img folders,
    it's trivial - use some other pc if you got to

     */
    }

    ATLAS atlas;
    ObjectMap<ATLAS, Set<Texture>> map = new ObjectMap<>();

    public void writeAtlases() {
        //in some cases we might wanna add more textures?

        for (ATLAS key : map.keys()) {
            for (Texture texture : map.get(key)) {
                if (texture.getTextureData() instanceof FileTextureData) {
                    String path = ((FileTextureData) texture.getTextureData()).getFileHandle().file().getPath();
                    path = getOutputPath(path, atlas);
                    GdxImageMaster.writeImage(new FileHandle(path), texture);
                }

                //output to dir
                //what about the PATH?
            }

            // TexturePackerLaunch.pack(input, output, atlas.name() , settings);
        }


    }

    private String getOutputPath(String path, ATLAS atlas) {
        return null;
    }

    @Override
    public void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, boolean clockwise) {
        if (atlas != null) {
            map.get(atlas).add(region.getTexture());
        }
        super.draw(region, x, y, originX, originY, width, height, scaleX, scaleY, rotation, clockwise);
    }

    @Override
    public void draw(TextureRegion region, float x, float y, float width, float height) {
        super.draw(region, x, y, width, height);
    }

    @Override
    public void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
        super.draw(region, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
    }

    @Override
    public void draw(TextureRegion region, float width, float height, Affine2 transform) {
        super.draw(region, width, height, transform);
    }
}

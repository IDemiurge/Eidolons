package eidolons.libgdx.screens;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.utils.ObjectMap;
import eidolons.game.EidolonsGame;
import eidolons.libgdx.GdxImageMaster;
import main.data.filesys.PathFinder;
import main.system.launch.CoreEngine;

import java.util.LinkedHashSet;
import java.util.Set;

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
public class AtlasGenSpriteBatch extends CustomSpriteBatch {
    private final boolean overwrite = false;

    // Gdx revamp - use this to put ALL textures in 3-4 atlases
    public enum ATLAS {
        ui,
        grid,
        anims,
        macro,
        hq, //vs ui?
    }

    ATLAS atlas;
    ObjectMap<ATLAS, Set<Texture>> map = new ObjectMap<>();

    public AtlasGenSpriteBatch() {
        for (ATLAS value : ATLAS.values()) {
            map.put(value, new LinkedHashSet<>());
        }
    }

    public void setAtlas(ATLAS atlas) {
        this.atlas = atlas;
    }

    public void writeAtlases() {
        //TODO only two - MAIN and UI!
        // but we will need ATLAS PER LEVEL in many cases! which is loaded on DUNGEON SCREEN INIT when we've selected
        //
        //in some cases we might wanna add more textures?
        for (ATLAS key : map.keys()) {
            if (atlas == ATLAS.ui) {
                if (CoreEngine.isLevelEditor()) {
                    continue;
                }
            }
            for (Texture texture : map.get(key)) {
                if (texture.getTextureData() instanceof FileTextureData) {
                    String path = ((FileTextureData) texture.getTextureData()).getFileHandle().file().getPath();
                    path = getOutputPath(path, atlas);
                    main.system.auxiliary.log.LogMaster.log(1, atlas + " texture output to: " + path);
                    FileHandle handle = new FileHandle(path);
                    if (handle.exists()) {
                        if (!overwrite) {
                            continue;
                        }
                    }
                    GdxImageMaster.writeImage(handle, texture);
                }
            }
            // if (pack)
            //     TexturePackerLaunch.pack(input, output, atlas.name() , settings);
        }
    }

    private String getOutputPath(String path, ATLAS atlas) {
        if (atlas == ATLAS.grid) {
            String lvlPath = EidolonsGame.lvlPath;
            return PathFinder.getImagePath() + "atlas img/" + atlas +
                    lvlPath + "/" + "/" +
                    GdxImageMaster.cropImagePath(path);
        }
        return PathFinder.getImagePath() + "atlas img/" + atlas + "/" +
                GdxImageMaster.cropImagePath(path);

    }

    @Override
    public void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, boolean clockwise) {
        checkAdd(region.getTexture());
        super.draw(region, x, y, originX, originY, width, height, scaleX, scaleY, rotation, clockwise);
    }

    @Override
    public void draw(TextureRegion region, float x, float y, float width, float height) {
        checkAdd(region.getTexture());
        super.draw(region, x, y, width, height);
    }

    @Override
    public void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
        checkAdd(region.getTexture());
        super.draw(region, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
    }

    @Override
    public void draw(TextureRegion region, float width, float height, Affine2 transform) {
        checkAdd(region.getTexture());
        super.draw(region, width, height, transform);
    }

    private void checkAdd(Texture texture) {
        if (atlas != null) {
            map.get(atlas).add(texture);
        }
    }

    @Override
    public void draw(Texture texture, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) {
        checkAdd(texture);
        super.draw(texture, x, y, originX, originY, width, height, scaleX, scaleY, rotation, srcX, srcY, srcWidth, srcHeight, flipX, flipY);
    }

    @Override
    public void draw(Texture texture, float x, float y, float width, float height, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) {
        checkAdd(texture);
        super.draw(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, flipX, flipY);
    }

    @Override
    public void draw(Texture texture, float x, float y, int srcX, int srcY, int srcWidth, int srcHeight) {
        checkAdd(texture);
        super.draw(texture, x, y, srcX, srcY, srcWidth, srcHeight);
    }

    @Override
    public void draw(Texture texture, float x, float y, float width, float height, float u, float v, float u2, float v2) {
        checkAdd(texture);
        super.draw(texture, x, y, width, height, u, v, u2, v2);
    }

    @Override
    public void draw(Texture texture, float x, float y) {
        checkAdd(texture);
        super.draw(texture, x, y);
    }

    @Override
    public void draw(Texture texture, float x, float y, float width, float height) {
        checkAdd(texture);
        super.draw(texture, x, y, width, height);
    }

    @Override
    public void draw(Texture texture, float[] spriteVertices, int offset, int count) {
        checkAdd(texture);
        super.draw(texture, spriteVertices, offset, count);
    }

    @Override
    public void draw(TextureRegion region, float x, float y) {
        checkAdd(region.getTexture());
        super.draw(region, x, y);
    }
}

package eidolons.libgdx.screens;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.utils.ObjectMap;
import eidolons.game.EidolonsGame;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.anims.anim3d.AnimMaster3d;
import main.data.filesys.PathFinder;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
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
        for (ATLAS atlas : map.keys()) {
            if (atlas == ATLAS.ui) {
                if (CoreEngine.isLevelEditor()) {
                    continue;
                }
            }
            for (Texture texture : map.get(atlas)) {
                if (texture.getTextureData() instanceof FileTextureData) {
                    String path = ((FileTextureData) texture.getTextureData()).getFileHandle().file().getPath();
                    if (checkIgnoreTexture(path, texture)) {
                        continue;
                    }

                    path = getOutputPath(path, atlas == ATLAS.grid ? "main/" : "", atlas);
                    write(path, texture, atlas);
                    if (atlas == ATLAS.grid) {
                        path = getOutputPath(path, EidolonsGame.lvlPath + "/", atlas);
                        write(path, texture, atlas);
                    }

                }
            }
            // if (pack)
            //     TexturePackerLaunch.pack(input, output, atlas.name() , settings);

            for (String s : AnimMaster3d.getAtlasMap().keySet()) {
                ATLAS type = checkAtlas(s);
                if (type != null) {
                    writeSubAtlasImages(s, type);
                }

            }
        }
    }

    private ATLAS checkAtlas(String path) {
        if (path.contains("weapons3d"))
            return null;
        if (path.contains("background"))
            return null;
        if (path.contains("fullscreen"))
            return null;
        if (path.contains("potions"))
            return null;
        if (path.contains("ui"))
            return ATLAS.ui;
        return ATLAS.grid;
    }

    private String getOutputPath(String path, String suffix, ATLAS atlas) {
        return PathFinder.getImagePath() + "atlas img/" + atlas +
                "/" + suffix + GdxImageMaster.cropImagePath(path);

    }

    private void writeSubAtlasImages(String path, ATLAS type) {
        TextureAtlas atlas = AnimMaster3d.getOrCreateAtlas(path);
        path = StringMaster.cropFormat(path);
        int i = 0;
        for (TextureAtlas.AtlasRegion region : atlas.getRegions()) {
            String frame = "_" + NumberUtils.getFormattedTimeString(i++, 5);
            String outputPath = getOutputPath(path + frame + ".png", "", type);
            FileHandle handle = new FileHandle(outputPath);
            if (!handle.exists()) {
                try {
                    GdxImageMaster.writeImage(handle, region);
                    main.system.auxiliary.log.LogMaster.log(1, atlas + " atlas region texture output to: \n" + path);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }

        }

    }

    private void write(String path, Texture texture, ATLAS atlas) {
        FileHandle handle = new FileHandle(path);
        if (handle.exists()) {
            if (!overwrite) {
                return;
            }
        }
        try {
            GdxImageMaster.writeImage(handle, texture);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        main.system.auxiliary.log.LogMaster.log(1, atlas + " texture output to: " + path);
    }

    private boolean checkIgnoreTexture(String path, Texture texture) {
        if (texture.getWidth() > 1800) {
            return true;
        }
        if (texture.getHeight() > 1800) {
            return true;
        }
        if (path.contains("level_editor")) {
            return true;
        }
        if (path.contains("background")) {
            return true;
        }
        if (path.contains("fonts")) {
            return true;
        }
        if (path.contains("sprites")) {
            if (!path.contains("hanging"))
                return true;
        }
        if (path.contains(PathFinder.SKIN_NAME)) {
            return true;
        }
        return path.contains("atlas");
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

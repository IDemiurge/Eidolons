package eidolons.libgdx.bf.decor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.decor.ShardVisuals.SHARD_OVERLAY;
import eidolons.libgdx.bf.decor.ShardVisuals.SHARD_SIZE;
import eidolons.libgdx.bf.decor.ShardVisuals.SHARD_TYPE;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.particles.EMITTER_PRESET;
import eidolons.libgdx.particles.EmitterActor;
import eidolons.libgdx.texture.TextureCache;
import main.data.filesys.PathFinder;
import main.game.bf.directions.DIRECTION;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.data.FileManager;

import java.util.List;

/**
 * Created by JustMe on 10/8/2018.
 */
public class Shard extends SuperActor {
    SHARD_TYPE type;
    SHARD_SIZE size;
    SHARD_OVERLAY overlay;
    int x;
    int y;
    Object arg;
    DIRECTION direction;

    ImageContainer background;
    ImageContainer foreground; //idea - fade between 2 variants?
    //list of overlays
    List<EmitterActor> emitters;

    public static final boolean TEST_MODE=true;
    LabelX debugInfo;

    public Shard(int x, int y, SHARD_TYPE type, SHARD_SIZE size, SHARD_OVERLAY overlay, Object direction) {
        this.type = type;
        this.size = size;
        this.overlay = overlay;
        this.x = x;
        this.y = y;
        arg = direction;
        if (arg instanceof DIRECTION) {
            this.direction = (DIRECTION) arg;
        }
        init();
        if (TEST_MODE) {
            debugInfo = new LabelX(
             "" +
             "" +
             "" +
             "", StyleHolder.getDebugLabelStyle());
        }
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return null;
    }

    private String getBackgroundTexturePath() {
        return getBackgroundTexturePath(arg);
    }

    private String getBackgroundTexturePath(Object arg) {
        String path = null;
        if (direction == null) {
            path = StrPathBuilder.build("UI", "cells", "shards",
             type.toString(), "isles", "isle");
        } else if (direction.isDiagonal()) {
            path = StrPathBuilder.build("UI", "cells", "shards",
             type.toString(), arg.toString());
        } else {
            path = size == SHARD_SIZE.SMALL
             ?
             StrPathBuilder.build("UI", "cells", "shards",
              type.toString(), arg.toString(), size + "up")
             :
             StrPathBuilder.build("UI", "cells", "shards",
              type.toString(), arg.toString(), size + "down");
        }
        String file = FileManager.getRandomFilePathVariant(
         PathFinder.getImagePath() +
          path, ".png", false, false);
        if (file == null) {
            return null;
        }
        return GdxImageMaster.cropImagePath(file);
    }

    private String getForegroundTexturePath() {
        return getForegroundTexturePath(arg);
    }

    private String getForegroundTexturePath(Object arg) {
        if (!(arg instanceof DIRECTION)) {
            arg = direction.DOWN;
        }
        if (arg instanceof DIRECTION) {
            if (((DIRECTION) arg).isDiagonal()) {
                arg = direction.DOWN;
            }
        }
        return StrPathBuilder.build("UI", "cells", "shards", "overlay",
         overlay.toString(),
         //         size.toString() +
         arg + ".png");
    }

    private String getRandomForegroundTexturePath(Object arg) {
        String path = getForegroundTexturePath(arg);

        String file = FileManager.getRandomFilePathVariant(
         PathFinder.getImagePath() +
          path, ".png", false, false);
        if (file == null) {
            return null;
        }
        return GdxImageMaster.cropImagePath(file);
    }

    public void init() {
        //        if (getBackgroundTexturePath() == null) {
        //            GdxImageMaster.flip(getBackgroundTexturePath(direction.flip()),
        //             !direction.isVertical(), direction.isVertical(), true, getBackgroundTexturePath());
        //        }
        addActor(background = new ImageContainer(getBackgroundTexturePath()));
        setSize(background.getWidth(), background.getHeight());

        if (!TextureCache.isImage(getForegroundTexturePath())) {
            GdxImageMaster.flip(getForegroundTexturePath(direction.flip()),
             !direction.isVertical(), direction.isVertical(), true, getForegroundTexturePath());
        }
        addActor(foreground = new ImageContainer(getRandomForegroundTexturePath(arg)));
        ALPHA_TEMPLATE template = ShardVisuals.getTemplateForOverlay(overlay);
        foreground.setAlphaTemplate(template);

        EMITTER_PRESET[] presets = ShardVisuals.getEmitters(overlay, size);
        for (EMITTER_PRESET preset : presets) {
            EmitterActor actor = new EmitterActor(preset);
            emitters.add(actor);
            addActor(actor);
            actor.start();
        }
        //generic system for binding emitters to stuff?
    }
    public boolean isCachedPosition() {
        return true;
    }
    @Override
    public void act(float delta) {
        if (isIgnored())
            return;
        super.act(delta);
    }
}

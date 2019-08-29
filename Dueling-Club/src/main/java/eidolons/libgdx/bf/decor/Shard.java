package eidolons.libgdx.bf.decor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.entity.obj.DC_Cell;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.decor.ShardVisuals.SHARD_OVERLAY;
import eidolons.libgdx.bf.decor.ShardVisuals.SHARD_SIZE;
import eidolons.libgdx.bf.decor.ShardVisuals.SHARD_TYPE;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.shaders.DarkShader;
import eidolons.libgdx.shaders.ShaderDrawer;
import eidolons.libgdx.texture.TextureCache;
import main.content.enums.GenericEnums;
import main.data.filesys.PathFinder;
import main.game.bf.directions.DIRECTION;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.data.FileManager;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 10/8/2018.
 */
public class Shard extends SuperActor {
    public static final boolean TEST_MODE = false;
    SHARD_TYPE type;
    SHARD_SIZE size;
    SHARD_OVERLAY overlay;
    int x;
    int y;
    Object arg;
    DIRECTION direction;
    ImageContainer background;
    //list of overlays
    ImageContainer foreground; //idea - fade between 2 variants?
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
            addActor(debugInfo = new LabelX(
             direction +
              "" +
              "" +
              "", StyleHolder.getSizedColoredLabelStyle(FONT.AVQ, 20, Color.RED)));
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
            path = StrPathBuilder.build("ui", "cells", "shards",
             type.toString(), "isles", "isle");
        } else if (direction.isDiagonal()) {
            path = StrPathBuilder.build("ui", "cells", "shards",
             type.toString(), arg.toString());
        } else {
            if (size == null) {
                return null ;
            }
            path = size == SHARD_SIZE.SMALL
             ?
             StrPathBuilder.build("ui", "cells", "shards",
              type.toString(), arg.toString(), size + "up")
             :
             StrPathBuilder.build("ui", "cells", "shards",
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
        return StrPathBuilder.build("ui", "cells", "shards", "overlay",
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
//        main.system.auxiliary.log.LogMaster.log(1,"Shard init: " +
//         "overlay=" +overlay +
//         "size =" +size+
//         "type" + type+
//         "BackgroundTexture=" +
//         getBackgroundTexturePath() );

        //        if (getBackgroundTexturePath() == null) {
        //            GdxImageMaster.flip(getBackgroundTexturePath(direction.flip()),
        //             !direction.isVertical(), direction.isVertical(), true, getBackgroundTexturePath());
        //        }
        if (size != null) {
        addActor(background = new ImageContainer(getBackgroundTexturePath()));
        background.setFluctuateAlpha(false);
        setSize(background.getWidth(), background.getHeight());
        }

        if (overlay == null)
            return;

        if (!TextureCache.isImage(getForegroundTexturePath())) {
            GdxImageMaster.flip(getForegroundTexturePath(direction.flip()),
             !direction.isVertical(), direction.isVertical(), true, getForegroundTexturePath());
        }

        addActor(foreground = new ImageContainer(getRandomForegroundTexturePath(arg)));
        GenericEnums.ALPHA_TEMPLATE template = ShardVisuals.getTemplateForOverlay(overlay);
        foreground.setAlphaTemplate(template);

    }

    public boolean isCachedPosition() {
        return true;
    }

    @Override
    public void act(float delta) {
        if (isIgnored())
            return;
        if (getUserObject()!=null)
         resetColor();

        super.act(delta);
    }

    private void resetColor() {
//        DC_Cell cell = (DC_Cell) getUserObject();
//        setDebug(cell.isPlayerHasSeen());
//        if (!cell.isPlayerHasSeen()) {
//            if (foreground != null) {
//                foreground. setColor(0.6f,0.6f,0.6f,1f);
//            }
//            if (background != null) {
//            background. setColor(0.6f,0.6f,0.6f,1f);
//            }
//        }
//        else {
//            setColor(1f,1f,1f,1f);
//        }
//        switch (cell.getPlayerVisionStatus()) {
//            case DETECTED:
//                break;
//            case KNOWN:
//                break;
//            case UNKNOWN:
//                break;
//            case CONCEALED:
//                break;
//            case INVISIBLE:
//                break;
//            case INVISIBLE_ALLY:
//                break;
//        }
//        switch (cell.getVisibilityLevel()) {
//
//            case CLEAR_SIGHT:
//                break;
//            case OUTLINE:
//                break;
//            case VAGUE_OUTLINE:
//                break;
//            case CONCEALED:
//                break;
//            case BLOCKED:
//                break;
//            case UNSEEN:
//                break;
//        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        if (getUserObject()==null || parentAlpha == ShaderDrawer.SUPER_DRAW
//         || batch.getShader() == GrayscaleShader.getGrayscaleShader()
        ) {
            super.draw(batch, 1);
        } else {
            if (isIgnored())
                return;
//            if (GridPanel.SHADER_FOR_UNKNOWN_CELLS)
            ShaderDrawer.drawWithCustomShader(this,
                    batch,
                    !getUserObject().isPlayerHasSeen() ?
                            DarkShader.getDarkShader()
//             FishEyeShader.getShader()
                            : null, true);
        }
//        super.draw(batch, parentAlpha);
    }

    @Override
    public DC_Cell getUserObject() {
        return (DC_Cell) super.getUserObject();
    }

    public SHARD_TYPE getType() {
        return type;
    }

    public SHARD_SIZE getSize() {
        return size;
    }

    public SHARD_OVERLAY getOverlay() {
        return overlay;
    }

    public Object getArg() {
        return arg;
    }

    public DIRECTION getDirection() {
        return direction;
    }
}

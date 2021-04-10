package libgdx.bf.decor.shard;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.entity.obj.DC_Cell;
import eidolons.game.battlecraft.logic.battlefield.vision.colormap.LightHandler;
import libgdx.GdxImageMaster;
import libgdx.bf.SuperActor;
import eidolons.content.consts.VisualEnums.SHARD_OVERLAY;
import eidolons.content.consts.VisualEnums.SHARD_SIZE;
import eidolons.content.consts.VisualEnums.SHARD_TYPE;
import libgdx.bf.generic.ImageContainer;
import libgdx.bf.grid.handlers.GridManager;
import libgdx.gui.LabelX;
import libgdx.texture.TextureCache;
import main.content.enums.GenericEnums;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;

import java.util.function.Function;

/**
 * Created by JustMe on 10/8/2018.
 */
public class Shard extends SuperActor {
    SHARD_TYPE type;
    SHARD_SIZE size;
    SHARD_OVERLAY overlay;
    int x, y;
    Object arg;
    DIRECTION direction;
    ImageContainer background;
    //list of overlays
    ImageContainer foreground; //idea - fade between 2 variants?
    LabelX debugInfo;
    private Coordinates coord;
    private Function<Coordinates, Color> colorFunc;
    private boolean broken;

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
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return null;
    }

    private String getBackgroundTexturePath() {
        return ShardEnums.getBackgroundTexturePath(arg, direction, type, size);
    }

    private String getForegroundTexturePath() {
        return ShardEnums.getForegroundTexturePath(arg, overlay);
    }

    public void init() {
        if (size != null) {
            addActor(background = new ImageContainer(getBackgroundTexturePath()));
            background.setFluctuateAlpha(false);
            if (background.getContent() != null) {
                setSize(background.getWidth(), background.getHeight());
            } else {
                //TODO
                broken=true;
                return;
            }
        }

        if (overlay == null)
            return;

        if (!TextureCache.isImage(getForegroundTexturePath())) {
            GdxImageMaster.flip(ShardEnums.getForegroundTexturePath(direction.flip(), overlay),
                    !direction.isVertical(), direction.isVertical(), true, getForegroundTexturePath());
        }

        addActor(foreground = new ImageContainer(ShardEnums.getRandomForegroundTexturePath(arg, overlay)));
        GenericEnums.ALPHA_TEMPLATE template = ShardEnums.getTemplateForOverlay(overlay);
        foreground.setAlphaTemplate(template);
    }

    public boolean isCachedPosition() {
        return true;
    }

    @Override
    public void act(float delta) {
        if (broken) {
            return;
        }
        if (isIgnored())
            return;
        if (coord != null)
            resetColor();

        super.act(delta);
    }

    @Override
    public boolean isWithinCamera() {
        if (GridManager.isCustomDraw()) {
            return true;
        }
        return super.isWithinCamera();
    }

    protected void resetColor() {
        //same as pillars?
        Color c = LightHandler.applyLightnessToColor(  colorFunc.apply(coord), false);
        // float a = Math.max(0.3f, lightnessFunc.apply(coord) * 3);
        if (background.getContent() == null) {
            return;
        }
        background.setColor(c );
        if (foreground != null) {
            foreground.getColor().set(c.r  , c.g  , c.b  , foreground.getColor().a);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // if (batch instanceof CustomSpriteBatch) {
        //     ((CustomSpriteBatch) batch).resetBlending();
        // }
        if (broken) {
            return;
        }
        if (!GridManager.isCustomDraw()) {
            if (isIgnored())
                return;
        }
        super.draw(batch, 1);
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        if (userObject instanceof DC_Cell) {
            coord = ((DC_Cell) userObject).getCoordinates();
        }
    }

    public void setLightnessFunc(Function<Coordinates, Float> lightnessFunc) {
    }

    public void setColorFunc(Function<Coordinates, Color> colorFunc) {
        this.colorFunc = colorFunc;
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

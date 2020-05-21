package eidolons.libgdx.gui.generic.btn;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import eidolons.libgdx.texture.TextureCache;

import java.util.function.Supplier;

public class FlipDrawable implements Drawable {
    Drawable background;
    Supplier<Boolean> x, y;

    public FlipDrawable(Drawable background, Supplier<Boolean> x, Supplier<Boolean> y) {
        this.background = background;
        this.x = x;
        this.y = y;
    }

    public FlipDrawable(String path, Supplier<Boolean> x, Supplier<Boolean> y) {
        this(TextureCache.getOrCreateTextureRegionDrawable(path), x, y);
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        background.draw(batch,
                this.x.get() ? x+width:  x,
                this.y.get() ? y+height : y,
                this.x.get() ? -width : width,
                this.y.get() ? -height : height);
    }

    @Override
    public float getLeftWidth() {
        return background.getLeftWidth();
    }

    @Override
    public void setLeftWidth(float leftWidth) {
        background.setLeftWidth(leftWidth);
    }

    @Override
    public float getRightWidth() {
        return background.getRightWidth();
    }

    @Override
    public void setRightWidth(float rightWidth) {
        background.setRightWidth(rightWidth);
    }

    @Override
    public float getTopHeight() {
        return background.getTopHeight();
    }

    @Override
    public void setTopHeight(float topHeight) {
        background.setTopHeight(topHeight);
    }

    @Override
    public float getBottomHeight() {
        return background.getBottomHeight();
    }

    @Override
    public void setBottomHeight(float bottomHeight) {
        background.setBottomHeight(bottomHeight);
    }

    @Override
    public float getMinWidth() {
        return background.getMinWidth();
    }

    @Override
    public void setMinWidth(float minWidth) {
        background.setMinWidth(minWidth);
    }

    @Override
    public float getMinHeight() {
        return background.getMinHeight();
    }

    @Override
    public void setMinHeight(float minHeight) {
        background.setMinHeight(minHeight);
    }
}

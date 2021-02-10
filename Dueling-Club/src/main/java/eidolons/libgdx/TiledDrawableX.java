package eidolons.libgdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;

public class TiledDrawableX extends TiledDrawable {

    private final int w;
    private final int h;
    private final Texture background;
    private final TextureRegion corner1;
    private final TextureRegion corner2;
    private final TextureRegion corner3;
    private final TextureRegion corner4;
    private final TextureRegion bottom;
    private final TextureRegion left;
    private final TextureRegion right;
    private final TextureRegion top;
    private final int offsetX;
    private final int offsetY;

    public TiledDrawableX(int w, int h, Texture background, boolean fillWithBlack, TextureRegion corner1,
                          TextureRegion corner2, TextureRegion corner3, TextureRegion corner4,
                          TextureRegion bottom, TextureRegion left, TextureRegion right, TextureRegion top, int offsetX, int offsetY) {
        this.w = w;
        this.h = h;
        this.background = background;
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.corner3 = corner3;
        this.corner4 = corner4;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
        this.top = top;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }


    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {

        if (background != null) {
            batch.draw(background, x+offsetX, y+offsetY, w, h); // crop rather than scale!
        }

        //use super method for each side, then draw corners on top of it...
        setRegion(top);
        super.draw(batch, offsetX + x, y + height - top.getRegionHeight() +offsetY, width, top.getRegionHeight());
        setRegion(bottom);
        super.draw(batch, offsetX + x, offsetY + y, width, top.getRegionHeight());

        setRegion(right);
        super.draw(batch, x + width - right.getRegionWidth(), offsetY + y, right.getRegionWidth(), height);
        setRegion(left);
        super.draw(batch, offsetX + x, offsetY + y, left.getRegionWidth(), height);

        batch.draw(corner1, x, y+(h-corner1.getRegionHeight())+offsetY);
        batch.draw(corner2, x+(w-corner2.getRegionWidth()), y+(h-corner1.getRegionHeight())+offsetY);
        batch.draw(corner3, x, y+offsetY);
        batch.draw(corner4, x+(w-corner2.getRegionWidth()), y+offsetY);
    }

    @Override
    public float getMinHeight() {
        return h;
    }

    @Override
    public float getMinWidth() {
        return w;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

}

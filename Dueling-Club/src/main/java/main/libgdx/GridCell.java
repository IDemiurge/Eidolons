package main.libgdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class GridCell extends Group implements Borderable {
    protected Image backImage;
    protected Texture backTexture;
    protected int gridX;
    protected int gridY;
    private GridCell innerDrawable;
    private Image border = null;

    public GridCell(Texture backTexture, int gridX, int gridY) {
        this.backTexture = backTexture;
        this.gridX = gridX;
        this.gridY = gridY;
    }

    public GridCell init() {
        backTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        backImage = new Image(backTexture);
        addActor(backImage);
        setWidth(backImage.getWidth());
        setHeight(backImage.getHeight());
        return this;
    }

    public void addInnerDrawable(GridCell cell) { //add null to reset cell
        GridCell old = innerDrawable;
        innerDrawable = cell;
        if (old != null) {
            removeActor(old);
            old.dispose();
            //return;
        }
        if (innerDrawable != null) {
            addActor(innerDrawable);
            removeActor(backImage);
        } else {
            addActor(backImage);
        }
    }

    public GridCell getInnerDrawable() {
        return innerDrawable;
    }

    public void updateInnerDrawable(GridCell cell) {
        addInnerDrawable(null);
        addInnerDrawable(cell);
    }

    private void dispose() {
        removeActor(backImage);
        backImage = null;
    }

    public Actor hitChilds(float x, float y, boolean touchable) {
        return super.hit(x, y, touchable);
    }


    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (touchable && getTouchable() != Touchable.enabled) return null;
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight() ? this : null;
    }

    @Override
    public void setBorder(Image image) {
        if (image == null) {
            removeActor(border);
            border = null;
        } else {
            addActor(image);
            border = image;
        }
    }

    @Override
    public int getW() {
        return (int) getWidth();
    }

    @Override
    public int getH() {
        return (int) getHeight();
    }

    @Override
    public Actor getBorder() {
        return border;
    }
}

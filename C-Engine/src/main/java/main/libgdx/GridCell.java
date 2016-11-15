package main.libgdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class GridCell extends Group {
    protected Image backImage;
    protected Texture backTexture;
    protected String imagePath;
    protected int gridX;
    protected int gridY;
    private GridCell innerDrawable;

    public GridCell(Texture backTexture, String imagePath, int gridX, int gridY) {
        this.backTexture = backTexture;
        this.imagePath = imagePath;
        this.gridX = gridX;
        this.gridY = gridY;
    }

    TextureRegion tr;

    public GridCell init() {
        backTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        //tr = new TextureRegion(backTexture);
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
        }
        if (innerDrawable != null) {
            addActor(innerDrawable);
            removeActor(backImage);
        } else {
            addActor(backImage);
        }
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (touchable && getTouchable() != Touchable.enabled) return null;
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight() ? this : null;
    }
}

package main.libgdx.bf;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import main.game.core.game.DC_Game;
import main.libgdx.StyleHolder;

public class GridCell extends Group implements Borderable {
    protected Image backImage;
    protected Texture backTexture;
    private int gridX;
    private int gridY;
    private GridCell innerDrawable;
    private Image border = null;
    private Label cordsText;
    private float gamma;

    public GridCell(Texture backTexture, int gridX, int gridY) {
        this.backTexture = backTexture;
        this.setGridX(gridX);
        this.setGridY(gridY);
    }

    public GridCell init() {
        backTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        backImage = new Image(backTexture);
        addActor(backImage);
        setWidth(backImage.getWidth());
        setHeight(backImage.getHeight());

        cordsText = new Label(getGridX() + ":" + getGridY(), StyleHolder.getDefaultLabelStyle());
        cordsText.setPosition(getWidth() / 2 - cordsText.getWidth() / 2, getHeight() / 2 - cordsText.getHeight() / 2);
        cordsText.setVisible(false);
        addActor(cordsText);

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
            cordsText.setVisible(false);
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

    @Override
    public void act(float delta) {
        super.act(delta);
        if (GridMaster.isGammaOn()) {
            backImage.setColor(gamma, gamma, gamma, 1 - gamma / 2);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (getInnerDrawable() == null) {
            if (DC_Game.game.isDebugMode()) {
                if (!cordsText.isVisible()) {
                    cordsText.setVisible(true);
                }
            } else {
                if (cordsText.isVisible()) {
                    cordsText.setVisible(false);
                }
            }
        }

        super.draw(batch, parentAlpha);
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
        if (touchable && getTouchable() != Touchable.enabled) {
            return null;
        }
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight() ? this : null;
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
    public Image getBorder() {
        return border;
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
    public void updateBorderSize() {
        border.setX(-4);
        border.setY(-4);
        border.setHeight(getWidth() - 8);
        border.setWidth(getHeight() - 8);
    }

    public int getGridX() {
        return gridX;
    }

    public void setGridX(int gridX) {
        this.gridX = gridX;
    }

    public int getGridY() {
        return gridY;
    }

    public void setGridY(int gridY) {
        this.gridY = gridY;
    }

    public void setGamma(float gamma) {
        this.gamma = gamma;
    }
}

package main.libgdx.bf;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import main.game.core.game.DC_Game;
import main.libgdx.StyleHolder;

public class GridCell extends Group implements Borderable {
    protected Image backImage;
    protected TextureRegion backTexture;
    protected Image border = null;
    private int gridX;
    private int gridY;
    private GridCell innerDrawable;
    private TextureRegion borderTexture;

    private Label cordsText;
    private float gamma;

    public GridCell(TextureRegion backTexture, int gridX, int gridY) {
        this.backTexture = backTexture;
        this.setGridX(gridX);
        this.setGridY(gridY);
    }

    public GridCell init() {
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
//        if (GridMaster.isGammaOn()) {
//            backImage.setColor(gamma, gamma, gamma, 1 - gamma / 2);
//        }
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
    public TextureRegion getBorder() {
        return borderTexture;
    }

    @Override
    public void setBorder(TextureRegion texture) {
        if (border != null) {
            removeActor(border);
        }

        if (texture == null) {
            border = null;
            borderTexture = null;
        } else {
            addActor(border = new Image(texture));
            borderTexture = texture;
            updateBorderSize();
        }
    }

    private void updateBorderSize() {
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

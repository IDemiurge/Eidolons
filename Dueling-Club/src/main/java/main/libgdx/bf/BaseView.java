package main.libgdx.bf;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class BaseView extends Group implements Borderable {
    protected Image portrait;
    protected Image border = null;
    private TextureRegion borderTexture;

    public BaseView(UnitViewOptions o) {
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
        if (border != null) {
            border.setX(-6);
            border.setY(-6);
            border.setHeight(getHeight() + 12);
            border.setWidth(getWidth() + 12);
        }
    }
}

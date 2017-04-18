package main.libgdx.bf;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class BaseView extends Group implements Borderable {
    protected Image portrait;
    protected Image border = null;

    public BaseView(UnitViewOptions o) {
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
        if (border != null) {
            removeActor(border);
        }

        if (image == null) {
            border = null;
        } else {
            addActor(image);
            border = image;
            updateBorderSize();
        }
    }

    @Override
    public void updateBorderSize() {
        if (border != null) {
            border.setX(-4);
            border.setY(-4);
            border.setHeight(getHeight() + 8);
            border.setWidth(getWidth() + 8);
        }
    }
}

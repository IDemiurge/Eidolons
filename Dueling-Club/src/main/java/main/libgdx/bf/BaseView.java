package main.libgdx.bf;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class BaseView extends Group implements Borderable {
    protected Image portrait;
    private Image border = null;

    public BaseView(UnitViewOptions o) {
        o.getUnitMap().put(o.getObj(), this);//todo fix this shit
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
        }
    }
}

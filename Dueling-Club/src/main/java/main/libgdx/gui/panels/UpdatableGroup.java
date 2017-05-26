package main.libgdx.gui.panels;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.function.Supplier;

public class UpdatableGroup extends Group {
    protected boolean updateRequired;

    @Deprecated
    public void setBackground(Drawable background) {
        if (background instanceof TextureRegionDrawable) {
            final TextureRegionDrawable drawable = ((TextureRegionDrawable) background);
            final TextureRegion region = drawable.getRegion();
            setSize(region.getRegionWidth(), region.getRegionHeight());
        }

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (updateRequired) {
            updateAct(delta);
            afterUpdateAct(delta);
            updateRequired = false;
        }
    }

    public void afterUpdateAct(float delta) {

    }

    public void updateAct(float delta) {

    }

    @Override
    public Object getUserObject() {
        final Object userObject = super.getUserObject();
        if (userObject instanceof Supplier) {
            return ((Supplier) userObject).get();
        } else {
            return userObject;
        }
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        getChildren().forEach(ch -> ch.setUserObject(userObject));
        updateRequired = true;
    }
}

package main.libgdx.gui.panels.dc.newlayout;


import com.badlogic.gdx.scenes.scene2d.Group;

public class BasePanel extends Group {
    protected boolean updateRequired;

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
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        updateRequired = true;
    }
}

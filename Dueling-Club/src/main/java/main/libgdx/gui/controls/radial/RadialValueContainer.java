package main.libgdx.gui.controls.radial;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.libgdx.gui.panels.dc.actionpanel.ActionValueContainer;

import java.util.ArrayList;
import java.util.List;

public class RadialValueContainer extends ActionValueContainer {
    private List<RadialValueContainer> childs = new ArrayList<>();
    private RadialValueContainer parent;

    public RadialValueContainer(TextureRegion texture, String name, String value, Runnable action) {
        super(texture, name, value, action);
    }

    public RadialValueContainer(TextureRegion texture, Runnable action) {
        super(texture, action);
    }

    public RadialValueContainer(TextureRegion texture, String value, Runnable action) {
        super(texture, value, action);
    }

    public RadialValueContainer(String name, String value, Runnable action) {
        super(name, value, action);
    }

    public List<RadialValueContainer> getChilds() {
        return childs;
    }

    public void setChilds(List<RadialValueContainer> childs) {
        this.childs = childs;
    }

    @Override
    public RadialValueContainer getParent() {
        return parent;
    }

    public void setChildVisible(boolean visible) {
        childs.forEach(el -> el.setVisible(visible));
/*            if (visible) {
                updatePosition();
            }*/
    }
}

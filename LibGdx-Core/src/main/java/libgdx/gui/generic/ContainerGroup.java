package libgdx.gui.generic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ContainerGroup extends GroupX {

    @Override
    public void setColor(Color color) {
        super.setColor(color);
        for (Actor child : getChildren()) {
            child.setColor(color);
        }
    }
}

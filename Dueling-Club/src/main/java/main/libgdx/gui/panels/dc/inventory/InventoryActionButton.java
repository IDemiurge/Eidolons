package main.libgdx.gui.panels.dc.inventory;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import main.libgdx.StyleHolder;

public class InventoryActionButton extends Button {
    public InventoryActionButton(String backGroundPath) {
        super(StyleHolder.getCustomButtonStyle(backGroundPath));
    }

    @Override
    public void setDisabled(boolean isDisabled) {
        super.setDisabled(isDisabled);
    }
}

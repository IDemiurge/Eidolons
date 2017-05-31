package main.libgdx.gui.panels.headquarters;

import com.badlogic.gdx.scenes.scene2d.Actor;
import main.libgdx.gui.panels.dc.ValueContainer;

import java.util.List;

/**
 * Created by JustMe on 5/31/2017.
 */
public class ShopListPanel extends Actor {
    private final List<ValueContainer> icons;

    public ShopListPanel(List<ValueContainer> icons) {
        this.icons = icons;
    }
}

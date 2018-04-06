package eidolons.libgdx.gui.panels.headquarters;

import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.libgdx.gui.generic.ValueContainer;

import java.util.List;

/**
 * Created by JustMe on 5/31/2017.
 */
public class ShopValueContainerList extends Actor {
    private final List<ValueContainer> icons;

    public ShopValueContainerList(List<ValueContainer> icons) {
        this.icons = icons;
    }
}

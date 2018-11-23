package eidolons.libgdx.gui.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import eidolons.libgdx.StyleHolder;

/**
 * Created by JustMe on 11/21/2018.
 */
public class ScrollPaneX extends ScrollPane {
    public ScrollPaneX(Actor widget) {
        super(widget, StyleHolder.getScrollStyle());
    }

    public ScrollPaneX(Actor widget, Skin skin) {
        super(widget, skin);
    }

    public ScrollPaneX(Actor widget, Skin skin, String styleName) {
        super(widget, skin, styleName);
    }

    public ScrollPaneX(Actor widget, ScrollPaneStyle style) {
        super(widget, style);
    }
}

package main.libgdx.gui.panels.generic;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import main.libgdx.StyleHolder;

/**
 * Created by JustMe on 1/9/2017.
 */
public class TextComp extends Label {

    public TextComp(CharSequence text) {
        this(text, StyleHolder.getDefaultLabelStyle());
    }

    public TextComp(CharSequence text, LabelStyle style) {
        super(text, style);
//        setWrap(true);

    }
}

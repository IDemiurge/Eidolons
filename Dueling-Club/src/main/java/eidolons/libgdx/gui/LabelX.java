package eidolons.libgdx.gui;

import com.kotcrab.vis.ui.widget.VisLabel;
import eidolons.libgdx.StyleHolder;

/**
 * Created by JustMe on 4/16/2018.
 */
public class LabelX extends VisLabel {

    public LabelX(CharSequence text, int fontSize) {
        super(text, StyleHolder.getHqLabelStyle(fontSize));

    }
}

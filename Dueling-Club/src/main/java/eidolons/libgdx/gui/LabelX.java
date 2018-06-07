package eidolons.libgdx.gui;

import com.kotcrab.vis.ui.widget.VisLabel;
import eidolons.libgdx.StyleHolder;
import main.system.text.TextWrapper;

/**
 * Created by JustMe on 4/16/2018.
 */
public class LabelX extends VisLabel {

    private boolean wrapped;
    private float maxWidth;

    public LabelX(CharSequence text, int fontSize) {
        super(text, StyleHolder.getHqLabelStyle(fontSize));

    }

    public LabelX(String text, LabelStyle sizedLabelStyle) {
        super(text, sizedLabelStyle);
    }

    @Override
    public void setWrap(boolean wrap) {
        wrapped = wrap;
    }

    @Override
    public float getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(float maxWidth) {
        this.maxWidth = maxWidth;
        setWrap(true);
    }

    @Override
    public void setText(CharSequence newText) {
        if (wrapped){
            newText = TextWrapper.processText((int) getMaxWidth()*2/3, newText.toString(), getStyle());
        }
        super.setText(newText);
    }
}

package eidolons.libgdx.gui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.kotcrab.vis.ui.widget.VisLabel;
import eidolons.libgdx.StyleHolder;
import main.system.text.TextWrapper;

/**
 * Created by JustMe on 4/16/2018.
 */
public class LabelX extends VisLabel {

    private boolean wrapped;
    private float maxWidth;
    private float wrapCoef=0.72f;

    public LabelX(CharSequence text, int fontSize) {
        super(text, StyleHolder.getHqLabelStyle(fontSize));

    }

    public LabelX(String text, LabelStyle sizedLabelStyle) {
        super(text, sizedLabelStyle);
    }

    public LabelX() {
        this("",  StyleHolder.getDefaultLabelStyle());
    }

    @Override
    public void setWrap(boolean wrap) {
        wrapped = wrap;
        setText(getText());
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
    public void setWidth(float width) {
        super.setWidth(width);
        setMaxWidth(width);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float a = getStyle().fontColor.a;
        getStyle().fontColor.a = getColor().a * parentAlpha;
        super.draw(batch, parentAlpha);
        getStyle().fontColor.a = a;
    }

    @Override
    public void setText(CharSequence newText) {
        if (wrapped){
            newText = TextWrapper.processText(Math.round(getMaxWidth() * getWrapCoef()), newText.toString(), getStyle());
        }
        super.setText(newText);
    }

    public float getWrapCoef() {
        return wrapCoef;
    }

    public void setWrapCoef(float wrapCoef) {
        this.wrapCoef = wrapCoef;
    }
}

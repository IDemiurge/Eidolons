package main.libgdx.gui.panels.dc.logpanel.text;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;
import main.libgdx.GdxColorMaster;
import main.libgdx.StyleHolder;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 11/14/2017.
 */
public class TextBuilder {
    protected Message Message;

    protected StringBuilder sb;

    public TextBuilder() {
        sb = new StringBuilder();
    }

    public static TextBuilder createNew() {
        return new TextBuilder();
    }

    public Message build(float w) {
        Label l = new Label(sb.toString(), getDefaultLabelStyle()
        );
        l.setWrap(true);
        l.setAlignment(Align.left);
        Message = newMessage();
        Message.setFillParent(true);
        Message.align(Align.bottomLeft);
        Message.add(l).fill().width(w);
        Message.setLayoutEnabled(true);
        Message.pack();
        Message.padTop(5);
        Message.padBottom(5);
        addHoverObjects();


        return Message;
    }

    protected Message newMessage() {
        return new Message();
    }

    protected LabelStyle getDefaultLabelStyle() {
        return
         StyleHolder.getSizedColoredLabelStyle(getAdjustCoef(), getFontStyle(), getFontSize(),
          getColor());
    }

    protected Color getColor() {
        return GdxColorMaster.GOLDEN_WHITE;
    }

    protected float getAdjustCoef() {
        return 0.0f;
    }

    protected int getFontSize() {
        return 20;
    }

    protected FONT getFontStyle() {
        return FONT.RU;
    }

    protected void addHoverObjects() {

    }

    public TextBuilder addString(String s) {
        sb.append(s);
        return this;
    }

    public TextBuilder addString(String s, String color) {
        startColor(color);
        sb.append(s);
        sb.append("[]");
        return this;
    }

    public void startColor(String colorRGBA) {
        sb.append("[");
        if (!colorRGBA.startsWith("#")) {
            sb.append("#");
        }
        sb.append(colorRGBA.toUpperCase());
        sb.append("]");
    }
}

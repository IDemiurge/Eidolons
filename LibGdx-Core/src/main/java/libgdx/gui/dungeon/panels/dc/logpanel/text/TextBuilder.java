package libgdx.gui.dungeon.panels.dc.logpanel.text;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;
import eidolons.content.consts.libgdx.GdxColorMaster;
import libgdx.StyleHolder;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 11/14/2017.
 */
public class TextBuilder {
    protected Message message;
    protected StringBuilder sb;
    LabelStyle style;

    public TextBuilder() {
        this(null);
    }

    public TextBuilder(LabelStyle style) {
        sb = new StringBuilder();
        if (style == null) {
            style = getDefaultLabelStyle();
        }
        this.style = style;
    }

    public static TextBuilder createNew() {
        return new TextBuilder();
    }


    public Message build(float w) {
        Label l = new Label("", style);
        l.setWrap(true);
        l.setAlignment(Align.left);
        l.setWidth(w);
        l.setText(sb.toString());
//        if (isZigZag()){
//            l.setZigZagLines(true);
//            l.setText(sb.toString());
//        }
        message = newMessage();
        message.setFillParent(true);
        message.align(Align.bottomLeft);
        if (w != 0) {
            message.add(l).fill().width(w);
        } else {
            message.add(l).fill().growX();
        }
        message.setLayoutEnabled(true);
        message.pack();
        message.padTop(5);
        message.padBottom(5);
        pad(message);
        addHoverObjects();
        return message;
    }

    protected boolean isZigZag() {
        return false;
    }

    protected void pad(Message message) {
        message.padTop(5);
        message.padBottom(5);
    }

    protected Message newMessage() {
        return new Message();
    }

    protected LabelStyle getDefaultLabelStyle() {
        if (StyleHolder.isHieroOn()) {
            return StyleHolder.getDefaultHiero();
        }
        return
                StyleHolder.getSizedColoredLabelStyle(getAdjustCoef(), getFontStyle(), getFontSize(),
                        getColor());
    }

    protected Color getColor() {
        return GdxColorMaster.PALE_GOLD;
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

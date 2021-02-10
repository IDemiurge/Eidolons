package eidolons.libgdx.gui.panels.dc.logpanel.text;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.StyleHolder;
import main.data.ability.construct.VariableManager;
import main.system.auxiliary.StringMaster;
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

    public static final String COLOR = "#";

    public static String parseColors(String text) {
        StringBuilder resultBuilder = new StringBuilder();
        for (String s : text.split(COLOR)) {
            String key = VariableManager.getVar(s);
            if (!key.isEmpty()) {
            Color color = GdxColorMaster.getColorByName(key);
            if (color != null) {
                resultBuilder.append(wrapInColor(color, s));
                continue;
            }
            }
            resultBuilder.append(s);
        }
        String result = resultBuilder.toString();
        if (result.isEmpty()) {
            return text;
        }
        return result;
    }

    public static String wrapInColor(Color color, String s) {
        return StringMaster.wrapInBrackets("#" + GdxColorMaster.toStringForLog(color))
                + s + StringMaster.wrapInBrackets("");
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

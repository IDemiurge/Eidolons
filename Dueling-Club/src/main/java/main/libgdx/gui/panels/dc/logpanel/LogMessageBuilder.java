package main.libgdx.gui.panels.dc.logpanel;

import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import main.libgdx.StyleHolder;
import main.libgdx.gui.panels.dc.logpanel.text.TextBuilder;

public class LogMessageBuilder extends TextBuilder {

    public static LogMessageBuilder createNew() {
        return new LogMessageBuilder();
    }

    @Override
    public LogMessage build(float w) {
        return (LogMessage) super.build(w);
    }

    @Override
    public LogMessageBuilder addString(String s) {
        return (LogMessageBuilder) super.addString(s);
    }

    @Override
    public LogMessageBuilder addString(String s, String color) {
        return (LogMessageBuilder) super.addString(s, color);
    }

    protected LabelStyle getDefaultLabelStyle() {
        return StyleHolder.getDefaultLabelStyle();
    }

    protected main.libgdx.gui.panels.dc.logpanel.text.Message newMessage() {
        return new LogMessage();
    }
}

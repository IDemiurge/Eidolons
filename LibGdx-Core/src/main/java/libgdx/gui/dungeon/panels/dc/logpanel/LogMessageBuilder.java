package libgdx.gui.dungeon.panels.dc.logpanel;

import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import libgdx.StyleHolder;
import libgdx.gui.dungeon.panels.dc.logpanel.text.TextBuilder;
import libgdx.gui.dungeon.panels.dc.logpanel.text.Message;

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

    protected Message newMessage() {
        return new LogMessage();
    }
}

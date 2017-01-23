package main.libgdx.gui.dialog;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import main.libgdx.StyleHolder;

public class LogMessageBuilder {
    private LogMessage logMessage;

    private StringBuilder sb;

    private LogMessageBuilder() {
        //to prevent direct instance creation
        sb = new StringBuilder();
    }

    public static LogMessageBuilder createNew() {
        return new LogMessageBuilder();
    }

    public LogMessage build(float w) {
        Label l = new Label(sb.toString(), StyleHolder.getDefaultLabelStyle());
        l.setWrap(true);
        l.setAlignment(Align.left);
        logMessage = new LogMessage();
        logMessage.setFillParent(true);
        logMessage.align(Align.bottomLeft);
        logMessage.add(l).fill().width(w);
        logMessage.setLayoutEnabled(true);
        logMessage.pack();
        addHoverObjects();


        return logMessage;
    }

    private void addHoverObjects() {

    }

    public LogMessageBuilder addString(String s) {
        sb.append(s);
        return this;
    }

    public LogMessageBuilder addString(String s, String color) {
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

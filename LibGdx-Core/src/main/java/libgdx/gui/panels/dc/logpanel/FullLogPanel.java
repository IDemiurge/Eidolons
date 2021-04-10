package libgdx.gui.panels.dc.logpanel;

import main.system.GuiEventType;

import static main.system.GuiEventType.FULL_LOG_ENTRY_ADDED;

public class FullLogPanel extends LogPanel {

    public FullLogPanel() {
        this(0, 0);
    }

    public FullLogPanel(int x, int y) {
        super();
        setPosition(x, y);
        setVisible(false);
    }

//    @Override
//    protected void setDefaultSize() {
//        setSize(1280, 720);
//    }

    @Override
    protected GuiEventType getCallbackEvent() {
        return FULL_LOG_ENTRY_ADDED;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            setZIndex(Integer.MAX_VALUE);
        }
    }
}

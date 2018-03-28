package main.libgdx.gui.panels.dc.logpanel;

import main.libgdx.gui.panels.dc.logpanel.text.ScrollTextPanel;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;
import main.system.graphics.ColorManager;

import static main.system.GuiEventType.LOG_ENTRY_ADDED;

/**
 * Created by JustMe on 1/5/2017.
 */
public class LogPanel extends ScrollTextPanel {

    public LogPanel() {
        super(250, 400);
        if (getCallbackEvent() != null)
            bind();
    }

    protected GuiEventType getCallbackEvent() {
        return LOG_ENTRY_ADDED;
    }

    @Override
    protected float getDefaultHeight() {
        return 250;
    }

    @Override
    protected float getDefaultWidth() {
        return 400;
    }

    public void bind() {
        GuiEventManager.bind(getCallbackEvent(), p -> {
            LogMessageBuilder builder = LogMessageBuilder.createNew();

            builder.addString(
             p.get().toString(),
             ColorManager.toStringForLog(ColorManager.GOLDEN_WHITE));

            LogMessage message = builder.build(getWidth() - offsetX);
            message.setFillParent(true);
            scrollPanel.addElement(message);
        });
    }


    protected String getBgPath() {
        return new StrPathBuilder().build_("UI",
         "components",
         "2017",
         "dialog",
         "log"

         , "background.png");
    }


}

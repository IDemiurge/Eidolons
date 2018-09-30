package eidolons.libgdx.gui.panels.dc.logpanel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.libgdx.gui.panels.dc.logpanel.text.ScrollTextPanel;
import eidolons.libgdx.texture.TextureCache;
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

    protected boolean isScrolledAlways() {
        return true;
    }

    protected GuiEventType getCallbackEvent() {
        return LOG_ENTRY_ADDED;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
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


    public void initBg() {
        if (isTiledBg()) {

        } else {
            super.initBg();
        }
    }

    private boolean isTiledBg() {
        return false;
    }

    protected String getBgPath() {
        return new StrPathBuilder().build_("UI",
         "components",
         "dc",
         "dialog",
         "log"
         , "log background.png");
    }


}

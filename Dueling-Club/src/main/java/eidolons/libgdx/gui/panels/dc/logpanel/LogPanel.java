package eidolons.libgdx.gui.panels.dc.logpanel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.panels.ScrollPanel;
import eidolons.libgdx.gui.panels.dc.logpanel.text.Message;
import eidolons.libgdx.gui.panels.dc.logpanel.text.ScrollTextWrapper;
import eidolons.libgdx.texture.Images;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;
import eidolons.system.text.DC_LogManager;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.graphics.ColorManager;

import static main.system.GuiEventType.LOG_ENTRY_ADDED;

/**
 * Created by JustMe on 1/5/2017.
 */
public class LogPanel extends ScrollTextWrapper {

    public LogPanel() {
        super(250, 400);
        if (getCallbackEvent() != null)
            bind();
    }

    protected ScrollPanel<Message> createScrollPanel() {
        return new TextScroll() {

            public int getMaxTableElements() {
                if (!OptionsMaster.getGameplayOptions().getBooleanValue(GAMEPLAY_OPTION.LIMIT_LOG_LENGTH)) {
                    return 0;
                }
                return OptionsMaster.getGameplayOptions().getIntValue(GAMEPLAY_OPTION.LOG_LENGTH_LIMIT);
            }

            @Override
            protected void initAlignment() {
                left().bottom();
            }

            @Override
            protected void pad(ScrollPanel scrollPanel) {
                padScroll(scrollPanel);
            }

            @Override
            protected boolean isAlwaysScrolled() {
                return isScrolledAlways();
            }

            @Override
            public int getDefaultOffsetY() {
                return getInitialYOffset();
            }
        };
    }

    protected boolean isScrolledAlways() {
        return true;
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
            Actor toAdd = null;
            if (p.get() == Images.SEPARATOR_ALT) {
                toAdd = new ImageContainer(Images.SEPARATOR_ALT);
                scrollPanel.addElement(toAdd).center();
                return;
            } else if (p.get() == null) {
                toAdd = new ImageContainer(Images.SEPARATOR);
                scrollPanel.addElement(toAdd).center();
                return;
            }

            LogMessageBuilder builder = LogMessageBuilder.createNew();
            String text = p.get().toString();

            int align = Align.left;
            if (text.contains(DC_LogManager.ALIGN_CENTER)) {
                text = text.replace(DC_LogManager.ALIGN_CENTER, "");
                align = Align.center;
            }
            builder.addString(
                    text,
                    ColorManager.toStringForLog(ColorManager.GOLDEN_WHITE));

            LogMessage message = builder.build(getWidth() - offsetX);
            message.setFillParent(true);
            toAdd = message;
            if (align == Align.center) {
            scrollPanel.addElement(toAdd).center();
            } else {
            scrollPanel.addElement(toAdd).align(align);
            }

        });
    }

    protected GuiEventType getCallbackEvent() {
        return LOG_ENTRY_ADDED;
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
//        return new StrPathBuilder().build_("ui",
//         "components",
//         "dc",
//         "dialog",
//         "log"
//         , "log background.png");
        return null;
    }


}

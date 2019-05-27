package eidolons.libgdx.gui.panels.dc.logpanel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.panels.ScrollPanel;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.dc.logpanel.text.Message;
import eidolons.libgdx.gui.panels.dc.logpanel.text.ScrollTextWrapper;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;
import eidolons.system.text.DC_LogManager;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.graphics.ColorManager;

import java.util.regex.Pattern;

import static main.system.GuiEventType.LOG_ENTRY_ADDED;

/**
 * Created by JustMe on 1/5/2017.
 */
public class LogPanel extends ScrollTextWrapper {

    public LogPanel() {
        super(310, 500);
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
//        if (DungeonScreen.getInstance().getController().isWithinCamera(this)) {
//            return;
//        }  if it worked, could boost performance a bit when rolled out...
        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    protected float getDefaultHeight() {
        return 310;
    }

    @Override
    protected float getDefaultWidth() {
        return 500;
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
            String image = null;
            if (text.contains(DC_LogManager.IMAGE_SEPARATOR)) {
                String[] parts = text.split(Pattern.quote(DC_LogManager.IMAGE_SEPARATOR));
                image = parts[0];
                text = parts[1];
            }

            builder.addString(
                    text,
                    ColorManager.toStringForLog(ColorManager.GOLDEN_WHITE));

            float imageOffset = 0;
            Image img = null;
            if (image != null) {
                img = new Image(TextureCache.getOrCreate(image));
                img.setSize(Math.min(img.getWidth(), 32),
                        Math.min(img.getHeight(), 32));
                imageOffset = img.getWidth();
            }
            LogMessage message = builder.build(getWidth() - offsetX - imageOffset);
            message.setFillParent(true);
            toAdd = message;
            if (image != null) {
                TablePanelX<Actor> table = new TablePanelX<>(
//                        getWidth() - offsetX, Math.max(message.getHeight(), img.getHeight())
                );
                table.defaults().space(2).pad(5).padLeft(7);
                table.add(img);
                table.add(message);
                table.pack();
                toAdd = table;
            }

            if (image != null) {
                scrollPanel.addElement(toAdd).padLeft(16).center();
            } else if (align == Align.center) {
                scrollPanel.addElement(toAdd).center().padLeft(28);
            } else {
                scrollPanel.addElement(toAdd).align(align).padLeft(28);
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

package main.libgdx.gui.panels.dc.logpanel;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.libgdx.GdxMaster;
import main.libgdx.gui.panels.dc.logpanel.text.Message;
import main.libgdx.texture.TextureCache;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.graphics.ColorManager;

import static main.system.GuiEventType.LOG_ENTRY_ADDED;

/**
 * Created by JustMe on 1/5/2017.
 */
public class LogPanel extends Group {
    public final static String path = "UI\\components\\2017\\dialog\\log\\";
    public final static String bgPath = path +
            "background.png";

    protected static final String loremIpsum = "[#FF0000FF]Lorem ipsum[] dolor sit amet, [#00FF00FF]consectetur adipiscing elit[]. [#0000FFFF]Vestibulum faucibus[], augue sit amet porttitor rutrum, nulla eros finibus mauris, nec sagittis mauris nulla et urna. Sed ac orci nec urna ornare aliquam a sit amet neque. Nulla condimentum iaculis dolor, et porttitor dui sollicitudin vel. Fusce convallis fringilla dolor eu mollis. Nam porta augue nec ullamcorper ultricies. Morbi bibendum libero efficitur metus accumsan viverra at ut metus. Duis congue pulvinar ligula, sed maximus tellus lacinia eu.";

    protected boolean updatePos = false;
    protected float offsetX = 20;
    protected ScrollPanel<Message> scrollPanel;

    public LogPanel() {
        setDefaultSize();
        Image bg = new Image(TextureCache.getOrCreateR(bgPath));
        bg.setFillParent(true);
        addActor(bg);

        updatePos = true;

        initScrollPanel();
        bind();
    }

    protected void initScrollPanel() {
        if (scrollPanel != null) {
            scrollPanel.remove();
        }
        scrollPanel = new ScrollPanel<>();

        scrollPanel.pad(1, 10, 1, 10);
        scrollPanel.fill();

        addActor(scrollPanel);
    }

    protected void setDefaultSize() {
        setSize(400* GdxMaster.getFontSizeMod(), 250* GdxMaster.getFontSizeMod());
    }

    protected GuiEventType getCallbackEvent() {
        return LOG_ENTRY_ADDED;
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

    @Override
    public void act(float delta) {
        super.act(delta);
        if (updatePos) {
            updateAct();
            updatePos = false;
        }
    }

    protected void updateAct() {
        scrollPanel.setBounds(10, 10, getWidth() - 20, getHeight() - 20);
    }

    protected LogMessage getTestMessage() {
        return LogMessageBuilder.createNew()
                .addString("Lorem ipsum", "FF0000FF")
                .addString(" dolor sit amet, ")
                .addString("consectetur adipiscing elit", "00FF00FF")
                .addString("Vestibulum faucibus", "0000FFFF")
                .addString(", augue sit amet porttitor rutrum, nulla eros finibus mauris," +
                        " nec sagittis mauris nulla et urna. Sed ac orci nec urna ornare aliquam a sit amet neque. " +
                        "Nulla condimentum iaculis dolor, et porttitor dui sollicitudin vel. " +
                        "Fusce convallis fringilla dolor eu mollis. Nam porta augue nec ullamcorper ultricies. " +
                        "Morbi bibendum libero efficitur metus accumsan viverra at ut metus. " +
                        "Duis congue pulvinar ligula, sed maximus tellus lacinia eu.")
                .build(getWidth() - offsetX);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        Actor actor = super.hit(x, y, touchable);
        if (actor == null) {
            return null;
        }

        if (actor instanceof Image) {
            return null;
        }
        return actor;
    }
}

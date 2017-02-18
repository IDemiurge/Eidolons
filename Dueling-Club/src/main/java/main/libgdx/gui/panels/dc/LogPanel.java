package main.libgdx.gui.panels.dc;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.libgdx.bf.mouse.MovableHeader;
import main.libgdx.gui.dialog.LogMessage;
import main.libgdx.gui.dialog.LogMessageBuilder;
import main.libgdx.texture.TextureManager;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.graphics.ColorManager;

/**
 * Created by JustMe on 1/5/2017.
 */
public class LogPanel extends Group {
    public final static String path = "UI\\components\\2017\\dialog\\log\\";
    public final static String bgPath = path +
            "background.png";

    private static final String loremIpsum = "[#FF0000FF]Lorem ipsum[] dolor sit amet, [#00FF00FF]consectetur adipiscing elit[]. [#0000FFFF]Vestibulum faucibus[], augue sit amet porttitor rutrum, nulla eros finibus mauris, nec sagittis mauris nulla et urna. Sed ac orci nec urna ornare aliquam a sit amet neque. Nulla condimentum iaculis dolor, et porttitor dui sollicitudin vel. Fusce convallis fringilla dolor eu mollis. Nam porta augue nec ullamcorper ultricies. Morbi bibendum libero efficitur metus accumsan viverra at ut metus. Duis congue pulvinar ligula, sed maximus tellus lacinia eu.";
    private MovableHeader movableHeader;
    private ExtendButton extendButton;
    private boolean updatePos = false;
    private float offsetX = 20;
    private ScrollPanel<LogMessage> scrollPanel;

    public LogPanel() {
        setSize(400, 250);
        Image bg = new Image(TextureManager.getOrCreate(bgPath));
        bg.setFillParent(true);
        addActor(bg);

        movableHeader = new MovableHeader();
        movableHeader.setBounds(0, getHeight() - 10, getWidth(), 10);
        movableHeader.addCaptureListener(new InputListener() {
            private Vector2 offset;

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                float x1 = getX();
                float y1 = getY();

                Vector2 vector2 = new Vector2(x, y);
                vector2 = localToParentCoordinates(vector2);

                offset = new Vector2(x1 - vector2.x, y1 - vector2.y);
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                x = Math.min(Math.max(getX() + x + offset.x, 0), getStage().getWidth() - getWidth());
                y = Math.min(Math.max(getY() + y + offset.y, 0), getStage().getHeight() - getHeight());
                setPosition(x, y);
                updatePos = true;
                event.stop();
            }
        });

        addActor(movableHeader);

        extendButton = new ExtendButton();
        addActor(extendButton);

        extendButton.setPosition(getWidth() / 2 - extendButton.getWidth() / 2, getHeight() - movableHeader.getHeight() + 4);

        extendButton.addCaptureListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                event.stop();
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                setHeight(Math.min(Math.max(getHeight() + y, 250), 600));
                updatePos = true;
            }
        });

        updatePos = true;

        scrollPanel = new ScrollPanel<>();

        scrollPanel.pad(1, 10, 1, 10);
        scrollPanel.fill();

        addActor(scrollPanel);
        bind();
    }

    public void bind() {
        GuiEventManager.bind(GuiEventType.LOG_ENTRY_ADDED, p -> {
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
            scrollPanel.setBounds(10, 10, getWidth() - 20, getHeight() - 20);

            movableHeader.setBounds(0, getHeight() - 10, getWidth(), 10);
            extendButton.setPosition(getWidth() / 2 - extendButton.getWidth() / 2, getHeight() - movableHeader.getHeight() + 4);

            updatePos = false;
        }
    }

    private LogMessage getTestMessage() {
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

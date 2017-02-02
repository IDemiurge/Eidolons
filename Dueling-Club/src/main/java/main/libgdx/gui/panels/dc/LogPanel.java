package main.libgdx.gui.panels.dc;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import main.libgdx.bf.mouse.MovableHeader;
import main.libgdx.gui.dialog.LogMessage;
import main.libgdx.gui.dialog.LogMessageBuilder;
import main.libgdx.texture.TextureManager;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 1/5/2017.
 */
public class LogPanel extends Group {
    public final static String path = "UI\\components\\2017\\dialog\\log\\";
    public final static String bgPath = path +
            "background.png";

    private static final String loremIpsum = "[#FF0000FF]Lorem ipsum[] dolor sit amet, [#00FF00FF]consectetur adipiscing elit[]. [#0000FFFF]Vestibulum faucibus[], augue sit amet porttitor rutrum, nulla eros finibus mauris, nec sagittis mauris nulla et urna. Sed ac orci nec urna ornare aliquam a sit amet neque. Nulla condimentum iaculis dolor, et porttitor dui sollicitudin vel. Fusce convallis fringilla dolor eu mollis. Nam porta augue nec ullamcorper ultricies. Morbi bibendum libero efficitur metus accumsan viverra at ut metus. Duis congue pulvinar ligula, sed maximus tellus lacinia eu.";
    private Container<Table> innerScrollContainer;
    private MovableHeader movableHeader;
    private ExtendButton extendButton;
    private boolean updatePos = false;
    private float offsetY = 0;
    private float offsetX = 20;
    private float instantOffsetY = 0;
    private Container<WidgetGroup> container;
    private boolean widgetPosChanged = false;
    private Table table;
    private ScrollPanel<LogMessage> scrollPanel;

    public LogPanel() {
        setSize(400, 250);
        Image bg = new Image(TextureManager.getOrCreate(bgPath));
        bg.setFillParent(true);
        addActor(bg);

        scrollPanel = new ScrollPanel<>();
        scrollPanel.setBounds(15, 15, getWidth() - 30, getHeight() - 30);
        addActor(scrollPanel);

/*        table = new Table();
        table.setFillParent(true);
        //tb.setDebug(true);
        table.align(Align.left);*/
/*
        for (int i = 0; i < 32; i++) {
            if (i != 0) table.row();
            LogMessage message = getTestMessage();
            message.setFillParent(true);
//            table.add(message).fill().padLeft(10).width(getWidth() - 20);
            table.add(message).fill().padLeft(10).width(getWidth() - 20);
            //message.pack();
        }
*/

        //require to calc valid height
        /*table.setLayoutEnabled(true);
        table.pack();

        container = new Container<>();
        container.setBounds(15, 15, getWidth() - 30, getHeight() - 30);
        container.setClip(true);
        addActor(container);
        innerScrollContainer = new InnerScrollContainer<>();
        innerScrollContainer.setWidth(getWidth() - 30);
        innerScrollContainer.setActor(table);
        container.setActor(innerScrollContainer);
        innerScrollContainer.setX(0);
        innerScrollContainer.setY(0);
        innerScrollContainer.setDebug(true);

        container.addCaptureListener(new InputListener() {
            private float yy;

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                event.stop();
                yy = y;
                instantOffsetY = 0;
                offsetY = 0;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                offsetY += instantOffsetY * 6;
                instantOffsetY = 0;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                instantOffsetY += y - yy;
            }

            public boolean scrolled(InputEvent event, float x, float y, int amount) {
                offsetY += amount * 3500;
                return true;
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                getStage().setScrollFocus(innerScrollContainer);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                getStage().setScrollFocus(null);
            }
        });*/

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
        bind();
    }

    public void bind() {
        GuiEventManager.bind(GuiEventType.LOG_ENTRY_ADDED, p -> {
//            LogEntryNode entry = (LogEntryNode) p.get();
            LogMessageBuilder builder = LogMessageBuilder.createNew();
//            entry.getTextLines().forEach(line ->{
            builder.addString(p.get().toString(), "FF0000FF");
//            });

            LogMessage message = builder.build(getWidth() - offsetX);
            message.setFillParent(true);
            scrollPanel.addElement(message);
        });

    }

    @Override
    public void drawDebug(ShapeRenderer shapes) {
        super.drawDebug(shapes);
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

/*        if (!widgetPosChanged && innerScrollContainer.getY() != 0) {
            innerScrollContainer.setY(0);
            widgetPosChanged = true;
        }

        if (0 != ((int) offsetY) || (int) instantOffsetY != 0) {
            float cy = innerScrollContainer.getY();

            float step = (float) Math.sqrt(Math.abs(offsetY)) * 2;

            if (offsetY < 0) {
                step *= -1;
            }

            step += instantOffsetY;

            cy = Math.min(cy + step * delta, 0);

            innerScrollContainer.setY(cy);
            if ((int) offsetY != 0) {
                offsetY -= step;
            }
        }*/
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
        if (actor == null) return null;

        if (actor instanceof Image) return null;
        return actor;
    }
}

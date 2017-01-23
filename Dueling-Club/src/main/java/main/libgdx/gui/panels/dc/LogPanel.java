package main.libgdx.gui.panels.dc;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import main.libgdx.StyleHolder;
import main.libgdx.bf.mouse.MovableHeader;
import main.libgdx.gui.dialog.LogMessage;
import main.libgdx.gui.dialog.LogMessageBuilder;
import main.libgdx.texture.TextureManager;

/**
 * Created by JustMe on 1/5/2017.
 */
public class LogPanel extends Group {
    public final static String path = "UI\\components\\2017\\dialog\\log\\";
    public final static String bgPath = path +
            "background.png";

    private static final String loremIpsum = "[#FF0000FF]Lorem ipsum[] dolor sit amet, [#00FF00FF]consectetur adipiscing elit[]. [#0000FFFF]Vestibulum faucibus[], augue sit amet porttitor rutrum, nulla eros finibus mauris, nec sagittis mauris nulla et urna. Sed ac orci nec urna ornare aliquam a sit amet neque. Nulla condimentum iaculis dolor, et porttitor dui sollicitudin vel. Fusce convallis fringilla dolor eu mollis. Nam porta augue nec ullamcorper ultricies. Morbi bibendum libero efficitur metus accumsan viverra at ut metus. Duis congue pulvinar ligula, sed maximus tellus lacinia eu.";

    Container<Table> widgetGroup;
    MovableHeader movableHeader;
    ExtendButton extendButton;
    private boolean updatePos = false;
    private float offsetY = 0;
    private Container<WidgetGroup> container;
    private boolean widgetPosChanged = false;
    private Table table;

    public LogPanel() {
        setSize(300, 500);
        Image bg = new Image(TextureManager.getOrCreate(bgPath));
        bg.setFillParent(true);
        addActor(bg);

        table = new Table();
        table.setFillParent(true);
        //tb.setDebug(true);
        table.align(Align.left);

        for (int i = 0; i < 32; i++) {
            if (i != 0) table.row();
            LogMessage message = getMessage();
            message.setFillParent(true);
//            table.add(message).fill().padLeft(10).width(getWidth() - 20);
            table.add(message).fill().padLeft(10).width(getWidth() - 20);
            //message.pack();
        }

        //require to calc valid height
        table.setLayoutEnabled(true);
        table.pack();

        container = new Container<>();
        container.setBounds(15, 15, getWidth() - 30, getHeight() - 30);
        container.setClip(true);
        addActor(container);
        widgetGroup = new Container<>();
        widgetGroup.setWidth(getWidth() - 30);
        widgetGroup.setActor(table);
        container.setActor(widgetGroup);
        widgetGroup.setX(0);
        widgetGroup.setY(0);
        widgetGroup.setDebug(true);

        movableHeader = new MovableHeader();
        movableHeader.setBounds(0, getHeight() - 10, getWidth(), 10);
        //movableHeader.setDebug(true);
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
            }
        });

        addActor(movableHeader);

        addCaptureListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                //event.stop();

                return true;
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                getStage().setScrollFocus(LogPanel.this);
            }
        });

        addListener(new InputListener() {
            public boolean scrolled(InputEvent event, float x, float y, int amount) {
                offsetY += amount * 3500;
                return true;
            }
        });

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
                setHeight(Math.min(Math.max(getHeight() + y, 300), 600));
                updatePos = true;
            }
        });

        updatePos = true;
    }

    private static Label getLabel() {
//        Label l = new Label("loremIpsum", StyleHolder.getDefaultLabelStyle());
        Label l = new Label(loremIpsum, StyleHolder.getDefaultLabelStyle());
        l.setWrap(true);
        l.setAlignment(Align.left);
        return l;
    }

    @Override
    public void drawDebug(ShapeRenderer shapes) {
        super.drawDebug(shapes);

/*        if (widgetGroup.getDebug()) {
            shapes.set(ShapeRenderer.ShapeType.Line);
            shapes.setColor(this.getStage().getDebugColor());
            shapes.rect(widgetGroup.getX(), widgetGroup.getY(), widgetGroup.getOriginX(), widgetGroup.getOriginY(), widgetGroup.getWidth(), widgetGroup.getHeight(), widgetGroup.getScaleX(), widgetGroup.getScaleY(), widgetGroup.getRotation());
        }*/
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (updatePos) {
            container.setBounds(10, 10, getWidth() - 20, getHeight() - 20);

            widgetGroup.setY(0);

            movableHeader.setBounds(0, getHeight() - 10, getWidth(), 10);
            extendButton.setPosition(getWidth() / 2 - extendButton.getWidth() / 2, getHeight() - movableHeader.getHeight() + 4);

            updatePos = false;
        }

        if (!widgetPosChanged && widgetGroup.getY() != 0) {
            widgetGroup.setY(0);
            widgetPosChanged = true;
        }

        if (0 != ((int) offsetY)) {
            float cy = widgetGroup.getY();

            float step = (float) Math.sqrt(Math.abs(offsetY)) * 2;
            if (offsetY < 0) {
                step *= -1;
            }

            cy = Math.min(cy + step * delta, 0);

            widgetGroup.setY(cy);
            offsetY -= step;
        }
    }

    private LogMessage getMessage() {
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
                .build(getWidth() - 20);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        Actor actor = super.hit(x, y, touchable);
        if (actor == null) return null;
        if (actor instanceof MovableHeader || actor instanceof ExtendButton) {
            return actor;
        }
        return this;
    }
}

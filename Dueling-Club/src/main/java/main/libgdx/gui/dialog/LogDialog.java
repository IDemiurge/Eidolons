package main.libgdx.gui.dialog;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Align;
import main.libgdx.StyleHolder;
import main.libgdx.bf.mouse.MovableHeader;
import main.libgdx.texture.TextureManager;

/**
 * Created by JustMe on 1/5/2017.
 */
public class LogDialog extends Group {
    public final static String path = "UI\\components\\2017\\dialog\\log\\";
    public final static String bgPath = path +
            "background.png";

    private static final String loremIpsum = "[#FF0000FF]Lorem ipsum[] dolor sit amet, [#00FF00FF]consectetur adipiscing elit[]. [#0000FFFF]Vestibulum faucibus[], augue sit amet porttitor rutrum, nulla eros finibus mauris, nec sagittis mauris nulla et urna. Sed ac orci nec urna ornare aliquam a sit amet neque. Nulla condimentum iaculis dolor, et porttitor dui sollicitudin vel. Fusce convallis fringilla dolor eu mollis. Nam porta augue nec ullamcorper ultricies. Morbi bibendum libero efficitur metus accumsan viverra at ut metus. Duis congue pulvinar ligula, sed maximus tellus lacinia eu.";

    WidgetGroup widgetGroup;
    private boolean updatePos = false;

    public LogDialog() {
        setSize(300, 500);
        Image bg = new Image(TextureManager.getOrCreate(bgPath));
        bg.setFillParent(true);
        addActor(bg);

        Table tb = new Table();
        tb.setFillParent(true);
        //tb.setDebug(true);
        tb.align(Align.left);

        for (int i = 0; i < 32; i++) {
            if (i != 0) tb.row();
            tb.add(getLabel()).fill().padLeft(10).width(getWidth() - 20);
        }

        //require to calc valid height
        tb.setLayoutEnabled(true);
        tb.pack();

        widgetGroup = new WidgetGroup();
        widgetGroup.setWidth(getWidth() - 50);
        widgetGroup.setHeight(tb.getHeight());
        //widgetGroup.setDebug(true);
        widgetGroup.addActor(tb);

        Actor movableHeader = new MovableHeader();
        movableHeader.setBounds(0, getHeight() - 10, getWidth(), 10);
        //movableHeader.setDebug(true);
        movableHeader.addCaptureListener(new InputListener() {
            private Vector2 offset;

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                float x1 = LogDialog.this.getX();
                float y1 = LogDialog.this.getY();

                Vector2 vector2 = new Vector2(x, y);
                vector2 = localToParentCoordinates(vector2);

                offset = new Vector2(x1 - vector2.x, y1 - vector2.y);
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                x = Math.min(Math.max(LogDialog.this.getX() + x + offset.x, 0), getStage().getWidth() - LogDialog.this.getWidth());
                y = Math.min(Math.max(LogDialog.this.getY() + y + offset.y, 0), getStage().getHeight() - LogDialog.this.getHeight());
                LogDialog.this.setPosition(x, y);
                updatePos = true;
            }
        });

        addActor(movableHeader);

        addCaptureListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                //event.stop();
                getStage().setScrollFocus(LogDialog.this);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //widgetGroup.setY(widgetGroup.getY() + 10);
            }
        });

        addListener(new InputListener() {
            public boolean scrolled(InputEvent event, float x, float y, int amount) {
                widgetGroup.setY(widgetGroup.getY() + amount * 10);
                return true;
            }
        });

/*        ScrollPane sp = new ScrollPane(widgetGroup);
        sp.setFillParent(true);
        //sp.setDebug(true);
        sp.setForceScroll(false, true);
        sp.setScrollBarPositions(true, true);*/

/*        Table table = new Table();
        table.setFillParent(true);*/
        //table.add(sp).fill().expand();
        //table.setDebug(true);

        //addActor(sp);
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

        if (widgetGroup.getDebug()) {
            shapes.set(ShapeRenderer.ShapeType.Line);
            shapes.setColor(this.getStage().getDebugColor());
            shapes.rect(widgetGroup.getX(), widgetGroup.getY(), widgetGroup.getOriginX(), widgetGroup.getOriginY(), widgetGroup.getWidth(), widgetGroup.getHeight(), widgetGroup.getScaleX(), widgetGroup.getScaleY(), widgetGroup.getRotation());
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (updatePos) {
            //Vector2 v2 = new Vector2(getX(), getY()+10);
            //v2 = localToParentCoordinates(v2);
            widgetGroup.setX(getX());
            widgetGroup.setY(getY() + 10);
            updatePos = false;
        }
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        Actor actor = super.hit(x, y, touchable);
        if (actor == null) return null;
        if (actor instanceof MovableHeader) {
            return actor;
        }
        return this;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        Rectangle scissorBounds = new Rectangle(getX(), getY() + 10, getWidth(), getHeight() - 20);

        if (ScissorStack.pushScissors(scissorBounds)) {
            widgetGroup.draw(batch, parentAlpha);
            batch.flush();
            ScissorStack.popScissors();
        }
    }
}

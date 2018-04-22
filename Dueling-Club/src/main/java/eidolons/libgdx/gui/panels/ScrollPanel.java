package eidolons.libgdx.gui.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public class ScrollPanel<T extends Actor> extends Container<Container> {


    private TablePanel table;
    private InnerScrollContainer<Table> innerScrollContainer;
    private int instantOffsetY;
    private int offsetY;
    private boolean widgetPosChanged;

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        table.setSize(width, height);
        innerScrollContainer.setSize(width, height);
        table.setFixedSize(true);
    }

    public ScrollPanel() {
        init();
    }

    public InnerScrollContainer<Table> getInnerScrollContainer() {
        return innerScrollContainer;
    }

    @Override
    @Deprecated
    public Container getActor() {
        throw new UnsupportedOperationException("Do not use this!");
    }

    @Override
    @Deprecated
    public void setActor(Container actor) {
        throw new UnsupportedOperationException("Use ScrollPanel#addElement.");
    }

    public void addElement(T obj) {
        table.addNormalSize(obj).fill();
        table.row();
        table.pack();
        offsetY = 200;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            getStage().setScrollFocus(ScrollPanel.this);
        }
    }

    private void init() {
        this.setTouchable(Touchable.enabled);
        left().bottom();
        setClip(true);

        table = new TablePanel();
        table.setFillParent(true);
        table.align(Align.left);

        table.setLayoutEnabled(true);
        table.pack();

        innerScrollContainer = new InnerScrollContainer<>();
        innerScrollContainer.left().bottom();
        innerScrollContainer.setActor(table);
        innerScrollContainer.setX(0);
        innerScrollContainer.setY(0);
        super.setActor(innerScrollContainer);

        addCaptureListener(new InputListener() {
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
                getStage().setScrollFocus(ScrollPanel.this);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                getStage().setScrollFocus(null);
            }
        });
    }

    @Override
    protected void positionChanged() {
        super.positionChanged();
    }

    @Override
    public void act(float delta) {
        if (!widgetPosChanged && innerScrollContainer.getY() != 0) {
            innerScrollContainer.setY(0);
            widgetPosChanged = true;
        }

        if (0 != offsetY || instantOffsetY != 0) {
            float cy = innerScrollContainer.getY();

            float step = (float) Math.sqrt(Math.abs(offsetY)) * 2;

            if (offsetY < 0) {
                step *= -1;
            }

            step += instantOffsetY;

            cy = Math.min(cy + step * delta, 0);

            innerScrollContainer.setY(cy);
            if (offsetY != 0) {
                offsetY -= step;
            }
        }
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        innerScrollContainer.setWidth(getWidth() - 30);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        Actor a = super.hit(x, y, touchable);
        if (a == null) {
            return null;
        }
        return this;

    }
}

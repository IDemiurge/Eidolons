package eidolons.libgdx.gui.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import main.system.auxiliary.StringMaster;

public class ScrollPanel<T extends Actor> extends Container<Container> {


    private TablePanel table;
    private InnerScrollContainer<Table> innerScrollContainer;
    private int instantOffsetY;
    private int offsetY;
    private boolean widgetPosChanged;

    public ScrollPanel() {
        init();
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        table.setSize(width, height);
        innerScrollContainer.setSize(width, height);
        table.setFixedSize(true);
        offsetY = getDefaultOffsetY();
    }

    public int getDefaultOffsetY() {
        return 0;
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

    public Cell addElement(T obj) {
        Cell cell = table.addNormalSize(obj).fill();
        table.row();
        table.pack();
        offsetY = 200;
        return cell;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            getStage().setScrollFocus(ScrollPanel.this);
        }
    }

    protected void pad(ScrollPanel<T> tScrollPanel) {
    }
    private void init() {
        this.setTouchable(Touchable.enabled);
        left().bottom();
        pad(this);
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
               if (amount<0)
                   if (innerScrollContainer.getY()<=-innerScrollContainer.getHeight())
                   {
                       offsetY=0;
                       return true;
                   }
                offsetY += amount *6000;// getScrollAmount();
                return true;
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (getStage() != null)
                    getStage().setScrollFocus(ScrollPanel.this);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (getStage() != null)
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
        super.act(delta);
        if (!isAlwaysScrolled())
            if (getHeight() >= innerScrollContainer.getHeight())
                return;
        if (!widgetPosChanged && innerScrollContainer.getY() != 0) {
            innerScrollContainer.setY(0);
            widgetPosChanged = true;
        }

        if (0 != offsetY ||
         instantOffsetY != 0) {
            float cy = innerScrollContainer.getY();
           if (offsetY<0)
               if (cy<=-innerScrollContainer.getHeight()/2) {
                return;
            }

            float step =
             new Float(StringMaster.formatFloat(2,
              (float) Math.sqrt(Math.abs(offsetY)))) * 5;

            if (offsetY < 0) {
                step *= -1;
            }

            step += instantOffsetY;

            cy = Math.min(cy + step * delta, innerScrollContainer.getHeight()/2);
            if (Math.abs(innerScrollContainer.getY() - cy) > 6f)
                innerScrollContainer.setY(cy);
            if (offsetY != 0) {
                offsetY -= step;
            }
        }
    }


    protected boolean isAlwaysScrolled() {
        return false;
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

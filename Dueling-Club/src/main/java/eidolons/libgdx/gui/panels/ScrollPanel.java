package eidolons.libgdx.gui.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.system.options.ControlOptions.CONTROL_OPTION;
import eidolons.system.options.OptionsMaster;
import main.system.auxiliary.NumberUtils;

public class ScrollPanel<T extends Actor> extends Container  {


    protected static Integer scrollAmount;
    protected InnerScrollContainer<Table> innerScrollContainer;
    protected float instantOffsetY;
    protected float offsetY;
    protected boolean widgetPosChanged;
    private TablePanelX table;
    private ScrollPane scroll;

    public ScrollPanel() {
        init();
        offsetY = getDefaultOffsetY();
    }

    public ScrollPane getScroll() {
        return scroll;
    }

    protected boolean isLibgdxImpl() {
        return true;
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        getTable().setSize(width, height);
        innerScrollContainer.setSize(width, height);
        getTable().setFixedSize(true);
//        offsetY = getDefaultOffsetY(); wtfffff
    }

    public int getDefaultOffsetY() {
        return 0;
    }

    public InnerScrollContainer<Table> getInnerScrollContainer() {
        return innerScrollContainer;
    }

    public Cell addElement(T obj) {
        Cell cell = getTable().addNormalSize(obj).fill();
        getTable().row();
        getTable().pack();
        if (isLibgdxImpl()) {
            scroll.setScrollPercentY(105);
        }
        if (isFadeIn()){
            ActorMaster.addFadeInAction(obj, 1);
        }
//        else
        if (offsetY == 0)
            offsetY = 200;
        checkClear();
        return cell;
    }

    private boolean isFadeIn() {
        return true;
    }

    private void checkClear() {
        int max = getMaxTableElements();
        if (max != 0) {
            while (table.getChildren().size > max) {
                Actor child = table.getChildren().first();
//                offsetY -= child.getHeight();
                child.remove();
            }
        }
    }

    public int getMaxTableElements() {
        return 0;
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

    protected void init() {
        this.setTouchable(Touchable.enabled);
        initAlignment();
        pad(this);
        setClip(true);

        setTable(new TablePanelX());
        getTable().setFillParent(true);
        getTable().align(getInnerTableAlignment());

        getTable().setLayoutEnabled(true);
        getTable().pack();
        innerScrollContainer = new InnerScrollContainer<>();
        alignInnerScroll();
        innerScrollContainer.setActor(getTable());
        innerScrollContainer.setX(0);
        innerScrollContainer.setY(0);

        if (isLibgdxImpl()) {
            ScrollPaneStyle style = StyleHolder.getScrollStyle();
            super.setActor(scroll = new ScrollPane(innerScrollContainer, style));
            scroll.setFillParent(true);
            scroll.setClamp(true);
//            scroll.setCullingArea(true);
            scroll.setFlingTime(2);
            scroll.setForceScroll(false, true);
            scroll.setScrollingDisabled(true, false);
            scroll.setVariableSizeKnobs(false);

//            scroll.setupFadeScrollBars();
//            scroll.setFlickScroll(false);
//            scroll.setFlickScrollTapSquareSize(1);
        } else {
            super.setActor(innerScrollContainer);
            initScrollListener();
        }

    }

    protected void initAlignment() {
        left().top();
    }

    protected int getInnerTableAlignment() {
        return Align.top;
    }

    protected void alignInnerScroll() {
        innerScrollContainer.left().bottom();
    }

    public void initScrollListener() {
        clearListeners();
        addCaptureListener(new InputListener() {
            protected float yy;

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (isTouchScrolled()) {
                    event.stop();
                    yy = y;
                    instantOffsetY = 0;
                    offsetY = 0;
                    return true;
                }
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (!isTouchScrolled()) {
                    return;
                }
                offsetY += instantOffsetY * 6;
                instantOffsetY = 0;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if (isTouchScrolled()) {
                    instantOffsetY += y - yy;
                }
            }

            public boolean scrolled(InputEvent event, float x, float y, int amount) {
                if (amount < 0)
                    if (innerScrollContainer.getY() <= -innerScrollContainer.getHeight()) {
                        offsetY = 0;
                        return true;
                    }
                offsetY += amount * getScrollAmount();
                return true;
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (getStage() != null)
                    getStage().setScrollFocus(ScrollPanel.this);
                super.enter(event, x, y, pointer, fromActor);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (getStage() != null) {
                    if (toActor != ScrollPanel.this)
                        if (!GdxMaster.getAncestors(toActor).contains(ScrollPanel.this))
                            getStage().setScrollFocus(null);
                }
                super.exit(event, x, y, pointer, toActor);
            }
        });
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        table.setUserObject(userObject);
    }

    protected boolean isTouchScrolled() {
        return false;
    }

    @Override
    protected void positionChanged() {
        super.positionChanged();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!isAlwaysScrolled())
            if (getLowerLimit() >0)
                if (getUpperLimit()>0)
            if (getHeight() >= innerScrollContainer.getHeight())
                return;
        if (!widgetPosChanged && innerScrollContainer.getY() != 0) {
            innerScrollContainer.setY(0);
            widgetPosChanged = true;
        }

        if (0 != offsetY ||
         instantOffsetY != 0) {
            float cy = innerScrollContainer.getY();
            if (offsetY < 0) {
                if (getUpperLimit()>0)
                if (cy <= -getUpperLimit()) {
                    return;
                }
            } else {
                if (getLowerLimit() >0)
                    if (cy >= getLowerLimit()) {
                    return;
                }
            }

            float step =
             new Float(NumberUtils.formatFloat(2,
              (float) Math.sqrt(Math.abs(offsetY)))) * 5;
            if (offsetY < 0) {
                step = Math.max(offsetY, -step);
            } else {
                step = Math.min(offsetY, step);
            }

            step += instantOffsetY;

            cy = Math.min(cy + step * delta, innerScrollContainer.getHeight() / 2);
//            if (isLibgdxImpl()) {
//                scroll.setScrollY(cy);
//            } else
                {
                if (Math.abs(innerScrollContainer.getY() - cy) > 6f)
                    innerScrollContainer.setY(cy);
            }

            if (offsetY != 0) {
                offsetY -= step;
            }
        }
    }

    protected float getLowerLimit() {
        return getHeight();// * 0.82f;
    }

    protected float getUpperLimit() {
        return innerScrollContainer.getHeight();// * 0.73f;
    }


    protected boolean isAlwaysScrolled() {
        return true;
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
        if (getStage() == null) {
            return null;
        }
        if (isLibgdxImpl()) {
            getStage().setScrollFocus(scroll);
        } else
        getStage().setScrollFocus(this);
        return a;
    }

    public Integer getScrollAmount() {
        if (scrollAmount == null) {
            scrollAmount = 60 * OptionsMaster.getControlOptions().getIntValue(CONTROL_OPTION.SCROLL_SPEED);
        }
        return scrollAmount;
    }

    public static void setScrollAmount(Integer scrollAmount) {
        ScrollPanel.scrollAmount = 60 * scrollAmount;
    }

    public TablePanelX getTable() {
        return table;
    }

    public void setTable(TablePanelX table) {
        this.table = table;
    }

}

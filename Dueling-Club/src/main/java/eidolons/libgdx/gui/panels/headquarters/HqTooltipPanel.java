package eidolons.libgdx.gui.panels.headquarters;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.tooltips.Tooltip;
import main.system.auxiliary.RandomWizard;

import java.util.Collection;

public class HqTooltipPanel extends TablePanelX {

    public static final float WIDTH = 820;
    public static final float INNER_WIDTH = WIDTH * 0.8f;
    ScrollPane scrollPane;
    private Container<Actor> inner;

    public HqTooltipPanel() {
        init();
    }

    public void init() {
        setSize(WIDTH, 200);
        add(scrollPane = new ScrollPane(inner = new Container<Actor>() {
            @Override
            public float getPrefHeight() {
//                return super.getPrefHeight();
                return  getHeight();
            }
        }) {
            @Override
            public float getPrefHeight() {
//                return super.getPrefHeight();
                return getActor().getHeight()*1.8f;
            }
        });
        inner.setSize(1.2f * WIDTH, 200);
        inner.
                setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());
        inner.
                setBackground(NinePatchFactory.getHqDrawable());
        setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());
        scrollPane.setStyle(StyleHolder.getScrollStyle());
        scrollPane.setClamp(true);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);

    }

    @Override
    public float getMinWidth() {
        return super.getWidth();
    }

    @Override
    public float getMinHeight() {
        return super.getHeight();
    }

    @Override
    protected Drawable getDefaultBackground() {
        return new NinePatchDrawable(NinePatchFactory.getLightDecorPanelDrawable());
    }

    public void init(Tooltip tooltip) {
        init(tooltip, true);
    }

    public void init(Tooltip tooltip, boolean first) {
//        scrollPane.setScrollBarPositions(true, false);
        inner
                .setActor(tooltip);



        scrollPane.setScrollbarsOnTop(true);
        Actor actor = null;

        if (tooltip.getUserObject() instanceof Collection) {
            actor = (Actor) ((Collection) tooltip.getUserObject()).iterator().next();
        } else {
            if (tooltip.getUserObject() instanceof Actor) {
                actor = (ValueContainer) tooltip.getUserObject();
            }
        }
        if (actor == null) {
            actor = tooltip;
        }
//        tooltip.setActorWidth(getWidth()-50);

        if (actor instanceof Table) {
            inner.setHeight(((Table) actor).getPrefHeight() + 15);
        } else {
            inner.setHeight(actor.getHeight() + 15);
        }

        if (actor instanceof ValueContainer) {
            Label label = ((ValueContainer) actor).getNameLabel();
            tooltip.setHeight(((ValueContainer) actor).getPrefHeight());
//            tooltip.setWidth(getWidth()- scrollPane.getScrollWidth());
//            tooltip.setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());
            scrollPane.setScrollY(tooltip.getHeight());
            if (label == null) {
                label = ((ValueContainer) actor).getValueLabel();
            }
//            label.setWidth(getWidth()*2);
//            label.setWrap(true);
            label.setAlignment(Align.left);
            tooltip.pad(5);
//            label.setText(label.getText());
            label = ((ValueContainer) actor).getValueLabel();
            if (label != null) {
//            label.setWidth(getWidth());
            }
        }
//        tooltip.pack();
        tooltip.removeBackground();

        if (inner.getHeight() < 300) {
            scrollPane.setForceScroll(false, false);
        } else
            scrollPane.setForceScroll(false, true);

        scrollPane.setScrollY(inner.getHeight() / 4 + RandomWizard.getRandomInt(12 + (int)
                (tooltip.getHeight() / 3)));
        scrollPane.updateVisualScroll();
        getStage().setScrollFocus(scrollPane);
        if (first)
            for (int i = 0; i < 3; i++) {
                init(tooltip, false);
            }

    }

    @Override
    public void act(float delta) {
        if (inner != null)
        if (inner.getActor() != null) {
        if (inner.getHeight() != inner.getActor().getHeight()) {
//            if (inner.getHeight() < 200) {
//                scrollPane.setForceScroll(false, false);
//            } else{
                scrollPane.setForceScroll(false, true);
                inner.setHeight(inner.getActor().getHeight());
//                scrollPane.setScrollY(inner.getHeight() / 4 + RandomWizard.getRandomInt(12 + (int)
//                        (inner.getHeight() / 3)));
                scrollPane.updateVisualScroll();
//            }
            scrollPane.layout(); //TODO igg demo fix good that it works, but why do I have to do this?
        }
        }

        super.act(delta);
    }
}

package eidolons.libgdx.gui.panels.headquarters;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.ScrollPanel;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.dc.logpanel.text.ScrollTextWrapper;
import eidolons.libgdx.gui.tooltips.Tooltip;
import eidolons.system.options.GameplayOptions;
import eidolons.system.options.OptionsMaster;
import main.system.auxiliary.RandomWizard;

import java.util.Collection;
import java.util.Collections;

public class HqTooltipPanel extends TablePanelX {

    ScrollPane scrollPane;
    private Container<Actor> inner;

    public HqTooltipPanel() {
        init();
    }

    public void init() {
        setSize(700, 200);
        add(scrollPane = new ScrollPane(inner= new Container<Actor>(){
            @Override
            public float getPrefHeight() {
                return getHeight();
            }
        }){
            @Override
            public float getPrefHeight() {
                return getActor().getHeight();
            }
        });
        inner.setSize(700, 200);
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
        inner
                .setActor(tooltip);
        Actor actor = null;

        if (tooltip.getUserObject() instanceof Collection) {
            actor = (Actor) ((Collection) tooltip.getUserObject()).iterator().next();
        } else actor =
                (Actor) tooltip.getUserObject();
//        tooltip.setActorWidth(getWidth()-50);


        if (actor instanceof ValueContainer) {
            Label label = ((ValueContainer) actor).getNameLabel();
            tooltip.setHeight(((ValueContainer) actor).getPrefHeight());
            tooltip.setWidth(getWidth());
            inner.setHeight(tooltip.getHeight()*1.5f);
            scrollPane.setScrollY(tooltip.getHeight());
            if (label == null) {
                label =((ValueContainer) actor).getValueLabel();
            }
//            label.setWidth(getWidth()*2);
//            label.setWrap(true);
            label.setAlignment(Align.left);
            tooltip.pad(20);
//            label.setText(label.getText());
            label =((ValueContainer) actor).getValueLabel();
            if (label != null) {
//            label.setWidth(getWidth());
            }
        }
        tooltip.pack();
        tooltip.removeBackground();

        scrollPane.setScrollY(inner.getHeight() / 4 + RandomWizard.getRandomInt(25 + (int)
                (tooltip.getHeight() / 3)));
        scrollPane.updateVisualScroll();
        getStage().setScrollFocus(scrollPane);
    }

}

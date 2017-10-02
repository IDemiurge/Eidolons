package main.libgdx.gui.controls.radial;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import main.libgdx.anims.ActorMaster;
import main.libgdx.bf.mouse.BattleClickListener;
import main.libgdx.gui.panels.dc.actionpanel.ActionValueContainer;
import main.libgdx.gui.tooltips.ToolTip;
import main.system.auxiliary.data.ListMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RadialValueContainer extends ActionValueContainer {
    Runnable lazyChildInitializer;
    private List<RadialValueContainer> childNodes = new ArrayList<>();
    private RadialValueContainer parent;
    private Supplier<ToolTip> tooltipSupplier;
    private ToolTip tooltip;

    public RadialValueContainer(TextureRegion texture, String name, String value, Runnable action) {
        super(texture, name, value, action);
    }

    public RadialValueContainer(TextureRegion texture, Runnable action) {
        super(texture, action);
    }

    public RadialValueContainer(TextureRegion texture, String value, Runnable action) {
        super(texture, value, action);
    }

    public RadialValueContainer(TextureRegion texture, String value, Runnable action, Runnable lazyChildInitializer) {
        super(texture, value, action);
        this.lazyChildInitializer = lazyChildInitializer;
//if (isAddListener())
        addListener(new BattleClickListener(){
            boolean hover;
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                if (hover)
                    return false;
                hover =true;
                ActorMaster.addScaleAction(RadialValueContainer.this, 1.2f,1.2f, 0.7f);
                return super.mouseMoved(event, x, y);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (!hover) {
                    hover = true;
                    ActorMaster.addScaleAction(RadialValueContainer.this, 1.2f,1.2f, 0.7f);
                }
                super.enter(event, x, y, pointer, fromActor);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                hover =false;
                ActorMaster.addScaleAction(RadialValueContainer.this, 1  ,1 , 0.7f);
                super.exit(event, x, y, pointer, toActor);
            }
        });
    }

    public List<RadialValueContainer> getChildNodes() {
        if (!ListMaster.isNotEmpty(childNodes))
            if (lazyChildInitializer != null)
                lazyChildInitializer.run();
        return childNodes;
    }

    public void setChildNodes(List<RadialValueContainer> childNodes) {
        this.childNodes = childNodes;
    }

    @Override
    public RadialValueContainer getParent() {
        return parent;
    }

    public void setParent(RadialValueContainer parent) {
        this.parent = parent;
    }

    public void setChildVisible(boolean visible) {
        childNodes.forEach(el -> el.setVisible(visible));

    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible)
            if (tooltip == null)
                if (getTooltipSupplier() != null) {
                    try {
                        tooltip = tooltipSupplier.get();
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    }
                    if (tooltip != null)
                    addListener(tooltip.getController());

                }
    }

    public Supplier<ToolTip> getTooltipSupplier() {
        return tooltipSupplier;
    }

    public void setTooltipSupplier(Supplier<ToolTip> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
    }
}

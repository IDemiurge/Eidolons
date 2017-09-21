package main.libgdx.gui.controls.radial;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.libgdx.gui.panels.dc.actionpanel.ActionValueContainer;
import main.libgdx.gui.tooltips.ToolTip;
import main.system.auxiliary.data.ListMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RadialValueContainer extends ActionValueContainer {
    private List<RadialValueContainer> childNodes = new ArrayList<>();
    private RadialValueContainer parent;
    Runnable lazyChildInitializer;
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
    }

    public List<RadialValueContainer> getChildNodes() {
        if (!ListMaster.isNotEmpty(childNodes))
            if (lazyChildInitializer!=null)
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
            if (tooltip ==null )
                if (getTooltipSupplier()!=null ){
                 tooltip = tooltipSupplier.get();
                    addListener(tooltip.getController());

            }
    }

    public void setTooltipSupplier(Supplier<ToolTip> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
    }

    public Supplier<ToolTip> getTooltipSupplier() {
        return tooltipSupplier;
    }
}

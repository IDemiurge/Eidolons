package main.client.cc.gui.neo.tooltip;

import main.entity.Entity;
import main.entity.obj.DC_HeroObj;
import main.swing.generic.components.ComponentVisuals;
import main.swing.generic.components.G_Panel;
import main.system.auxiliary.secondary.BooleanMaster;

import java.awt.*;

public class ToolTipPanel extends G_Panel {

    Entity item;
    DC_HeroObj hero;
    ToolTipTextComp text;
    private Boolean prompted;

    public ToolTipPanel() {
        text = new ToolTipTextComp();
        text.setDefaultSize(new Dimension(getVisuals(false).getWidth() - 30, getVisuals(false)
                .getHeight() - 30));
        setPanelSize(getVisuals(BooleanMaster.isTrue(isPrompted())).getSize());
    }

    public void refresh() {
        removeAll();
        setVisuals(getVisuals(BooleanMaster.isTrue(isPrompted())));
        setPanelSize(getVisuals(BooleanMaster.isTrue(isPrompted())).getSize());
        add(text, "pos 10 10");
        revalidate();
        text.setReq(prompted);
        text.setHero(getHero());
        text.setItem(getItem());
        text.refresh();

        super.refresh();
    }

    private ComponentVisuals getVisuals(boolean prompted) {
        return prompted ? VISUALS.TOOLTIP_PANEL : VISUALS.TOOLTIP_PANEL_HIGHLIGHTED;
    }

    public Boolean isPrompted() {
        return prompted;
    }

    public Entity getItem() {
        return item;
    }

    public void setItem(Entity item) {
        this.item = item;
    }

    public DC_HeroObj getHero() {
        return hero;
    }

    public void setHero(DC_HeroObj hero) {
        this.hero = hero;
    }

    public void setPrompted(Boolean prompted) {
        this.prompted = prompted;
    }

}

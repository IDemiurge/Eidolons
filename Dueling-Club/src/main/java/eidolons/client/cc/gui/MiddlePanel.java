package eidolons.client.cc.gui;

import eidolons.client.cc.CharacterCreator;
import eidolons.client.cc.gui.MainPanel.HERO_TABS;
import eidolons.client.cc.gui.MainViewPanel.HERO_VIEWS;
import eidolons.client.cc.gui.lists.ItemListManager;
import eidolons.client.cc.gui.misc.AddRemoveComponent;
import eidolons.client.cc.gui.neo.points.StatsControlComponent;
import eidolons.client.cc.gui.neo.slide.MasterySlidePanel;
import eidolons.client.cc.gui.neo.tabs.HC_StatsTab;
import eidolons.client.cc.gui.neo.tooltip.ToolTipPanel;
import eidolons.client.cc.gui.pages.HC_PagedInfoPanel;
import eidolons.client.cc.gui.views.HeroItemView;
import eidolons.entity.obj.unit.Unit;
import eidolons.swing.components.panels.page.info.DC_PagedInfoPanel;
import main.entity.Entity;
import main.swing.generic.components.G_Panel;

public class MiddlePanel extends G_Panel {

    private HC_PagedInfoPanel upperPanel;
    private HC_PagedInfoPanel lowerPanel; // always same size or not?

    private AddRemoveComponent arc;

    private MainViewPanel mvp;
    private HERO_VIEWS view;
    private StatsControlComponent scc;
    private Unit hero;
    private ToolTipPanel toolTipPanel;

    public MiddlePanel(MainViewPanel mvp, Unit hero) {
        this.hero = hero;
        this.mvp = mvp;
        upperPanel = new HC_PagedInfoPanel();
        lowerPanel = new HC_PagedInfoPanel();
        scc = new StatsControlComponent(hero);
        scc.init();
        arc = new AddRemoveComponent(upperPanel, lowerPanel, hero); // special
        toolTipPanel = new ToolTipPanel();
    }

    public boolean isTooltipPanelDisplayed() {
        switch (view) {
            case CLASS_TREE:
            case SKILL_TREE:
            case CLASSES:
            case SKILLS:
            case LIBRARY:
                return true;
        }
        return false;
    }

    public void resetBuffer() {
        ((HC_StatsTab) CharacterCreator.getHeroPanel(hero).getTab(HERO_TABS.STATS)).getView()
         .setBuffer(scc.getBufferType());
        ((MasterySlidePanel) mvp.getView(HERO_VIEWS.STATS)).getCurrentView().setBuffer(
         scc.getBufferType());

        // if (initialized) {
        CharacterCreator.getHeroPanel(hero).getCurrentTab().refresh();
        mvp.getView(view).refresh();
        // }
    }

    private void addComps() {
        if (view == HERO_VIEWS.STATS) {
            scc.setMp(this);
            scc.resetBuffer();
            add(scc, "id scc, pos 0 0");
            resetBuffer();
            return;
        } else {
            add(upperPanel, "id up, pos 0 0");
            add(arc, "id arc, pos 0 up.y2");
            add(lowerPanel, "id lp, pos 0 arc.y2");
        }
        if (isTooltipPanelDisplayed()) {
            add(toolTipPanel, "id toolTipPanel, pos 0 lp.y2");
            toolTipPanel.repaint();
        }
    }

    protected ItemListManager getItemManager() {
        try {
            return mvp.getItemManager();
        } catch (Exception e) {
            return null;
        }
    }

    public void setUpperPanelInfoObj(Entity entity) {
        upperPanel.setEntity(entity);
        upperPanel.refresh();
    }

    public void setLowerPanelInfoObj(Entity entity) {
        upperPanel.setEntity(entity);
        lowerPanel.refresh();
    }

    @Override
    public void refresh() {
        removeAll();
        addComps();
        // if (view == HERO_VIEWS.STATS)
        scc.refresh();
        // else
        arc.refresh();

        if (hasItemManager()) {
            getItemManager().setHeroInfoPanel(lowerPanel);
            getItemManager().setItemInfoPanel(upperPanel);
        } else {
            // attrs should also have selection listener!
        }

        revalidate();
        initialized = true;
    }

    private boolean hasItemManager() {
        try {
            return (mvp.getView(view) instanceof HeroItemView);
        } catch (Exception e) {
            return false;
        }
    }

    public void activateView(HERO_VIEWS linkedView) {
        this.view = linkedView;
        if (view == HERO_VIEWS.STATS) {
            scc.resetBackupBuffer();
        }
        arc.setView(linkedView);
        arc.refresh();
        if (isTooltipPanelDisplayed()) {
            toolTipPanel.setPrompted(null);
            toolTipPanel.refresh();
        }
        refresh();
    }

    public synchronized AddRemoveComponent getArc() {
        return arc;
    }

    public DC_PagedInfoPanel getUpperPanel() {
        return upperPanel;
    }

    public DC_PagedInfoPanel getLowerPanel() {
        return lowerPanel;
    }

    public MainViewPanel getMvp() {
        return mvp;
    }

    public HERO_VIEWS getView() {
        return view;
    }

    public StatsControlComponent getScc() {
        return scc;
    }

    public Unit getHero() {
        return hero;
    }

    public ToolTipPanel getToolTipPanel() {
        return toolTipPanel;
    }

}

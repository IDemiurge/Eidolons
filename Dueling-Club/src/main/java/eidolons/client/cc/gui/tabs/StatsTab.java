package eidolons.client.cc.gui.tabs;

import eidolons.client.cc.gui.MainViewPanel;
import eidolons.client.cc.gui.MainViewPanel.HERO_VIEWS;
import eidolons.client.cc.gui.views.PointView;
import eidolons.entity.obj.unit.Unit;
import main.content.ContentManager;
import main.content.values.parameters.PARAMETER;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.G_TabbedPanel;
import main.system.auxiliary.log.Chronos;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.List;

public class StatsTab extends HeroPanelTab implements ChangeListener {

    PointView attributes;
    PointView masteries;
    PointView params;

    G_Panel attributesPanel = new G_Panel();
    G_Panel masteriesPanel = new G_Panel();
    G_Panel paramsPanel = new G_Panel();
    G_TabbedPanel tabs;

    private boolean editable = false;
    private HERO_VIEWS[] viewsArray;
    private PointView currentTab;

    public StatsTab(MainViewPanel mvp, Unit hero) {
        super("Stats", mvp, hero);
        initTabs();
        add(tabs, "pos 0 0");
    }

    @Override
    public void refresh() {
        // removeAll();
        if (currentTab != null) {
            if (hero.isDirty()) {
                currentTab.refresh();
            }
        }
        // initAttrPane();
        // initMasteryPane();
        // initParamPane();

        // add(tabs, "pos 0 0");

    }

    public PointView initTab(int i) {
        switch (i) {
            case 0: {
                if (attributes != null) {
                    attributes.refresh();
                    currentTab = attributes;
                    return attributes;
                }
                attributes = new PointView(editable, true, hero);
                currentTab = attributes;
                attributesPanel.add(attributes);
                return attributes;
            }
            case 1: {
                if (masteries != null) {
                    masteries.refresh();
                    currentTab = masteries;
                    return masteries;
                }
                masteries = new PointView(editable, false, hero);
                currentTab = masteries;
                masteriesPanel.add(masteries);
                return masteries;
            }
            case 2: {
                if (params != null) {
                    params.refresh();
                    currentTab = params;
                    return params;
                }
                List<PARAMETER> data = ContentManager.getHeroStatsTabValueList();
                params = new PointView(hero, data);
                currentTab = params;
                paramsPanel.add(params);
                return params;
            }
        }
        return attributes;
    }

    private void initTabs() {

        tabs = new G_TabbedPanel();
        initTab(0);

        tabs.addTab(attributesPanel, "Attributes");
        tabs.addTab(masteriesPanel, "Masteries");
        tabs.addTab(paramsPanel, "Parameters");

        tabs.getTabs().addChangeListener(this);

    }

    @Override
    public void initView() {
        viewsArray = new HERO_VIEWS[]{
         HERO_VIEWS.STATS};
        linkedView = viewsArray[0];

    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Chronos.mark("stats tab changed");
        int index = ((JTabbedPane) e.getSource()).getSelectedIndex();

        PointView tab = initTab(index);
        currentTab = tab;

        linkedView = viewsArray[index];

        mvp.activateView(getLinkedView());
        revalidate();
        Chronos.logTimeElapsedForMark("stats tab changed");

    }
}

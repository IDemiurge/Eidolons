package eidolons.client.cc.gui.neo.tabs;

import eidolons.client.cc.gui.MainViewPanel;
import eidolons.client.cc.gui.MainViewPanel.HERO_VIEWS;
import eidolons.client.cc.gui.tabs.HeroPanelTab;
import eidolons.client.cc.gui.neo.points.HC_PointView;
import eidolons.client.cc.gui.neo.slide.MasterySlidePanel;
import eidolons.entity.obj.unit.Unit;

public class HC_StatsTab extends HeroPanelTab {

    private HC_PointView view;

    public HC_StatsTab(MainViewPanel mvp, Unit hero) {
        super("Stats", mvp, hero);
        setView(new HC_PointView(true, hero));
        add(getView(), "pos 0 0");
    }

    @Override
    public void activate() {
        view.reset();
        for (HC_PointView view : ((MasterySlidePanel) mvp.getView(linkedView))
         .getViews()) {
            view.reset();
        }
    }

    @Override
    public void refresh() {
        getView().refresh();
    }

    @Override
    public void initView() {
        linkedView = HERO_VIEWS.STATS;
    }

    public HC_PointView getView() {
        return view;
    }

    public void setView(HC_PointView view) {
        this.view = view;
    }

}

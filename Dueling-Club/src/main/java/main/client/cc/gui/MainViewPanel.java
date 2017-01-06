package main.client.cc.gui;

import main.client.cc.HC_Master;
import main.client.cc.gui.lists.ItemListManager;
import main.client.cc.gui.neo.slide.MasterySlidePanel;
import main.client.cc.gui.neo.tabs.HC_Tab;
import main.client.cc.gui.neo.tree.view.ClassTreeView;
import main.client.cc.gui.neo.tree.view.HT_View;
import main.client.cc.gui.neo.tree.view.SkillTreeView;
import main.client.cc.gui.views.*;
import main.content.DC_ContentManager;
import main.content.parameters.PARAMETER;
import main.entity.obj.DC_HeroObj;
import main.swing.generic.components.G_Panel;

import java.util.HashMap;
import java.util.Map;

public class MainViewPanel extends G_Panel {
    Map<HERO_VIEWS, HeroView> viewsMap = new HashMap<>();
    private HeroView currentViewComp;
    private HERO_VIEWS currentView;
    private DC_HeroObj hero;
    private ItemListManager itemListManager;
    private Object treeArg;
    private HT_View treeView;
    private boolean treeViewMode;

    public MainViewPanel(DC_HeroObj hero) {
        this.hero = hero;
        // this.setVisuals(VISUALS.MAIN);

    }

    @Override
    public boolean isAutoSizingOn() {
        return true;
    }

    @Override
    public void refresh() {

        getCurrentViewComp().refresh();
    }

    public HeroView createView(HERO_VIEWS VIEW) {
        if (VIEW == null)
            return null;
        switch (VIEW) {
            case CLASSES:
                return new ClassView(hero);

            case LIBRARY:
                return new LibraryView(hero);
            case STATS:
                return new MasterySlidePanel(hero);
            // return new HC_PointView(false, hero);
            case SHOP:
                return new ShopView(hero);
            case SKILLS:
                return new SkillPickView(hero);
        }
        return getCurrentViewComp();
    }

    private void initView(HERO_VIEWS VIEW) {
        HeroView view = null;
        try {
            // view = new ViewWorker(VIEW, hero).getOrCreate();
            view = createView(VIEW);
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewsMap.put(VIEW, view);

    }

    public void activateView(final HERO_VIEWS view) {
        treeView = null;
        if (treeViewMode) {
            if (view == HERO_VIEWS.SKILLS)
                treeView = (HT_View) (viewsMap.get(HERO_VIEWS.SKILL_TREE));
            if (view == HERO_VIEWS.CLASSES)
                treeView = (HT_View) (viewsMap.get(HERO_VIEWS.CLASS_TREE));
        }
        if (treeView == null)
            setView(getView(view));
        else
            setView(treeView);
        setCurrentView(view);
        // SwingUtilities.invokeLater(new Runnable() {
        //
        // @Override
        // public void run() {
        // Chronos.mark(view.toString() + " init");
        // setView(getView(view));
        // Chronos.logTimeElapsedForMark(view.toString() + " init");
        // }
        // });

    }

    public HeroView getView(HERO_VIEWS VIEW) {
        if (viewsMap.get(VIEW) == null)
            initView(VIEW);
        return viewsMap.get(VIEW);
    }

    private boolean checkTemplate(DC_HeroObj hero) {
        return true;
    }

    private synchronized void setView(HeroView heroView) {
        if (getCurrentViewComp() == heroView)
            return;
        if (heroView == null) {
            main.system.auxiliary.LogMaster.log(1, "null hero view. Current view: "
                    + getCurrentViewComp().getClass().getSimpleName());
            return;
        }
        removeAll();
        add(heroView, "pos 0 0");
        heroView.activate();
        heroView.refresh();
        setCurrentView(heroView);

        if (heroView instanceof HeroItemView) {
            this.itemListManager = ((HeroItemView) heroView).getItemManager();
        }

        revalidate();
        repaint();
    }

    public HeroView getCurrentViewComp() {
        return currentViewComp;
    }

    public void setCurrentView(HeroView currentView) {
        this.currentViewComp = currentView;
    }

    public ItemListManager getItemManager() {
        return itemListManager;
    }

    public HERO_VIEWS getCurrentView() {
        return currentView;
    }

    public void setCurrentView(HERO_VIEWS currentView) {
        this.currentView = currentView;
    }

    public void toggleAltMode() {
        treeView.altToggled();
    }

    public void toggleTreeView() {
        if (getCurrentViewComp() == treeView) {
            treeViewMode = false;
            setView(getView(getCurrentView()));
            return;
        }

        treeViewMode = true;
        boolean skills = getCurrentView() == HERO_VIEWS.SKILLS;
        treeArg = skills ? HC_Master.getSelectedMastery() : null;
        // if (!skills) TODO
        // if (getCurrentViewComp() instanceof HeroItemView) {
        // HeroItemView heroItemView = (HeroItemView) getCurrentViewComp();
        // treeArg = new
        // EnumMaster<CLASS_GROUP>().retrieveEnumConst(CLASS_GROUP.class,
        // heroItemView.getVendorPanel().getSelectedTabName());
        // }
        HERO_VIEWS TREE_VIEW = skills ? HERO_VIEWS.SKILL_TREE : HERO_VIEWS.CLASS_TREE;
        treeView = (HT_View) viewsMap.get(TREE_VIEW);

        if (treeArg == null)
            treeArg = skills ? DC_ContentManager.getHighestMastery(hero) : DC_ContentManager
                    .getMainClassGroup(hero);

        if (treeView == null) {
            if (skills)
                treeView = new SkillTreeView(treeArg, hero);
            else
                treeView = new ClassTreeView(treeArg, hero);
            viewsMap.put(TREE_VIEW, treeView);
        }
        if (treeArg != null)
            treeView.tabSelected(treeArg.toString());
        treeView.refresh();
        setView(treeView);

    }

    public void goToSkillTree(PARAMETER param) {
        if (treeView == null || !treeViewMode)
            toggleTreeView();

        treeArg = param;
        // if (!treeViewMode)

        // else {

        int i = 0;
        // if (treeView.isAlt)
        for (HC_Tab tab : treeView.getTabList()) {
            if (tab.getName().equalsIgnoreCase(param.getName()))
                break;
            i++;
        }
        treeView.getDisplayedTabPanel().adjustPageIndexToSelectTab(treeView.getTabList().get(i));
        treeView.getDisplayedTabPanel().select(i);
        treeView.tabSelected(param.getName());

        treeView.refresh();
        // }

    }

    public enum HERO_VIEWS {
        CLASSES, LIBRARY, SHOP, SKILLS, STATS, SKILL_TREE, CLASS_TREE,

    }
}

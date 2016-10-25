package main.client.cc.gui;

import main.client.cc.gui.MainViewPanel.HERO_VIEWS;
import main.client.cc.gui.neo.HeroPanel;
import main.client.cc.gui.neo.principles.PrincipleView;
import main.client.cc.gui.neo.tabs.HC_StatsTab;
import main.client.cc.gui.neo.tabs.HC_TabPanel;
import main.client.cc.gui.neo.tabs.TabChangeListener;
import main.client.cc.gui.neo.top.HC_Controls;
import main.client.cc.gui.neo.top.SpecialControls;
import main.client.cc.gui.tabs.*;
import main.client.dc.Launcher;
import main.entity.Entity;
import main.entity.obj.DC_HeroObj;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.auxiliary.Chronos;
import main.system.auxiliary.ColorManager;
import main.system.images.ImageManager;

import javax.swing.*;
import java.awt.*;

public class MainPanel implements TabChangeListener {
    public static final int MAIN_PANEL_HEIGHT = 930;
    public static final int MAIN_PANEL_WIDTH = 460;
    public static final String MAIN_INFO = "Stats";
    public static final String ITEMS = "Items";
    public static final String SPELLS = "Spells";
    public static final String SKILLS = "Skills";
    public static final String CLASSES = "Classes";
    public static final Dimension SIZE = new Dimension(MAIN_PANEL_WIDTH, MAIN_PANEL_HEIGHT);
    private static final int DEFAULT_INDEX = 1;
    private static final int PAGE_SIZE = 5;
    private static final int OFFSET_Y = 28;
    PrincipleView principleViewComp;
    private G_Panel comp;
    private MainViewPanel mvp;
    private HeroPanel heroPanel;
    private HC_TabPanel tabs; // sizes?

    private DC_HeroObj hero;

    private HC_StatsTab statsTab;
    private SpellTab spellTab;
    private SkillTabNew skillTab;
    private ItemsTab itemTab;
    private ClassTab classTab;
    private HeroPanelTab[] tabArray;
    private HeroPanelTab currentTab;

    private HC_Controls controls;
    private SpecialControls specialControls;

    private MiddlePanel mp;
    private int index;
    private HERO_VIEWS view;
    private boolean principleView;
    public MainPanel(DC_HeroObj hero) {
        this.setHero(hero);
        this.comp = createMainPanel();
        heroPanel = new HeroPanel(getHero());
        controls = new HC_Controls(hero);

        initMvp();
        mp = new MiddlePanel(mvp, hero);
        specialControls = new SpecialControls(hero, mvp);
        initTabs();

        // currentTab = tabArray[0];
        // currentTab.activate();
        // mvp.activateView(currentTab.getLinkedView());
        // mp.activateView(currentTab.getLinkedView());

        tabs.setPanelSize(SIZE);
        addComponents();
        comp.setBackground(ColorManager.BLACK);

    }

    public static int getMainComponentsY() {
        return VISUALS.TOP_HC.getHeight() + 8;
    }

    private G_Panel createMainPanel() {
        return new G_Panel() {

            @Override
            public void paint(Graphics g) {
                // TODO Auto-generated method stub
                super.paint(g);
            }

        };
    }

    private void addComponents() {
        comp.setAutoZOrder(true);
        addMainComponents();
        comp.add(heroPanel, "id header, pos 0 0");
        comp.add(controls, "id controls,pos header.x2+" + VISUALS.BUTTON.getWidth() / 2 + " "
                + OFFSET_Y);
        comp.add(specialControls, "id specControls, pos mvp.x controls.y2");

        JLabel bg = new JLabel(ImageManager.getIcon(getBackgroundPath()));
        comp.add(bg, "id bg, pos 0 0");
    }

    private void addMainComponents() {
        comp.add(tabs, "id tabs, pos header.x2 " + getMainComponentsY());
        comp.setComponentZOrder(tabs, 0);
        comp.add(mp, "id mp, pos tabs.x2 " + getMainComponentsY());
        comp.setComponentZOrder(mp, 1);
        comp.add(mvp, "id mvp, pos mp.x2 " + getMainComponentsY());
        comp.setComponentZOrder(mvp, 2);
    }

    protected boolean isAutoZOrder() {
        return true;
    }

    private String getBackgroundPath() {
        return "big\\HC\\Default.jpg";
    }

    private void initMvp() {
        Chronos.mark("mvp");
        this.mvp = new MainViewPanel(getHero());

        Chronos.logTimeElapsedForMark("mvp");
    }

    public void refresh() {
        for (HeroPanelTab tab : tabArray) {
            if (tab != currentTab)
                tab.setDirty(true);
        }
        controls.refresh();
        specialControls.refresh();
        heroPanel.refresh();

        if (principleView) {
            principleViewComp.refresh();
        } else {

            currentTab.refresh();
            currentTab.activate();
            mvp.refresh();
            mp.refresh();
        }
        comp.revalidate();
        comp.repaint();

    }

    private void initTabs() {
        Chronos.mark("tabs");
        tabs = new HC_TabPanel();
        tabs.setPageSize(PAGE_SIZE);
        statsTab = new HC_StatsTab(mvp, getHero());
        classTab = new ClassTab(mvp, getHero());
        skillTab = new SkillTabNew(mvp, getHero());
        spellTab = new SpellTab(mvp, getHero());
        itemTab = new ItemsTab(mvp, getHero());
        tabArray = new HeroPanelTab[]{skillTab, statsTab, spellTab, itemTab, classTab};
        tabs.addTab(SKILLS, skillTab);
        tabs.addTab(MAIN_INFO, statsTab);
        tabs.addTab(SPELLS, spellTab);
        tabs.addTab(ITEMS, itemTab);
        tabs.addTab(CLASSES, classTab);

        tabs.setChangeListener(this);

        Chronos.logTimeElapsedForMark("tabs");
    }

    public void togglePrincipleView() {
        principleView = !principleView;
        if (principleView) {
            comp.remove(tabs);
            comp.remove(mvp);
            comp.remove(mp);
            comp.add(getPrincipleViewComp(), "id principleViewComp, pos header.x2 "
                    + getMainComponentsY());
            comp.setComponentZOrder(principleViewComp, 0);
        } else {
            comp.setAutoZOrder(isAutoZOrder());
            comp.removeAll();
            addComponents();
        }
        refresh();
    }

    public MiddlePanel getMiddlePanel() {
        return mp;
    }

    public G_Panel getComp() {
        return comp;
    }

    public void setComp(G_Panel comp) {
        this.comp = comp;
    }

    @Override
    public void tabSelected(int index) {
        this.index = index;
        currentTab = tabArray[index];

        currentTab.activate();
        if (currentTab.isDirty())
            currentTab.refresh();
        view = currentTab.getLinkedView();
        mvp.activateView(view);
        mp.activateView(view);
        HC_TabPanel tabbedPanel = mvp.getCurrentViewComp().getTabbedPanel();
        Launcher.getHcKeyListener().setTabPanel(tabbedPanel);
        if (tabbedPanel != null)
            tabbedPanel.requestFocusInWindow();
        specialControls.refresh();
    }

    public HERO_VIEWS getView() {
        return view;
    }

    public int getIndex() {
        return index;
    }

    public DC_HeroObj getHero() {
        return hero;
    }

    private void setHero(DC_HeroObj hero) {
        this.hero = hero;
    }

    public MainViewPanel getMvp() {
        return mvp;
    }

    public HeroPanelTab getTab(HERO_TABS tab) {
        switch (tab) {
            case CLASSES:
                return classTab;
            case ITEMS:
                return itemTab;
            case SKILLS:
                return skillTab;
            case SPELLS:
                return spellTab;
            case STATS:
                return statsTab;
        }
        return null;
    }

    public HeroPanelTab getCurrentTab() {
        return currentTab;
    }

    public MiddlePanel getMp() {
        return mp;
    }

    public void initSelection() {
        tabs.select(DEFAULT_INDEX);
    }

    public void typeSelected(Entity type) {
        getMiddlePanel().getArc().refresh();
        if (currentTab == itemTab)
            itemTab.getActionPanel().refresh();
    }

    @Override
    public void tabSelected(String name) {
        // TODO Auto-generated method stub

    }

    public SpecialControls getSpecialControls() {
        return specialControls;
    }

    public void setSpecialControls(SpecialControls specialControls) {
        this.specialControls = specialControls;
    }

    public HC_TabPanel getTabs() {
        return tabs;
    }

    public PrincipleView getPrincipleViewComp() {
        if (principleViewComp == null) {
            principleViewComp = new PrincipleView(hero);
            principleViewComp.init();
        }
        return principleViewComp;
    }

    public boolean isPrincipleView() {
        return principleView;
    }

    public enum HERO_TABS {
        SKILLS, STATS, SPELLS, ITEMS, CLASSES
    }

}

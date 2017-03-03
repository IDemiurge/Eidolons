package main.client.cc.gui.neo.tabs;

import main.client.cc.CharacterCreator;
import main.client.dc.Launcher;
import main.swing.SwingMaster;
import main.swing.generic.components.ComponentVisuals;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.G_Panel;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.log.Chronos;
import main.system.graphics.ColorManager;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class HC_TabPanel extends G_Panel implements MouseListener {
    public static final VISUALS TAB_DEFAULT = VISUALS.TAB;
    public static final VISUALS TAB_SELECTED_DEFAULT = VISUALS.TAB_SELECTED;
    private static final int PAGE_SIZE = 4;
    protected List<HC_Tab> tabs;
    protected HC_PagedTabs tabPanel;
    protected int index = 0;
    protected TabChangeListener listener;
    protected int pageSize = PAGE_SIZE;
    protected ComponentVisuals TAB = TAB_DEFAULT;
    protected ComponentVisuals TAB_SELECTED = TAB_SELECTED_DEFAULT;
    boolean vertical;
    Class<?> ENUM;
    private G_Component currentComp;

    public HC_TabPanel(HC_Tab... tabs) {
        this(new LinkedList<>(Arrays.asList(tabs)));
    }

    public HC_TabPanel(List<HC_Tab> tabs) {
        this.tabs = tabs;
        if (!tabs.isEmpty()) {
            tabPanel = new HC_PagedTabs(tabs, this);
            select(0);
        }
        panelSize = CharacterCreator.getHeroPanelSize();
        setBackground(ColorManager.BACKGROUND);
        setOpaque(true);
        if (Launcher.getHcKeyListener() != null) {
            Launcher.getHcKeyListener().setTabPanel(this);
        }
    }

    public HC_TabPanel() {
        this(new LinkedList<>());
    }

    public ComponentVisuals getTAB() {
        return TAB;
    }

    public ComponentVisuals getTAB_SELECTED() {
        return TAB_SELECTED;
    }

    public void removeTab(int i) {
        tabs.remove(i);

        SoundMaster.playStandardSound(STD_SOUNDS.ACTION_CANCELLED);
        tabPanel.setData(tabs);
        tabPanel.refresh();
        select(0);
        for (int j = i; j < tabs.size(); j++) {
            tabs.get(j).setIndex(j);
        }
        refresh();
    }

    public void addTab(String imagePath, G_Component comp) {
        addTab(imagePath, imagePath, comp);

    }

    public void addTab(String title, String imgPath, G_Component comp) {
        addTab(null, title, imgPath, comp);
    }

    public void addTab(Integer index, String title, String imgPath, G_Component comp) {
        HC_Tab tab = new HC_Tab(title, comp, tabs.size());
        if (index != null) {
            tabs.add(index, tab);
        } else {
            tabs.add(tab);
        }
        if (tabPanel == null) {
            tabPanel = new HC_PagedTabs(tabs, this);
            if (getPageSize() != 0) {
                tabPanel.setPageSize(getPageSize());
            }
        }
        if (ENUM != null) {
            sort();
        }
        tab.setImagePath(imgPath);
        tabPanel.setData(tabs);
        refresh();
    }

    protected void sort() {
        Collections.sort(tabs, new EnumMaster<HC_Tab>().getEnumSorter(ENUM));
        int j = 0;
        for (HC_Tab t : tabs) {
            t.setIndex(j);
            j++;
        }
    }

    public void refresh() {
        removeAll();
        tabPanel.refresh();
        if (Launcher.getHcKeyListener() != null) {
            Launcher.getHcKeyListener().setTabPanel(this);
        }
        requestFocusInWindow();
        add(tabPanel, "id tabs, pos " + getTabsOffsetX() + " " + getTabsOffsetY());
        G_Component component = tabs.get(getIndex()).getComponent();
        component.refresh();
        // component.refreshComponents(); //TODO
        add(component, "pos " + getCompOffsetX() + " tabs.y2+" + getCompOffsetY());
        revalidate();
        repaint();
    }

    protected String getTabsOffsetY() {
        return "0";
    }

    protected String getTabsOffsetX() {
        return "0";
    }

    protected String getCompOffsetY() {
        return "0";
    }

    protected String getCompOffsetX() {
        return "0";
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        SoundMaster.playStandardSound(getClickSound());
        // int i = 0;
        // for (HC_Tab tab : tabs) {
        // if (tab.getComponent() == e.getSource())
        // break;
        // i++;
        // }
        // if (e.getSource() instanceof HC_TabComp)
        // select(((HC_TabComp) e.getSource()).getIndex());
        // else
        Chronos.mark("[[[click]]]");
        select(SwingMaster.getComponentIndex(((Component) e.getSource()).getParent(), (Component) e
                .getSource()));
        Chronos.logTimeElapsedForMark("[[[click]]]");

    }

    protected STD_SOUNDS getClickSound() {
        // return STD_SOUNDS.SLOT;
        return STD_SOUNDS.DIS__OPEN_MENU;
    }

    public void selected(String name) {
        int i = -1;
        for (HC_Tab s : tabs) {
            i++;
            if (s.getName().equalsIgnoreCase(name)) {

                break;
            }
        }
        TabChangeListener buffer = listener;
        listener = null;
        try {
            select(i);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            listener = buffer;
        }
    }

    public void select(int newIndex) {
        if (tabs.size() == 0) {
            return;
        }
        if (newIndex < 0) {
            newIndex = tabs.size() - 1;
        } else if (tabs.size() <= newIndex) {
            newIndex -= tabs.size();
        }
        if (tabPanel != null) {
            if (tabPanel.getCurrentIndex() > 0) {
                // int pageIndex = newIndex/ tabPanel.getPageSize();
                newIndex += tabPanel.getPageSize()
                        * (tabPanel.getCurrentIndex() - newIndex / tabPanel.getPageSize());
            }
        }
        if (tabs.size() > getIndex()) {
            tabs.get(getIndex()).setSelected(false);
        }
        setIndex(newIndex);
        currentComp = tabs.get(getIndex()).getComponent();
        tabs.get(getIndex()).setSelected(true);
        try {
            adjustPageIndexToSelectTab(tabs.get(getIndex()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (listener != null) {
            notifyListener();
        }
        Chronos.mark("[[[refresh]]]");

        refresh();
        Chronos.logTimeElapsedForMark("[[[refresh]]]");

    }

    public void notifyListener() {
        listener.tabSelected(getSelectedTabName());
        listener.tabSelected((getIndex()));
    }

    public String getSelectedTabName() {
        return tabs.get(getIndex()).getName();
    }

    public void selectLast() {
        int newIndex = tabs.size() - 1;
        adjustPageIndexToSelectTab(tabs.get(newIndex));
        select(newIndex);
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public List<HC_Tab> getTabs() {
        return tabs;
    }

    public HC_PagedTabs getTabPanel() {
        return tabPanel;
    }

    public Component getSelectedTabComponent() {
        return getTabs().get(index).getComponent();
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setChangeListener(TabChangeListener listener) {
        this.listener = listener;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Class<?> getENUM() {
        return ENUM;
    }

    public void setENUM(Class<?> eNUM) {
        ENUM = eNUM;
    }

    public G_Component getCurrentComp() {
        return currentComp;
    }

    public Component generateEmptyTabComp() {
        return new HC_TabComp(getTAB(), getTAB_SELECTED(), "", true, false, 0);
    }

    public Component generateTabComp(HC_Tab tab) {
        return tab.generateTabComp(this);
    }

    public void adjustPageIndexToSelectTab(HC_Tab tab) {
        // tabPanel.getIndex();
        // tabPanel.flipPage(forward);
        tabPanel.adjustPageIndexToSelectTab(tab);

    }

}

package eidolons.client.cc.gui.neo.tabs;

import main.swing.generic.components.G_Panel;

import java.awt.*;
import java.util.List;

public class TabPage extends G_Panel {
    private List<HC_Tab> list;
    private HC_TabPanel tabPanel;

    public TabPage(List<HC_Tab> list, HC_TabPanel tabPanel) {
        this.tabPanel = tabPanel;
        this.list = list;
        addComps();
    }

    public void addComps() {
        int x = 0;
        int i = 0;
        for (HC_Tab tab : list) {

            Component comp;
            if (tab == null) {
                comp = generateEmptyTabComp(i == list.size() - 1);
            } else {
                comp = tabPanel.generateTabComp(tab);
                comp.addMouseListener(tabPanel);
            }
            add(comp, "pos " + x + " 0");
            x += comp.getPreferredSize().getWidth();
            i++;
        }
    }

    private Component generateEmptyTabComp(boolean last) {
        // last = "new"
        return tabPanel.generateEmptyTabComp();
    }

}

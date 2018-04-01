package main.swing.generic.components;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class G_TabbedPanel extends G_Panel {
    JTabbedPane tabs;
    private List<Component> tabList = new ArrayList<>();

    public G_TabbedPanel() {
        super();
        tabs = new JTabbedPane();
        add(tabs, "id tabs, pos 0 0");
    }

    public G_TabbedPanel(Dimension d) {
        this();
        this.panelSize = d;
    }

    public void addTab(Component c, String title) {
        addTab(c, title, null);
    }

    public void addTab(Component c, String title, Icon icon) {
        if (icon == null) {
            tabs.addTab(title, c);
        } else {
            tabs.addTab(title, icon, c);
        }
        tabList.add(c);
        if (panelSize == null) {
            panelSize = c.getPreferredSize();
        } else {
            double w = c.getPreferredSize().getWidth();
            double h = c.getPreferredSize().getHeight();
            panelSize = new Dimension((int) Math.max(panelSize.getWidth(), w),
             (int) Math.max(panelSize.getHeight(), h));
        }

    }

    public JTabbedPane getTabs() {
        return tabs;
    }

    @Override
    public String toString() {
        String tabString = "";
        for (Component tab : tabList) {
            tabString += "\n" + tab.toString();
        }
        return tabs.getTabCount() + " TABS: " + tabString;
    }
}

package main.level_editor.gui.palette;

import eidolons.libgdx.gui.panels.TabbedPanel;
import main.content.DC_TYPE;
import main.data.DataManager;

import java.util.List;

public class UpperPalette extends TabbedPanel {
    public UpperPalette(DC_TYPE TYPE) {

        List<String> tabs = DataManager.getTabGroups(TYPE);
        for (String group : tabs) {
            addTab(new ObjectPalette(TYPE, group), group);
        }
    }
}

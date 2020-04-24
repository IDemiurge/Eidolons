package main.level_editor.gui.panels.palette.tab;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

public class TabImpl extends Tab {
    String name;
    Table table;

    public TabImpl(String name, Table table) {
        this.name = name;
        this.table = table;
    }

    @Override
    public boolean isCloseableByUser() {
        return false;
    }

    @Override
    public String getTabTitle() {
        return name;
    }

    @Override
    public Table getContentTable() {
        return table;
    }
}

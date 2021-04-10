package main.level_editor.gui.tree;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;
import main.level_editor.backend.handlers.structure.FloorManager;
import main.level_editor.backend.struct.level.LE_Floor;
import main.system.auxiliary.StringMaster;

public class FloorTabs extends TabbedPane implements TabbedPaneListener {
    public FloorTabs() {
        addListener(this);
    }

    public void addTab(LE_Floor floor) {
        addTab(new FloorTab(floor), 0);
        main.system.auxiliary.log.LogMaster.log(1,"Tab added for "+floor.getName() );
    }

    public void setActiveTab(LE_Floor parameter) {
//        getActiveTab() TODO
        for (Tab tab : getTabs()) {
            if (tab instanceof FloorTab) {
                if (((FloorTab) tab).floor==parameter) {
                    switchTab(tab);
                    break;
                }
            }
        }
    }

    public void switchedTab(Tab tab) {
        if (tab instanceof FloorTab) {
              FloorManager.floorSelected(((FloorTab) tab).floor);
        }
    }

    @Override
    public void removedTab(Tab tab) {
        if (tab instanceof FloorTab) {
        FloorManager.removed(((FloorTab) tab).floor);
        }
    }

    @Override
    public void removedAllTabs() {
    }


    public static class FloorTab extends Tab {
        protected String name;
        protected LE_Floor floor;

        public FloorTab(String name) {
            this.name = name;
        }

        public FloorTab(LE_Floor floor) {
            this.floor = floor;
        }

        @Override
        public boolean isCloseableByUser() {
            return true;
        }

        @Override
        public String getTabTitle() {
            if (floor != null) {
                return StringMaster.getNameFromPath(floor.getName());
            }
            return StringMaster.getNameFromPath(name);
        }

        @Override
        public Table getContentTable() {
            return null;
        }

    }
}

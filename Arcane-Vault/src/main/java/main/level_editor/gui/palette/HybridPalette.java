package main.level_editor.gui.palette;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;
import eidolons.libgdx.gui.panels.TablePanelX;
import main.content.DC_TYPE;
import main.level_editor.gui.palette.tab.TabImpl;
import main.level_editor.gui.panels.ClosablePanel;

public class HybridPalette extends ClosablePanel implements TabbedPaneListener {

    public enum PALETTE{
        obj, unit, vfx, custom, script
    }
    private final TablePanelX<Actor> table;

    public HybridPalette( ) {
        TabbedPane tabs = new TabbedPane();
        for (PALETTE value : PALETTE.values()) {
            DC_TYPE arg=null;
            switch (value) {
                case obj:
                    arg=DC_TYPE.BF_OBJ;
                    break;
                case unit:
                    arg=DC_TYPE.UNITS;
                    break;
                default:
                    arg = null;
                    break;
            }
        tabs.add(new TabImpl(value.toString() , new UpperPalette(arg)));
        }
        add(tabs.getTabsPane()).row();
        add(table = new TablePanelX<>());

    }

    @Override
    public void switchedTab(Tab tab) {
        table.clearChildren();
        table.add(tab.getContentTable());
    }

    @Override
    public void removedTab(Tab tab) {

    }

    @Override
    public void removedAllTabs() {

    }
}

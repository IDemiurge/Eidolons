package main.level_editor.gui.panels.palette;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;
import eidolons.libgdx.gui.panels.TablePanelX;
import main.content.DC_TYPE;
import main.level_editor.gui.panels.ClosablePanel;
import main.level_editor.gui.panels.palette.tab.TabImpl;

public class HybridPalette extends ClosablePanel implements TabbedPaneListener {

    private final TabbedPane tabs;

    public enum PALETTE {
        obj, unit, vfx, custom, script
    }

    private final TablePanelX<Actor> table;

    public HybridPalette() {
        tabs = new TabbedPane();
        add(tabs.getTabsPane().top()).height(60).width(400).top().row();
        add(table = new TablePanelX<>()).height(700).width(400).bottom().left();
        tabs.addListener(this);
        for (PALETTE value : PALETTE.values()) {
            DC_TYPE arg = null;
            switch (value) {
                case obj:
                    arg = DC_TYPE.BF_OBJ;
                    break;
                case unit:
                    arg = DC_TYPE.UNITS;
                    break;
                default:
                    arg = null;
                    break;
            }
            tabs.add(new TabImpl(value.toString(), new UpperPalette(arg)));
        }

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        debugAll();
    }

    @Override
    public void switchedTab(Tab tab) {
        table.clearChildren();
        table.add(tab.getContentTable()).bottom().left().fill().size(400, 700);
        tab.getContentTable().act(0);
    }

    @Override
    public void removedTab(Tab tab) {

    }

    @Override
    public void removedAllTabs() {

    }
}

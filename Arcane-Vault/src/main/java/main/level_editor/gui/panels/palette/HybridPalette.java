package main.level_editor.gui.panels.palette;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.panels.TablePanelX;
import main.level_editor.gui.panels.ClosablePanel;
import main.level_editor.gui.panels.palette.tab.TabImpl;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.LinkedHashMap;
import java.util.Map;

public class HybridPalette extends ClosablePanel implements TabbedPaneListener {

    private final TabbedPane tabs = new TabbedPane();
    Map<PALETTE, TabImpl> tabMap = new LinkedHashMap<>();
    private UpperPalette customPalette;
    private UpperPalette blockPalette;
    private UpperPalette palette;
    private Tab lastSelected;

    public enum PALETTE {
        encounters, obj, unit, custom, blocks,  //groups
    }

    private final TablePanelX<Actor> table;

    public HybridPalette() {
        add(tabs.getTabsPane().top()).height(60).width(500).top().row();
        add(table = new TablePanelX<>()).height(700).width(500).bottom().left();
        tabs.getTabsPane().setBackground(NinePatchFactory.getLightPanelFilledSmallDrawable());
        table.setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());
        tabs.addListener(this);

        reload();

        GuiEventManager.bind(GuiEventType.LE_DESELECT, p -> {
            palette.getTree().deselect();
        });
        GuiEventManager.bind(GuiEventType.LE_PALETTE_RESELECT, p -> {
            palette.getTree().reselect();
        });

        GuiEventManager.bind(GuiEventType.LE_PALETTE_SELECTION, p -> {
            switchedTab(tabMap.get(PALETTE.custom));
            customPalette.setUserObject(p.get());
        });
        GuiEventManager.bind(GuiEventType.LE_BLOCK_PALETTE_SELECTION, p -> {
            switchedTab(tabMap.get(PALETTE.blocks));
            blockPalette.setUserObject(p.get());
        });
    }

    public void reload() {
        tabs.removeAll();
        Tab toSelect = lastSelected;
        for (PALETTE value : PALETTE.values()) {
            UpperPalette palette;
            TabImpl tab;
            tabs.add(tab = new TabImpl(value.toString(), palette = new UpperPalette(value)));
            tabMap.put(value, tab);
            if (value == PALETTE.blocks) {
                blockPalette = palette;
            }
            if (value == PALETTE.custom) {
                customPalette = palette;
            }
        }
        if (toSelect != null) {
            switchedTab(toSelect);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void switchedTab(Tab tab) {
        table.clearChildren();
        table.add(tab.getContentTable()).fill();
        tab.getContentTable().act(0);
        palette = (UpperPalette) tab.getContentTable();
        lastSelected = tab;
    }

    @Override
    public void removedTab(Tab tab) {

    }

    @Override
    public void removedAllTabs() {

    }

    public UpperPalette getCustomPalette() {
        return customPalette;
    }

    public UpperPalette getBlockPalette() {
        return blockPalette;
    }

    public UpperPalette getPalette() {
        return palette;
    }
}

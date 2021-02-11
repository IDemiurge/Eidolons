package main.level_editor.gui.panels.control;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;
import libgdx.gui.panels.TablePanelX;
import main.level_editor.gui.panels.control.impl.*;
import main.system.auxiliary.StringMaster;

public class TabbedControlPanel extends TabbedPane implements TabbedPaneListener {

    private final TablePanelX holder;
    CtrlModulePanel modulePanel= new CtrlModulePanel();
    CtrlStructurePanel structurePanel= new CtrlStructurePanel();
    CtrlFloorPanel floorPanel= new CtrlFloorPanel();
    CtrlLayerPanel layerPanel= new CtrlLayerPanel();
    CtrlFuncPanel funcsPanel= new CtrlFuncPanel();
    CtrlAiPanel aiPanel= new CtrlAiPanel();
    CtrlRngPanel rngPanel= new CtrlRngPanel();
    CtrlSelectionPanel selectionPanel= new CtrlSelectionPanel();
    CtrlPalettePanel palettePanel= new CtrlPalettePanel();
    CtrlCustomPanel customPanel = new CtrlCustomPanel();
    CtrlScriptPanel scriptPanel = new CtrlScriptPanel();
    CtrlDecorPanel decorPanel = new CtrlDecorPanel();
    CtrlDisplayPanel displayPanel = new CtrlDisplayPanel();

    LE_ControlPanel[] panels = {
//            modulePanel,
            customPanel,
            structurePanel,
            palettePanel,
            funcsPanel,
            scriptPanel,
            // aiPanel,
            selectionPanel,decorPanel,displayPanel
    };

    public TabbedControlPanel(TablePanelX holder) {
        this.holder=holder;
        for (LE_ControlPanel panel : panels) {
            String title =getTitleFromClass(panel.getClazz());

            Tab tab = new LE_Tab(panel, title);
            add(tab);
        }
        addListener(this);
        //tTODO nit hotkeys
    }

    public static String getTitleFromClass(Class clazz) {
        String title = StringMaster.format(clazz.getSimpleName(), true);
        title = StringMaster.cropFirstSegment(title.trim(), " ");
        title = StringMaster.cropLastSegment(title, " ");
        return title;
    }


    @Override
    public void switchedTab(Tab tab) {
        super.switchTab(tab);
        holder.setUserObject(
                tab.getContentTable());
    }

    @Override
    public void removedTab(Tab tab) {

    }
    @Override
    public void removedAllTabs() {

    }

    public LE_ControlPanel[] getPanels() {
        return panels;
    }

    public class LE_Tab extends Tab {

        private final Table table;
        private final String title;

        public LE_Tab(Table table, String title) {
            super(false, false);
            this.table = table;
            this.title = title;
        }

        @Override
        public String getTabTitle() {
            return title;
        }

        @Override
        public Table getContentTable() {
            return table;
        }
    }

}

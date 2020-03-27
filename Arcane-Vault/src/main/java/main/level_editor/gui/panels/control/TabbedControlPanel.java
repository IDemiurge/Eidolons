package main.level_editor.gui.panels.control;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;
import eidolons.libgdx.gui.panels.TablePanelX;
import main.level_editor.gui.panels.control.structure.*;
import main.system.auxiliary.StringMaster;

public class TabbedControlPanel extends TabbedPane implements TabbedPaneListener {

    private final TablePanelX holder;
    CtrlModulePanel modulePanel= new CtrlModulePanel();
    CtrlStructurePanel structurePanel= new CtrlStructurePanel();
    CtrlFloorPanel floorPanel= new CtrlFloorPanel();
    CtrlLayerPanel layerPanel= new CtrlLayerPanel();
    CtrlFuncsPanel funcsPanel= new CtrlFuncsPanel();
    CtrlAiPanel aiPanel= new CtrlAiPanel();
    CtrlRngPanel rngPanel= new CtrlRngPanel();
    CtrlSelectionPanel selectionPanel= new CtrlSelectionPanel();
    CtrlPalettePanel palettePanel= new CtrlPalettePanel();

    LE_ControlPanel[] panels = {
            modulePanel,
            structurePanel,
            layerPanel,
            funcsPanel,
            aiPanel,
            selectionPanel,
    };

    public TabbedControlPanel(TablePanelX holder) {
        this.holder=holder;
        for (LE_ControlPanel panel : panels) {
            String title = StringMaster.getWellFormattedString(panel.getClazz().getSimpleName(), true);
            title = StringMaster.cropFirstSegment(title, " ");
            title = StringMaster.cropLastSegment(title, " ");
            Tab tab = new LE_Tab(panel, title);
            add(tab);
        }
        addListener(this);
        //tTODO nit hotkeys
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

    public class LE_Tab extends Tab {

        private Table table;
        private String title;

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

package eidolons.libgdx.gui.menu.selection.scenario;

import eidolons.game.battlecraft.logic.meta.igg.IGG_Launcher;
import eidolons.libgdx.gui.menu.selection.ItemListPanel;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.menu.selection.SelectableItemDisplayer;
import eidolons.libgdx.gui.menu.selection.SelectionPanel;
import eidolons.libgdx.screens.SCREEN_TYPE;
import eidolons.libgdx.screens.ScreenData;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created by JustMe on 11/30/2017.
 */
public class ScenarioSelectionPanel extends SelectionPanel {
    Supplier<List<? extends Entity>> dataSupplier;


    public ScenarioSelectionPanel(Supplier<List<? extends Entity>> dataSupplier) {
        super();
        this.dataSupplier = dataSupplier;
        init();
    }
    protected String getDoneText() {
        return "Next";
    }
    protected String getTitle() {
        return "Select a Scenario";
    }
    @Override
    public void closed(Object selection) {
        fadeOut();
        if (selection == null) {
            return;
        }

        scenarioChosen(  DataManager.getType(selection.toString(), DC_TYPE.SCENARIOS));

    }

    protected void scenarioChosen(ObjType type) {
        ScreenData screenData= new ScreenData(SCREEN_TYPE.BATTLE, type.getName());
        if (IGG_Launcher.isDemo(type))
        screenData.setParam(new EventCallbackParam(type));
        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN,
       screenData);
    }

    @Override
    protected SelectableItemDisplayer createInfoPanel() {
        return new ScenarioInfoPanel(null);
    }

    protected boolean isReadyToBeInitialized() {
        return false;
    }

    @Override
    protected List<SelectableItemData> createListData() {
        return listPanel.toDataList(dataSupplier.get());
    }

    @Override
    protected ItemListPanel createListPanel() {
        return new ScenarioListPanel();
    }
}

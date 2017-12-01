package main.libgdx.gui.menu.selection.scenario;

import main.entity.Entity;
import main.libgdx.gui.menu.selection.ItemInfoPanel;
import main.libgdx.gui.menu.selection.ItemListPanel;
import main.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import main.libgdx.gui.menu.selection.SelectionPanel;
import main.libgdx.screens.ScreenData;
import main.libgdx.screens.ScreenType;
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


    @Override
    public void closed(Object selection) {
        setVisible(false);
        if (selection == null)
            return;
//        new Thread(new Runnable() {
//            public void run() {
//                Eidolons.initScenario(new ScenarioMetaMaster(selection.toString()));
//            }
//        }, " thread").start();
        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN,
         new ScreenData(ScreenType.BATTLE, selection.toString()
         ));
    }

    @Override
    protected ItemInfoPanel createInfoPanel() {
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

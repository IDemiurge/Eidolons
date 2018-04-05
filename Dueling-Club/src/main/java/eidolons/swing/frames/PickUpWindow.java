package eidolons.swing.frames;

import eidolons.ability.InventoryTransactionManager;
import eidolons.client.cc.gui.tabs.HeroItemTab;
import eidolons.client.cc.gui.tabs.operation.ItemSwapPanel;
import eidolons.entity.obj.unit.Unit;
import main.swing.generic.components.G_Panel.VISUALS;

public class PickUpWindow extends OperationWindow {

    private ItemSwapPanel tabComp;

    public PickUpWindow(InventoryTransactionManager inventoryManager, Unit hero,
                        Integer nOfOperations) {
        super(inventoryManager, hero, nOfOperations);
    }

    @Override
    public void init() {
        super.init();
        tabComp.getSwapManager().setOperationsLeft(getNumberOfOperations());
    }

    @Override
    protected HeroItemTab getComponent() {
        if (tabComp == null) {
            tabComp = new ItemSwapPanel(heroModel, cell);
        }
        return tabComp;

    }

    @Override
    protected VISUALS getVisuals() {
        return VISUALS.INV_PANEL;
    }

}

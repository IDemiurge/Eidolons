package main.swing.frames;

import main.ability.InventoryTransactionManager;
import main.client.cc.gui.tabs.HeroItemTab;
import main.client.cc.gui.tabs.operation.ItemSwapPanel;
import main.entity.obj.unit.Unit;
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
        tabComp.getSwapManager().setNumberOfOperations(getNumberOfOperations());
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

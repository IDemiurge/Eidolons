package main.swing.frames;

import main.ability.InventoryTransactionManager;
import main.client.cc.gui.tabs.HeroItemTab;
import main.client.cc.gui.tabs.ItemsTab;
import main.entity.obj.unit.Unit;
import main.swing.generic.components.G_Panel.VISUALS;

public class InventoryWindow extends OperationWindow {

    protected ItemsTab tabComp;

    public InventoryWindow(InventoryTransactionManager inventoryManager, Unit hero,
                           Integer nOfOperations) {
        super(inventoryManager, hero, nOfOperations);
    }

    protected HeroItemTab getComponent() {
        if (tabComp == null) {
            tabComp = new ItemsTab(heroModel,
                    inventoryManager.getInvListManager());
        }
        return tabComp;
    }

    public String getPoolText() {
        return getNumberOfOperations() + " left";
    }

    @Override
    protected VISUALS getVisuals() {
        return VISUALS.INV_PANEL;
    }

}

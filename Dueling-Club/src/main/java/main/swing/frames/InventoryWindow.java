package main.swing.frames;

import main.ability.InventoryManager;
import main.client.cc.gui.tabs.HeroItemTab;
import main.client.cc.gui.tabs.ItemsTab;
import main.entity.obj.unit.DC_HeroObj;
import main.swing.generic.components.G_Panel.VISUALS;

public class InventoryWindow extends OperationWindow {

    protected ItemsTab tabComp;

    public InventoryWindow(InventoryManager inventoryManager, DC_HeroObj hero,
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

    protected String getPoolText() {
        return getNumberOfOperations() + " left";
    }

    @Override
    protected VISUALS getVisuals() {
        return VISUALS.INV_PANEL;
    }

}

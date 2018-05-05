package eidolons.ability.effects.oneshot.dialog;

import eidolons.ability.InventoryTransactionManager;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.math.Formula;
import main.system.threading.WaitMaster;

public class InventoryDialogEffect extends DialogEffect {
    protected Formula numberOfOperations;

    public InventoryDialogEffect(Formula numberOfOperations) {
        this.numberOfOperations = numberOfOperations;
    }

    @Override
    protected void automaticDialogResolve() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean applyThis() {
        getGame().getInventoryTransactionManager().setActive(true);
        Integer operations = numberOfOperations.getInt(ref);
//        getGame().getInventoryManager().setHero(getSource());
        getGame().getInventoryManager().setOperationsPool(operations);
//        CharacterCreator.getHeroManager().addHero(getSource());

        GuiEventManager.trigger(GuiEventType.SHOW_INVENTORY,
         new InventoryDataSource((Unit) ref.getSourceObj()));
        boolean result = (boolean) WaitMaster.waitForInputAnew(
         InventoryTransactionManager.OPERATION);
        getGame().getInventoryTransactionManager().setActive(false);
        if (!result)
            ref.getActive().setCancelled(true);
        return result;
    }

    @Deprecated
    @Override
    protected boolean showDialog() {
        return false;
    }

    protected boolean isPickUp() {
        return false;
    }


}

package main.ability.effects.oneshot.dialog;

import main.ability.InventoryTransactionManager;
import main.client.cc.CharacterCreator;
import main.entity.obj.unit.Unit;
import main.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import main.system.EventCallbackParam;
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
        Integer operations = numberOfOperations.getInt(ref);
        getGame().getInventoryManager().setHero(getSource());
         getGame().getInventoryManager().setOperationsPool(operations);
        CharacterCreator.getHeroManager().addHero(getSource());
        GuiEventManager.trigger(GuiEventType.SHOW_INVENTORY,
         new EventCallbackParam(new InventoryDataSource((Unit) ref.getSourceObj())));
        return (boolean) WaitMaster.waitForInput(InventoryTransactionManager.OPERATION);
    }

    @Override
    protected String getOperationsData() {
        return hero.getGame().getInventoryTransactionManager()
         .getWindow().getOperationsData();
    }

    @Override
    protected boolean showDialog() {
        Integer nOfOperations = numberOfOperations.getInt(ref);
        return hero.getGame().
         getInventoryTransactionManager().showInvWindow(hero, nOfOperations, isPickUp());
    }

    protected boolean isPickUp() {
        return false;
    }

    @Override
    protected void processOperationCommand(String string) {
        hero.getGame().getInventoryManager().setHero(hero);
        hero.getGame().getInventoryTransactionManager().getInvListManager()
         .processOperationCommand(string);
        hero.getGame().getInventoryManager().setHero(null);

    }

}

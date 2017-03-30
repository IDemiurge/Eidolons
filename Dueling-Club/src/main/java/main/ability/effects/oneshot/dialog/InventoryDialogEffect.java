package main.ability.effects.oneshot.dialog;

import main.system.math.Formula;

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
        hero.getGame().getInventoryManager().setHero(hero );
        hero.getGame().getInventoryTransactionManager().getInvListManager()
                .processOperationCommand(string);
        hero.getGame().getInventoryManager().setHero(null );

    }

}

package main.ability.effects.dialog;

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
        return hero.getGame().getInventoryManager().getWindow().getOperationsData();
    }

    @Override
    protected boolean showDialog() {
        Integer nOfOperations = numberOfOperations.getInt(ref);
        return hero.getGame().getInventoryManager().showInvWindow(hero, nOfOperations, isPickUp());
    }

    protected boolean isPickUp() {
        return false;
    }

    @Override
    protected void processOperationCommand(String string) {
        hero.getGame().getInventoryManager().getInvListManager().setHero(hero);
        hero.getGame().getInventoryManager().getInvListManager().processOperationCommand(string);

    }

}

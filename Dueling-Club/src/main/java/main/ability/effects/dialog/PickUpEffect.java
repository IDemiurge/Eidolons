package main.ability.effects.dialog;

import main.system.math.Formula;

public class PickUpEffect extends InventoryDialogEffect {

    public PickUpEffect(Formula numberOfOperations) {
        super(numberOfOperations);
    }

    @Override
    protected boolean isPickUp() {
        return true;
    }
}

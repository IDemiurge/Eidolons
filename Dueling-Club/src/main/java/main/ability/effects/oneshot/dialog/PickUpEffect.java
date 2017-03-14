package main.ability.effects.oneshot.dialog;

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

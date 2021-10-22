package eidolons.ability.ignored.dialog;

import eidolons.entity.obj.DC_Cell;
import eidolons.entity.unit.Unit;
import eidolons.game.exploration.objects.ContainerMaster;
import main.system.math.Formula;

public class PickUpEffect extends InventoryDialogEffect {

    public PickUpEffect(Formula numberOfOperations) {
        super(numberOfOperations);
    }

    @Override
    protected boolean isPickUp() {
        return true;
    }

    @Override
    public boolean applyThis() {
        Unit unit = (Unit) ref.getSourceObj();
        DC_Cell cell = getGame().getCell(unit.getCoordinates());
        return ContainerMaster.loot(unit, cell);
    }
}

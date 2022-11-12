package eidolons.ability.ignored.dialog;

import eidolons.entity.obj.GridCell;
import eidolons.entity.unit.Unit;
import eidolons.game.exploration.dungeon.objects.ContainerMaster;
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
        GridCell cell = getGame().getCell(unit.getCoordinates());
        return ContainerMaster.loot(unit, cell);
    }
}

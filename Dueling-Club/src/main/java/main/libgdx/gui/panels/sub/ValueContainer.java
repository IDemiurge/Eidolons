package main.libgdx.gui.panels.sub;

import main.content.VALUE;
import main.entity.obj.DC_Obj;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created by JustMe on 1/7/2017.
 */
public class ValueContainer extends TableContainer {

    private DC_Obj obj;
    Supplier<List<VALUE>> valueSupplier;

    public ValueContainer(DC_Obj obj,
                          int rows, int columns, Supplier<List<VALUE>> valueSupplier) {
        super(rows, columns);

        this.obj = obj;
        this.valueSupplier = valueSupplier;
        valueSupplier.get().forEach(value -> {
            ValueComp comp = new ValueComp(value, obj,
                    isNameDisplayed(), isIconDisplayed());
//            StyleHolder.getDefaultLabelStyle() TODO
            addActor(comp);
        });
    }

    protected boolean isNameDisplayed() {
        return true;
    }

    protected boolean isIconDisplayed() {
        return true;
    }

}

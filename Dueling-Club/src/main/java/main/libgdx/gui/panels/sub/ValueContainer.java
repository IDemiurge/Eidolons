package main.libgdx.gui.panels.sub;

import main.content.VALUE;
import main.entity.obj.DC_Obj;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by JustMe on 1/7/2017.
 */
public class ValueContainer extends TableContainer {

    private DC_Obj obj;
    Supplier<List<? extends VALUE>> valueSupplier;

    public ValueContainer(DC_Obj obj,
                          int rows, int columns, Supplier<List<? extends VALUE>> valueSupplier) {
        super(rows, columns, getCompSupplier(valueSupplier, obj
        ));

        this.obj = obj;
        this.valueSupplier = valueSupplier;
        valueSupplier.get().forEach(value -> {
            ValueComp comp = new ValueComp(value, obj,
                    isNameDisplayed(), isIconDisplayed());
//            StyleHolder.getDefaultLabelStyle() TODO
            addActor(comp);
        });
    }

    private static Supplier<List<Comp>> getCompSupplier(Supplier<List<? extends VALUE>>
                                                                valueSupplier, DC_Obj obj) {
        return new Supplier<List<Comp>>() {
            @Override
            public List<Comp> get() {
                List<Comp> list = new LinkedList<>();
                valueSupplier.get().forEach(value -> {
                    ValueComp comp = new ValueComp(value, obj,
//                   isNameDisplayed(), isIconDisplayed() TODO
                            true, true
                    );
                });
                return list;
            }
        };

    }

    protected boolean isNameDisplayed() {
        return true;
    }
    protected boolean isIconDisplayed() {
        return true;
    }

}

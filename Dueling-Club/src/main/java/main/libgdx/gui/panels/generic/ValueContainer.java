package main.libgdx.gui.panels.generic;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import main.content.VALUE;
import main.entity.obj.DC_Obj;
import main.libgdx.StyleHolder;
import main.system.images.ImageManager.ALIGNMENT;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by JustMe on 1/7/2017.
 */
public class ValueContainer extends TableContainer {

    Supplier<List<? extends VALUE>> valueSupplier;
    private LabelStyle style;
    private ALIGNMENT textAlignment;
    private boolean isIconDisplayed;
    private boolean isNameDisplayed;
    private DC_Obj obj;

    public ValueContainer(DC_Obj obj,
                          int rows, int columns,
                          Supplier<List<? extends VALUE>> valueSupplier) {
        this(obj, rows, columns,
         true, true, ALIGNMENT.EAST, StyleHolder.getDefaultLabelStyle(),
         valueSupplier, null);
    }

    public ValueContainer(DC_Obj obj,
                          int rows, int columns, boolean isIconDisplayed, boolean isNameDisplayed,
                          ALIGNMENT textAlignment, LabelStyle style,
                          Supplier<List<? extends VALUE>> valueSupplier, String bgImage) {
        super(rows, columns, getCompSupplier(valueSupplier, obj,
         isNameDisplayed, isIconDisplayed, style, textAlignment,
         bgImage));

        this.obj = obj;
        this.style = style;
        this.textAlignment = textAlignment;
        this.valueSupplier = valueSupplier;
        this.isNameDisplayed = isNameDisplayed;
        this.isIconDisplayed = isIconDisplayed;
    }

    private static Supplier<List<Actor>> getCompSupplier(Supplier<List<? extends VALUE>>
                                                          valueSupplier, DC_Obj obj,
                                                         boolean isNameDisplayed,
                                                         boolean isIconDisplayed,
                                                         LabelStyle style, ALIGNMENT
                                                          textAlignment, String bgImage) {
        return new Supplier<List<Actor>>() {
            @Override
            public List<Actor> get() {
                List<Actor> list = new LinkedList<>();
                valueSupplier.get().forEach(value -> {
                    ValueComp comp = new ValueComp(value, obj,
                     isNameDisplayed, isIconDisplayed, textAlignment, style, bgImage);
                    list.add(comp);
                });
                return list;
            }
        };

    }

    protected boolean isNameDisplayed() {
        return isNameDisplayed;
    }

    protected boolean isIconDisplayed() {
        return isIconDisplayed;
    }

}

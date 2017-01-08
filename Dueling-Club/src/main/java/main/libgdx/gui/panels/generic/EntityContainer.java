package main.libgdx.gui.panels.generic;

import main.entity.Entity;
import main.entity.obj.DC_Obj;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by JustMe on 1/7/2017.
 */
public class EntityContainer extends TableContainer {

    private String name;
    private int itemSize;

    public EntityContainer(String name, int itemSize, int columns, int rows
     , Supplier<Collection<? extends Entity>> supplier,
                           DC_Obj obj) {

        super(columns, rows, getCompSupplier(supplier, obj, itemSize));
        this.name = name;
        this.itemSize = itemSize;

    }

    private static Supplier<List<Comp>> getCompSupplier(
     Supplier<Collection<? extends Entity>>
      supplier, DC_Obj obj,
     int itemSize) {
        return new Supplier<List<Comp>>() {
            @Override
            public List<Comp> get() {
                List<Comp> list = new LinkedList<>();
                supplier.get().forEach(value -> {
                    EntityComp comp = new EntityComp(obj);
                    list.add(comp);
                });
                return list;
            }
        };

    }
}

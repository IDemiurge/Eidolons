package main.libgdx.gui.panels.generic;

import main.entity.obj.DC_Obj;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Created by JustMe on 1/8/2017.
 */
public class PagedContainer<T> extends Comp{
    TableContainer container;
    PagedListPanel pages;

    public PagedContainer(String name, boolean entity, int itemSize, int columns, int rows,
                          DC_Obj obj
     , Supplier<Collection<T>> supplier ) {

//        container=entity?
//         new EntityContainer(name,itemSize, rows, columns )
//         :         null;
    }
}

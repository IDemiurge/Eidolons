package main.libgdx.gui.panels.sub;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Created by JustMe on 1/7/2017.
 */
public class EntityContainer extends PagedPanel {

    private String name;
    private int itemSize;
    private Supplier<Collection> supplier;

    public EntityContainer(String name, int itemSize, int columns, int rows
            , Supplier<Collection> supplier
    ) {
        super(columns, rows);
        this.name = name;
        this.itemSize = itemSize;
        this.col = columns;
        this.row = rows;

        this.supplier = supplier;
    }
}

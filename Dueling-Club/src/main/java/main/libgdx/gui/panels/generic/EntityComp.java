package main.libgdx.gui.panels.generic;

import main.entity.Entity;

import java.util.function.Supplier;

/**
 * Created by JustMe on 1/6/2017.
 */
public class EntityComp extends Comp {

    Entity entity;

    public EntityComp() {

    }

    public EntityComp(Supplier<? extends Entity> supplier) {
        super(supplier.get()==null  ? null :  ()-> supplier.get().getImagePath());
    }
    public EntityComp(Entity obj) {
        super(()-> obj==null ? "" : obj.getImagePath());
    }
}

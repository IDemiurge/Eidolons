package main.libgdx.gui.panels.generic;

import main.entity.Entity;
import main.entity.obj.DC_Obj;

/**
 * Created by JustMe on 1/6/2017.
 */
public class EntityComp extends Comp {

    Entity entity;

    public EntityComp() {

    }

    public EntityComp(DC_Obj obj) {
        super(()->{

            return obj.getImagePath();
        });
    }
}

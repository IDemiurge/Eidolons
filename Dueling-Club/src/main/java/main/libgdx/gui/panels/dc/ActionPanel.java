package main.libgdx.gui.panels.dc;

import main.entity.Entity;
import main.entity.obj.DC_Obj;
import main.entity.obj.unit.Unit;
import main.libgdx.gui.panels.generic.EntityContainer;
import main.system.EventCallback;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Created by JustMe on 1/5/2017.
 */
public class ActionPanel<T extends DC_Obj> extends EntityContainer {


    public ActionPanel(Unit hero,
                       Supplier<Collection<? extends Entity>> supplier
            , final EventCallback event,
                       int columns) {
        super("", 64, columns, 1, supplier, hero, event);
    }


}

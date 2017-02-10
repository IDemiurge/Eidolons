package main.libgdx.gui.panels.generic;

import com.badlogic.gdx.scenes.scene2d.Actor;
import main.entity.Entity;
import main.entity.obj.DC_Obj;
import main.system.EventCallback;
import main.system.EventCallbackParam;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by JustMe on 1/7/2017.
 */
public class EntityContainer extends TableContainer {

    private int itemSize;
    private EventCallback event;

    public EntityContainer(String imagePath, int itemSize, int columns, int rows
            , Supplier<Collection<? extends Entity>> supplier, DC_Obj obj) {
        this(imagePath, itemSize, columns, rows, supplier, obj, param -> {
        });
    }

    public EntityContainer(String imagePath, int itemSize, int columns, int rows
            , Supplier<Collection<? extends Entity>> supplier, DC_Obj obj,
                           final EventCallback event) {

        super(columns, rows, getCompSupplier(supplier, obj, itemSize));
        this.imagePath = imagePath;
        this.event = event;
        this.itemSize = itemSize;
    }

    private static Supplier<List<Actor>> getCompSupplier(
            Supplier<Collection<? extends Entity>>
                    supplier, DC_Obj obj,
            int itemSize) {
        return new Supplier<List<Actor>>() {
            @Override
            public List<Actor> get() {
                List<Actor> list = new LinkedList<>();
                supplier.get().forEach(value -> {
                    EntityComp comp = new EntityComp(value);
                    list.add(comp);
                });
                return list;
            }
        };

    }

    public void clicked(Entity obj) {
        event.call(new EventCallbackParam<>(obj));
    }
}

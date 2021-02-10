package libgdx.screens.map.obj;

import libgdx.screens.map.editor.EditorManager;
import eidolons.macro.entity.MacroObj;
import main.system.launch.Flags;

/**
 * Created by JustMe on 2/10/2018.
 */
public abstract class MapObjFactory<E extends MapActor, T extends MacroObj> {

    public abstract E get(T obj);

    public E create(T obj) {
        E e = get(obj);
        handle(e, obj);
        return e;
    }

    public void handle(E actor, T obj) {
        if (Flags.isMapEditor()) {
            EditorManager.map(actor, obj);
            actor.addListener(EditorManager.getMouseListener(actor));
        }
    }
}

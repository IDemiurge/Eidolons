package eidolons.libgdx.screens.map.obj;

import eidolons.game.module.adventure.entity.MacroObj;
import eidolons.libgdx.screens.map.editor.EditorManager;
import main.system.launch.CoreEngine;

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
        if (CoreEngine.isMapEditor()) {
            EditorManager.map(actor, obj);
            actor.addListener(EditorManager.getMouseListener(actor));
        }
    }
}

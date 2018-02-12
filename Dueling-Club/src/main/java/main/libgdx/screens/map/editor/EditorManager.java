package main.libgdx.screens.map.editor;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import main.content.DC_TYPE;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.entity.type.ObjType;
import main.game.module.adventure.MacroGame;
import main.game.module.adventure.MacroRef;
import main.game.module.adventure.entity.MacroObj;
import main.game.module.adventure.entity.MacroParty;
import main.game.module.adventure.map.Place;
import main.libgdx.screens.map.editor.EditorControlPanel.MAP_EDITOR_MOUSE_MODE;
import main.libgdx.screens.map.obj.MapActor;
import main.system.GuiEventManager;
import main.system.MapEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 2/10/2018.
 */
public class EditorManager {
    static MAP_EDITOR_MOUSE_MODE mode;
    static EditorMapView view;
    private static Map<MapActor, MacroObj> actorObjMap = new HashMap<>();

    public static void add(int screenX, int screenY) {
        ObjType type = view.getGuiStage().getPalette().getSelectedType();

        MacroObj obj = create(type);
        added(obj, screenX, screenY);
    }

    public static void added(MacroObj obj, Integer screenX, Integer screenY) {
        if (screenX != null)
            obj.setX(screenX);
        if (screenY != null)
            obj.setY(screenY);
        GuiEventManager.trigger((obj instanceof Place) ?
         MapEvent.CREATE_PLACE :
         MapEvent.CREATE_PARTY);
    }

    private static <E extends MapActor> MacroObj create(ObjType type) {
        MacroRef ref = new MacroRef(MacroGame.game);
        if (type.getOBJ_TYPE_ENUM() == MACRO_OBJ_TYPES.PLACE)
            return new Place(MacroGame.game, type, ref);
        if (type.getOBJ_TYPE_ENUM() == DC_TYPE.PARTY)
            return new MacroParty(type, MacroGame.game, ref);
        return null;
    }

    public static <E extends MapActor> void remove(E actor) {
        MacroObj obj = actorObjMap.remove(actor);
        MacroGame.getGame().getState().removeObject(obj.getId());
        GuiEventManager.trigger(MapEvent.REMOVE_MAP_OBJ, actor);
    }

    public static <E extends MapActor> EventListener getMouseListener(E actor) {
        return EditorInputController.getListener(actor);
    }

    public static MAP_EDITOR_MOUSE_MODE getMode() {
        return mode;
    }

    public static void setMode(MAP_EDITOR_MOUSE_MODE mode) {
        EditorManager.mode = mode;
    }

    public static void map(MapActor actor, MacroObj obj) {
        actorObjMap.put(actor, obj);
    }
}

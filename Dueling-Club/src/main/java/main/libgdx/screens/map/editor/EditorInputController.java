package main.libgdx.screens.map.editor;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.libgdx.bf.mouse.MapInputController;
import main.libgdx.screens.map.editor.EditorControlPanel.MAP_EDITOR_MOUSE_MODE;
import main.libgdx.screens.map.obj.MapActor;
import main.libgdx.screens.map.obj.PlaceActor;
import main.system.GuiEventManager;
import main.system.MapEvent;

import static com.badlogic.gdx.Input.Keys.*;
/**
 * Created by JustMe on 2/10/2018.
 */
public class EditorInputController extends MapInputController {
    MAP_EDITOR_MOUSE_MODE mode;

    public EditorInputController(OrthographicCamera camera) {
        super(camera);
    }

    public static <E extends MapActor> EventListener getListener(E actor) {
        return new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                if (button ==RIGHT)
                    EditorManager.remove(actor);
                MAP_EDITOR_MOUSE_MODE mode = EditorManager.getMode();
       /*
       add listener to each actor
        */
       if (mode!=null )
                switch (mode) {
                    //EDIT
                    //drag!
                    case CLEAR:
                        GuiEventManager.trigger(
                         (actor instanceof PlaceActor) ?
                          MapEvent.REMOVE_PLACE :
                          MapEvent.REMOVE_PARTY
                        );
                        break;
                    case TRACE:
                        break;
                    case ADD:

                        break;
                }
                return super.touchDown(event, x, y, pointer, button);
            }
        };
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        if (button ==1)
        {
            EditorManager.add(screenX, screenY);
            return true;
        }


        return super.touchDown(screenX, screenY, pointer, button);
    }
}

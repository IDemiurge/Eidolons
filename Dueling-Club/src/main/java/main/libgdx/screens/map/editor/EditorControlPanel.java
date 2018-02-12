package main.libgdx.screens.map.editor;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.game.module.adventure.MacroManager;
import main.game.module.adventure.entity.MacroObj;
import main.libgdx.GdxColorMaster;
import main.libgdx.GdxMaster;
import main.libgdx.StyleHolder;
import main.libgdx.gui.panels.dc.TablePanel;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 2/9/2018.
 */
public class EditorControlPanel extends TablePanel {
    public EditorControlPanel() {
        updateRequired = true;
    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);
        init();
    }

    public void init() {
//
        debug();
        setSize(GdxMaster.getWidth()/ 3 * 2, 64);
        TextButtonStyle style = StyleHolder.getTextButtonStyle(FONT.AVQ,
         GdxColorMaster.GOLDEN_WHITE, 18);
        for (MAP_EDITOR_FUNCTION sub : MAP_EDITOR_FUNCTION.values()) {
            TextButton button = new TextButton(sub.name(), style);
            button.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    handleFunction(sub);
                    return super.touchDown(event, x, y, pointer, button);
                }
            });
            add(button);
        }

    }

    public void handleFunction(MAP_EDITOR_FUNCTION function) {
        switch (function) {
            case ADD_ROUTE:
                break;
            case ADD_INNER:
                break;
            case OPTIONS:
                break;
            case VIEW:
                break;
            case SCRIPTS:
                break;
            case EDIT:
                //type? create local type
                break;
            case SAVE:
                MacroManager.saveTheWorld();
//                MacroManager.saveCustomTypes();
                //data into World/Campaign type?
                break;
            case UNDO:
//                operationArgMap
//                Stack<EDITOR_OPERATION> operationStack;
                break;
            case REFRESH:
                break;
        }
    }

    public void operationDone(EDITOR_OPERATION operation, Object arg) {

    }
    public void doOperation(EDITOR_OPERATION operation, Object arg) {
        switch (operation) {
            case ADD_OBJ:
                EditorManager.added((MacroObj) arg, null , null );
                break;
            case REMOVE_OBJ:
                break;
            case CHANGE_OBJ:
                break;
        }
    }
    public enum EDITOR_OPERATION {
        ADD_OBJ,
        REMOVE_OBJ,
        CHANGE_OBJ,

    }
        public enum MAP_EDITOR_FUNCTION {
        ADD_ROUTE,
        //TEST,
        ADD_INNER,
        OPTIONS,
        VIEW,
        SCRIPTS,

        //SHOP OR TAVERN TO A PLACE/TOWN?
        EDIT,
        SAVE,
        UNDO,
        REFRESH
    }

    public enum MAP_EDITOR_MOUSE_MODE {
        CLEAR,
        TRACE,
        ADD,

    }
}

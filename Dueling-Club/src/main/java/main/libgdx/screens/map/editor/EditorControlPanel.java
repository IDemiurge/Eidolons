package main.libgdx.screens.map.editor;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.kotcrab.vis.ui.layout.HorizontalFlowGroup;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.game.module.adventure.MacroGame;
import main.game.module.adventure.MacroManager;
import main.game.module.adventure.entity.MacroObj;
import main.libgdx.GdxColorMaster;
import main.libgdx.GdxMaster;
import main.libgdx.StyleHolder;
import main.libgdx.gui.NinePatchFactory;
import main.system.auxiliary.EnumMaster;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 2/9/2018.
 */
public class EditorControlPanel extends HorizontalFlowGroup {
    public EditorControlPanel() {
        super(10);
        init();
    }


    public void init() {
//
        setSize(GdxMaster.getWidth() / 3 * 2, 64);
        TextButtonStyle style = StyleHolder.getTextButtonStyle(FONT.AVQ,
         GdxColorMaster.GOLDEN_WHITE, 18);
        for (MAP_EDITOR_FUNCTION sub : MAP_EDITOR_FUNCTION.values()) {
            TextButton button = new TextButton(sub.name(), style);
            button.setBackground(new NinePatchDrawable(NinePatchFactory.getTooltip()));
            button.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    try {
                        handleFunction(sub);
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    }
                    return super.touchDown(event, x, y, pointer, button);
                }
            });
            addActor(button);
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
                EditorMapView.getInstance().getEditorParticles().saveAll();
                MacroManager.getPointMaster()
                 .save();
//                MacroManager.saveCustomTypes();
                //data into World/Campaign type?
                break;
            case ALL_TIMES:
                MacroGame.getGame().prepareSetTime(null);
                break;
            case NEXT_TIME:
                int i = EnumMaster.getEnumConstIndex(DAY_TIME.class, MacroGame.getGame().getTime());
                i++;
                if (DAY_TIME.values().length <= i)
                    i = 0;
                DAY_TIME time = DAY_TIME.values()[i];
                MacroGame.getGame().prepareSetTime(time);
                break;
            case UNDO:
                EditorManager.undo();
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
                EditorManager.added((MacroObj) arg, null, null);
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
        NEXT_TIME,
        ALL_TIMES, REFRESH
    }

    public enum MAP_EDITOR_MOUSE_MODE {
        CLEAR,
        TRACE,
        ADD, EMITTER, POINT,

    }
}

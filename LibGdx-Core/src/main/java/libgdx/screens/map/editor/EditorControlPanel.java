package libgdx.screens.map.editor;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.kotcrab.vis.ui.layout.HorizontalFlowGroup;
import eidolons.content.consts.libgdx.GdxColorMaster;
import libgdx.GdxMaster;
import libgdx.StyleHolder;
import libgdx.gui.NinePatchFactory;
import eidolons.macro.MacroGame;
import eidolons.macro.entity.MacroObj;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.system.GuiEventManager;
import main.system.MapEvent;
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
                GdxColorMaster.PALE_GOLD, 18);
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
            case REFRESH:
            case SCRIPTS:
            case VIEW:
            case OPTIONS:
            case ADD_INNER:
                break;
            case EDIT:
                //type? create local type
                break;
            case SAVE:
                //save master worked?TODO
                EditorMapView.getInstance().getEditorParticles().saveAll();
                MapPointMaster.getInstance()
                        .save();
//                MacroManager.saveCustomTypes();
                //data into World/Campaign type?
                break;
            case ALL_TIMES:
                GuiEventManager.trigger(MapEvent.PREPARE_TIME_CHANGED, null);
                break;
            case NEXT_TIME:
                int i = EnumMaster.getEnumConstIndex(DAY_TIME.class, MacroGame.getGame().getTime());
                i++;
                if (DAY_TIME.values().length <= i)
                    i = 0;
                DAY_TIME time = DAY_TIME.values()[i];
                GuiEventManager.trigger(MapEvent.PREPARE_TIME_CHANGED, time);
                break;
            case UNDO:
                EditorManager.undo();
//                operationArgMap
//                Stack<EDITOR_OPERATION> operationStack;
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

        //SHOPS OR TAVERN TO A PLACE/TOWN?
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

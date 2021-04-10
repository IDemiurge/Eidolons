package main.level_editor.backend.handlers.operation;

import eidolons.content.consts.VisualEnums;
import libgdx.bf.grid.handlers.GridAnimHandler;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.gui.screen.LE_Screen;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class LE_HandlerDelegate extends LE_Handler implements IHandlerDelegate {
    public LE_HandlerDelegate(LE_Manager manager) {
        super(manager);
    }

    @Override
    public void toggleVoid() {
        getAdvFuncs().toggleVoid();
    }
    @Override
    public void addBlock() {
        getStructureHandler().addBlock();
    }

    @Override
    public void exportStruct() {
        getStructureHandler().exportStruct();
    }

    @Override
    public void gameView() {
        getDisplayHandler().gameView();
    }

    @Override
    public void fromBlock() {
        getPaletteHandler().fromBlock();
    }

    @Override
    public void gridAnim() {
         VisualEnums.VIEW_ANIM view_anim = LE_Screen.getInstance().getGuiStage().getEnumChooser().chooseEnum(
                 VisualEnums.VIEW_ANIM.class);
        GuiEventManager.trigger(GuiEventType.CHOOSE_GRID_ANIM, view_anim);
    }

    @Override
    public void areaToBlock() {
        getPaletteHandler().areaToBlock();

    }

    @Override
    public void mirror() {
        getAdvFuncs().mirror();
    }

    @Override
    public void rotate() {
        getAdvFuncs().rotate();

    }
}

package main.level_editor.backend.handlers.operation;

import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;

public class LE_HandlerDelegate extends LE_Handler implements IHandlerDelegate {
    public LE_HandlerDelegate(LE_Manager manager) {
        super(manager);
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
    public void addZone() {
        getStructureHandler().addZone();
    }

    @Override
    public void fromBlock() {
        getPaletteHandler().fromBlock();
    }

    @Override
    public void fromAll() {

        getPaletteHandler().fromAll();
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

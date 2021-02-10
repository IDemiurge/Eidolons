package main.level_editor.backend.handlers.structure;

import main.level_editor.backend.handlers.ControlButtonHandler;
import main.level_editor.gui.panels.control.IgnoredCtrlMethod;

public interface IStructureHandler extends ControlButtonHandler {

    void addBlock();
    void mergeBlock();
    @IgnoredCtrlMethod
    void removeBlock();
    void insertBlock();
    void assignBlock();

    @IgnoredCtrlMethod
    void removeCells();
    void addCells();

    void exportStruct();

    void addZone();
    @IgnoredCtrlMethod
    void removeZone();

}

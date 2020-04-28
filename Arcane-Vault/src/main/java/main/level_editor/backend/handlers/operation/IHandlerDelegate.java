package main.level_editor.backend.handlers.operation;

import main.level_editor.backend.handlers.ControlButtonHandler;

public interface IHandlerDelegate extends ControlButtonHandler {

    void addBlock();
    void exportStruct();

    void addZone();
    void fromBlock();
    void fromAll();
    void areaToBlock();
    void mirror();
    void rotate();
}

package main.level_editor.backend.handlers.operation;

import main.level_editor.backend.handlers.ControlButtonHandler;

public interface IHandlerDelegate extends ControlButtonHandler {

    void toggleVoid();

    void addBlock();
    void exportStruct();

    void gameView();
    void fromBlock();
    void gridAnim();
    void areaToBlock();
    void mirror();
    void rotate();
}

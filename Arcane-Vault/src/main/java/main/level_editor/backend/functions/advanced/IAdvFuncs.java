package main.level_editor.backend.functions.advanced;

import main.level_editor.backend.handlers.ControlButtonHandler;

public interface IAdvFuncs extends ControlButtonHandler {
    //operates on selection?

    void fill();

    void clear();

    void setVoid();

    void mirror();
    void rotate();

    void repeat();


}

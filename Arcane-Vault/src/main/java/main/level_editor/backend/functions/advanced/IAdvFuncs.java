package main.level_editor.backend.functions.advanced;

import main.level_editor.backend.handlers.ControlButtonHandler;

public interface IAdvFuncs extends ControlButtonHandler {
    //operates on selection?

    void fill();

    void clear();

    void toggleVoid();

    void setVoid();

    void mirror();
    void rotate();

    void replace();

    void platform();

    void repeat();


}

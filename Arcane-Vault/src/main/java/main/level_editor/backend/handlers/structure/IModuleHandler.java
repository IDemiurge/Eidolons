package main.level_editor.backend.handlers.structure;

import main.level_editor.backend.handlers.ControlButtonHandler;

public interface IModuleHandler extends ControlButtonHandler {

    void addModule();
    void removeModule();
    void editModule();
    void moveModule();

    void swapModules();
    void offsetModule();
    void remap();
    void resetBorders();

    void cloneModule();


}

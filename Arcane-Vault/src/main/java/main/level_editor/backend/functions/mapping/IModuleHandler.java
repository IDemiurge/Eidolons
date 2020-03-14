package main.level_editor.backend.functions.mapping;

public interface IModuleHandler {

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

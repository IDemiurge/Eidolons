package main.level_editor.backend.functions.palette;

public interface IPaletteHandler {

    void createTab();
    void removeTab();
    void mergeTabs();
    void cloneTab();

    void addToTab();
    void removeFromTab();
}

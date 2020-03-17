package main.level_editor.backend.functions.palette;

public interface IPaletteHandler {

    void createPalette();
    void removePalette();
    void mergePalettes();
    void clonePalette();

    void addToPalette();
    void removeFromPalette();
}

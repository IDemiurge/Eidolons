package main.level_editor.backend.functions.palette;

import main.level_editor.backend.handlers.ControlButtonHandler;

public interface IPaletteHandler  extends ControlButtonHandler {

    void fromBlock();
    void fromZone();
    void fromAll();
    void areaToBlock();
    void createPalette();

    void removePalette();
    void mergePalettes();
    void clonePalette();
    void addToPalette();
    void removeFromPalette();


}

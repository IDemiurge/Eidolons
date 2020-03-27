package main.level_editor.backend.handlers.structure;

import main.level_editor.backend.handlers.ControlButtonHandler;

public interface IFloorHandler  extends ControlButtonHandler {

    void editMeta(); //background, default atmo/vfx, module grid, entrances,  params - global illum, TYPE, cell types,
    //should have similar options PER MODULE

    // options from VIS are good example - all kinds of editing options
    void transform(); //top level - all modules?


}

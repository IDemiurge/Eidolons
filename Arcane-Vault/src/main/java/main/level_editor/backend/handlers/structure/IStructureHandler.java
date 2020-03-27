package main.level_editor.backend.handlers.structure;

import main.level_editor.backend.handlers.ControlButtonHandler;

public interface IStructureHandler extends ControlButtonHandler {

    void addBlock();
    void insertBlock();
    void moveBlock();
    void transformBlock();

    void mergeBlock();
    void removeCellsFromBlock();
    void addCellsToBlock();
    void removeBlock();

    void addZone();
    void updateTree();
    void removeZone();
    void assignBlock();




}

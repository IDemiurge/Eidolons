package main.level_editor.backend.functions.structure;

public interface IStructureManager {

    void addBlock();
    void insertBlock();
    void moveBlock();
    void transformBlock();

    void mergeBlock();
    void removeCellsFromBlock();
    void addCellsToBlock();
    void removeBlock();

    void addZone();
    void removeZone();
    void assignBlock();
    void moveZone();




}

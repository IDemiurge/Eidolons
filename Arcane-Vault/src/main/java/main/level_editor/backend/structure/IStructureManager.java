package main.level_editor.backend.structure;

public interface IStructureManager {

    void addZone();
    void removeZone();

    void addBlock();
    void removeBlock();

    void moveBlock();
    void moveZone();

    void mergeCurBlock();

    void removeSelectedCellsFromCurBlock();
    void addSelectedCellsToCurBlock();
    void transformBlock();
    void insertBlock();

    void assignBlockToZone();

    void addModule();
    void transformModule();


}

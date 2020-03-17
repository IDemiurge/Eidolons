package main.level_editor.backend.functions.selection;

public interface ISelectionHandler {

    void selectAll();

    void selectFilter();

    void deselect();

    void undo();

    void redo();



}

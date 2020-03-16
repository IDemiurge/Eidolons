package main.level_editor.backend.functions.handlers;

public interface ISelectionHandler {

    void selectAll();

    void selectFilter();

    void deselect();

    void undo();

    void redo();



}

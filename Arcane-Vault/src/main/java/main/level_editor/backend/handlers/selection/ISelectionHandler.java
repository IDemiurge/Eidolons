package main.level_editor.backend.handlers.selection;

import main.level_editor.backend.handlers.ControlButtonHandler;

public interface ISelectionHandler  extends ControlButtonHandler {


    void count();


    void selectAll();

    void selectFilter();

    void deselect();

    void undo();

    void redo();



}

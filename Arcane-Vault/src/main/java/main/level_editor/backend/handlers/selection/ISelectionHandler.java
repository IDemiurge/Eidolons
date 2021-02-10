package main.level_editor.backend.handlers.selection;

import main.level_editor.backend.handlers.ControlButtonHandler;

public interface ISelectionHandler  extends ControlButtonHandler {


    void selectAll();

    void selectFilter();

    void freeze();

    void unfreeze();

    void toDiamond();

    void deselect();

    void undo();

    void redo();



}

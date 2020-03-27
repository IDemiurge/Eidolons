package main.level_editor.backend.functions.rng;

import main.level_editor.backend.handlers.ControlButtonHandler;

public interface IRngHandler  extends ControlButtonHandler {

    void randomBlock();

    void randomTransform();

    void reinitTileMap();

    void shuffleObjects();



}

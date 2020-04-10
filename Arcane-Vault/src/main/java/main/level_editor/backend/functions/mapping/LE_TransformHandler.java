package main.level_editor.backend.functions.mapping;

import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;

public class LE_TransformHandler extends LE_Handler implements ITransformHandler {
    //all operations are ON MODULES ?
    //should we support dynamic trans for DC?
    //try not to use any LE_Classes? Or use some other CORE
    //we sure had tilemap transforms!

    public LE_TransformHandler(LE_Manager manager) {
        super(manager);
    }

    @Override
    public void rotateCW() {
         //all getCoordinates() must go through a rotating function before save... then reload

    }

    @Override
    public void rotateCCW() {

    }

    @Override
    public void rotateR() {

    }

    @Override
    public void rotate180() {

    }

    @Override
    public void flipX() {

    }

    @Override
    public void flipY() {

    }

    @Override
    public void flipR() {

    }

    @Override
    public void randomTransform() {

    }
}

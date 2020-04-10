package main.level_editor.backend.functions.mapping;

public interface ITransformHandler {

    void rotateCW();
    void rotateCCW();
    void rotateR();
    void rotate180();

    void flipX();
    void flipY();
    void flipR();
    void randomTransform();

}

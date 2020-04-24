package main.level_editor.backend.brush;

public class LE_Brush {

    BrushShape shape;
    LE_BrushType brushType;

    public LE_Brush(BrushShape shape, LE_BrushType brushType) {
        this.shape = shape;
        this.brushType = brushType;
    }

    public BrushShape getShape() {
        return shape;
    }

    public LE_BrushType getBrushType() {
        return brushType;
    }

    public void setShape(BrushShape shape) {
        this.shape = shape;
    }

    public void setBrushType(LE_BrushType brushType) {
        this.brushType = brushType;
    }
}

package main.level_editor.backend.brush;

import main.game.bf.Coordinates;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;

public class BrushHandler extends LE_Handler {
    public BrushHandler(LE_Manager manager) {
        super(manager);
    }

    public void fill(Coordinates origin){

//        brush = new LE_Brush(getModel().getBrush().brushType, getModel().getBrush().shape);
//        fill(getModel().getBrush().brushType, getModel().getBrush().shape, origin);
    }

    private void fill(LE_Brush brush, Coordinates origin) {

    }
}

package main.level_editor.gui.grid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.bf.grid.GridCell;
import eidolons.libgdx.bf.grid.GridCellContainer;
import eidolons.libgdx.gui.LabelX;
import main.level_editor.LevelEditor;

import java.util.Map;

public class LE_GridCell extends GridCellContainer {


    //colorOverlay
Map<Color,     FadeImageContainer> colorOverlays;

    public LE_GridCell(GridCell parent) {
        super(parent);
    }

    @Override
    protected boolean isCoordinatesShown() {
        return LevelEditor.getModel().getDisplayMode().isShowCoordinates();
    }

    @Override
    protected EventListener createListener() {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                LevelEditor.getCurrent().getManager().getMouseHandler().handleCellClick(event, getTapCount(),getGridX(), getGridY());
            }
        };
    }
}

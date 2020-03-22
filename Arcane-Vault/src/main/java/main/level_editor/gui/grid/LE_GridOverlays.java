package main.level_editor.gui.grid;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.entity.obj.DC_Cell;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.bf.grid.GridCellContainer;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.overlays.GridOverlaysManager;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.level_editor.LevelEditor;

public class LE_GridOverlays extends GridOverlaysManager {
    public LE_GridOverlays(GridPanel gridPanel) {
        super(gridPanel);
    }

    @Override
    public void drawOverlay(Actor parent, OVERLAY overlay, Batch batch, Obj obj, int x, int y) {
        super.drawOverlay(parent, overlay, batch, obj, x, y);
    }

    @Override
    protected void initOverlayColor(Batch batch, Obj obj, OVERLAY overlay) {
        //zone colors?
        super.initOverlayColor(batch, obj, overlay);
    }

    @Override
    protected void addTooltip(Obj obj, Actor parent, OVERLAY overlay, Vector2 v, int x, int y) {


    }

    @Override
    protected void drawOverlaysForCell(GridCellContainer container, int x, int y, Batch batch) {
        DC_Cell cell = Eidolons.getGame().getMaster().getCellByCoordinate(Coordinates.get(x, y));

        if (LevelEditor.getManager().getSelectionHandler().isSelected(cell)){
            drawOverlay(container, OVERLAY.IN_PLAIN_SIGHT, batch,cell, x, y);
        }
        if (LevelEditor.getModel().getBlock() == null) {
            return;
        }
        boolean block = LevelEditor.getModel().getBlock().getCoordinatesList().contains(cell.getCoordinates());

        if (block){
            drawOverlay(container, OVERLAY.IN_PLAIN_SIGHT, batch,cell, x, y);
        }
//        if (zone){
//            drawOverlay(container, OVERLAY.IN_SIGHT, batch,cell, x, y);
//        }


    }
}

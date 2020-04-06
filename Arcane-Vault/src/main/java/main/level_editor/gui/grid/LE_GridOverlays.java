package main.level_editor.gui.grid;

import com.badlogic.gdx.graphics.Color;
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

import static eidolons.libgdx.bf.overlays.GridOverlaysManager.OVERLAY.IN_PLAIN_SIGHT;

public class LE_GridOverlays extends GridOverlaysManager {
    public LE_GridOverlays(GridPanel gridPanel) {
        super(gridPanel);
    }

    @Override
    public void drawOverlay(Actor parent, OVERLAY overlay, Batch batch, Obj obj, int x, int y) {
        super.drawOverlay(parent, overlay, batch, obj, x, y);
    }

    @Override
    protected void addTooltip(Obj obj, Actor parent, OVERLAY overlay, Vector2 v, int x, int y) {


    }
    protected void drawOverlays(Batch batch) {
        for (int x = 0; x < cells.length; x++) {
            for (int y = cells[x].length - 1; y >=0 ; y--) {
                GridCellContainer cell = cells[x][y];
                drawOverlaysForCell(cell, x, y, batch);
            }
        }
    }
    @Override
    protected void drawOverlaysForCell(GridCellContainer container, int x, int y, Batch batch) {
        DC_Cell cell = Eidolons.getGame().getMaster().getCellByCoordinate(Coordinates.get(x, y));

        if (LevelEditor.getManager().getSelectionHandler().isSelected(cell)) {
            drawOverlay(container, IN_PLAIN_SIGHT, batch, cell, x, y);
        }
        if (LevelEditor.getModel().getBlock() == null) {
            return;
        }
        boolean block = LevelEditor.getModel().getBlock().getCoordinatesSet().contains(cell.getCoordinates());

        if (block) {
            Color c = LevelEditor.getCurrent().getManager().getStructureManager().
                    getColorForBlock(LevelEditor.getModel().getBlock());
            batch.setColor(c);
            try {
                drawOverlay(container, IN_PLAIN_SIGHT, batch, cell, x, y);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            batch.setColor(new Color(1, 1, 1, 1));
        }
//        if (zone){
//            drawOverlay(container, OVERLAY.IN_SIGHT, batch,cell, x, y);
//        }

    }
    protected void initOverlayColor(Batch batch, Obj obj, OVERLAY overlay) {
        if (overlay==IN_PLAIN_SIGHT) {
            return;
        }
        if (isOverlayAlphaOn(overlay)) {
            batch.setColor(1, 1, 1, fluctuatingAlpha);
        } else {
            batch.setColor(1, 1, 1, 1);
        }
    }
}

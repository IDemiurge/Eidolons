package main.level_editor.gui.grid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.DC_Obj;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelStruct;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.grid.cell.GridCellContainer;
import eidolons.libgdx.bf.grid.moving.PlatformController;
import eidolons.libgdx.bf.overlays.GridOverlaysManager;
import main.game.bf.Coordinates;
import main.level_editor.LevelEditor;
import main.level_editor.backend.display.LE_DisplayMode;
import main.system.math.PositionMaster;

import static eidolons.libgdx.bf.overlays.GridOverlaysManager.OVERLAY.*;

public class LE_GridOverlays extends GridOverlaysManager {
    public LE_GridOverlays(GridPanel gridPanel) {
        super(gridPanel);
    }

    @Override
    public void drawOverlay(Actor parent, OVERLAY overlay, Batch batch, DC_Obj obj, int x, int y) {
        super.drawOverlay(parent, overlay, batch, obj, x, y);
    }

    @Override
    protected void addTooltip(DC_Obj obj, Actor parent, OVERLAY overlay, Vector2 v, int x, int y) {


    }

    protected void drawOverlays(Batch batch) {
        for (int x = 0; x < cells.length; x++) {
            for (int y = cells[x].length - 1; y >= 0; y--) {
                GridCellContainer cell = cells[x][y];
                drawOverlaysForCell(cell, x, y, batch);
            }
        }
        for (PlatformController platform : gridPanel.getPlatformHandler().getPlatforms()) {
            Coordinates c = platform.getDestination();
            GridCellContainer cell = cells[c.x][c.y];
            drawOverlay(cell, SPOTTED, batch, cell.getUserObject(),
                    cell.getGridX(), cell.getGridY());
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    protected void drawOverlaysForCell(GridCellContainer container, int x, int y, Batch batch) {
        DC_Cell cell = container.getUserObject();
        LE_DisplayMode mode = LevelEditor.getModel().getDisplayMode();
        if (getOverlayActor(container, INFO_TEXT) instanceof Label) {
            StringBuilder builder = new StringBuilder();
            if (mode.isShowCoordinates()) {
                builder.append("[").append((int) container.getX()).append(" : ").
                        append((int) container.getY()).append("]");
            }
            if (LevelEditor.getModel().getDisplayMode().isShowGamma()) {
                builder.append("\n").append((gridPanel.getGridManager().getColor(cell.getCoordinates())));
                builder.append("\n").append((gridPanel.getGridManager().getBaseColor(cell.getCoordinates())));
                builder.append("\n").append((gridPanel.getGridManager().getOrigColor(cell.getCoordinates())));
            }

            ((Label) getOverlayActor(container, INFO_TEXT)).setText(
                    builder.toString());
            drawOverlay(container, INFO_TEXT, batch, cell, x, y);
        }
        if (LevelEditor.getManager().getSelectionHandler().isSelected(cell)) {
            drawOverlay(container, IN_PLAIN_SIGHT, batch, cell, x, y);
        }
        if (LE_GridCell.hoveredCell != null)
            if (container == LE_GridCell.hoveredCell) {
                drawOverlay(container, IN_SIGHT, batch, cell, x, y);
            } else {
                boolean keyPressed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);
                if (keyPressed) {
                    if (LE_GridCell.hoveredCell.getGridX() == x
                            || LE_GridCell.hoveredCell.getGridY() == y) {
                        drawOverlay(container, IN_SIGHT, batch, cell, x, y);
                    }
                }
                keyPressed = Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT);
                if (keyPressed) {
                    if (PositionMaster.inLineDiagonally(cell.getCoordinates(),
                            Coordinates.get(LE_GridCell.hoveredCell.getGridX(), LE_GridCell.hoveredCell.getGridY()))) {
                        drawOverlay(container, IN_SIGHT, batch, cell, x, y);
                    }
                }
            }
        if (LevelEditor.getModel().getDisplayMode().isShowAllColors()) {
            for (LevelBlock block : LevelEditor.getGame().getMetaMaster().
                    getDungeonMaster().getStructMaster().getBlocks()) {
                checkDrawForStruct(batch, container, cell, block);
            }
        } else {
            LevelStruct struct = LevelEditor.getModel().getLastSelectedStruct();
            if (struct == null) {
                return;
            }
            checkDrawForStruct(batch, container, cell, struct);
        }

    }

    protected String getInfoText(DC_Obj obj) {
        StringBuilder builder = new StringBuilder();
        // builder.append(ListMaster.toStringList(((DC_Cell) obj).getMarks())).append("\n");
        builder.append("Gamma: ").append(obj.getGame().getVisionMaster()
                .getGammaMaster().getGamma(Eidolons.getMainHero(), obj)).append("\n");
        return builder.toString();
    }

    private void checkDrawForStruct(Batch batch, GridCellContainer container, DC_Cell cell, LevelStruct struct) {
        if (struct.getCoordinatesSet().contains(cell.getCoordinates())) {
            boolean block = struct instanceof LevelBlock;
            if (block) {
                Color c = LevelEditor.getCurrent().getManager().getStructureHandler().
                        getColorForBlock((LevelBlock) struct);
                batch.setColor(new Color(c.r, c.g, c.b, 0.33f));

            }
            try {
                drawOverlay(container, block ? IN_PLAIN_SIGHT : IN_SIGHT, batch, cell,
                        cell.getX(), cell.getY());
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            if (block)
                batch.setColor(new Color(1, 1, 1, 1));
        }
    }
    //        if (zone){
    //            drawOverlay(container, OVERLAY.IN_SIGHT, batch,cell, x, y);
    //        }  }

    protected void initOverlayColor(Batch batch, DC_Obj obj, OVERLAY overlay) {
        if (overlay == IN_PLAIN_SIGHT) {
            return;
        }
        if (isOverlayAlphaOn(overlay)) {
            batch.setColor(1, 1, 1, fluctuatingAlpha);
        } else {
            batch.setColor(1, 1, 1, 1);
        }
    }
}

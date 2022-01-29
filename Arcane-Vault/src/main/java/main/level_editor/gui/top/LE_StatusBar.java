package main.level_editor.gui.top;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.entity.obj.GridCell;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.core.game.DC_BattleFieldGrid;
import eidolons.game.core.game.DC_Game;
import eidolons.game.exploration.dungeon.struct.LevelStruct;
import libgdx.bf.mouse.InputController;
import libgdx.gui.NinePatchFactory;
import libgdx.gui.generic.ValueContainer;
import libgdx.gui.dungeon.panels.TablePanelX;
import libgdx.screens.handlers.ScreenMaster;
import libgdx.assets.texture.TextureCache;
import main.game.bf.Coordinates;
import main.level_editor.LevelEditor;
import main.level_editor.backend.handlers.model.EditorModel;
import main.level_editor.gui.grid.LE_GridCell;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import static main.system.auxiliary.NumberUtils.formatFloat;

public class LE_StatusBar extends TablePanelX {
    /*

selection
palette
last
     */
    EditorModel model;
    ValueContainer palette = new ValueContainer(new TextureRegion(
            TextureCache.getMissingTexture()), "Palette: ", "");
    ValueContainer overlaying = new ValueContainer("Overlaying: ", "");
    ValueContainer curCoordinate = new LE_StatusContainer("Coord: ", "");

    ValueContainer dstUp = new LE_StatusContainer("Up: ", "");
    ValueContainer dstDown = new LE_StatusContainer("Down: ", "");
    ValueContainer dstRight = new LE_StatusContainer("Right: ", "");
    ValueContainer dstLeft = new LE_StatusContainer("Left: ", "");

    ValueContainer selectionInfo = new LE_StatusContainer("", "");
    ValueContainer structInfo = new ValueContainer("", "");

    ValueContainer layerInfo = new LE_StatusContainer("Layer: ", "");
    ValueContainer zoomInfo = new LE_StatusContainer("Zoom: ", "");


    public LE_StatusBar() {
        super(800, 40);
        // zoomInfo.addListener()  cycle zoom
        TablePanelX<Actor> table = new TablePanelX<>(190, 40);
        table.setBackground(NinePatchFactory.getLightPanelFilled90Drawable());
        table.add(zoomInfo).left();
        table.add(layerInfo).left();

        layerInfo.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
             LevelEditor.getManager().cycleLayer();
             super.clicked(event, x, y);
            }
        });
        table.add(selectionInfo).left();
        selectionInfo.setBackground(NinePatchFactory.getLightPanelFilled90Drawable());
        table.add(structInfo).left();
        add(table);


        table = new TablePanelX<>(100, 40);
        table.setBackground(NinePatchFactory.getLightPanelFilled90Drawable());
        table.add(curCoordinate).left();
        add(table);
        table = new TablePanelX<>(500, 40);
        table.setBackground(NinePatchFactory.getLightPanelFilled90Drawable());
        add(table);
        table.add(dstUp).uniformX().left();
        table.add(dstDown).uniformX().left();
        table.add(new TablePanelX<>()).uniformX().left();
        table.add(dstRight).uniformX().left();
        table.add(dstLeft).uniformX().left();

        dstDown.setBackground(NinePatchFactory.getLightPanelFilled90Drawable());
        dstRight.setBackground(NinePatchFactory.getLightPanelFilled90Drawable());

        table = new TablePanelX<>(180, 40);
        table.setBackground(NinePatchFactory.getLightPanelFilled90Drawable());
        add(table);
        table.add(palette);
        table.add(overlaying);

        GuiEventManager.bind(GuiEventType.LE_MODEL_CHANGED, p ->
        {
            model = (EditorModel) p.get();
            setUpdateRequired(true);

        });
    }


    @Override
    public void act(float delta) {
        super.act(delta);
        layerInfo.setValueText(LevelEditor.getManager().getLayer().toString());
        InputController controller = ScreenMaster.getScreen().controller;
        zoomInfo.setValueText(formatFloat(0, controller.getZoom()*100) +"%; " +
                formatFloat(0, controller.getAbsoluteCursorX()  ) +
                ":"+ formatFloat(0, controller.getAbsoluteCursorY() ));

        if (LE_GridCell.hoveredCell == null) {
            return;
        }

        Coordinates c = LE_GridCell.hoveredCell.getUserObject().getCoordinates();
        curCoordinate.setValueText(c.toString());


        DC_BattleFieldGrid grid = DC_Game.game.getGrid();
        if (LE_GridCell.hoveredCell.getParent() != ScreenMaster.getGrid()) {
            LE_GridCell.hoveredCell=null;
            return;
        }
        GridCell[][] cells = grid.getCells();
        LevelStruct struct = DC_Game.game.getDungeonMaster().getStructMaster().getLowestStruct(c);
        if (struct != null) {
            CharSequence type = struct.getClass().getSimpleName();
            structInfo.setNameText(type); //full path?
            structInfo.setValueText(struct.getName());
        }
        int dst = 0;
        boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);
        if (shift) {
            // wall or module end
            Module module = DC_Game.game.getMetaMaster().getModuleMaster().getModule(c);
            if (module == null) {
                return;
            }
            int left = c.x - module.getX();
            int right = module.getWidth() - left - 1;
            int top = c.y - module.getY();
            int bottom = module.getHeight() - top - 1;
            dstLeft.setValueText(left + "");
            dstRight.setValueText(right + "");
            dstDown.setValueText(bottom + "");
            dstUp.setValueText(top + "");
            return;
        }
        for (int i = c.x + 1; i < cells.length; i++) {

            if (grid.getObjects(i, c.y).length > 0)
                break;
            dst++;
        }
        dstRight.setValueText(dst + "");

        dst = 0;
        for (int i = c.x - 1; i >= 0; i--) {
            if (grid.getObjects(i, c.y).length > 0)
                break;
            dst++;
        }
        dstLeft.setValueText(dst + "");

        dst = 0;
        for (int i = c.y + 1; i < cells[0].length; i++) {
            if (grid.getObjects(c.x, i).length > 0)
                break;
            dst++;
        }
        dstDown.setValueText(dst + "");

        dst = 0;
        for (int i = c.y - 1; i >= 0; i--) {
            if (grid.getObjects(c.x, i).length > 0)
                break;
            dst++;
        }
        dstUp.setValueText(dst + "");

    }

    @Override
    public void updateAct(float de) {
        if (model.getDisplayMode().isGameView()) {
            zoomInfo.setName("Game View; Zoom: ");
        } else
            zoomInfo.setName("Zoom: ");
        String img = model.getPaletteSelection().getObjType().getImagePath();
        String name = model.getPaletteSelection().getObjType().getName();
        palette.getImageContainer().getActor().setImage(img);
        palette.getNameContainer().getActor().setText(name);
        if (model.getPaletteSelection().getObjTypeOverlaying() == null) {
            //            overlaying.getImageContainer().getActor().setImage(TextureCache.getEmptyPath());
            overlaying.setValueText("");
        } else {
            // img = model.getPaletteSelection().getObjTypeOverlaying().getImagePath();
            name = model.getPaletteSelection().getObjTypeOverlaying().getName();
            //        overlaying.getImageContainer().getActor().setImage(img);
            overlaying.setValueText(name);
        }
        updateSelectionInfo(model);
    }

    private void updateSelectionInfo(EditorModel model) {
        if (model.getSelection() == null) {
            selectionInfo.setNameText("null" );
            selectionInfo.setValueText( " ");
            return;
        }
        int size = model.getSelection().getIds().size();
        int width = CoordinatesMaster.getWidth(model.getSelection().getCoordinates());
        int height = CoordinatesMaster.getHeight(model.getSelection().getCoordinates());
        selectionInfo.setNameText(width + ":" + height);
        selectionInfo.setValueText(size + " objs");
    }
}

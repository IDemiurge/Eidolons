package main.level_editor.gui.top;

import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.entity.obj.DC_Cell;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.TablePanelX;
import main.game.bf.Coordinates;
import main.level_editor.backend.handlers.model.EditorModel;
import main.level_editor.gui.grid.LE_GridCell;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class LE_StatusBar extends TablePanelX {
    /*

selection
palette
last
     */
    EditorModel model;
    ValueContainer palette = new ValueContainer("Palette: ", "");
    ValueContainer overlaying = new ValueContainer("Overlaying: ", "");
    ValueContainer curCoordinate = new ValueContainer("Coord: ", "");

    ValueContainer dstUp = new ValueContainer("Up: ", "");
    ValueContainer dstDown = new ValueContainer("Down: ", "");
    ValueContainer dstRight = new ValueContainer("Right: ", "");
    ValueContainer dstLeft = new ValueContainer("Left: ", "");

    public LE_StatusBar() {
        super(800, 40);
        TablePanelX<Actor> table = new TablePanelX<>(120, 40);
        table.setBackground(NinePatchFactory.getLightPanelFilledDrawable());
        table.add(curCoordinate).left();
        add(table);
        table = new TablePanelX<>(500, 40);
        table.setBackground(NinePatchFactory.getLightPanelFilledDrawable());
        add(table);
        table.add(dstUp).uniformX().left();
        table.add(dstDown).uniformX().left();
        table.add(new TablePanelX<>()).uniformX().left();
        table.add(dstRight).uniformX().left();
        table.add(dstLeft).uniformX().left();

        dstDown.setBackground(NinePatchFactory.getLightPanelFilledDrawable());
        dstRight.setBackground(NinePatchFactory.getLightPanelFilledDrawable());

        table = new TablePanelX<>(180, 40);
        table.setBackground(NinePatchFactory.getLightPanelFilledDrawable());
        add(table);
        table.add(palette);
        table.add(overlaying);

        GuiEventManager.bind(GuiEventType.LE_GUI_RESET, p ->
        {
            model = (EditorModel) p.get();
            setUpdateRequired(true);
        });

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (LE_GridCell.hoveredCell == null) {
            return;
        }
        Coordinates c = LE_GridCell.hoveredCell.getUserObject().getCoordinates();
        curCoordinate.setValueText(c.toString());
        DC_Cell[][] cells = DC_Game.game.getGrid().getCells();
        int dst = 0;
        for (int i = c.x + 1; i < cells.length; i++) {
            if (DC_Game.game.getGrid().getObjects(i, c.y).length > 0)
                break;
            dst++;
        }
        dstRight.setValueText(dst + "");

        dst = 0;
        for (int i = c.x - 1; i >= 0; i--) {
            if (DC_Game.game.getGrid().getObjects(i, c.y).length > 0)
                break;
            dst++;
        }
        dstLeft.setValueText(dst + "");

        dst = 0;
        for (int i = c.y + 1; i < cells[0].length; i++) {
            if (DC_Game.game.getGrid().getObjects(c.x, i).length > 0)
                break;
            dst++;
        }
        dstDown.setValueText(dst + "");

        dst = 0;
        for (int i = c.y - 1; i >= 0; i--) {
            if (DC_Game.game.getGrid().getObjects(c.x, i).length > 0)
                break;
            dst++;
        }
        dstUp.setValueText(dst + "");

    }

    @Override
    public void update() {
        String img = model.getPaletteSelection().getObjType().getImagePath();
        String name = model.getPaletteSelection().getObjType().getName();
        palette.getImageContainer().getActor().setImage(img);
        palette.getNameContainer().getActor().setText(name);
        if (model.getPaletteSelection().getObjTypeOverlaying() == null) {
//            overlaying.getImageContainer().getActor().setImage(TextureCache.getEmptyPath());
            overlaying.setValueText("");
        }
        img = model.getPaletteSelection().getObjTypeOverlaying().getImagePath();
        name = model.getPaletteSelection().getObjTypeOverlaying().getName();
//        overlaying.getImageContainer().getActor().setImage(img);
        overlaying.setValueText(name);

    }
}

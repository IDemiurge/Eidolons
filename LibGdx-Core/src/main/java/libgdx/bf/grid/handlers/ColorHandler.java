package libgdx.bf.grid.handlers;

import com.badlogic.gdx.graphics.Color;
import eidolons.content.consts.VisualEnums;
import eidolons.content.consts.libgdx.GdxColorMaster;
import eidolons.game.battlecraft.logic.battlefield.vision.colormap.ColorMapDataSource;
import eidolons.game.core.game.DC_Game;
import libgdx.bf.grid.GridPanel;
import libgdx.bf.light.ShadeLightCell;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.Flags;

public class ColorHandler extends GridHandler {
    private static boolean staticColors = Flags.isLiteLaunch();

    public ColorHandler(GridPanel grid) {
        super(grid);
        GridManager gridManager = grid.getGridManager();

        GuiEventManager.bind(GuiEventType.COLORMAP_RESET, p ->
                colorMap = new ColorMap((ColorMapDataSource) p.get()));
    }

    public static boolean isStaticColors() {
        return staticColors;
    }

    public static void setStaticColors(boolean staticColors) {
        ColorHandler.staticColors = staticColors;
    }


    public Float getLightness(Coordinates c) {
        if (grid.getShadowMap().getCells(VisualEnums.SHADE_CELL.GAMMA_SHADOW) == null) {
            return 1f;
        }
        ShadeLightCell cell = grid.getShadowMap().getCells(VisualEnums.SHADE_CELL.GAMMA_SHADOW)[c.x][c.y];
        if (cell == null) {
            return 0f;
        }
        return 1 - cell.getColor().a;
    }

    ColorMap colorMap;

    public void act(float delta) {
        if (getColorMap() != null) {
            getColorMap().act(delta);
        }
    }

    public ColorMap getColorMap() {
        return colorMap;
    }

    public Color getBaseColor(Coordinates c) {
        if (DC_Game.game.getColorMapDS().getBase().get(c) == null) {
            return GdxColorMaster.get(Color.BLACK);
        }
        return GdxColorMaster.get(DC_Game.game.getColorMapDS().getBase().get(c));
    }

    public Color getOrigColor(Coordinates c) {
        return GdxColorMaster.get(DC_Game.game.getColorMapDS().getOriginal().get(c));
    }

    //TODO optimization - into array?
    public Color getColor(Coordinates c) {
        if (getColorMap() == null) {
            return GdxColorMaster.WHITE;
        }
        if (!getColorMap().getOutput().containsKey(c)) {
            return getColorMap().getOriginal().get(c);
            // return GdxColorMaster.get(GdxColorMaster.NULL_COLOR);
        }
        // if (CoreEngine.isLevelEditor()) {
        //     return GdxColorMaster.get(GdxColorMaster.NULL_COLOR);
        // }
        return GdxColorMaster.get(getColorMap().getOutput().get(c));
        // return cell.getColor();
    }
}
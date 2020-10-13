package eidolons.libgdx.bf.grid.handlers;

import com.badlogic.gdx.graphics.Color;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.light.ShadeLightCell;
import eidolons.libgdx.bf.light.ShadowMap;
import main.game.bf.Coordinates;
import main.system.launch.Flags;

public class ColorHandler extends GridHandler{
    private static boolean staticColors= Flags.isLiteLaunch();

    public ColorHandler(GridPanel grid) {
        super(grid);
        GridManager gridManager = grid.getGridManager();
    }

    public static boolean isStaticColors() {
        return staticColors;
    }

    public static void setStaticColors(boolean staticColors) {
        ColorHandler.staticColors = staticColors;
    }


    public Float getLightness(Coordinates c) {
        if (grid.getShadowMap().getCells(ShadowMap.SHADE_CELL.GAMMA_SHADOW) == null) {
            return 1f;
        }
        ShadeLightCell cell = grid.getShadowMap().getCells(ShadowMap.SHADE_CELL.GAMMA_SHADOW)[c.x][c.y];
        if (cell == null) {
            return 0f;
        }
        return 1 - cell.getColor().a;
    }

    public Color getBaseColor(Coordinates c) {
        if (DC_Game.game.getColorMap().getBase().get(c) == null) {
            return GdxColorMaster.get(Color.BLACK);
        }
        return GdxColorMaster.get(DC_Game.game.getColorMap().getBase().get(c));
    }

    public Color getOrigColor(Coordinates c) {
        return GdxColorMaster.get(DC_Game.game.getColorMap().getOriginal().get(c));
    }
//TODO optimization - into array?
    public Color getColor(Coordinates c) {
        if (!DC_Game.game.getColorMap().getOutput().containsKey(c)) {
            return DC_Game.game.getColorMap().getOriginal().get(c);
            // return GdxColorMaster.get(GdxColorMaster.NULL_COLOR);
        }
        // if (CoreEngine.isLevelEditor()) {
        //     return GdxColorMaster.get(GdxColorMaster.NULL_COLOR);
        // }
        return GdxColorMaster.get(DC_Game.game.getColorMap().getOutput().get(c));
        // return cell.getColor();
    }
}
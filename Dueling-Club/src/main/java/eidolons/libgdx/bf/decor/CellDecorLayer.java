package eidolons.libgdx.bf.decor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.libgdx.bf.datasource.GraphicData;
import eidolons.libgdx.bf.grid.GridLayer;
import eidolons.libgdx.bf.grid.GridPanel;
import main.game.bf.Coordinates;
import main.system.launch.CoreEngine;

import java.util.ArrayList;
import java.util.List;

public class CellDecorLayer extends GridLayer {

    public static boolean spriteTest = CoreEngine.isLevelEditor();
    List<Actor>[][] map;

    public CellDecorLayer(GridPanel grid) {
        super(grid);
        map = new List[grid.getModuleCols()][grid.getModuleRows()];
    }


    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    protected void act(int x, int y, float delta) {
        if (map[x][y] != null) {
            for (Actor actor : map[x][y]) {
                actor.act(delta);
            }
        }
    }

    @Override
    protected void draw(int x, int y, Batch batch, float parentAlpha) {
        if (map[x][y] != null) {
            for (Actor actor : map[x][y]) {
                setColor(actor, x, y);
                actor.draw(batch, parentAlpha);
            }
        }
    }

    public void add(Coordinates c, List<GraphicData> graphicData) {
        remove(c);
        List<Actor> list = new ArrayList<>();
        for (GraphicData graphicDatum : graphicData) {
            Actor decor = DecorFactory.createDecor(c, graphicDatum);
            addActor(decor);
            int x = c.x * 128;
            int y = grid.getGdxY_ForModule(c.y) * 128;
            ////TODO centered?
            decor.setPosition(x + graphicDatum.getIntValue(GraphicData.GRAPHIC_VALUE.x),
                    y + graphicDatum.getIntValue(GraphicData.GRAPHIC_VALUE.y));

            list.add(decor);
        }

        map[c.x][c.y] = list;

    }

    public void remove(Coordinates c) {
        List<Actor> list = map[c.x][c.y];
        if (list != null)
            for (Actor noHitImage : list) {
                noHitImage.remove();
            }
    }


    public enum CELL_PATTERN {
        CROSS,
        CROSS_DIAG,
        CENTERPIECE,
        CHESS,

        GRID,
        SPIRAL,
        CONCENTRIC,
        OUTER_BORDER,
        //        DIAMOND,
    }

    public enum CELL_UNDERLAY {
        CRACKS,
        ROCKS,
        VINES,
        DARK,
        cobwebs,
        ruins,

    }
}

package libgdx.bf.decor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import eidolons.content.consts.libgdx.GdxColorMaster;
import libgdx.bf.datasource.GraphicData;
import libgdx.bf.grid.GridLayer;
import libgdx.bf.grid.GridPanel;
import main.game.bf.Coordinates;
import main.system.launch.CoreEngine;

import java.util.ArrayList;
import java.util.List;

public class CellDecorLayer extends GridLayer<CellDecor> {

    public static boolean spriteTest = CoreEngine.isLevelEditor();
    List<CellDecor>[][] map;

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
            for (CellDecor actor : map[x][y]) {
                setColor(actor, x, y);
                actor.draw(batch, parentAlpha);
            }
        }
    }

    public void add(Coordinates c, CellDecor decor, int x1, int y1) {
        addActor(decor);
        int x = c.x * 128;
        int y = grid.getGdxY_ForModule(c.y) * 128;
        decor.setPosition(x + x1, y + y1);
    }

    public void add(Coordinates c, List<GraphicData> graphicData, EventListener listener) {
        remove(c);
        List<CellDecor> list = new ArrayList<>();
        for (GraphicData graphicDatum : graphicData) {
            CellDecor decor = DecorFactory.createDecor(c, graphicDatum);
            add(c, decor, graphicDatum.getIntValue(GraphicData.GRAPHIC_VALUE.x),
                    graphicDatum.getIntValue(GraphicData.GRAPHIC_VALUE.y));

            if (listener != null) {
                decor.addListener(listener);
            }
            list.add(decor);
        }

        map[c.x][c.y] = list;

    }


    @Override
    protected void setColor(CellDecor actor, int x, int y) {
        if (actor.getBaseColor() != null && !actor.getBaseColor().equals(GdxColorMaster.WHITE)) {
            return;
        }
        super.setColor(actor, x, y);
    }

    public void remove(Coordinates c) {
        List<CellDecor> list = map[c.x][c.y];
        if (list != null)
            for (CellDecor cellDecor : list) {
                cellDecor.remove();
            }
    }


}

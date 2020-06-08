package eidolons.libgdx.bf.decor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.libgdx.bf.datasource.GraphicData;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.gui.generic.GroupX;
import main.game.bf.Coordinates;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CellDecorLayer extends GroupX {

    public static boolean spriteTest=true;
    GridPanel gridPanel;
    Map<Coordinates, List<Actor>> map = new LinkedHashMap<>();

    public CellDecorLayer(GridPanel gridPanel) {
        this.gridPanel = gridPanel;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    public void add(Coordinates c, List<GraphicData> graphicData) {
            remove(c);
        List<Actor> list = new ArrayList<>();
        for (GraphicData graphicDatum : graphicData) {
            Actor decor = DecorFactory.createDecor(c, graphicDatum);
            addActor(decor);
            int x = c.x * 128;
            int y = gridPanel.getGdxY_ForModule(c.y) * 128;
            ////TODO centered?
            decor.setPosition(x + graphicDatum.getIntValue(GraphicData.GRAPHIC_VALUE.x),
                    y + graphicDatum.getIntValue(GraphicData.GRAPHIC_VALUE.y));

            list.add(decor);
        }

        map.put(c, list);
    }

    public void remove(Coordinates c) {
        List<Actor> list = map.get(c);
        if (list != null) {
        for (Actor noHitImage : list) noHitImage.remove();
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

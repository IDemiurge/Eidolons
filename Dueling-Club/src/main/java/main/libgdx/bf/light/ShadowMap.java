package main.libgdx.bf.light;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import main.game.core.game.DC_Game;
import main.libgdx.bf.GridConst;
import main.libgdx.bf.GridPanel;
import main.system.auxiliary.StrPathBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by JustMe on 8/16/2017.
 */
public class ShadowMap {

    public static final SHADE_LIGHT[] SHADE_LIGHT_VALUES = SHADE_LIGHT.values();
    private static boolean on=true;
    private GridPanel grid;
    private Map<SHADE_LIGHT, ShadeLightCell[][]> cells = new LinkedHashMap<>();

    public ShadowMap(GridPanel grid) {
        this.grid = grid;
        init();
    }

    public static boolean isOn() {
        return on;
    }
//TODO act -> fluctuate alpha

    public static void setOn(boolean on) {
        ShadowMap.on = on;
    }

    private void init() {
        for (SHADE_LIGHT type : SHADE_LIGHT_VALUES) {
            getCells().put(type, new ShadeLightCell[grid.getCols()][grid.getRows()]);
            for (int x = 0; x < grid.getCols(); x++) {
                for (int y = 0; y < grid.getRows(); y++) {
                    ShadeLightCell cell = new ShadeLightCell(type, x, y);
                    getCells(type)[x][y] = cell;
                    grid.addActor(cell);
                    float offsetX= (GridConst.CELL_W-cell.getWidth())/2;
                    float offsetY=  (GridConst.CELL_H-cell.getHeight())/2;

                    cell.setPosition(
                     grid.getCells()[x][grid.getRows() - 1 - y].getX()+offsetX,
                     grid.getCells()[x][grid.getRows() - 1 - y].getY()+offsetY);

                    cell.setColor(1, 1, 1, type.defaultAlpha);
                    cell.addListener(new EventListener() {
                        @Override
                        public boolean handle(Event event) {
                            return true;
                        }
                    });
                }
            }
        }
        bindEvents();
//        update();

    }

    private void bindEvents() {
//        GuiEventManager.bind(GuiEventType.UPDATE_LIGHT, p -> {
//            update();
//        }); now part of gridPanel act()

    }

    public void update() {
        for (int x = 0; x < grid.getCols(); x++) {
            for (int y = 0; y < grid.getRows(); y++) {
                for (SHADE_LIGHT type : SHADE_LIGHT_VALUES) {
                    float alpha = 0;
                    if (isOn())
                        try {
                            alpha = DC_Game.game.getVisionMaster().
                             getGammaMaster().getAlphaForShadowMapCell(x, y, type);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    if (type == SHADE_LIGHT.GAMMA_LIGHT) {
                        alpha /= 2;
                    }
                    if (getCells(type)[x][y].getColor().a != alpha) {
                        getCells(type)[x][y].setBaseAlpha(alpha);
                        getCells(type)[x][y].setColor(1, 1, 1, alpha);
                    }

                    getCells(type)[x][y].adjustPosition(x, y);
                }
            }

        }
    }

    public Map<SHADE_LIGHT, ShadeLightCell[][]> getCells() {
        return cells;
    }

    public ShadeLightCell[][] getCells(SHADE_LIGHT type) {
        return cells.get(type);
    }

    public void setZtoMax(SHADE_LIGHT sub) {
        ShadeLightCell[][] array = getCells().get(sub);
        for (int x = 0; x < array.length; x++) {
            for (int y = 0; y < array[x].length; y++) {
                ShadeLightCell cell = array[x][y]; if (cell.getColor().a!=0)
                cell.setZIndex(Integer.MAX_VALUE);
            }
        }

    }

    public enum SHADE_LIGHT {
        GAMMA_SHADOW(0.75f, StrPathBuilder.build("UI", "outlines", "shadows", "shadow.png")),
        GAMMA_LIGHT(0, StrPathBuilder.build("UI", "outlines", "shadows", "light.png")),
        LIGHT_EMITTER(0, StrPathBuilder.build("UI", "outlines", "shadows", "light emitter.png")),
        CONCEALMENT(0.5f, StrPathBuilder.build("UI", "outlines", "shadows", "concealment.png")),
        BLACKOUT(0 , StrPathBuilder.build("UI", "outlines", "shadows", "blackout.png")),
        HIGLIGHT(0 , StrPathBuilder.build("UI", "outlines", "shadows", "highlight.png")),




        ;
        public float defaultAlpha;
        private String texturePath;

        SHADE_LIGHT(float alpha, String texturePath) {
            defaultAlpha = alpha;
            this.texturePath = texturePath;
        }

        public String getTexturePath() {
            return texturePath;
        }

    }

}









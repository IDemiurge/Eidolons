package eidolons.libgdx.bf.light;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.SuperActor.ALPHA_TEMPLATE;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.gui.generic.GroupX;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

import static eidolons.libgdx.bf.light.ShadowMap.SHADE_LIGHT.*;

/**
 * Created by JustMe on 8/16/2017.
 */
public class ShadowMap extends GroupX {

    public static final SHADE_LIGHT[] SHADE_LIGHT_VALUES = {
     GAMMA_SHADOW,
     GAMMA_LIGHT,
     LIGHT_EMITTER,
     BLACKOUT,
     HIGLIGHT
    };
    private static boolean on = true;
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

    public static boolean isColoringSupported() {
        return false;
    }

    private void init() {
        setSize(grid.getWidth(), grid.getHeight());
        for (SHADE_LIGHT type : SHADE_LIGHT_VALUES) {
            getCells().put(type, new ShadeLightCell[grid.getCols()][grid.getRows()]);
            for (int x = 0; x < grid.getCols(); x++) {
                for (int y = 0; y < grid.getRows(); y++) {
                    ShadeLightCell cell = new ShadeLightCell(type );
                    if (grid.getCells()[x][y] == null) {
                        if (type == GAMMA_SHADOW)
                            cell.setVoid(true);
                        else
                            continue;
                    }
                    getCells(type)[x][y] = cell;
                    addActor(cell);
                    float offsetX = (GridMaster.CELL_W - cell.getWidth()) / 2;
                    float offsetY = (GridMaster.CELL_H - cell.getHeight()) / 2;

                    cell.setPosition(x * GridMaster.CELL_W + offsetX, y * GridMaster.CELL_H + offsetY);

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
        GuiEventManager.bind(GuiEventType.UPDATE_LIGHT, p -> {
            update();
            main.system.auxiliary.log.LogMaster.log(1, "MANUAL SHADOW MAP UPDATE ");
        });

    }

    public void update() {
        for (SHADE_LIGHT type : SHADE_LIGHT_VALUES) {
            for (int x = 0; x < grid.getCols(); x++) {
                for (int y = 0; y < grid.getRows(); y++) {
                    ShadeLightCell cell = getCells(type)[x][y];
                    if (cell == null) {
                        continue;
                    }
                    float alpha = 0;
                    if (isOn())
                        alpha = DC_Game.game.getVisionMaster().
                         getGammaMaster().getAlphaForShadowMapCell(x, y, type);
                    if (cell.getColor().a != alpha) {
                        cell.setBaseAlpha(alpha);

                        if (type == LIGHT_EMITTER)
                            cell.setColor(1, 1, 1, alpha); //was this the reason for the light-glitches?
                    }
                    if (type == SHADE_LIGHT.LIGHT_EMITTER)
                        cell.adjustPosition(x, y);
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
                ShadeLightCell cell = array[x][y];
                if (cell != null)
                    if (!cell.isIgnored())
                        if (cell.getColor().a != 0)
                            cell.setZIndex(Integer.MAX_VALUE);
            }
        }

    }

    public static ALPHA_TEMPLATE getTemplateForShadeLight(SHADE_LIGHT type) {
        switch (type) {
            case GAMMA_SHADOW:
            case GAMMA_LIGHT:
                break;
            case LIGHT_EMITTER:
                break;
            case CONCEALMENT:
                break;
            case BLACKOUT:
                break;
            case HIGLIGHT:
                break;
        }
        return ALPHA_TEMPLATE.HIGHLIGHT_MAP;
    }

    public enum SHADE_LIGHT {
        GAMMA_SHADOW(0.75f, StrPathBuilder.build("UI", "outlines", "shadows", "shadow neu.png")),
        GAMMA_LIGHT(0, StrPathBuilder.build("UI", "outlines", "shadows", "light.png")),
        LIGHT_EMITTER(0, StrPathBuilder.build("UI", "outlines", "shadows", "light emitter.png")),
        CONCEALMENT(0.5f, StrPathBuilder.build("UI", "outlines", "shadows", "concealment.png")),
        BLACKOUT(0, StrPathBuilder.build("UI", "outlines", "shadows", "blackout.png")),
        HIGLIGHT(0, StrPathBuilder.build("UI", "outlines", "shadows", "highlight.png")),;
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









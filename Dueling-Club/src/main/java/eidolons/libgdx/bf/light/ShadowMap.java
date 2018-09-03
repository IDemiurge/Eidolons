package eidolons.libgdx.bf.light;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import eidolons.ability.effects.common.LightEmittingEffect;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.SuperActor.ALPHA_TEMPLATE;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.gui.generic.GroupX;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;
import main.system.math.PositionMaster;

import java.util.*;

import static eidolons.libgdx.bf.light.ShadowMap.SHADE_CELL.*;

/**
 * Created by JustMe on 8/16/2017.
 */
public class ShadowMap extends GroupX {

    public static final SHADE_CELL[] SHADE_CELL_VALUES = {
     VOID,
     GAMMA_SHADOW,
     GAMMA_LIGHT,
     LIGHT_EMITTER,
     BLACKOUT,
     HIGLIGHT
    };
    private static boolean on = true;
    private GridPanel grid;
    private Map<SHADE_CELL, ShadeLightCell[][]> cells = new LinkedHashMap<>();
    private List<LightEmitter>[][] emitters;

    public ShadowMap(GridPanel grid) {
        this.grid = grid;
        setSize(grid.getWidth(), grid.getHeight());
        init();
        setTransform(false);
    }

    public static boolean isOn() {
        return on;
    }
    //TODO act -> fluctuate alpha

    public static void setOn(boolean on) {
        ShadowMap.on = on;
    }

    public static boolean isColoringSupported() {
        return true;
    }

    public static  float getInitialAlphaCoef() {
        return 0.8f;
    }
    public static ALPHA_TEMPLATE getTemplateForShadeLight(SHADE_CELL type) {
        switch (type) {
            case VOID:
                return null;
            case GAMMA_SHADOW:
                return ALPHA_TEMPLATE.SHADE_CELL_GAMMA_SHADOW;
            case GAMMA_LIGHT:
                return ALPHA_TEMPLATE.SHADE_CELL_GAMMA_LIGHT;
            case LIGHT_EMITTER:
                return ALPHA_TEMPLATE.SHADE_CELL_LIGHT_EMITTER;
            case CONCEALMENT:
                break;
            case BLACKOUT:
                break;
            case HIGLIGHT:
                return ALPHA_TEMPLATE.SHADE_CELL_HIGHLIGHT;
        }
        return ALPHA_TEMPLATE.HIGHLIGHT_MAP;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!on)
            return;
        setTransform(false);
        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        if (!on)
            return;
        super.act(delta);
    }

    private void init() {
        setSize(grid.getWidth(), grid.getHeight());
        emitters = new List[grid.getCols()][grid.getRows()];
        for (SHADE_CELL type : SHADE_CELL_VALUES) {
            getCells().put(type, new ShadeLightCell[grid.getCols()][grid.getRows()]);

            for (int x = 0; x < grid.getCols(); x++) {
                for (int y = 0; y < grid.getRows(); y++) {
                    if (grid.getCells()[x][y] == null) {
                        if (type != VOID)
                            continue;
                    } else
                    if (type==VOID)
                        continue;
                    if (type != LIGHT_EMITTER) {
                        ShadeLightCell cell = new ShadeLightCell(type);
                        getCells(type)[x][y] = cell;
                        addShadowMapElement(cell, x, y, type.defaultAlpha);
                    } else {
                        if (emitters[x][y] == null) {
                            emitters[x][y] = new ArrayList<>();
                        }
                        Coordinates c = Coordinates.get(x, grid.getRows() - 1 - y);
                        Set<BattleFieldObject> objects = DC_Game.game.getOverlayingObjects(c);
                        if (objects.isEmpty()) {
                            objects = DC_Game.game.getObjectsAt(c);
                        }
                        if (objects.isEmpty()) {
                            continue;
                        }
                        Iterator<BattleFieldObject> iterator = objects.iterator();
                        while (iterator.hasNext()) {
                            BattleFieldObject obj = iterator.next();
                            if (obj instanceof Unit)
                                continue;
                            LightEmittingEffect effect = DC_Game.game.getRules().getIlluminationRule().getEffectCache().get(obj);
                            if (effect == null) {
                                continue;
                            }
                            LightEmitter emitter = new LightEmitter(obj, effect);
                            emitters[x][y].add(emitter);
                            addShadowMapElement(emitter, x, y, type.defaultAlpha);
                        }
                    }

                }
            }
        }
        bindEvents();
        //        update();

    }

    private void addShadowMapElement(Group element, int x, int y, float defaultAlpha) {
        addActor(element);
        float offsetX = (GridMaster.CELL_W - element.getWidth()) / 2;
        float offsetY = (GridMaster.CELL_H - element.getHeight()) / 2;

        element.setPosition(x * GridMaster.CELL_W + offsetX, y * GridMaster.CELL_H + offsetY);

        element.setColor(1, 1, 1, defaultAlpha);
        element.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                return true;
            }
        });
    }

    private void bindEvents() {
        GuiEventManager.bind(GuiEventType.UPDATE_LIGHT, p -> {
            update();
            main.system.auxiliary.log.LogMaster.log(1, "MANUAL SHADOW MAP UPDATE ");
        });

    }

    public void update() {
        if (!isOn())
            return ;
        for (SHADE_CELL type : SHADE_CELL_VALUES) {
            for (int x = 0; x < grid.getCols(); x++) {
                for (int y = 0; y < grid.getRows(); y++) {
                    ShadeLightCell cell = getCells(type)[x][y];
                    if (cell != null) {
                        if (type==VOID) {
                            if (cell.getBaseAlpha()!=0) {
                                continue;
                            }
                        }
                        float  alpha = DC_Game.game.getVisionMaster().
                             getGammaMaster().getAlphaForShadowMapCell(x, PositionMaster.getLogicalY(y), type);
                        if (Math.abs(cell.getBaseAlpha() - alpha) > 0.1f) {
                            cell.setBaseAlpha(alpha);

                            //                        if (type == LIGHT_EMITTER)
                            //                            cell.setColor(1, 1, 1, alpha); //was this the reason for the light-glitches?
                        }
                    }
                    if (type == SHADE_CELL.LIGHT_EMITTER) {
                        //                        cell.adjustPosition(x, y);
                        List<LightEmitter> list = emitters[x][y];
                        if (list == null)
                            continue; //for void
                        for (LightEmitter lightEmitter : list) {
                            float  alpha = DC_Game.game.getVisionMaster().
                             getGammaMaster().getLightEmitterAlpha(x, PositionMaster.getLogicalY(y));
                            if (Math.abs(lightEmitter.getBaseAlpha() - alpha) > 0.1f)
                                lightEmitter.setBaseAlpha(alpha);

                            lightEmitter.update();
                        }
                    }

                }
            }

        }
    }

    public Map<SHADE_CELL, ShadeLightCell[][]> getCells() {
        return cells;
    }

    public ShadeLightCell[][] getCells(SHADE_CELL type) {
        return cells.get(type);
    }

    public void setZtoMax(SHADE_CELL sub) {
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

    public enum SHADE_CELL {
        GAMMA_SHADOW(0.5f, StrPathBuilder.build("UI", "outlines", "shadows", "shadow neu.png")),
        GAMMA_LIGHT(0, StrPathBuilder.build("UI", "outlines", "shadows", "light.png")),
        LIGHT_EMITTER(0, StrPathBuilder.build("UI", "outlines", "shadows", "light emitter.png")),
        CONCEALMENT(0.5f, StrPathBuilder.build("UI", "outlines", "shadows", "concealment.png")),
        BLACKOUT(0, StrPathBuilder.build("UI", "outlines", "shadows", "blackout.png")),
        HIGLIGHT(1, StrPathBuilder.build("UI", "outlines", "shadows", "highlight.png")),
        VOID(0, StrPathBuilder.build("UI", "outlines", "shadows",   "void.png")),;
        public float defaultAlpha;
        private String texturePath;

        SHADE_CELL(float alpha, String texturePath) {
            defaultAlpha = alpha;
            this.texturePath = texturePath;
        }

        public String getTexturePath() {
            return texturePath;
        }

    }

}









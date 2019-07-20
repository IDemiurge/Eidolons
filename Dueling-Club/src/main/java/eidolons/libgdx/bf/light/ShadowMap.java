package eidolons.libgdx.bf.light;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import eidolons.ability.effects.common.LightEmittingEffect;
import eidolons.content.PROPS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.bf.Fluctuating;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.Fluctuating.ALPHA_TEMPLATE;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.gui.generic.GroupX;
import main.content.CONTENT_CONSTS;
import main.data.filesys.PathFinder;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
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
    private static final Color DEFAULT_COLOR = new Color(1, 0.9f, 0.7f, 1);
    private static boolean on = true;
    private GridPanel grid;
    private Map<SHADE_CELL, ShadeLightCell[][]> cells = new LinkedHashMap<>();
    private List<LightEmitter>[][] emitters;

    public ShadowMap(GridPanel grid) {
        this.grid = grid;
        setSize(grid.getWidth(), grid.getHeight());
        try {
            init();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
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

    public static float getInitialAlphaCoef() {
        return 0.8f;
    }

    public static ALPHA_TEMPLATE getTemplateForShadeLight(SHADE_CELL type) {
        switch (type) {
            case VOID:
                return null;
            case GAMMA_SHADOW:
                return Fluctuating.ALPHA_TEMPLATE.SHADE_CELL_GAMMA_SHADOW;
            case GAMMA_LIGHT:
                return Fluctuating.ALPHA_TEMPLATE.SHADE_CELL_GAMMA_LIGHT;
            case LIGHT_EMITTER:
                return Fluctuating.ALPHA_TEMPLATE.SHADE_CELL_LIGHT_EMITTER;
            case CONCEALMENT:
                break;
            case BLACKOUT:
                break;
            case HIGLIGHT:
                return Fluctuating.ALPHA_TEMPLATE.SHADE_CELL_HIGHLIGHT;
        }
        return Fluctuating.ALPHA_TEMPLATE.HIGHLIGHT_MAP;
    }

    public static Color getLightColor(BattleFieldObject userObject) {

        CONTENT_CONSTS.COLOR_THEME colorTheme = null;
        if (userObject != null) {
            colorTheme = new EnumMaster<CONTENT_CONSTS.COLOR_THEME>().
             retrieveEnumConst(CONTENT_CONSTS.COLOR_THEME.class, userObject.getProperty
              (PROPS.COLOR_THEME, true));
        }
        if (colorTheme == null) {
            Dungeon obj = Eidolons.game.getDungeon();
            colorTheme = obj.getColorTheme();
        }

        Color c = null;
        if (colorTheme != null)
            c = GdxColorMaster.getColorForTheme(colorTheme);
        if (c != null) return c;
        return  DEFAULT_COLOR;
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
            if (type == VOID) {
                if (EidolonsGame.BOSS_FIGHT)
                    continue;
            }
            getCells().put(type, new ShadeLightCell[grid.getCols()][grid.getRows()]);

            for (int x = 0; x < grid.getCols(); x++) {
                for (int y = 0; y < grid.getRows(); y++) {
                    if (grid.getCells()[x][y] == null) {
                        if (type != VOID)
                            continue;
                    } else if (type == VOID)
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
                            LightEmittingEffect effect = DC_Game.game.getRules().getIlluminationRule().
                                    getLightEmissionEffect(obj);
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

    private LightEmitter getEmitterForObj(BattleFieldObject obj) {
        LightEmitter emitter = null;
        Coordinates c = obj.getBufferedCoordinates();
        List<LightEmitter> list = emitters[c.x][grid.getRows() - c.y];

        for (LightEmitter lightEmitter : list) {
            if (lightEmitter.getUserObject() == obj) {
                emitter = lightEmitter;
                break;
            }
        }
        return emitter;
    }
    private void bindEvents() {
        GuiEventManager.bind(GuiEventType.RESET_LIGHT_EMITTER, p -> {
            BattleFieldObject obj = (BattleFieldObject) p.get();
            LightEmitter emitter =getEmitterForObj(obj);
//            emitter.setTeamColor(color);
            emitter.reset();

        });
        GuiEventManager.bind(GuiEventType.LIGHT_EMITTER_MOVED, p -> {

            BattleFieldObject obj = (BattleFieldObject) p.get();
            Coordinates c = obj.getBufferedCoordinates();
            List<LightEmitter> list = emitters[c.x][grid.getRows() - c.y];

            LightEmitter emitter =getEmitterForObj(obj);

            list.remove(emitter);

            Coordinates c1 = obj.getCoordinates();
            list = emitters[c1.x][grid.getRows() - c1.y];

            list.add(emitter);

            int x = (c1.x - c.x) * 128;
            int y = (c1.y - c.y) * 128;

            ActionMaster.addMoveByAction(emitter, x, y, 0.5f);

            main.system.auxiliary.log.LogMaster.log(1, "RESET_LIGHT_EMITTER " + p);

        });
        GuiEventManager.bind(GuiEventType.UPDATE_LIGHT, p -> {
            update();
            main.system.auxiliary.log.LogMaster.log(1, "MANUAL SHADOW MAP UPDATE ");
        });

    }


    public void update() {
        if (!isOn())
            return;
        for (SHADE_CELL type : SHADE_CELL_VALUES) {
            for (int x = 0; x < grid.getCols(); x++) {
                for (int y = 0; y < grid.getRows(); y++) {
                    if (type == VOID) {
                        if (EidolonsGame.BOSS_FIGHT)
                            continue;
                    }
                    ShadeLightCell cell = getCells(type)[x][y];
                    if (cell != null) {
                        if (type == VOID) {
                            if (cell.getBaseAlpha() != 0) {
                                continue;
                            }
                        }
                        float alpha = DC_Game.game.getVisionMaster().
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
                            float alpha = DC_Game.game.getVisionMaster().
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
        GAMMA_SHADOW(0.75f, StrPathBuilder.build(PathFinder.getShadeCellsPath(), "shadow neu.png")),
        GAMMA_LIGHT(0, StrPathBuilder.build(PathFinder.getShadeCellsPath(), "light.png")),
        LIGHT_EMITTER(0, StrPathBuilder.build(PathFinder.getShadeCellsPath(), "light emitter.png")),
        CONCEALMENT(0.5f, StrPathBuilder.build(PathFinder.getShadeCellsPath(), "concealment.png")),
        BLACKOUT(0, StrPathBuilder.build(PathFinder.getShadeCellsPath(), "blackout.png")),
        HIGLIGHT(0, StrPathBuilder.build(PathFinder.getShadeCellsPath(), "highlight.png")),
        VOID(0, StrPathBuilder.build(PathFinder.getShadeCellsPath(), "void.png")),
        ;

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









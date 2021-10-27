package libgdx.bf.light;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import eidolons.ability.effects.common.LightEmittingEffect;
import eidolons.content.PROPS;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.core.game.DC_Game;
import eidolons.game.exploration.dungeon.struct.LevelStruct;
import eidolons.content.consts.libgdx.GdxColorMaster;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.bf.GridMaster;
import libgdx.bf.grid.GridPanel;
import libgdx.bf.grid.sub.GridElement;
import libgdx.gui.generic.GroupX;
import main.content.CONTENT_CONSTS;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.ALPHA_TEMPLATE;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.launch.CoreEngine;

import java.util.*;

import static eidolons.content.consts.VisualEnums.SHADE_CELL.*;

/**
 * Created by JustMe on 8/16/2017.
 */
public class ShadowMap extends GroupX implements GridElement {

    protected int cols;
    protected int rows;
    private int x1,  y1 ;

    private static final Color DEFAULT_COLOR = new Color(1, 0.9f, 0.7f, 1);
    private static boolean on = true;
    private final GridPanel grid;
    private final Map<VisualEnums.SHADE_CELL, ShadeLightCell[][]> cells = new LinkedHashMap<>();
    private List<LightEmitter>[][] emitters;

    public ShadowMap(GridPanel grid) {
        this.grid = grid;
        bindEvents();
        setSize(grid.getWidth(), grid.getHeight());
        setTransform(false);
        setTouchable(Touchable.disabled);
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

    public static Color getLightColor(Object userObject) {
        if (userObject instanceof DC_Obj) {
            DC_Obj obj = (DC_Obj) userObject;
            CONTENT_CONSTS.COLOR_THEME colorTheme = null;
            if (userObject != null && !obj.getProperty
                    (PROPS.COLOR_THEME).isEmpty()) {
                colorTheme = new EnumMaster<CONTENT_CONSTS.COLOR_THEME>().
                        retrieveEnumConst(CONTENT_CONSTS.COLOR_THEME.class, obj.getProperty
                                (PROPS.COLOR_THEME, true));
            }

            if (colorTheme == null) {
                LevelStruct lowestStruct = obj.getGame().getDungeonMaster().getStructMaster().
                        getLowestStruct(obj.getCoordinates());
                colorTheme =
                        lowestStruct.getColorTheme();

            }

            Color c = null;
            if (colorTheme != null)
                c = GdxColorMaster.getColorForTheme(colorTheme);
            if (c != null) return c;
        }
        return DEFAULT_COLOR;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!on)
            return;
        setTransform(false);
            for (ShadeLightCell[][] value : cells.values()) {
                for (int x = grid.drawX1; x < grid.drawX2; x++) {
                    for (int y = grid.drawY1; y < grid.drawY2; y++) {
                        ShadeLightCell cell = value[x][y];
                        if (cell != null) {
                            cell.draw(batch, 1);
                        }
                    }
                }
            }
    }

    @Override
    public void act(float delta) {
        if (!on)
            return;

        setY(-(y1) * 128); //ToDo-Cleanup

            for (ShadeLightCell[][] value : cells.values()) {
                for (int x = grid.drawX1; x < grid.drawX2; x++) {
                    for (int y = grid.drawY1; y < grid.drawY2; y++) {
                        ShadeLightCell cell = value[x][y];
                        if (cell != null) {
                            cell.act(delta);
                        }
                    }
                }
            }
    }

    @Override
    public void setModule(Module module) {
        if (!CoreEngine.isLevelEditor()){
        x1 = module.getOrigin().x;
        y1 = module.getOrigin().y;
        }
        cols = module.getEffectiveWidth();
        rows = module.getEffectiveHeight();
        //cache cells/emitters for modules?
        try {
            init();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        offset(x1 * 128, grid.getGdxY_ForModule(y1-1) * 128); //TODO is that right?
    }

    private void init() {
        clearChildren();
        for (VisualEnums.SHADE_CELL type : SHADE_CELL_VALUES) {
            getCells().put(type, new ShadeLightCell[grid.getModuleCols()][grid.getModuleRows()]);
            emitters = new List[grid.getModuleCols()][grid.getModuleRows()];

            for (int x = 0; x < grid.getModuleCols() ; x++) {
                for (int y = 0; y < grid.getModuleRows(); y++) {
                    DC_Cell cellObj = grid.getCells()[x1 + x][y1 + y].getUserObject();
                    if (cellObj.isVOID()) {
                        if (type != VOID)
                            continue;
                    } else if (type == VOID)
                        continue;
                    if (type != LIGHT_EMITTER) {
                        if (getCells(type)[x][y] != null) {
                            //TODO
                        } else {
                            ShadeLightCell cell = new ShadeLightCell(type, cellObj);
                            getCells(type)[x][y] = cell;
                            addShadowMapElement(cell, x, y, type.defaultAlpha);
                        }
                    } else {
                        if (!isLightEmittersOn())
                            continue;
                        if (emitters[x][y] == null) {
                            emitters[x][y] = new ArrayList<>();
                        }
                        Coordinates c = Coordinates.get(x, (y));
                        Set<BattleFieldObject> objects = DC_Game.game.getOverlayingObjects(c);
                        objects.addAll(DC_Game.game.getObjectsNoOverlaying(c));
                        if (objects.isEmpty()) {
                            continue;
                        }
                        Iterator<BattleFieldObject> iterator = objects.iterator();
                        while (iterator.hasNext()) {
                            BattleFieldObject obj = iterator.next();
                            if (obj instanceof Unit)
                                continue;
                            LightEmittingEffect effect = null;
                            try {
                                effect = DC_Game.game.getVisionMaster().getIllumination().
                                        getLightEmissionEffect(obj);
                            } catch (Exception e) {
                                main.system.ExceptionMaster.printStackTrace(e);
                            }
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
        //        update();

    }

    private boolean isLightEmittersOn() {
        return true;
    }

    private void addShadowMapElement(Group element, int x, int y, float defaultAlpha) {
        float offsetX = (GridMaster.CELL_W - element.getWidth()) / 2;
        float offsetY = (GridMaster.CELL_H - element.getHeight()) / 2;

        element.setPosition(x * GridMaster.CELL_W + offsetX,
                grid.getGdxY_ForModule(y)
                        * GridMaster.CELL_H + offsetY);

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
        List<LightEmitter> list = emitters[c.x][(c.y)];

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
            LightEmitter emitter = getEmitterForObj(obj);
            //            emitter.setTeamColor(color);
            emitter.reset();

        });
        GuiEventManager.bind(GuiEventType.LIGHT_EMITTER_MOVED, p -> {

            BattleFieldObject obj = (BattleFieldObject) p.get();
            Coordinates c = obj.getBufferedCoordinates();
            List<LightEmitter> list = emitters[c.x][(c.y)];

            LightEmitter emitter = getEmitterForObj(obj);

            list.remove(emitter);

            Coordinates c1 = obj.getCoordinates();
            list = emitters[c1.x][(c1.y)];

            list.add(emitter);

            int x = (c1.x - c.x) * 128;
            int y = (c1.y - c.y) * 128;

            ActionMasterGdx.addMoveByAction(emitter, x, y, 0.5f);

            main.system.auxiliary.log.LogMaster.log(1, "RESET_LIGHT_EMITTER " + p);

        });
        GuiEventManager.bind(GuiEventType.UPDATE_SHADOW_MAP, p -> {
            update();
            //            main.system.auxiliary.log.LogMaster.log(1, "MANUAL SHADOW MAP UPDATE ");
        });

    }


    public void update() {
        if (!isOn())
            return;
        for (VisualEnums.SHADE_CELL type : SHADE_CELL_VALUES) {
            for (int x = 0; x < grid.getModuleCols(); x++) {
                for (int y = 0; y < grid.getModuleRows(); y++) {
                    //                    if (type == VOID) {
                    //                        if (EidolonsGame.BOSS_FIGHT)
                    //                            continue;
                    //                    }
                    ShadeLightCell[][] cells = getCells(type);
                    if (cells.length <= x || cells[0].length <= y) {
                        continue;
                    }
                    ShadeLightCell cell = cells[x][y];
                    if (cell != null) {
                        if (type == VOID) {
                            if (cell.getBaseAlpha() != 0) {
                                continue;
                            }
                        }
                        float alpha = DC_Game.game.getVisionMaster().
                                getGammaMaster().getAlphaForShadowMapCell(x, y, type);
                        if (Math.abs(cell.getBaseAlpha() - alpha) > 0.1f) {
                            cell.setBaseAlpha(alpha);
                        }
                    }

                }
            }

        }
    }

    public Map<VisualEnums.SHADE_CELL, ShadeLightCell[][]> getCells() {
        return cells;
    }

    public ShadeLightCell[][] getCells(VisualEnums.SHADE_CELL type) {
        return cells.get(type);
    }

    public void setZtoMax(VisualEnums.SHADE_CELL sub) {
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

    public static final VisualEnums.SHADE_CELL[] SHADE_CELL_VALUES = {
            VOID,
            GAMMA_SHADOW,
            HIGHLIGHT
    };

    public static ALPHA_TEMPLATE getTemplateForShadeLight(VisualEnums.SHADE_CELL type) {
        switch (type) {
            case VOID:
                return null;
            case GAMMA_SHADOW:
                return GenericEnums.ALPHA_TEMPLATE.SHADE_CELL_GAMMA_SHADOW;
            case GAMMA_LIGHT:
                return GenericEnums.ALPHA_TEMPLATE.SHADE_CELL_GAMMA_LIGHT;
            case LIGHT_EMITTER:
                return GenericEnums.ALPHA_TEMPLATE.SHADE_CELL_LIGHT_EMITTER;
            case CONCEALMENT:
            case BLACKOUT:
                break;
            case HIGHLIGHT:
                return GenericEnums.ALPHA_TEMPLATE.SHADE_CELL_HIGHLIGHT;
        }
        return GenericEnums.ALPHA_TEMPLATE.HIGHLIGHT_MAP;
    }


}









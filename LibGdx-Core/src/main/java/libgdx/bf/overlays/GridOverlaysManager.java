package libgdx.bf.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import eidolons.content.PARAMS;
import eidolons.entity.item.HeroItem;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.GridCell;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.DC_MovementManager;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionRule;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.dungeon.puzzle.maze.voidy.VoidMazeHandler;
import eidolons.game.core.Core;
import eidolons.game.exploration.story.cinematic.Cinematics;
import eidolons.game.exploration.handlers.ExplorationMaster;
import libgdx.GdxMaster;
import libgdx.bf.SuperActor;
import libgdx.bf.grid.DC_GridPanel;
import libgdx.bf.grid.GridPanel;
import libgdx.bf.grid.cell.GenericGridView;
import libgdx.bf.grid.cell.GridCellContainer;
import libgdx.bf.grid.sub.GridElement;
import libgdx.gui.dungeon.tooltips.SmartClickListener;
import libgdx.gui.dungeon.tooltips.Tooltip;
import libgdx.gui.dungeon.tooltips.ValueTooltip;
import libgdx.assets.texture.TextureCache;
import main.content.enums.GenericEnums;
import main.content.enums.rules.VisionEnums.UNIT_VISION;
import main.data.filesys.PathFinder;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.images.ImageManager;
import main.system.launch.Flags;
import main.system.math.MathMaster;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;

import static libgdx.bf.overlays.GridOverlaysManager.OVERLAY.*;

/**
 * Created by JustMe on 2/20/2017.
 */
public class GridOverlaysManager extends SuperActor implements GridElement {

    protected final GridCellContainer[][] cells;
    protected GridPanel gridPanel;
    protected boolean sightInfoDisplayed;
    public static boolean debug;
    protected ObjectMap<OVERLAY, ObjectMap<Actor, ClickListener>> listenerCaches = new ObjectMap<>();
    protected ObjectMap<Entity, ObjectMap<Rectangle, Tooltip>> tooltipMap = new ObjectMap<>();
    protected ObjectMap<Entity, ObjectMap<OVERLAY, Rectangle>> overlayMap = new ObjectMap<>();
    protected BattleFieldObject observer;

    protected int cols;
    protected int rows;

    public GridOverlaysManager(GridPanel gridPanel) {
        this.gridPanel = gridPanel;
        cells = gridPanel.getCells();
        setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE.OVERLAYS);
        for (OVERLAY sub : OVERLAY.values()) {
            if (isTooltipRequired(sub)) {
                listenerCaches.put(sub, new ObjectMap<>());
            }
        }
        gridPanel.addListener(getGlobalOverlayListener(gridPanel));
    }

    public void clearTooltip(Entity e) {
        //        main.system.auxiliary.log.LogMaster.log(1, "Removing mapping for " + e
        //         + ":\n " + tooltipMap.getVar(e) + ";\n " + overlayMap.getVar(e));
        tooltipMap.remove(e);
        overlayMap.remove(e);
    }

    public ClickListener getGlobalOverlayListener(GridPanel panel) {
        return new SmartClickListener(panel) {

            @Override
            protected boolean isBattlefield() {
                return true;
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                checkShowTooltip(x, y);
                //                super.enter(event, x, y, pointer, fromActor);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                ImmutablePair<Entity, OVERLAY> pair = getEntityAndOverlay(x, y);
                if (pair != null)
                    try {
                        OverlayClickHander.handle(pair.getLeft(), pair.getRight());
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    }

            }

            protected void checkShowTooltip(float x, float y) {
                Tooltip tooltip = getTooltip(x, y);
                if (tooltip != null) {
                    GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP,
                            tooltip);
                    showing = true;
                } else {
                    if (showing) {
                        GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP,
                                null);
                        showing = false;
                    }
                }
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                checkShowTooltip(x, y);
                return true;
            }

            protected ImmutablePair<Entity, OVERLAY> getEntityAndOverlay(float x, float y) {
                for (Entity e : tooltipMap.keys()) {
                    ObjectMap<OVERLAY, Rectangle> map = overlayMap.get(e);
                    if (map != null)
                        for (OVERLAY sub : map.keys()) {
                            if (map.get(sub).contains(x, y)) {
                                return new ImmutablePair<>(e, sub);
                            }
                        }
                }
                return null;
            }

            protected Entity getEntity(float x, float y) {
                for (Entity e : tooltipMap.keys()) {
                    ObjectMap<Rectangle, Tooltip> map = tooltipMap.get(e);
                    for (Rectangle sub : map.keys()) {
                        if (sub.contains(x, y)) {
                            return e;
                        }
                    }
                }
                return null;
            }

            protected Tooltip getTooltip(float x, float y) {

                Tooltip tooltip = null;
                for (ObjectMap<Rectangle, Tooltip> map : tooltipMap.values()) {
                    for (Rectangle sub : map.keys()) {
                        if (sub.contains(x, y)) {
                            tooltip = map.get(sub);
                            break;
                        }
                    }
                }
                return tooltip;
            }

            @Override
            protected void exited() {
                super.exited();
            }
        };
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (Cinematics.ON) {
            return;
        }
        if (Flags.isIDE())
            debug = (Gdx.input.isKeyPressed(Keys.ALT_LEFT));
        setSightInfoDisplayed(Gdx.input.isKeyPressed(Keys.CONTROL_LEFT));
        if (sightInfoDisplayed) {
            if (gridPanel.getHoverObj() != null) {
                observer = gridPanel.getHoverObj().getUserObject();
            }
            if (!(observer instanceof Unit)) {
                if (gridPanel instanceof DC_GridPanel) {
                    observer = Core.getMainHero();
                }
            } else {
                if (!VisionRule.isSightInfoAvailable(observer))
                    sightInfoDisplayed = false;
            }
        }
        super.draw(batch, parentAlpha);
        batch.setColor(1, 1, 1, 1);
        drawOverlays(batch);

        //        drawHoverObj(batch);

    }

    protected void drawHoverObj(Batch batch) {
        if (gridPanel.getHoverObj() != null) {
            Vector2 pointer = screenToLocalCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            Vector2 pos = new Vector2(gridPanel.getHoverObj().getX() + 64,
                    gridPanel.getHoverObj().getY() + 64);

            Vector2 stagePos = gridPanel.getHoverObj().localToStageCoordinates(new Vector2(pos));

            if (pointer.dst(stagePos) <= 64) {
                gridPanel.getHoverObj().setPosition(stagePos.x - pos.x, stagePos.y - pos.y);
                gridPanel.getHoverObj().draw(batch, 1);
                gridPanel.getHoverObj().setPosition(pos.x, pos.y);
            }
        }
    }

    public void setSightInfoDisplayed(boolean sightInfoDisplayed) {
        if (sightInfoDisplayed != this.sightInfoDisplayed) {
            this.sightInfoDisplayed = sightInfoDisplayed;
            gridPanel.setUpdateRequired(true);
        }
    }

    @Override
    public void setModule(Module module) {
        cols = module.getEffectiveWidth();
        rows = module.getEffectiveHeight();
    }

    protected void drawOverlays(Batch batch) {
        // if (GridManager.isCustomDraw()){
        // }
        for (int x = gridPanel.drawX1; x < gridPanel.drawX2; x++) {
            for (int y = gridPanel.drawY2 - 1; y >= gridPanel.drawY1; y--) {
                GridCellContainer cell = cells[x][y];
                if (cell.visibleViews != null)
                    for (Actor c : cell.visibleViews) {
                        drawOverlaysForView(((GenericGridView) c), batch, x, y);
                    }
                drawOverlaysForCell(cell, x, y, batch);
            }
        }
    }

    protected void drawOverlaysForView(GenericGridView actor, Batch batch, int x, int y) {
        //        TODO  if (actor.isHovered()) {
        //emblem etc?
        //        }
        BattleFieldObject obj = actor.getUserObject();
        if (debug) {
            if (getOverlayActor(actor, INFO_TEXT) instanceof Label) {
                ((Label) getOverlayActor(actor, INFO_TEXT)).setText(getInfoText(obj));
            }
            drawOverlay(actor, INFO_TEXT, batch, obj, x, y);
        }
        if (actor.getHpBar() != null)
            if (checkOverlayForObj(HP_BAR, obj, actor))
                drawOverlay(actor, HP_BAR, batch, obj, x, y);

        if (checkOverlayForObj(SPOTTED, obj, actor)) {
            drawOverlay(actor, SPOTTED, batch, obj, x, y);
        } else if (checkOverlayForObj(STEALTH, obj, actor)) {
            drawOverlay(actor, STEALTH, batch, obj, x, y);
        }
        //        if (checkOverlayForObj(BAG, obj)) {
        //            drawOverlay(actor, BAG, batch);
        //        }
    }

    protected void drawOverlaysForCell(GridCellContainer container, int x, int y,
                                       Batch batch) {

        boolean path = false;
        Coordinates c = Coordinates.get(x, y);
        if (ExplorationMaster.isExplorationOn())
        if (c.equals(DC_MovementManager.playerDestination) ||
                (DC_MovementManager.playerPath != null && DC_MovementManager.playerPath.contains(c))) {
            path = true;
        }
        if (debug || sightInfoDisplayed || path) {
            GridCell cell = Core.getGame().getObjMaster().
                    getCellByCoordinate(c);
            if (debug || (sightInfoDisplayed&&Flags.isIDE())) {
                drawOverlay(container, INFO_TEXT, batch, cell, x, y);
                // return;
            } else if (path) {
                if (c.equals(DC_MovementManager.playerDestination)) {
                    drawOverlay(container, DESTINATION, batch, cell, x, y);
                } else
                    drawOverlay(container, PATH, batch, cell, x, y);
                return;
            }
            UNIT_VISION vision = cell.getUnitVisionStatus(observer);
            if (vision == null) {
                return;
            }
            switch (vision) {
                case IN_PLAIN_SIGHT:
                    drawOverlay(container, IN_PLAIN_SIGHT, batch, cell, x, y);
                    break;
                case IN_SIGHT:
                    drawOverlay(container, IN_SIGHT, batch, cell, x, y);
                    break;
                case BLOCKED:
                    drawOverlay(container, BLOCKED, batch, cell, x, y);
                    break;
                case CONCEALED:
                    drawOverlay(container, FOG_OF_WAR, batch, cell, x, y);
                    break;
            }
        }
        Object data = getOverlayDataForCell(BAG, Coordinates.get(x, y));
        if (data != null) {
            drawOverlay(container, BAG, batch, null, x, y);
        }
    }

    public void drawOverlay(Actor parent, OVERLAY overlay, Batch batch, DC_Obj obj, int x, int y) {
        //TODO SCALING
        initOverlayColor(batch, obj, overlay);

        float xPos = 0, yPos = 0;
        if (overlay == INFO_TEXT) {
            // Label label = (Label) getOverlayActor(parent, overlay);
            // label.setText(getInfoText(obj)+"\n" +
            //        parent.getX()+ ":"+parent.getY());

        }
        if (overlay.alignment != null) {
            Vector2 v = GdxMaster.getAlignedPos(parent, overlay.alignment,
                    getOverlayWidth(overlay, parent), getOverlayHeight(overlay, parent));
            xPos = v.x;
            yPos = v.y;
        } else
            switch (overlay) {
                case DESTINATION:
                case PATH:
                    xPos=32;
                    yPos=32;
                    break;
                case HP_BAR: {
                    if (obj instanceof Unit)
                        yPos = -12;
                    break;
                }
            }
        Vector2 v = parent.localToStageCoordinates(new Vector2(xPos, yPos));
        drawOverlay(parent, overlay, batch, v);
        if (parent.getActions().size == 0) {
            addTooltip(obj, parent, overlay, v, x, y);
        }
        //        if (ActorMaster.getActionsOfClass(parent, MoveByActionLimited.class).size() == 0)
        //            if (ActorMaster.getActionsOfClass(parent, MoveToAction.class).size() == 0)
        //                addTooltip(obj, parent, overlay, v, x, y);

    }

    protected String getInfoText(DC_Obj obj) {
        StringBuilder builder = new StringBuilder();
        if (!VoidMazeHandler.TEST_MODE) {
            if (obj instanceof GridCell) {
                builder.append(ListMaster.toStringList(((GridCell) obj).getMarks())).append("\n");
            }
        } else {
            builder.append("Gamma: ").append(obj.getGamma()).append("\n");
            builder.append(obj.getVisibilityLevel()).append("\n");
            builder.append(obj.getPlayerVisionStatus()).append("\n");
            builder.append(obj.getVisibilityLevel()).append("\n");
        }
        return builder.toString();
    }

    protected void initOverlayColor(Batch batch, DC_Obj obj, OVERLAY overlay) {
        if (isOverlayAlphaOn(overlay)) {
            batch.setColor(1, 1, 1, fluctuatingAlpha);
        } else {
            batch.setColor(1, 1, 1, 1);
        }
    }

    protected void addTooltip(DC_Obj obj, Actor parent, OVERLAY overlay, Vector2 v, int x, int y) {
        Rectangle rect;
        if (isTooltipRequired(overlay)) {
            if (obj == null) {
                obj = Core.getGame().getObjMaster().getCellByCoordinate(Coordinates.get(x, y));
            }
            ObjectMap<Rectangle, Tooltip> map = tooltipMap.get(obj);
            if (map == null) {
                map = new ObjectMap<>();
                tooltipMap.put(obj, map);
            }
            rect = getOverlayMap(obj).get(overlay);
            if (rect == null) {
                rect = new Rectangle(v.x, v.y, getOverlayWidth(overlay, parent)
                        , getOverlayHeight(overlay, parent));
            }
            Tooltip tooltip = map.get(rect);
            if (tooltip == null) {
                tooltip = getTooltip(overlay, parent, x, y);
                if (tooltip != null) {
                    map.put(rect, tooltip);
                }
            }
        }

        if (isClickListenerRequired(overlay)) {
            if (obj == null) {
                obj = Core.getGame().getObjMaster().getCellByCoordinate(Coordinates.get(x, y));
            }
            ObjectMap<OVERLAY, Rectangle> map2 = getOverlayMap(obj);
            rect = map2.get(overlay);
            if (rect == null) {
                rect = new Rectangle(v.x, v.y, getOverlayWidth(overlay, parent)
                        , getOverlayHeight(overlay, parent));
            }
            map2.put(overlay, rect);
        }


    }

    protected ObjectMap<OVERLAY, Rectangle> getOverlayMap(Obj obj) {
        ObjectMap<OVERLAY, Rectangle> map = overlayMap.get(obj);
        if (map == null) {
            map = new ObjectMap<>();
            overlayMap.put(obj, map);
        }
        return map;
    }

    protected boolean isClickListenerRequired(OVERLAY overlay) {
        switch (overlay) {
            case BAG:
                return true;
        }
        return false;
    }

    protected void drawOverlay(Actor parent, OVERLAY overlay, Batch batch, Vector2 v) {
        TextureRegion region = getRegion(overlay);
        float y = v.y;
        if (region != null) {
            batch.draw(region, v.x, y);
        } else {
            Actor actor = getOverlayActor(parent, overlay);
            if (actor != null)
                if (actor.isVisible()) {
                    actor.setPosition(v.x, y);
                    actor.setScale(parent.getScaleX(), parent.getScaleY());
                    actor.draw(batch, 1);
                }
        }


        //        ClickListener listener = listenerCaches.getVar(overlay).getVar(parent);
        //        if (listener == null) {
        //            listener = getOverlayListener(overlay, parent, v.x, v.y);
        //            listenerCaches.getVar(overlay).put(parent, listener);
        //        }
        //        if (!parent.getListeners().contains(listener, true)) {
        //            parent.addListener(listener);
        //        }
    }

    protected Tooltip getTooltip(OVERLAY overlay, Actor parent, int x, int y) {
        switch (overlay) {


            case SPOTTED:
                return new ValueTooltip("Stealth: Spotted");
            case STEALTH:
                return new ValueTooltip("Stealth: Hidden");
            case BAG:
                return new ValueTooltip("Dropped items");
            case HP_BAR:
                if (!isHpTooltipOn())
                    break;
                BattleFieldObject data = (BattleFieldObject) parent.getUserObject();
                String text = data.getName() + " has " +
                        data.getIntParam(PARAMS.TOUGHNESS_PERCENTAGE) / MathMaster.MULTIPLIER + "% Toughness and " +
                        data.getIntParam(PARAMS.ENDURANCE_PERCENTAGE) / MathMaster.MULTIPLIER +
                        "% Endurance left";
                return new ValueTooltip(text);


            case GRAVE:
            case TRAP:
            case ITEM:
            case CORPSE:
                break;
            case IN_PLAIN_SIGHT:
            case IN_SIGHT:
            case FOG_OF_WAR:
            case WATCH:
            case BLOCKED:
                return null;
        }
        return new ValueTooltip(StringMaster.
                format(overlay.name()));
    }

    protected boolean isHpTooltipOn() {
        return false;
    }

    protected boolean isTooltipRequired(OVERLAY overlay) {
        switch (overlay) {

            case SPOTTED:
            case BAG:
            case STEALTH:
                break;
            case HP_BAR:
                return isHpTooltipOn();
            case GRAVE:
            case CORPSE:
            case ITEM:
            case IN_PLAIN_SIGHT:
            case IN_SIGHT:
            case BLOCKED:
            case INFO_TEXT:
            case PATH:
            case DESTINATION:
                return false;
        }
        return true;
    }

    protected Actor getOverlayActor(Actor parent, OVERLAY overlay) {

        switch (overlay) {
            case INFO_TEXT: {
                if (parent instanceof libgdx.bf.grid.cell.GridCell) {
                    return ((libgdx.bf.grid.cell.GridCell) parent).getInfoTextLabel();
                }
                GenericGridView view = (GenericGridView) parent;
                return view.getInfoText();
            }
            case HP_BAR: {
                GenericGridView view = (GenericGridView) parent;
                return view.getHpBar();
            }
        }
        return null;
    }

    protected boolean isColorFlagOn(OVERLAY overlay) {
        switch (overlay) {
            case IN_PLAIN_SIGHT:
            case IN_SIGHT:
                return true;
        }
        return false;
    }

    protected boolean isOverlayAlphaOn(OVERLAY overlay) {
        switch (overlay) {
            case IN_PLAIN_SIGHT:
            case IN_SIGHT:
                return true;
        }
        return false;
    }

    protected TextureRegion getRegion(OVERLAY overlay) {
        switch (overlay) {
            case STEALTH:
                return TextureCache.getRegionUI_DC(ImageManager.getValueIconPath(PARAMS.STEALTH));
            case SPOTTED:
                return TextureCache.getRegionUI_DC(ImageManager.getValueIconPath(PARAMS.DETECTION));

            case HP_BAR:
            case ITEM:
            case CORPSE:
            case INFO_TEXT:
                return null;
        }
        return TextureCache.getOrCreateR(
                overlay.path);
    }

    public Object getOverlayDataForCell(OVERLAY overlay, Coordinates coordinates) {
        switch (overlay) {
            case BAG:
                //TODO kill this shit
                List<HeroItem> items = Core.game.getDroppedItemManager().
                        getDroppedItems(coordinates);
                if (items == null) {
                    Core.game.getDroppedItemManager().reset(coordinates.x, coordinates.y);
                    main.system.auxiliary.log.LogMaster.verbose( "dropped item forced reset " + coordinates);
                    items = Core.game.getDroppedItemManager().
                            getDroppedItems(coordinates);
                }
                if (!items.isEmpty())
                    return items;
        }
        return null;
    }

    public boolean checkOverlayForObj(OVERLAY overlay, BattleFieldObject object, GenericGridView actor) {
        if (actor.isPortraitMode()) {
            return false;
        }
        switch (overlay) {
            case STEALTH:
                if (object instanceof Unit)
                    return object.isSneaking();
                else
                    return false;
            case SPOTTED:
                return object.isSpotted();
            case BAG:
                return !object.getGame().getDroppedItemManager().
                        getDroppedItems(object.getCoordinates()).isEmpty();
            case HP_BAR:
                if (!actor.isHovered()) {
                    GridCellContainer container = (GridCellContainer) actor.getParent();
                    if (container.isHovered()) {
                        return false;
                    }
                }
                return actor.isHpBarVisible();
        }


        return false;
    }

    protected int getOverlayWidth(OVERLAY overlay, Actor parent) {
        TextureRegion region = getRegion(overlay);
        if (region != null)
            return region.getRegionWidth();
        if (getOverlayActor(parent, overlay) != null)
            return (int) getOverlayActor(parent, overlay).getWidth();
        return 0;

    }

    protected int getOverlayHeight(OVERLAY overlay, Actor parent) {
        TextureRegion region = getRegion(overlay);
        if (region != null)
            return region.getRegionHeight();
        return (int) getOverlayActor(parent, overlay).getHeight();

    }


    public enum OVERLAY {
        SPOTTED(Alignment.TOP_LEFT),
        STEALTH(Alignment.TOP_LEFT),
        BAG(Alignment.BOTTOM_RIGHT),
        HP_BAR,
        GRAVE,
        CORPSE,
        ITEM,
        TRAP,
        IN_PLAIN_SIGHT,
        IN_SIGHT,
        FOG_OF_WAR,
        WATCH,
        BLOCKED,
        INFO_TEXT,
        PATH,
        DESTINATION,
        ;

        public Alignment alignment;
        String path = StrPathBuilder.build(PathFinder.getComponentsPath(),
                "dc", "overlays", toString().toLowerCase() + ".png");

        OVERLAY() {

        }

        OVERLAY(Alignment alignment) {
            this(null, alignment);
        }

        OVERLAY(String path, Alignment alignment) {
            if (path != null)
                this.path = path;
            this.alignment = alignment;
        }
    }
}

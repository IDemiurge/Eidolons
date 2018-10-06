package eidolons.libgdx.bf.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import eidolons.content.PARAMS;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionRule;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.anims.actions.MoveByActionLimited;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.grid.GenericGridView;
import eidolons.libgdx.bf.grid.GridCellContainer;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.grid.GridUnitView;
import eidolons.libgdx.gui.tooltips.SmartClickListener;
import eidolons.libgdx.gui.tooltips.Tooltip;
import eidolons.libgdx.gui.tooltips.ValueTooltip;
import eidolons.libgdx.texture.TextureCache;
import main.content.enums.rules.VisionEnums.UNIT_VISION;
import main.data.filesys.PathFinder;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;
import main.system.math.MathMaster;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static eidolons.libgdx.bf.overlays.OverlaysManager.OVERLAY.*;

/**
 * Created by JustMe on 2/20/2017.
 */
public class OverlaysManager extends SuperActor {

    private final GridCellContainer[][] cells;
    GridPanel gridPanel;
    boolean sightInfoDisplayed;
    Map<OVERLAY, Map<Actor, ClickListener>> listenerCaches = new HashMap<>();
    Map<Entity, Map<Rectangle, Tooltip>> tooltipMap = new HashMap<>();
    Map<Entity, Map<OVERLAY, Rectangle>> overlayMap = new HashMap<>();
    private BattleFieldObject observer;

    public OverlaysManager(GridPanel gridPanel) {
        this.gridPanel = gridPanel;
        cells = gridPanel.getCells();
        setAlphaTemplate(ALPHA_TEMPLATE.OVERLAYS);
        for (OVERLAY sub : OVERLAY.values()) {
            if (isTooltipRequired(sub)) {
                listenerCaches.put(sub, new HashMap<>());
            }
        }
        gridPanel.addListener(getGlobalOverlayListener(gridPanel));
    }

    public void clearTooltip(Entity e) {
        //        main.system.auxiliary.log.LogMaster.log(1, "Removing mapping for " + e
        //         + ":\n " + tooltipMap.get(e) + ";\n " + overlayMap.get(e));
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

            private void checkShowTooltip(float x, float y) {
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

            private ImmutablePair<Entity, OVERLAY> getEntityAndOverlay(float x, float y) {
                for (Entity e : tooltipMap.keySet()) {
                    Map<OVERLAY, Rectangle> map = overlayMap.get(e);
                    if (map != null)
                        for (OVERLAY sub : map.keySet()) {
                            if (map.get(sub).contains(x, y)) {
                                return new ImmutablePair<>(e, sub);
                            }
                        }
                }
                return null;
            }

            private Entity getEntity(float x, float y) {
                for (Entity e : tooltipMap.keySet()) {
                    Map<Rectangle, Tooltip> map = tooltipMap.get(e);
                    for (Rectangle sub : map.keySet()) {
                        if (sub.contains(x, y)) {
                            return e;
                        }
                    }
                }
                return null;
            }

            private Tooltip getTooltip(float x, float y) {

                Tooltip tooltip = null;
                for (Map<Rectangle, Tooltip> map : tooltipMap.values()) {
                    for (Rectangle sub : map.keySet()) {
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

        setSightInfoDisplayed(Gdx.input.isKeyPressed(Keys.CONTROL_LEFT));
        if (sightInfoDisplayed) {
            observer = gridPanel.getObjectForView(gridPanel.getHoverObj());
            if (!(observer instanceof Unit)) {
                observer = gridPanel.getObjectForView(gridPanel.getMainHeroView());
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

    private void drawHoverObj(Batch batch) {
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
            ((GridPanel) getParent()).setUpdateRequired(true);
        }
    }

    private void drawOverlays(Batch batch) {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                GridCellContainer cell = cells[i][j];
                if (cell == null) {
                    continue;
                }
                int x = i;
                int y = cells[i].length - j - 1;

                for (Actor c : cell.getChildren()) {
                    if (c instanceof GridUnitView) {
                        if (c.isVisible())
                            drawOverlaysForView(((GenericGridView) c), batch, x, y);
                        else {
                            //TODO grave/corpse???
                            //                            clearTooltip((Entity) c.getUserObject());

                        }
                    }

                }

                drawOverlaysForCell(cells[i][j], x, y, batch);
            }
        }
    }

    private void drawOverlaysForView(GenericGridView actor, Batch batch, int x, int y) {
        //        TODO  if (actor.isHovered()) {
        //emblem etc?
        //        }
        BattleFieldObject obj = actor.getUserObject();

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

    private void drawOverlaysForCell(GridCellContainer container, int x, int y,
                                     Batch batch) {
        if (sightInfoDisplayed) {
            DC_Cell cell = Eidolons.getGame().getMaster().getCellByCoordinate(Coordinates.get(x, y));

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

    public void drawOverlay(Actor parent, OVERLAY overlay, Batch batch, Obj obj, int x, int y) {
        //TODO SCALING
        if (isOverlayAlphaOn(overlay)) {
            batch.setColor(1, 1, 1, fluctuatingAlpha);
        } else {
            batch.setColor(1, 1, 1, 1);
        }
        float xPos = 0, yPos = 0;
        if (overlay.alignment != null) {
            Vector2 v = GdxMaster.getAlignedPos(parent, overlay.alignment,
             getOverlayWidth(overlay, parent), getOverlayHeight(overlay, parent));
            xPos = v.x;
            yPos = v.y;
        } else
            switch (overlay) {
                case HP_BAR: {
                    if (obj instanceof Unit)
                        yPos = -12;
                    break;
                }
            }
        Vector2 v = parent.localToStageCoordinates(new Vector2(xPos, yPos));
        drawOverlay(parent, overlay, batch, v);
        if (ActorMaster.getActionsOfClass(parent, MoveByActionLimited.class).size() == 0)
            if (ActorMaster.getActionsOfClass(parent, MoveToAction.class).size() == 0)
                addTooltip(obj, parent, overlay, v, x, y);

    }

    private void addTooltip(Obj obj, Actor parent, OVERLAY overlay, Vector2 v, int x, int y) {
        Rectangle rect = null;
        if (isTooltipRequired(overlay)) {
            if (obj == null) {
                obj = Eidolons.getGame().getMaster().getCellByCoordinate(Coordinates.get(x, y));
            }
            Map<Rectangle, Tooltip> map = tooltipMap.get(obj);
            if (map == null) {
                map = new HashMap<>();
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
                obj = Eidolons.getGame().getMaster().getCellByCoordinate(Coordinates.get(x, y));
            }
            Map<OVERLAY, Rectangle> map2 = getOverlayMap(obj);
            rect = map2.get(overlay);
            if (rect == null) {
                rect = new Rectangle(v.x, v.y, getOverlayWidth(overlay, parent)
                 , getOverlayHeight(overlay, parent));
            }
            map2.put(overlay, rect);
        }


    }

    private Map<OVERLAY, Rectangle> getOverlayMap(Obj obj) {
        Map<OVERLAY, Rectangle> map = overlayMap.get(obj);
        if (map == null) {
            map = new HashMap<>();
            overlayMap.put(obj, map);
        }
        return map;
    }

    private boolean isClickListenerRequired(OVERLAY overlay) {
        switch (overlay) {
            case BAG:
                return true;
        }
        return false;
    }

    private void drawOverlay(Actor parent, OVERLAY overlay, Batch batch, Vector2 v) {
        TextureRegion region = getRegion(overlay);
        if (region != null) {
            batch.draw(region, v.x, v.y);
        } else {
            Actor actor = getOverlayActor(parent, overlay);
            if (actor != null)
                if (actor.isVisible()) {
                    actor.setPosition(v.x, v.y);
                    actor.setScale(parent.getScaleX(), parent.getScaleY());
                    actor.draw(batch, 1);
                }
        }


        //        ClickListener listener = listenerCaches.get(overlay).get(parent);
        //        if (listener == null) {
        //            listener = getOverlayListener(overlay, parent, v.x, v.y);
        //            listenerCaches.get(overlay).put(parent, listener);
        //        }
        //        if (!parent.getListeners().contains(listener, true)) {
        //            parent.addListener(listener);
        //        }
    }

    private Tooltip getTooltip(OVERLAY overlay, Actor parent, int x, int y) {
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
                break;
            case CORPSE:
                break;
            case ITEM:
                break;
            case TRAP:
                break;
            case IN_PLAIN_SIGHT:
            case IN_SIGHT:
            case FOG_OF_WAR:
            case WATCH:
            case BLOCKED:
                return null;
        }
        return new ValueTooltip(StringMaster.
         getWellFormattedString(overlay.name()));
    }

    private boolean isHpTooltipOn() {
        return false;
    }

    private boolean isTooltipRequired(OVERLAY overlay) {
        switch (overlay) {
            case HP_BAR:
                return isHpTooltipOn();
        }
        return true;
    }

    private Actor getOverlayActor(Actor parent, OVERLAY overlay) {
        switch (overlay) {
            case HP_BAR: {
                GenericGridView view = (GenericGridView) parent;
                return view.getHpBar();
            }
        }
        return null;
    }

    private boolean isColorFlagOn(OVERLAY overlay) {
        switch (overlay) {
            case IN_PLAIN_SIGHT:
            case IN_SIGHT:
                return true;
        }
        return false;
    }

    private boolean isOverlayAlphaOn(OVERLAY overlay) {
        switch (overlay) {
            case IN_PLAIN_SIGHT:
            case IN_SIGHT:
                return true;
        }
        return false;
    }

    private TextureRegion getRegion(OVERLAY overlay) {
        switch (overlay) {
            case STEALTH:
                return TextureCache.getOrCreateR(ImageManager.getValueIconPath(PARAMS.STEALTH));
            case SPOTTED:
                return TextureCache.getOrCreateR(ImageManager.getValueIconPath(PARAMS.DETECTION));

            case HP_BAR:
            case ITEM:
            case CORPSE:

                return null;
        }
        return TextureCache.getOrCreateR(
         overlay.path);
    }

    public Object getOverlayDataForCell(OVERLAY overlay, Coordinates coordinates) {
        switch (overlay) {
            case BAG:
                //TODO kill this shit
                List<DC_HeroItemObj> items = Eidolons.game.getDroppedItemManager().
                 getDroppedItems(coordinates);
                if (items == null) {
                    Eidolons.game.getDroppedItemManager().reset(coordinates.x, coordinates.y);
                    main.system.auxiliary.log.LogMaster.log(1, "dropped item forced reset " + coordinates);
                    items = Eidolons.game.getDroppedItemManager().
                     getDroppedItems(coordinates);
                }
                if (!items.isEmpty())
                    return items;
        }
        return null;
    }

    public boolean checkOverlayForObj(OVERLAY overlay, BattleFieldObject object, GenericGridView actor) {
        switch (overlay) {
            case STEALTH:
                return object.isSneaking();
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

    private int getOverlayWidth(OVERLAY overlay, Actor parent) {
        TextureRegion region = getRegion(overlay);
        if (region != null)
            return region.getRegionWidth();
        if (getOverlayActor(parent, overlay) != null)
            return (int) getOverlayActor(parent, overlay).getWidth();
        return 0;

    }

    private int getOverlayHeight(OVERLAY overlay, Actor parent) {
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
        BLOCKED,;

        public Alignment alignment;
        String path = StrPathBuilder.build(PathFinder.getComponentsPath(),
         "dc", "overlays", toString() + ".png");

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

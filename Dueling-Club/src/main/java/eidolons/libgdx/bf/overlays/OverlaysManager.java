package eidolons.libgdx.bf.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionRule;
import eidolons.libgdx.anims.text.FloatingTextMaster;
import eidolons.libgdx.anims.text.FloatingTextMaster.TEXT_CASES;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.grid.GenericGridView;
import eidolons.libgdx.bf.grid.GridCellContainer;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.grid.LastSeenView;
import eidolons.libgdx.gui.tooltips.SmartClickListener;
import eidolons.libgdx.gui.tooltips.ValueTooltip;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.texture.TextureCache;
import main.content.enums.rules.VisionEnums.UNIT_VISION;
import main.data.filesys.PathFinder;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;
import main.system.images.ImageManager;

import java.util.HashMap;
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
    private BattleFieldObject observer;

    public OverlaysManager(GridPanel gridPanel) {
        this.gridPanel = gridPanel;
        cells = gridPanel.getCells();
        setAlphaTemplate(ALPHA_TEMPLATE.OVERLAYS);
        for (OVERLAY sub : OVERLAY.values()) {
            if (isListenerRequired(sub)) {
                listenerCaches.put(sub, new HashMap<>());
            }
        }
    }

    public ClickListener getOverlayListener(OVERLAY overlay, Actor parent,
                                            float xPos, float yPos) {
//cache for easy remove?
        Vector2 pos = parent.stageToLocalCoordinates(new Vector2(xPos, yPos));
        int width = getOverlayWidth(overlay, parent);
        int height = getOverlayHeight(overlay, parent);
        return new SmartClickListener(parent) {
            private boolean checkPos(float x, float y) {
                if (Math.abs(pos.x - x) > width)
                    return false;
                if (Math.abs(pos.y - y) > height)
                    return false;
                return true;
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (!checkPos(x, y))
                    return;
                super.enter(event, x, y, pointer, fromActor);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (!checkPos(x, y))
                    return;
                super.exit(event, x, y, pointer, toActor);
            }

            @Override
            protected void exited() {
                overlayExited(overlay, parent);
            }

            @Override
            protected void entered() {
                overlayEntered(overlay, parent);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!checkPos(x, y))
                    return false;
                return overlayTouchDown(overlay, parent, pointer, button);
            }
        };
    }

    private void overlayEntered(OVERLAY overlay, Actor parent) {
        switch (overlay) {
            case HP_BAR:
                GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, new ValueTooltip("!"));
                break;
        }
    }

    private void overlayExited(OVERLAY overlay, Actor parent) {
        GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, null);
    }

    private boolean overlayTouchDown(OVERLAY overlay, Actor parent, int pointer, int button) {
        switch (overlay) {
            case HP_BAR:
                FloatingTextMaster.getInstance().createFloatingText(TEXT_CASES.REQUIREMENT,
                 "!!!!", null);

        }

        return true;
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
        try {
            drawOverlays(batch);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
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
                drawOverlaysForCell(cells[i][j], i, cells[i].length - j - 1, batch);

                for (GenericGridView sub : cells[i][j].getUnitViewsVisible()) {
                    drawOverlaysForView(sub, batch);
                }

            }
        }
    }

    private void drawOverlaysForView(GenericGridView actor, Batch batch) {
        if (actor.isHovered()) {
            //emblem etc?
        }
        if (GridPanel.isHpBarsOnTop()) {
            drawOverlay(actor, HP_BAR, batch);
        }
        BattleFieldObject obj = null;
        if (actor instanceof LastSeenView) {
            obj = DungeonScreen.getInstance().getGridPanel().
             getObjectForView(((LastSeenView) actor).getParentView());

        } else {
            obj = DungeonScreen.getInstance().getGridPanel().getObjectForView(actor);
        }
        if (obj == null) {
            return;
        }
        if (checkOverlayForObj(SPOTTED, obj)) {
            drawOverlay(actor, SPOTTED, batch);
        } else if (checkOverlayForObj(STEALTH, obj)) {
            drawOverlay(actor, STEALTH, batch);
        }
    }

    private void drawOverlaysForCell(GridCellContainer container, int x, int y, Batch batch) {
        if (sightInfoDisplayed) {
            DC_Cell cell = observer.getGame().getMaster().getCellByCoordinate(new Coordinates(x, y));

            UNIT_VISION vision = cell.getUnitVisionStatus(observer);
            if (vision == null) {
                return;
            }
            switch (vision) {
                case IN_PLAIN_SIGHT:
                    drawOverlay(container, IN_PLAIN_SIGHT, batch);
                    break;
                case IN_SIGHT:
                    drawOverlay(container, IN_SIGHT, batch);
                    break;
                case BLOCKED:
                    drawOverlay(container, BLOCKED, batch);
                    break;
                case CONCEALED:
                    drawOverlay(container, FOG_OF_WAR, batch);
                    break;
            }
        }

    }

    public void drawOverlay(Actor parent, OVERLAY overlay, Batch batch) {
        if (isOverlayAlphaOn(overlay)) {
            batch.setColor(1, 1, 1, fluctuatingAlpha);
        } else {
            batch.setColor(1, 1, 1, 1);
        }
        float x = 0, y = 0;
        if (overlay.alignment != null) {
            float w, h;
            if (getRegion(overlay) != null) {

            }
//            GdxMaster.getAlignedPos(parent, overlay.alignment, w, h);

        }
        switch (overlay) {
            case HP_BAR: {
                y = -12;
                break;
            }
            case STEALTH:
            case SPOTTED:
                y = parent.getHeight() - getOverlayHeight(overlay, parent);
                break;
            //init offsets
        }
        Vector2 v = parent.localToStageCoordinates(new Vector2(x, y));
        drawOverlay(parent, overlay, batch, v);

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
        if (!isListenerRequired(overlay))
            return;
        ClickListener listener = listenerCaches.get(overlay).get(parent);
        if (listener == null) {
            listener = getOverlayListener(overlay, parent, v.x, v.y);
            listenerCaches.get(overlay).put(parent, listener);
        }
        if (!parent.getListeners().contains(listener, true)) {
            parent.addListener(listener);
        }
    }

    private boolean isListenerRequired(OVERLAY overlay) {
        switch (overlay) {
            case HP_BAR:
                return true;
        }
        return false;
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

    public boolean checkOverlayForCell(OVERLAY overlay, Coordinates coordinates) {
        switch (overlay) {
            case GRAVE:

        }
        return false;
    }

    public boolean checkOverlayForObj(OVERLAY overlay, BattleFieldObject object) {
        switch (overlay) {
            case STEALTH:
                return object.isSneaking();
            case SPOTTED:
                return object.isSpotted();
            case GRAVE:

        }


        return false;
    }

    private int getOverlayWidth(OVERLAY overlay, Actor parent) {
        TextureRegion region = getRegion(overlay);
        if (region != null)
            return region.getRegionWidth();
        return (int) getOverlayActor(parent, overlay).getWidth();

    }

    private int getOverlayHeight(OVERLAY overlay, Actor parent) {
        TextureRegion region = getRegion(overlay);
        if (region != null)
            return region.getRegionHeight();
        return (int) getOverlayActor(parent, overlay).getHeight();

    }

    public enum OVERLAY {
        SPOTTED(Align.bottomLeft),
        HP_BAR,
        WATCH,
        BLOCKED,
        GRAVE,
        CORPSE,
        ITEM,
        BAG,
        TRAP,
        IN_PLAIN_SIGHT,
        IN_SIGHT,
        FOG_OF_WAR,
        STEALTH;

        public Integer alignment;
        String path = StrPathBuilder.build(PathFinder.getComponentsPath(),
         "2018", "overlays", toString() + ".png");

        OVERLAY() {

        }

        OVERLAY(int alignment) {
            this(null, alignment);
        }

        OVERLAY(String path, int alignment) {
            if (path != null)
                this.path = path;
            this.alignment = alignment;
        }
    }
}

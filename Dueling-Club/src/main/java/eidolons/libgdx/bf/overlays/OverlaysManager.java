package eidolons.libgdx.bf.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionRule;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.grid.GenericGridView;
import eidolons.libgdx.bf.grid.GridCellContainer;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.grid.LastSeenView;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.texture.TextureCache;
import main.content.enums.rules.VisionEnums.UNIT_VISION;
import main.data.filesys.PathFinder;
import main.game.bf.Coordinates;
import main.system.auxiliary.StrPathBuilder;

import static eidolons.libgdx.bf.overlays.OverlaysManager.OVERLAY.*;

/**
 * Created by JustMe on 2/20/2017.
 */
public class OverlaysManager extends SuperActor {

    private final GridCellContainer[][] cells;
    GridPanel gridPanel;
    boolean sightInfoDisplayed;
    private BattleFieldObject observer;

    public OverlaysManager(GridPanel gridPanel) {
        this.gridPanel = gridPanel;
        cells = gridPanel.getCells();
        setAlphaTemplate(ALPHA_TEMPLATE.OVERLAYS);
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
        BattleFieldObject obj = null ;
        if (actor instanceof LastSeenView) {
            obj =DungeonScreen.getInstance().getGridPanel().
             getObjectForView(  ((LastSeenView) actor).getParentView());

        } else
        {
            obj =DungeonScreen.getInstance().getGridPanel().getObjectForView(actor);
        }
        if (obj == null) {
            return;
        }

        if (checkOverlayForObj(STEALTH, obj)) {
            drawOverlay(actor, STEALTH, batch);
        } else if (checkOverlayForObj(SPOTTED, obj)) {
            drawOverlay(actor, SPOTTED, batch);
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

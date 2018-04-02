package eidolons.libgdx.bf.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionRule;
import eidolons.libgdx.bf.GridCellContainer;
import eidolons.libgdx.bf.GridPanel;
import eidolons.libgdx.bf.GridUnitView;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.texture.TextureCache;
import main.content.enums.rules.VisionEnums.UNIT_VISION;
import main.data.filesys.PathFinder;
import main.game.bf.Coordinates;
import main.system.auxiliary.StrPathBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static eidolons.libgdx.bf.overlays.OverlaysManager.OVERLAY.*;

/**
 * Created by JustMe on 2/20/2017.
 */
public class OverlaysManager extends SuperActor {

    private final static OVERLAY[] DEFAULT_VIEW_OVERLAYS = {
     HP_BAR
    };
    private static final OVERLAY[] SIGHT_INFO_OVERLAYS = {
     BLOCKED, IN_PLAIN_SIGHT, IN_SIGHT,
    };
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
        sightInfoDisplayed = Gdx.input.isKeyPressed(Keys.CONTROL_LEFT);
        if (sightInfoDisplayed) {
            observer = gridPanel.getObjectForView(gridPanel.getHoverObj());
            if (!(observer instanceof Unit)) {
                observer = gridPanel.getObjectForView(gridPanel.getMainHeroView());
            } else {
                if (!VisionRule.isSightInfoAvailable(observer))
                    sightInfoDisplayed=false ;
            }
        }
        super.draw(batch, parentAlpha);
        batch.setColor(1,1,1,1);
        try {
            drawOverlays(batch);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }


    }

    private void drawOverlays(Batch batch) {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                GridCellContainer actor = cells[i][j];
                List<OVERLAY> overlays = getOverlaysForCell(actor, i, cells[i].length - j - 1);
                for (OVERLAY overlay : overlays)
                    drawOverlay(actor, overlay, batch);

//                for (GridUnitView sub : actor.getUnitViewsVisible()) {
//                    overlays = getOverlaysForView(sub);
//                    for (OVERLAY overlay : overlays)
//                        drawOverlay(actor, overlay, batch);
//                }

            }
        }
    }

    private List<OVERLAY> getOverlaysForView(GridUnitView actor) {
        List<OVERLAY> list = new ArrayList<>(Arrays.asList(DEFAULT_VIEW_OVERLAYS));
        if (actor.isHovered()) {
            //emblem etc?
        }

        return list;
    }


    private List<OVERLAY> getOverlaysForCell(GridCellContainer actor, int x, int y) {
        List<OVERLAY> list = new ArrayList<>();

        if (sightInfoDisplayed) {
            DC_Cell cell = observer.getGame().getMaster().getCellByCoordinate(new Coordinates(x, y));

            UNIT_VISION vision = cell.getUnitVisionStatus(observer);
            if (vision == null) {
                return list;
            }
            switch (vision) {
                case IN_PLAIN_SIGHT:
                    list.add(IN_PLAIN_SIGHT);
                    break;
                case IN_SIGHT:
                    list.add(IN_SIGHT);
                    break;
                case BLOCKED:
                    list.add(BLOCKED);
                    break;
                case CONCEALED:
                    list.add(FOG_OF_WAR);
                    break;
            }
        }

        return list;
    }

    public void drawOverlay(Actor parent, OVERLAY overlay, Batch batch) {
        if (isOverlayAlphaOn(overlay)) {
            batch.setColor(1, 1, 1, fluctuatingAlpha);
        } else {
            batch.setColor(1,1,1,1);
        }
        float x = 0, y = 0;
        switch (overlay) {
            //init offsets
        }
        Vector2 v = parent.localToStageCoordinates(new Vector2(x, y));
        TextureRegion region = getRegion(overlay);

        batch.draw(region, v.x, v.y);
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
        return TextureCache.getOrCreateR(
         overlay.path);
    }

    public enum OVERLAY {
        SPOTTED,
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
        String path = StrPathBuilder.build(PathFinder.getComponentsPath(),
         "2018", "overlays", toString() + ".png");
    }

}

package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.datasource.GridCellDataSource;
import eidolons.libgdx.screens.DungeonScreen;
import main.game.bf.Coordinates;

import java.util.ArrayList;
import java.util.List;

public class GridCellContainer extends GridCell {
    private int unitViewCount = 0;
    private int overlayCount = 0;

    private GraveyardView graveyard;
    private boolean hasBackground;
    private GenericGridView topUnitView;
    private boolean dirty;
//    private List<GenericGridView> unitViews=new ArrayList<>(); //for cache and cycling
/*

 */

    public GridCellContainer(TextureRegion backTexture, int gridX, int gridY) {
        super(backTexture, gridX, gridY);
    }

    public GridCellContainer(GridCell parent) {
        super(parent.backTexture, parent.getGridX(), parent.getGridY());
    }

    @Override
    public GridCellContainer init() {
        super.init();


        graveyard = new GraveyardView();
        addActor(graveyard);
        graveyard.setWidth(getWidth());
        graveyard.setHeight(getHeight());

        setUserObject(new GridCellDataSource(
         new Coordinates(getGridX(), getGridY())
        ));
        return this;
    }

    public List<GenericGridView> getUnitViewsVisible() {
        return getUnitViews(true);
    }

    public List<GenericGridView> getUnitViews(boolean visibleOnly) {
        List<GenericGridView> list = new ArrayList<>();
        for (Actor actor : getChildren()) {
            if (visibleOnly)
                if (!actor.isVisible())
                    continue;
            if (actor instanceof LastSeenView)
            {
                if (((LastSeenView) actor).getParentView().isVisible())
                    continue;
            }
            if (actor instanceof GenericGridView)
            {
                list.add((GenericGridView) actor);
            }
        }
        return list;
    }


    public void recalcUnitViewBounds() {
        if (getUnitViewCount() == 0) {
            return;
        }
        hasBackground = false;
        int i = 0;

        for (GenericGridView actor : getUnitViewsVisible()) {
            if (actor.isCellBackground()) {
                i++;
                hasBackground = true;
                continue;
            }
            if (actor.getActionsOfClass(MoveToAction.class).size > 0) {
                return;
            }
            if (actor.getActionsOfClass(MoveByAction.class).size > 0) {
                return;
            }
            int offset = getUnitViewOffset();
            float scaleX = getObjScale();
            float scaleY = getObjScale();

            actor.setPosition(getViewX(offset, i), getViewY(offset, i, getUnitViewCountEffective()));
//             offset * ((getUnitViewCountEffective() - 1) - i));

            actor.setScale(scaleX, scaleY);
            actor.setScaledHeight(scaleY);
            actor.setScaledWidth(scaleX);
            actor.sizeChanged();

            recalcImagesPos(actor, offset, offset, i++);
        }
        if (graveyard != null) {
            graveyard.setZIndex(Integer.MAX_VALUE);
        }
        dirty = false;
    }

    private void recalcImagesPos(GenericGridView actor,
                                 float perImageOffsetX
     , float perImageOffsetY, int i) {
        perImageOffsetX = perImageOffsetX;// * getPosDiffFactorX();
        perImageOffsetY = perImageOffsetY;// * getPosDiffFactorY();
        actor.setX(getViewX(perImageOffsetX, i));
        actor.setY(getViewY(perImageOffsetY, i++, getUnitViewCountEffective())); //
        // (getUnitViewCountEffective() - 1) - i++));
//         perImageOffsetY * ((getUnitViewCountEffective() - 1) - i++));
    }

    public final float getViewX(float perImageOffsetX, int i) {
        return perImageOffsetX * i;
    }

    public final float getViewY(float perImageOffsetY, int i, int n) {
        return (n - 1) * perImageOffsetY - perImageOffsetY * i;
    }

    private void recalcImagesPos() {
        int i = 0;
        int offset = getUnitViewOffset();
        for (GenericGridView actor : getUnitViewsVisible()) {
            if (actor.isCellBackground()) {
                continue;
            }
            recalcImagesPos(actor, offset, offset, i++);

        }
    }

    //    private void recalcImagesPos() {
//        int i = 0;
//        final float perImageOffsetX = getUnitViewOffset() * getPosDiffFactorX();
//        final float perImageOffsetY = getUnitViewOffset() * getPosDiffFactorY();
//        for (GenericGridView actor : getUnitViews()) {
//            if (actor.isCellBackground()){
//                continue;
//            }
//            actor.setX(perImageOffsetX * i);
//            actor.setY(perImageOffsetY * ((getUnitViewCountEffective() - 1) - i++));
//
//        }
//    }
    public float getUnitViewSize() {
        return GridMaster.CELL_W - getUnitViewOffset() * (getUnitViewCount() - 1);
    }

    public float getObjScale() {
        return (getUnitViewSize()) / GridMaster.CELL_W;
    }

    @Override
    public boolean isEmpty() {
        return getUnitViewCount() == 0;
    }

    protected boolean checkIgnored() {
        if (!isVisible())
            return true;
        if (SuperActor.isCullingOff())
            return false;
        if (Eidolons.game == null)
            return true;
        if (!Eidolons.game.isStarted())
            return true;

        return !DungeonScreen.getInstance().
         controller.isWithinCamera(
         this
        );
    }

    @Override
    public void act(float delta) {
        if (checkIgnored())
            return;
        super.act(delta);
        List<GenericGridView> views = getUnitViewsVisible();
        int n = 0;
        GenericGridView hovered = null;
        for (GenericGridView actor : views) {
            if (!actor.isVisible())
                continue;
            if (actor.isCellBackground())
                actor.setZIndex(1); //over cell at least
            else if (actor.isHovered())
                hovered = actor;
            else if (actor.isActive()) {
                if (hovered == null) hovered = actor;
            } else if (!actor.isHpBarVisible()) {
                actor.setZIndex(n + 1);
            } else
                actor.setZIndex(n + 2);
            n++;
        }
        if (hovered != null)
            hovered.setZIndex(Integer.MAX_VALUE);
        graveyard.setZIndex(Integer.MAX_VALUE);
        if (dirty) {
            recalcUnitViewBounds();
        }
        if (n != unitViewCount) {
//            main.system.auxiliary.log.LogMaster.log(1, this + "*** unitviews reset to " + n);
            dirty = true;
//            recalcImagesPos();
        }
        if (dirty) {
            unitViewCount = n;
            recalcUnitViewBounds();
        }
    }


    public int getUnitViewOffset() {
        return Math.round(getWidth() /
         (getSizeFactorPerView() * getUnitViewCountEffective()));
    }

    public float getSizeFactorPerView() {
        if (hasBackground)
            return 4.0f;
        return 3.0f;
    }

    public void addActor(Actor actor) {
        super.addActor(actor);
        if (actor instanceof GenericGridView) {
            unitViewCount = getUnitViewsVisible().size();
//            main.system.auxiliary.log.LogMaster.log(1, actor + " added to " + this);
            dirty = true;
//            recalcUnitViewBounds();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " at " + getGridX() +
         ":" +
         getGridY() +
         " with " + unitViewCount;
    }

    public boolean removeActor(Actor actor) {
        return removeActor(actor, true);
    }

    public boolean removeActor(Actor actor, boolean unfocus) {
        boolean result = super.removeActor(actor, unfocus);

        if (result && actor instanceof GenericGridView) {
            unitViewCount = getUnitViewsVisible().size();
            main.system.auxiliary.log.LogMaster.log(1, actor + " removed from " + this);
            dirty = true;
//            recalcUnitViewBounds();
//            ((GenericGridView) actor).sizeChanged();
        }

        return result;
    }


    private float getPosDiffFactorX() {
        return 1.25f;
    }

    private float getPosDiffFactorY() {
        return 1.25f;
    }

    public void popupUnitView(GenericGridView uv) {
        uv.setZIndex(getChildren().size);
        recalcImagesPos();
        graveyard.setZIndex(Integer.MAX_VALUE);
        setTopUnitView(uv);
    }


    public void updateGraveyard() {
        graveyard.updateGraveyard();
    }

    public int getUnitViewCount() {

        return unitViewCount;
    }

    public int getUnitViewCountEffective() {
        return hasBackground ? unitViewCount - 1 : unitViewCount;//-graveyard.getGraveCount() ;
    }

    public GenericGridView getTopUnitView() {

        return topUnitView;
    }

    public void setTopUnitView(GenericGridView topUnitView) {
        this.topUnitView = topUnitView;

    }
}

package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.datasource.GridCellDataSource;
import eidolons.libgdx.screens.DungeonScreen;
import main.game.bf.Coordinates;
import main.system.auxiliary.data.MapMaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GridCellContainer extends GridCell {
    private int unitViewCount = 0;
    private int overlayCount = 0;

    private GraveyardView graveyard;
    private boolean hasBackground;
    private GenericGridView topUnitView;
    private boolean dirty;
    private boolean secondCheck;
    Map<Integer, GenericGridView> indexMap = new HashMap<>();
    private int Z=2;
    private boolean hovered;

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

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        graveyard.setUserObject(new GridCellDataSource(
         new Coordinates(getGridX(), getGridY())
        ));
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
            if (actor instanceof LastSeenView) {
                if (((LastSeenView) actor).getParentView().isVisible())
                    continue;
            }
            if (actor instanceof GenericGridView) {
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
            for (Action a : actor.getActions()) {
                if (a instanceof MoveByAction|| a instanceof MoveToAction)
                    actor.removeAction(a);
            }
            int offset = getUnitViewOffset();
            float scaleX = getObjScale();
            float scaleY = getObjScale();
            float y = getViewY(offset, i,
             getUnitViewCountEffective());
            float x = getViewX(offset, i);
            if (isAnimated()) {

                actor.setPosition(x, y);
                ActorMaster.addScaleAction(actor,
                 scaleX, scaleY, 0.25f);
                actor.sizeChanged();
            } else {
                actor.setPosition(x, y);
                actor.setScale(scaleX, scaleY);
                actor.sizeChanged();
            }
            actor.setScaledHeight(scaleY);
            actor.setScaledWidth(scaleX);

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

            actor.setX(getViewX(perImageOffsetX, i));
            actor.setY(getViewY(perImageOffsetY, i++, getUnitViewCountEffective()));
    }

    private boolean isAnimated() {
        return true;
    }

    public final float getViewX(  GridUnitView view) {
        return getViewX(getUnitViews(true).indexOf(view));
    }
    public final float getViewY(  GridUnitView view) {
        return getViewY(getUnitViews(true).indexOf(view), getUnitViewCount());
    }
        public final float getViewX(  int i) {
        return getViewX(getUnitViewOffset(), i);
    }
    public final float getViewY(  int i,int n) {
        return getViewY(getUnitViewOffset(), i, n);
    }
        public final float getViewX(float perImageOffsetX, int i) {
        return perImageOffsetX * i;
    }

    public final float getViewY(float perImageOffsetY, int i, int n) {
        if (isTopToBottom())
            return (n - 1) * perImageOffsetY - perImageOffsetY * (n-i-1);
            else
        return (n - 1) * perImageOffsetY - perImageOffsetY * i;
    }

    protected boolean isTopToBottom() {
        return true;
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

    private Integer getZIndexForView(GenericGridView actor) {
        if (actor.isCellBackground())
            return 1;
       if (!actor.isHpBarVisible()) {
            return Z++;
        } else
           return Z++ +1;
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
            } else {
                if (isStaticZindex()) {
                    Integer z = (Integer) MapMaster.getKeyForValue_(indexMap, actor);
                    if (z!=null ){
                        actor.setZIndex(z);
                        n++;
                        continue;
                    }
                }
                    if (!actor.isHpBarVisible()) {
                        actor.setZIndex(n + 1);
                    } else
                        actor.setZIndex(n + 2);

            }
            n++;
        }
        if (hovered != null)
            hovered.setZIndex(Integer.MAX_VALUE);
        graveyard.setZIndex(Integer.MAX_VALUE);
        if (n != unitViewCount || secondCheck) {
            dirty = true;
        } 
        
        
        if (dirty) {
            unitViewCount = n;
            recalcUnitViewBounds();
        }
    }

    private boolean isStaticZindex() {
        return true;
    }


    public int getUnitViewOffset() {
        return Math.round(getWidth() /
         (getSizeFactorPerView() * getUnitViewCountEffective()));
    }

    public float getSizeFactorPerView() {
        if (hasBackground)
            return 6.0f;
        return 5.0f;
    }

    public void addActor(Actor actor) {
        super.addActor(actor);
        if (actor instanceof GenericGridView) {
            GenericGridView view = (GenericGridView) actor;
            unitViewCount = getUnitViewsVisible().size();

            if (isAnimated())
            {
                ActorMaster.addFadeInAction(actor, getFadeDuration());
            }
            indexMap.put(getZIndexForView( view), view);
            recalcUnitViewBounds();

        }
    }

    protected float getFadeDuration() {
        return 0.3f;
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
            dirty = true;
            if (isAnimated())
                ActorMaster.addFadeOutAction(actor, getFadeDuration());
//            recalcUnitViewBounds();
            ((GenericGridView) actor).sizeChanged();
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
      uv.remove();
      addActor(uv);
        uv.setZIndex(getChildren().size);
        recalcImagesPos();
        graveyard.setZIndex(Integer.MAX_VALUE);
        setTopUnitView(uv);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
    }

    @Override
    public void setX(float x) {
        if (getX()!=x)
            super.setX(x);
    }

    @Override
    public void setPosition(float x, float y, int alignment) {
        super.setPosition(x, y, alignment);
    }

    @Override
    public void setX(float x, int alignment) {
        super.setX(x, alignment);
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

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isHovered() {
        return hovered;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }
}

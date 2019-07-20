package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import eidolons.content.PARAMS;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.meta.igg.death.ShadowMaster;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.dungeon.Entrance;
import eidolons.libgdx.GDX;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.datasource.GridCellDataSource;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.screens.DungeonScreen;
import main.game.bf.Coordinates;
import main.system.SortMaster;
import main.system.auxiliary.data.MapMaster;

import java.util.*;

public class GridCellContainer extends GridCell {
    Map<Integer, GenericGridView> indexMap = new LinkedHashMap<>();
    ValueContainer info;
    private int unitViewCount = 0;
    private int overlayCount = 0;
    private GraveyardView graveyard;
    private boolean hasBackground;
    private GenericGridView topUnitView;
    private boolean dirty;
    private boolean secondCheck;
    private int Z = 2;
    private boolean hovered;
    private boolean stackView;
    private float maxY;
    private SortMaster<GenericGridView> sorter;
    private List<GenericGridView> visibleViews;
    private List<GenericGridView> allViews;
    private boolean main;
    private int n;

    public GridCellContainer(TextureRegion backTexture, int gridX, int gridY) {
        super(backTexture, gridX, gridY);
    }

    public GridCellContainer(GridCell parent) {
        super(parent.backTexture, parent.getGridX(), parent.getGridY());
    }

    @Override
    public GridCellContainer init() {
        super.init();
        addActor(info = new ValueContainer(StyleHolder.getHqLabelStyle(20), "", "") {
            @Override
            protected boolean isVertical() {
                return true;
            }
        });
        info.padTop(12);
        info.padBottom(12);
        info.setVisible(false);
        graveyard = new GraveyardView();
        addActor(graveyard);
        graveyard.setWidth(getWidth());
        graveyard.setHeight(getHeight());

        setUserObject(new GridCellDataSource(
         Coordinates.get(getGridX(), getGridY())
        ));
        return this;
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        graveyard.setUserObject(new GridCellDataSource(
         Coordinates.get(getGridX(), getGridY())
        ));
    }

    public List<GenericGridView> getUnitViewsVisible() {
        return getUnitViews(true);
    }

    private boolean isViewCacheOn() {
        if (EidolonsGame.BOSS_FIGHT)  //hasBackground
            return true;
        //TODO igg demo hack

        if (checkIgnored())
            return false;
        if (main)
            return false;
        return true;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        visibleViews=null ;
        allViews=null ;
    }

    public List<GenericGridView> getUnitViews(boolean visibleOnly) {

        if ( isViewCacheOn())
            if (visibleOnly) {
                if (visibleViews != null && !main)
                    return visibleViews;
            } else {
                if (allViews != null && !main)
                    return allViews;
            }
        List<GenericGridView> list = new ArrayList<>();
        main = false;
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
                if (actor.getUserObject() == Eidolons.MAIN_HERO) {
                    main = true;
                }
            }
        }
        list.sort(getSorter().getSorterByExpression_(
         v -> indexMap.containsKey(v) ? -(Integer) MapMaster.getKeyForValue_(indexMap, v) : -Integer.MAX_VALUE
        ));
        if (visibleOnly) {
            visibleViews = list;
            if (main) {
                //                log(1, "visibleViews = " + list);
            }
        } else {
            allViews = list;
            if (main) {
                //                log(1, " allViews = " + list);
            }
        }
        return list;
    }


    public void recalcUnitViewBounds() {
        unitViewCount = getUnitViewsVisible().size();
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
                if (a instanceof MoveByAction || a instanceof MoveToAction)
                    actor.removeAction(a);
            }
            int offset = getUnitViewOffset();
            float scaleX = getObjScale(actor);
            float scaleY = getObjScale(actor);
            float y = getViewY(offset, i,
             getUnitViewCountEffective());
            float x = getViewX(offset, i);
            if (isAnimated()) {

                actor.setPosition(x, y);
                ActionMaster.addScaleAction(actor,
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
            if (actor.getY() + actor.getHeight() > maxY) {
                maxY = actor.getY() + actor.getHeight();
            }
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

    public final float getViewX(GridUnitView view) {
        return getViewX(getUnitViews(true).indexOf(view));
    }

    public final float getViewY(GridUnitView view) {
        return getViewY(getUnitViews(true).indexOf(view), getUnitViewCount());
    }

    public final float getViewX(int i) {
        return getViewX(getUnitViewOffset(), i);
    }

    public final float getViewY(int i, int n) {
        return getViewY(getUnitViewOffset(), i, n);
    }

    public final float getViewX(float perImageOffsetX, int i) {
        return perImageOffsetX * i;
    }

    public final float getViewY(float perImageOffsetY, int i, int n) {
        if (isTopToBottom())
            return (n - 1) * perImageOffsetY - perImageOffsetY * (n - i - 1);
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

    public float getUnitViewSize(BaseView actor) {
        if (actor instanceof UnitViewSprite)
        {
            return 128 - getUnitViewOffset() * (getUnitViewCount() - 1);
        }
        return actor.getPortrait().getWidth() - getUnitViewOffset() * (getUnitViewCount() - 1);
    }

    public float getObjScale(BaseView actor) {
        return getUnitViewSize(actor)/GridMaster.CELL_W ;
    }

    @Override
    public boolean isEmpty() {
        return getUnitViewCount() == 0;
    }

    protected boolean checkIgnored() {
        if (!isVisible())
            return true;
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
        if (actor.getUserObject() instanceof Structure || !actor.isHpBarVisible()) {
            return Z++;
        } else
            return Z++ + 1;
    }

    public void resetZIndices() {
        List<GenericGridView> views = getUnitViewsVisible();
        n = 0;
        GenericGridView    hovered =    resetZIndices(views, false);
        if (hovered != null) {
            GenericGridView unitHovered = resetZIndices(views, true);
            if (unitHovered != null)
                hovered= unitHovered;
        } else
            hovered =   resetZIndices(views, true);
        if (hovered != null)
            hovered.setZIndex(Integer.MAX_VALUE);
        if (getTopUnitView() != null)
            getTopUnitView().setZIndex(Integer.MAX_VALUE);
        graveyard.setZIndex(Integer.MAX_VALUE);
        backImage.setZIndex(0);
    }

    private GenericGridView resetZIndices(List<GenericGridView> filtered, boolean units) {
        GenericGridView hovered = null;

        for (GenericGridView actor : filtered) {
            if (units != actor.getUserObject() instanceof Unit) {
                continue;
            }
            if (!actor.isCellBackground())
                n++;
            if (!actor.isVisible())
                continue;
            if (actor.isCellBackground() || !units) {
                Integer i = (Integer) MapMaster.getKeyForValue_(indexMap, actor);
                if (i == null) {
                    i = 0;
                }
                i = Math.max(1, i);
                actor.setZIndex(Math.max(i - 1,

                        i * 2 - getUnitViewsVisible().size()
                )); //over cell at least
                //                 i / 2 +1
            } else if (actor.isHovered() || actor.getUserObject()== ShadowMaster.getShadowUnit())
                hovered = actor;
            else if (actor.isActive()) {
                if (hovered == null) hovered = actor;
            } else {
                if (isStaticZindex() && !units) { //useless?
                    Integer z = (Integer) MapMaster.getKeyForValue_(indexMap, actor);
                    if (z != null) {
                        actor.setZIndex(z);
//                        n++; why here
                        continue;
                    }
                }
                if (!actor.isHpBarVisible()) {
                    actor.setZIndex(n + 1);
                } else
                    actor.setZIndex(666);

                indexMap.put(n, actor);
            }

        }
        return hovered;
    }

    @Override
    public void act(float delta) {
        if (checkIgnored())
            return;
        super.act(delta);
         resetZIndices();

        if (n != unitViewCount || secondCheck) {
            dirty = true;
        }
        if (dirty) {
            unitViewCount = n;
            recalcUnitViewBounds();            dirty = false;
        }
        info.setPosition(GDX.centerWidth(info), maxY + 10);
    }

    private boolean isStaticZindex() {
        if (Gdx.input.isKeyPressed(Keys.TAB)
         || Gdx.input.isKeyPressed(Keys.ALT_LEFT)
         || Gdx.input.isKeyPressed(Keys.ALT_RIGHT)) {
            return false;
        }
        //        if (staticZindexAlways)
        //            return true;

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
        setDirty(true);
        if (actor instanceof GenericGridView) {
            GenericGridView view = (GenericGridView) actor;
            unitViewCount = getUnitViewsVisible().size();

            if (isAnimated()) {
                ActionMaster.addFadeInAction(actor, getFadeDuration()/1.5f); //igg demo hack
            }
            //recalc all
            indexMap.put(getZIndexForView(view), view);
            if (actor instanceof LastSeenView) {
                return;
            }
            recalcUnitViewBounds();

            if (actor.getUserObject() == Eidolons.MAIN_HERO
             || actor.getUserObject() instanceof Entrance)
                main = true;
        }
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
        visibleViews = null;
        allViews = null;
    }
    protected float getFadeDuration() {
        return 0.21f;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " at " + getGridX() +
         ":" +
         getGridY() +
         " with " + unitViewCount;
    }

    public boolean removeActor(Actor actor) {
        if (actor.getUserObject() == Eidolons.MAIN_HERO)
            main = false;
        return removeActor(actor, true);
    }

    public boolean removeActor(Actor actor, boolean unfocus) {
        boolean result = super.removeActor(actor, unfocus);

        if (result && actor instanceof GenericGridView) {
            setDirty(true);
            unitViewCount = getUnitViewsVisible().size();
            if (isAnimated())
                ActionMaster.addFadeOutAction(actor, getFadeDuration());
            //            recalcUnitViewBounds();
            ((GenericGridView) actor).sizeChanged();

            if (actor.getUserObject() == Eidolons.MAIN_HERO)
                main = false;
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
        uv.setHovered(true);
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
        if (getX() != x)
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


    public boolean isHovered() {
        return hovered;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public boolean isStackView() {
        return stackView;
    }

    public void setStackView(boolean stackView) {
        this.stackView = stackView;
        info.setVisible(stackView);
        info.setNameText("[" + getUnitViews(true).size() + " in stack, ESC to hide]");
        info.setValueText("[" +
         getUserObject().getParam(PARAMS.GIRTH) +
         " girth on " + 1000 +
         //         getUserObject().getIntParam() +
         "space]");
        info.pack();
        getUnitViews(false).forEach(view-> view.setStackView(stackView));
    }

    public SortMaster<GenericGridView> getSorter() {
        if (sorter == null) {
            sorter = new SortMaster<>();
        }
        return sorter;
    }

}

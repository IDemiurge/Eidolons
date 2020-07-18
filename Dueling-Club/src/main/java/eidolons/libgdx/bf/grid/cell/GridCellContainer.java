package eidolons.libgdx.bf.grid.cell;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.vision.colormap.LightConsts;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.dungeon.Entrance;
import eidolons.game.netherflame.main.death.ShadowMaster;
import eidolons.libgdx.GDX;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.actions.ActionMaster;
import eidolons.libgdx.anims.actions.FadeOutAction;
import eidolons.libgdx.bf.Hoverable;
import eidolons.libgdx.bf.datasource.GridCellDataSource;
import eidolons.libgdx.bf.decor.wall.WallMaster;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.bf.overlays.map.WallMap;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.screens.ScreenMaster;
import main.content.enums.GenericEnums;
import main.game.bf.Coordinates;
import main.system.SortMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ListMaster;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static main.system.auxiliary.log.LogMaster.log;

public class GridCellContainer extends GridCell implements Hoverable {
    CellCalculator calc = new CellCalculator(this);
    Map<GenericGridView, Integer> indexMap = new LinkedHashMap<>();
    ValueContainer info;
    protected GraveyardView graveyard;
    protected int nonBgUnitViewCount = 0;
    protected int n;
    protected float maxY;
    protected boolean dirty, secondCheck, hovered, stackView;
    private boolean screen;
    protected SortMaster<GenericGridView> sorter;
    public List<GenericGridView> visibleViews;
    protected List<GenericGridView> allViews;
    protected GenericGridView topUnitView;
    protected boolean mainHero, hasBackground, wall, lightEmitter, water;

    public GridCellContainer(TextureRegion backTexture, int gridX, int gridY, Function<Coordinates, Color> colorFunc) {
        super(backTexture, gridX, gridY, colorFunc);
    }

    public GridCellContainer(GridCell parent) {
        super(parent.backTexture, parent.getGridX(), parent.getGridY(),
                parent.getColorFunc());
    }

    public boolean isMainHero() {
        return mainHero;
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
        return this;
    }

    public void applyColor() {
        Coordinates coord = getUserObject().getCoordinates();
        Color c = colorFunc.apply(coord);
        applyColor(c);
    }


    public float getMinLightness() {
        return getUserObject().isPlayerHasSeen()
                ? LightConsts.MIN_LIGHTNESS_CELL_SEEN
                : LightConsts.MIN_LIGHTNESS_CELL_UNSEEN;
    }

    public void applyColor(float lightness, Color c) {
        ImageContainer imgContainer = this.cellImgContainer;
        if (overlay != null) {
            if (getUserObject().isArtPuzzleCell()) {
                imgContainer = overlay;
            }
        }
        float a1 = imgContainer.getColor().a;
        // if (getActions().size > 0) {
        //     a1 = getColor().a;
        // }
        if (a1 > 0) {
            imgContainer.setVisible(true);
            if (lightness > LightConsts.MIN_SCREEN && a1 > 0) {
                screen = true;
                imgContainer.setScreenOverlay(lightness);
            } else {
                imgContainer.setScreenOverlay(0);
                screen = false;
            }
            imgContainer.setColor(c.r, c.g, c.b, a1);

        }
        for (GenericGridView unitView : getUnitViews(true)) {
            float a = unitView.getPortrait().getContent().getColor().a;
            unitView.getPortrait().getContent().getColor().lerp(c, LightConsts.UNIT_VIEW_COLOR_LERP);
            unitView.getPortrait().getContent().getColor().a = a;
            if (lightness > LightConsts.MIN_SCREEN) {
                unitView.setScreenOverlay(lightness);
            } else
                unitView.setScreenOverlay(0);

        }
    }

    public void drawCell(Batch batch) {
        if (visibleViews == null) {
            draw(batch, 1f);
            return;
        }
        for (GenericGridView view : visibleViews) {
            view.setVisible(false, true); //TODO gdx review - will cause recalcBounds!!!
        }
        draw(batch, 1f);
        for (GenericGridView view : visibleViews) {
            view.setVisible(true, true);
        }
    }

    public void drawWithoutCell(Batch batch) {
        cellImgContainer.setVisible(false);
        draw(batch, 1f);
        cellImgContainer.setVisible(true);
    }

    public void drawScreen(Batch batch) {
        if (!ListMaster.isNotEmpty(visibleViews)) {
            cellImgContainer.setScreenEnabled(true);
            // SnapshotArray<Actor> children = getChildren();
            // clearChildren();
            // draw(batch, 1f);
            cellImgContainer.setPosition(getX(), getY());
            cellImgContainer.draw(batch, 1f);
            cellImgContainer.setPosition(0, 0);
            cellImgContainer.setScreenEnabled(false);
            return;
        }
        cellImgContainer.setScreenEnabled(false);
        for (GenericGridView visibleView : visibleViews) {
            float x = visibleView.getX();
            float y = visibleView.getY();
            visibleView.setPosition(x + getX(), y + getY());
            visibleView.drawScreen(batch);
            visibleView.setPosition(x, y);
        }
        // super.draw(batch, 1f);

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        if (graveyard != null)
            graveyard.setUserObject(new GridCellDataSource(
                    Coordinates.get(getGridX(), getGridY())
            ));
    }

    public List<GenericGridView> getUnitViewsVisible() {
        return getUnitViews(true);
    }

    protected boolean isViewCacheOn() {
        if (checkIgnored())
            return false;
        return !mainHero;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        visibleViews = null;
        allViews = null;
    }

    public List<GenericGridView> getUnitViews(boolean visibleOnly) {

        if (isViewCacheOn())
            if (visibleOnly) {
                if (visibleViews != null && !mainHero)
                    return visibleViews;
            } else {
                if (allViews != null && !mainHero)
                    return allViews;
            }
        List<GenericGridView> list = new ArrayList<>();
        mainHero = false;
        for (Actor actor : getChildren()) {
            if (!wall && visibleOnly && !actor.isVisible()) continue;
            if (actor instanceof LastSeenView) {
                if (((LastSeenView) actor).getParentView().isVisible())
                    continue;
            }
            if (actor instanceof GenericGridView) {
                list.add((GenericGridView) actor);
                if (actor.getUserObject() == Eidolons.MAIN_HERO) {
                    mainHero = true;
                }
            }
        }
        if (!list.isEmpty())
            list.sort(getSorter().getSorterByExpression_(
                    v -> indexMap.containsKey(v) ? -indexMap.get(v) : -Integer.MAX_VALUE
            ));
        if (visibleOnly) {
            visibleViews = list;
        } else {
            allViews = list;
        }
        return list;
    }


    public void recalcUnitViewBounds() {
        if (!wall && nonBgUnitViewCount == 0) {
            return;
        }
        hasBackground = false;
        lightEmitter = false;
        int index = 0;

        List<GenericGridView> unitViewsVisible = getUnitViewsVisible();
        for (int i = 0; i < unitViewsVisible.size(); i++) {
            GenericGridView actor = unitViewsVisible.get(i);
            if (actor.isCellBackground()) {
                index++;
                hasBackground = true;
                continue;
            }
            for (Action a : actor.getActions()) {
                if (a instanceof MoveByAction || a instanceof MoveToAction)
                    actor.removeAction(a);
            }
            int offset = calc.getUnitViewOffset();
            float scaleX = calc.getObjScale(actor);
            float scaleY = calc.getObjScale(actor);
            float y = calc.getViewY(offset, index,
                    getUnitViewCountEffective());
            float x = calc.getViewX(offset, index);
            actor.setPosition(x, y);
            //Gdx hack
            if (Math.abs(1 - actor.getScaleX()) < 0.5f && isAnimated()) {
                ActionMaster.addScaleAction(actor,
                        scaleX, scaleY, 0.25f);
            } else {
                actor.setScale(scaleX, scaleY);
            }
            actor.setScaledHeight(scaleY);
            actor.setScaledWidth(scaleX);
            actor.sizeChanged();

            recalcImagesPos(actor, offset, offset, index++);
            if (actor.getUserObject().isWall()) {
                actor.setPosition(x, y);
            }
            if (actor.getY() + actor.getHeight() > maxY) {
                maxY = actor.getY() + actor.getHeight();
            }
        }
        if (graveyard != null) {
            graveyard.setZIndex(Integer.MAX_VALUE);
        }
        dirty = false;
        info.setPosition(GDX.centerWidth(info), maxY + 10);
    }

    protected void recalcImagesPos(GenericGridView actor,
                                   float perImageOffsetX, float perImageOffsetY, int i) {

        actor.setX(calc.getViewX(perImageOffsetX, i));
        actor.setY(calc.getViewY(perImageOffsetY, i++, getUnitViewCountEffective()));
    }

    protected boolean isAnimated() {
        return true;
    }

    public final float getViewX(UnitGridView view) {
        return calc.getViewX(view);
    }

    public final float getViewY(UnitGridView view) {
        return calc.getViewY(view);
    }

    protected void recalcImagesPos() {
        int i = 0;
        int offset = calc.getUnitViewOffset();
        for (GenericGridView actor : getUnitViewsVisible()) {
            if (actor.isCellBackground()) {
                continue;
            }
            recalcImagesPos(actor, offset, offset, i++);

        }
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

        return !isWithinCamera();
    }


    public void resetZIndices() {
        List<GenericGridView> views = getUnitViewsVisible();
        n = 0;
        GenericGridView hovered = resetZIndices(views, false);
        if (hovered != null) {
            GenericGridView unitHovered = resetZIndices(views, true);
            if (unitHovered != null)
                hovered = unitHovered;
        } else {
            if (!views.isEmpty()) {
                hovered = resetZIndices(views, true);
            }
        }
        if (hovered != null)
            hovered.setZIndex(Integer.MAX_VALUE);
        if (getTopUnitView() != null)
            getTopUnitView().setZIndex(Integer.MAX_VALUE);
        if (graveyard != null) {
            graveyard.setZIndex(Integer.MAX_VALUE);
        }
        cellImgContainer.setZIndex(0);
    }

    protected GenericGridView resetZIndices(List<GenericGridView> filtered, boolean units) {
        GenericGridView hovered = null;

        for (GenericGridView actor : filtered) {
            if (units != actor.getUserObject() instanceof Unit) {
                continue;
            }
            if (!actor.isCellBackground())
                n++;
            if (!actor.isVisible())
                continue;
            if (actor.isCellBackground()) {
                Integer i = indexMap.get(actor);
                if (i == null) {
                    i = 0;
                }
                i = Math.max(2, i);
                actor.setZIndex(Math.max(i - 2,

                        i * 2 - getUnitViewsVisible().size()
                )); //over cell at least
                //                 i / 2 +1
            } else if (actor.isHovered() || actor.getUserObject() == ShadowMaster.getShadowUnit())
                hovered = actor;
            else if (actor.isActive()) {
                if (hovered == null) hovered = actor;
            } else {
                if (calc.isStaticZindex() && !units) { //useless?
                    Integer z = indexMap.get(actor);
                    if (z != null) {
                        if (!units) {
                            actor.setZIndex(z - 1);
                        } else
                            actor.setZIndex(z);
                        //                        n++; why here
                        continue;
                    }
                }
                if (!actor.isHpBarVisible()) {
                    actor.setZIndex(n + 1);
                } else
                    actor.setZIndex(666);

                indexMap.put(actor, n);
            }

        }
        if (overlay != null)
            if (overlay.getParent() != null) {
                overlay.setZIndex(999);
            }
        return hovered;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        resetZIndices();
        if (n != nonBgUnitViewCount || secondCheck) {
            dirty = true;
        }
        if (dirty) {
            nonBgUnitViewCount = n;
            recalcUnitViewBounds();
            dirty = false;
        }
        applyColor();
        // if (wall) {
        //     recalcUnitViewBounds();
        // }
    }

    protected boolean isStaticZindex() {
        //        if (staticZindexAlways)
        //            return true;
        return calc.isStaticZindex();
    }


    public int getUnitViewOffset() {
        return calc.getUnitViewOffset();
    }

    public float getSizeFactorPerView() {
        return calc.getSizeFactorPerView();
    }

    public void addActor(Actor actor) {
        super.addActor(actor);
        setDirty(true);
        if (actor instanceof GenericGridView) {
            GenericGridView view = (GenericGridView) actor;
            if (!getUserObject().isVOID()) {
                if (isAnimated()) {
                    ActionMaster.addFadeInAction(actor, getFadeDuration());
                }
            }

            //recalc all
            indexMap.put(view, calc.getZIndexForView(view));
            if (actor instanceof LastSeenView) {
                return;
            }

            BattleFieldObject userObject = (BattleFieldObject) actor.getUserObject();
            if (userObject == Eidolons.MAIN_HERO
                    || userObject instanceof Entrance)
                mainHero = true;
            if (userObject.isWall()) {
                if (!wall) {
                    calc.offsetX = WallMap.getOffsetX();
                    calc.offsetY = WallMap.getOffsetY();
                }
                wall = true;
                if (isRotation(true)) {
                    int n = RandomWizard.getRandomIntBetween(0, 4);
                    actor.setRotation(90 * n);
                    actor.setOrigin(64, 64);
                }
            } else {
                if (userObject.isWater()) {
                    water = true;
                    cellImgContainer.fadeOut(); //editor?!
                    view.getPortrait().setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE.WATER);
                }
            }


            if (userObject.isLightEmitter()) {
                lightEmitter = true;
            }
            recalcUnitViewBounds();
        }
    }

    @Override
    public boolean isRotation(boolean wall) {
        return WallMaster.isRotation(getCoordinates(), wall);
    }

    public Coordinates getCoordinates() {
        if (getUserObject() != null) {
            return getUserObject().getCoordinates();
        }
        return Coordinates.get(gridX, gridY);
    }

    @Override
    public void setVoid(boolean VOID, boolean animated) {
        super.setVoid(VOID, animated);
        for (GenericGridView unitView : getUnitViews(false)) {
            unitView.fadeIn();
        }
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
        visibleViews = null;
        allViews = null;
    }

    protected float getFadeDuration() {
        return 0.14f;
    }

    @Override
    public String toString() {
        return "Cell at " + getGridX() +
                ":" +
                getGridY() +
                " with " + allViews;
    }

    public boolean removeActor(Actor actor) {
        removed(actor);
        return removeActor(actor, true);
    }

    private void removed(Actor actor) {
        if (actor.getUserObject() == Eidolons.MAIN_HERO)
            mainHero = false;
        lightEmitter = false;
        if (wall) {
            wall = false;
            calc.offsetX = 0;
            calc.offsetY = 0;
        }
        if (water) {
            water = false;
            cellImgContainer.fadeIn();
        }
    }

    public boolean removeActor(Actor actor, boolean unfocus) {
        boolean result = super.removeActor(actor, unfocus);

        if (result && actor instanceof GenericGridView) {
            setDirty(true);
            if (isAnimated())
                ActionMaster.addFadeOutAction(actor, getFadeDuration());
            //            recalcUnitViewBounds();
            ((GenericGridView) actor).sizeChanged();
        }
        removed(actor);
        return result;
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
        if (graveyard == null) {
            initGraveyard();
        }
        graveyard.updateGraveyard();
    }

    private void initGraveyard() {
        addActor(graveyard = new GraveyardView());
        graveyard.setWidth(getWidth());
        graveyard.setHeight(getHeight());
        graveyard.setUserObject(new GridCellDataSource(Coordinates.get(getGridX(), getGridY())));
    }

    public int getUnitViewCount() {
        //        return nonBgUnitViewCount;
        return hasBackground ? nonBgUnitViewCount - 1 : nonBgUnitViewCount;
    }

    public int getUnitViewCountEffective() {
        return nonBgUnitViewCount;
        //        return hasBackground ? nonBgUnitViewCount - 1 : nonBgUnitViewCount;//-graveyard.getGraveCount() ;
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
        getUnitViews(false).forEach(view -> view.setStackView(stackView));
    }

    public SortMaster<GenericGridView> getSorter() {
        if (sorter == null) {
            sorter = new SortMaster<>();
        }
        return sorter;
    }

    protected boolean isWithinCameraCheck() {
        float expandHeight = 0;
        float expandWidth = 0;
        if (visibleViews != null)
            for (GenericGridView visibleView : visibleViews) {
                if (visibleView.getExpandHeight() > expandHeight) {
                    expandHeight = visibleView.getExpandHeight();
                }
                if (visibleView.getExpandWidth() > expandWidth) {
                    expandWidth = visibleView.getExpandWidth();
                }
            }
        return ScreenMaster.getScreen().getController().isWithinCamera(-expandWidth + getX(), -expandHeight + getY(),
                2 * expandWidth + getWidth(),
                2 * expandHeight + getHeight());
    }

    public void fadeInOverlay(TextureRegion texture) {
        if (getVoidAnimHappened())
            return;
        //        setOverlayTexture(texture);
        if (getUnitViewsVisible().size() >= 1) {
            return;
        }
        {
            if (getOverlay().getParent() == null) {
                addActor(overlay);
            }
            overlay.setContentsImmediately(new Image(texture));
        }
        overlay.getColor().a = 0;
        overlay.fadeIn();
        ActionMaster.addAfter(overlay, new FadeOutAction());
        // log(1, "fadeIn overlay" + this);
    }

    public void fadeOutOverlay() {
        //        setOverlayTexture(null );
        if (overlay == null) {
            return;
        }
        overlay.fadeOut();
        ActionMaster.addRemoveAfter(overlay);
        log(1, "fadeOut overlay" + overlay.getActions());
    }


    public boolean isWall() {
        return wall;
    }

    public void setWall(boolean wall) {
        this.wall = wall;
    }

    public boolean isLightEmitter() {
        return lightEmitter;
    }

    public Map<GenericGridView, Integer> getIndexMap() {
        return indexMap;
    }

    public boolean isScreen() {
        return screen;
    }

    @Override
    public void setTransform(boolean transform) {
        super.setTransform(transform);
        if (transform)
            main.system.auxiliary.log.LogMaster.log(1, this + " transform set: " + transform);
    }
}

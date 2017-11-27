package main.libgdx.bf;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import main.game.bf.Coordinates;
import main.game.core.Eidolons;
import main.libgdx.bf.datasource.GridCellDataSource;
import main.libgdx.screens.DungeonScreen;

import java.util.LinkedList;
import java.util.List;

public class GridCellContainer extends GridCell {
    private int unitViewCount = 0;
    private int overlayCount = 0;

    private GraveyardView graveyard;
    private boolean hasBackground;
    private GridUnitView topUnitView;
//    private List<GridUnitView> unitViews=new ArrayList<>(); //for cache and cycling


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

    public List<GridUnitView> getUnitViews() {
//        if (unitViews==null ){
//            unitViews = new ArrayList<>();
//        }
//        return unitViews;
        List<GridUnitView> list = new LinkedList<>();
        for (Actor actor : getChildren()) {
            if (actor.isVisible())
                if (actor instanceof GridUnitView)
                    list.add((GridUnitView) actor);
        }
        return list;
    }

    public void recalcUnitViewBounds() {
        if (getUnitViewCount() == 0) {
            return;
        }
        hasBackground =false;
        int i = 0;

        for (GridUnitView actor : getUnitViews()) {
            if (actor.isCellBackground()) {
                i++;
                hasBackground =true;
                continue;
            }
            int perImageOffsetX = getSizeDiffX();
            int perImageOffsetY =perImageOffsetX;// getSizeDiffY();
            int w = GridConst.CELL_W - perImageOffsetX * (getUnitViewCount() - 1);
            int h =w;// GridConst.CELL_H - perImageOffsetY * (getUnitViewCount() - 1);
            float scaleX = new Float(w) / GridConst.CELL_W;
            float scaleY = new Float(h) / GridConst.CELL_H;

            actor.setPosition(perImageOffsetX * i,
             perImageOffsetY * ((getUnitViewCountEffective() - 1) - i));
            actor.setScale(scaleX, scaleY);
            actor.setScaledHeight(scaleY);
            actor.setScaledWidth(scaleX);
            actor.sizeChanged();
            i++;
        }
//        recalcImagesPos();
        if (graveyard != null) {
            graveyard.setZIndex(Integer.MAX_VALUE);
        }
    }
    public   float getObjScale() {
        int size = GridConst.CELL_W - getSizeDiffX() * (getUnitViewCount() - 1);
        return new Float(size) / GridConst.CELL_W;
    }
    @Override
    public boolean isEmpty() {
        return getUnitViewCount()==0;
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

        if (!DungeonScreen.getInstance().
         getController().isWithinCamera(
         this
        )) {
            return true;
        }
        return false;
    }

    @Override
    public void act(float delta) {
        if (checkIgnored())
            return;
        super.act(delta);
        List<GridUnitView> views = getUnitViews();
        int n=0;
        for (GridUnitView actor : views) {
            if (!actor.isVisible())
                continue;
            if (actor.isCellBackground())
                actor.setZIndex(1); //over cell at least
            else if (actor.isHovered())
                actor.setZIndex(Integer.MAX_VALUE);
            else if (actor.isActive())
                actor.setZIndex(Integer.MAX_VALUE);
            n++;
        }
        graveyard.setZIndex(Integer.MAX_VALUE);
        if (n != unitViewCount) {
            main.system.auxiliary.log.LogMaster.log(1,  this + "*** unitviews reset to "+ n  );
            unitViewCount = n;
            recalcImagesPos();
        }
    }


        private  int getSizeDiffY() {
        return Math.round(getHeight() /
         (getSizeFactorPerView() * getUnitViewCountEffective()));
    }

    private int getSizeDiffX() {
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
        if (actor instanceof GridUnitView) {
            unitViewCount=getUnitViews().size();
            main.system.auxiliary.log.LogMaster.log(1,actor + " added to "+ this  );
            recalcUnitViewBounds();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+ " at " +getGridX()+
         ":" +
         getGridY() +
         " with "+ unitViewCount;
    }

    public boolean removeActor(Actor actor) {
       return  removeActor(actor, true);
    }
        public boolean removeActor(Actor actor, boolean unfocus) {
        boolean result = super.removeActor(actor, unfocus);

        if (result && actor instanceof GridUnitView) {
            unitViewCount=getUnitViews().size();
            main.system.auxiliary.log.LogMaster.log(1,actor + " removed from "+ this  );
            recalcUnitViewBounds();
            ((GridUnitView) actor).sizeChanged();
        }

        return result;
    }

    private void recalcImagesPos() {
        int i = 0;
        final float perImageOffsetX = getSizeDiffX() * getPosDiffFactorX();
        final float perImageOffsetY = getSizeDiffY() * getPosDiffFactorY();
        for (GridUnitView actor : getUnitViews()) {
            if (actor.isCellBackground()){
                continue;
            }
                actor.setX(perImageOffsetX * i);
                actor.setY(perImageOffsetY * ((getUnitViewCountEffective() - 1) - i++));
        }
    }

    private float getPosDiffFactorX() {
        return 1.25f;
    }

    private float getPosDiffFactorY() {
        return 1.25f;
    }

    public void popupUnitView(GridUnitView uv) {
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
        return hasBackground?unitViewCount-1 : unitViewCount;//-graveyard.getGraveCount() ;
    }

    public GridUnitView getTopUnitView() {

        return topUnitView;
    }

    public void setTopUnitView(GridUnitView topUnitView) {
        this.topUnitView = topUnitView;

    }
}

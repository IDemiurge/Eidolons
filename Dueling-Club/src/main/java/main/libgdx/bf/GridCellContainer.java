package main.libgdx.bf;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import main.game.bf.Coordinates;
import main.game.core.Eidolons;
import main.libgdx.bf.datasource.GridCellDataSource;
import main.libgdx.screens.DungeonScreen;

import java.util.ArrayList;
import java.util.List;

public class GridCellContainer extends GridCell {
    private int unitViewCount = 0;
    private int overlayCount = 0;

    private GraveyardView graveyard;
    private boolean hasBackground;
    private GridUnitView topUnitView;
//    private List<GridUnitView> unitViews=new ArrayList<>(); //for cache and cycling
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

    public List<GridUnitView> getUnitViews() {
//        if (unitViews==null ){
//            unitViews = new ArrayList<>();
//        }
//        return unitViews;
        List<GridUnitView> list = new ArrayList<>();
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
            int offset = getUnitViewOffset();
            float scaleX =getObjScale();
            float scaleY =getObjScale();

            actor.setPosition(offset * i,
             offset * ((getUnitViewCountEffective() - 1) - i));

            actor.setScale(scaleX, scaleY);
            actor.setScaledHeight(scaleY);
            actor.setScaledWidth(scaleX);
            actor.sizeChanged();

            recalcImagesPos(actor, offset, offset, i++);
        }
        if (graveyard != null) {
            graveyard.setZIndex(Integer.MAX_VALUE);
        }
    }

    private void recalcImagesPos(GridUnitView actor,
                                   float perImageOffsetX
                                 ,  float perImageOffsetY, int i) {
       perImageOffsetX = perImageOffsetX;// * getPosDiffFactorX();
       perImageOffsetY = perImageOffsetY;// * getPosDiffFactorY();
        actor.setX(perImageOffsetX * i);
        actor.setY(perImageOffsetY * ((getUnitViewCountEffective() - 1) - i++));
    }
    private void recalcImagesPos() {
        int i = 0;
        int offset = getUnitViewOffset();
        for (GridUnitView actor : getUnitViews()) {
            if (actor.isCellBackground()){
                continue;
            }
            recalcImagesPos(actor, offset, offset, i++);

        }
    }
//    private void recalcImagesPos() {
//        int i = 0;
//        final float perImageOffsetX = getUnitViewOffset() * getPosDiffFactorX();
//        final float perImageOffsetY = getUnitViewOffset() * getPosDiffFactorY();
//        for (GridUnitView actor : getUnitViews()) {
//            if (actor.isCellBackground()){
//                continue;
//            }
//            actor.setX(perImageOffsetX * i);
//            actor.setY(perImageOffsetY * ((getUnitViewCountEffective() - 1) - i++));
//
//        }
//    }
    private float getUnitViewSize() {
       return  GridConst.CELL_W - getUnitViewOffset() * (getUnitViewCount() - 1);
    }

    public   float getObjScale() {
        return  (getUnitViewSize()) / GridConst.CELL_W;
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
        float maxX=GridConst.CELL_W - getUnitViewSize()/2;
        float maxY=GridConst.CELL_H - getUnitViewSize()/2;
        for (GridUnitView actor : views) {
            if (!actor.isVisible())
                continue;
            if (actor.isCellBackground())
                actor.setZIndex(1); //over cell at least
            else if (actor.isHovered())
                actor.setZIndex(Integer.MAX_VALUE);
            else if (actor.isActive())
                actor.setZIndex(Integer.MAX_VALUE);

            if (actor.getX() > maxX)
                actor.setX(maxX);
            if (actor.getY() >  maxX)
                actor.setY(maxY);
            n++;
        }
        graveyard.setZIndex(Integer.MAX_VALUE);
        if (n != unitViewCount) {
            main.system.auxiliary.log.LogMaster.log(1,  this + "*** unitviews reset to "+ n  );
            unitViewCount = n;
            recalcImagesPos();
        }
    }

    public void limitUnitPositions(GridUnitView actor) {
    }


    private int getUnitViewOffset() {
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

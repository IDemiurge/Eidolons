package main.libgdx.bf;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import main.game.bf.Coordinates;
import main.libgdx.bf.datasource.GridCellDataSource;
import main.libgdx.screens.DungeonScreen;
import main.system.graphics.MigMaster;
import main.system.options.GraphicsOptions.GRAPHIC_OPTION;
import main.system.options.OptionsMaster;

import java.util.LinkedList;
import java.util.List;

public class GridCellContainer extends GridCell {
    private int unitViewCount = 0;
    private int overlayCount = 0;

    private GraveyardView graveyard;


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
        List<GridUnitView> list = new LinkedList<>();
        for (Actor actor : getChildren()) {
            if (actor instanceof GridUnitView)
                list.add((GridUnitView) actor);
        }
        return list;
    }

    private void recalcUnitViewBounds() {
        if (getUnitViewCountEffective() == 0) {
            return;
        }
        final int perImageOffsetX = getSizeDiffX();
        final int perImageOffsetY = getSizeDiffY();
        final int w = GridConst.CELL_W - perImageOffsetX * (getUnitViewCountEffective() - 1);
        final int h = GridConst.CELL_H - perImageOffsetY * (getUnitViewCountEffective() - 1);
        int i = 0;
        float scaleX = new Float(w) / GridConst.CELL_W;
        float scaleY = new Float(h) / GridConst.CELL_H;
        for (Actor actor : getChildren()) {
            if (actor instanceof GridUnitView) {
//                actor.setBounds(
//                 perImageOffsetX * i,
//                 perImageOffsetY * ((unitViewCount - 1) - i)
//                 , GridConst.CELL_W * scaleX, GridConst.CELL_H * scaleY
//                );
                actor.setPosition( perImageOffsetX * i,
                perImageOffsetY * ((getUnitViewCount() - 1) - i));
                actor.setScale(scaleX, scaleY);
                ((GridUnitView) actor).setScaledHeight(scaleY);
                ((GridUnitView) actor).setScaledWidth(scaleX);
                ((GridUnitView) actor).sizeChanged();
                i++;
            }
        }

        if (graveyard != null) {
            graveyard.setZIndex(Integer.MAX_VALUE);
        }
    }

    protected boolean checkIgnored() {
        if (OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.OPTIMIZATION_ON))
            if (!DungeonScreen.getInstance().
             getController().isWithinCamera(
             this
//              getX(), getY(), 2*getWidth(), 2*getHeight()
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
        for (GridUnitView actor : getUnitViews())
            if (actor.isHovered())
                actor.setZIndex(Integer.MAX_VALUE );
        for (GridUnitView actor : getUnitViews())
            if (actor.isActive())
                    actor.setZIndex(Integer.MAX_VALUE );

        graveyard.setZIndex(Integer.MAX_VALUE );
    }

    private int getSizeDiffY() {
        return Math.round(getHeight() /
         (getSizeFactorPerView() * getUnitViewCountEffective()));
    }

    private int getSizeDiffX() {
        return Math.round(getWidth() /
         (getSizeFactorPerView() * getUnitViewCountEffective()));
    }

    private float getSizeFactorPerView() {
        return 2.4f;
    }

    public void addActor(Actor actor) {
        super.addActor(actor);
        if (actor instanceof GridUnitView) {
            unitViewCount =   getUnitViews().size();
            recalcUnitViewBounds();
        }
    }

    public boolean removeActor(Actor actor) {
        boolean result = super.removeActor(actor);

        if (result && actor instanceof GridUnitView) {
            unitViewCount =   getUnitViews().size();
            recalcUnitViewBounds();
            ((GridUnitView) actor).sizeChanged();
        }

        return result;
    }

    private void recalcImagesPos() {
        int i = 0;
        final float perImageOffsetX = getSizeDiffX() * getPosDiffFactorX();
        final float perImageOffsetY = getSizeDiffY() * getPosDiffFactorY();
        for (Actor actor : getChildren()) {
            if (actor instanceof GridUnitView) {
                actor.setX(perImageOffsetX * i);
                actor.setY(perImageOffsetY * ((getUnitViewCountEffective() - 1) - i++));
            }
        }
    }

    private float getPosDiffFactorX() {
        return 1.25f;
    }

    private float getPosDiffFactorY() {
        return 1.25f;
    }

    public void popupUnitView(BaseView uv) {
        uv.setZIndex(getChildren().size - overlayCount);
        recalcImagesPos();
        graveyard.setZIndex(Integer.MAX_VALUE);
    }

    public void setOverlays(List<OverlayView> overlays) {
        if (overlays.size() == 0) {
            return;
        }

        final int width = (int) (getWidth()* OverlayView.SCALE);
        final int height = (int) (getHeight()* OverlayView.SCALE);
        overlays.forEach(view -> {
            Coordinates.DIRECTION direction = view.getDirection();
            int calcXOffset = 0;
            int calcYOffset = 0;
            if (direction == null) {
                calcXOffset+= (getWidth()-width)* OverlayView.SCALE;
                calcYOffset+= (getHeight()-height)* OverlayView.SCALE;
            } else {
                int size = width;
                int x = MigMaster.getCenteredPosition((int) getWidth(), size);
                if (direction != null) {
                    if (direction.isGrowX() == null) {
                        x = MigMaster.getCenteredPosition((int)getWidth(), size);
                    } else {
                        x = (direction.isGrowX()) ? (int)getWidth() - size : 0;
                    }
                }

                int y = MigMaster.getCenteredPosition((int)getHeight(), size);
                if (direction != null) {
                    if (direction.isGrowY() == null) {
                        y = MigMaster.getCenteredPosition((int)getHeight(), size);
                    } else {
                        y = (direction.isGrowY()) ? (int)getHeight() - size : 0;
                    }
                }
                calcXOffset = x;
                  calcYOffset = y;
//                switch (direction) {
//                    case UP:
//                        calcXOffset += (getWidth()-width)* OverlayView.SCALE;
//                        calcYOffset += height * 2;
//                        break;
//                    case DOWN:
//                        calcXOffset += (getWidth()-width)* OverlayView.SCALE;
//                        break;
//                    case LEFT:
//                        calcYOffset += height;
//                        break;
//                    case RIGHT:
//                        calcXOffset += width * 2;
//                        calcYOffset += height;
//                        break;
//                    case UP_LEFT:
//                        calcYOffset += height * 2;
//                        break;
//                    case UP_RIGHT:
//                        calcXOffset += width * 2;
//                        calcYOffset += height * 2;
//                        break;
//                    case DOWN_RIGHT:
//                        calcXOffset += width * 2;
//                        break;
//                    case DOWN_LEFT:
//                        break;
//                }
            }

            view.setBounds(calcXOffset, calcYOffset, width, height);
            addActor(view);
            overlayCount++;
        });
    }

    public void updateGraveyard() {
        graveyard.updateGraveyard();
    }

    public int getUnitViewCount() {

        return unitViewCount;
    }
    public int getUnitViewCountEffective() {
        return unitViewCount;//-graveyard.getGraveCount() ;
    }
}

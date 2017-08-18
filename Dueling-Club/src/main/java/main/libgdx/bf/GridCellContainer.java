package main.libgdx.bf;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import main.game.bf.Coordinates;
import main.libgdx.bf.datasource.GridCellDataSource;

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
        if (unitViewCount == 0) {
            return;
        }
        final int perImageOffsetX = getSizeDiffX();
        final int perImageOffsetY = getSizeDiffY();
        final int w = GridConst.CELL_W - perImageOffsetX * (unitViewCount - 1);
        final int h = GridConst.CELL_H - perImageOffsetY * (unitViewCount - 1);
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
                perImageOffsetY * ((unitViewCount - 1) - i));
                actor.setScale(scaleX, scaleY);
                ((GridUnitView) actor).sizeChanged();
                ((GridUnitView) actor).setScaledHeight(scaleY);
                ((GridUnitView) actor).setScaledWidth(scaleX);
                i++;
            }
        }

        if (graveyard != null) {
            graveyard.setZIndex(Integer.MAX_VALUE);
        }
    }

    @Override
    public void act(float delta) {
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
         (getSizeFactorPerView() * unitViewCount));
    }

    private int getSizeDiffX() {
        return Math.round(getWidth() /
         (getSizeFactorPerView() * unitViewCount));
    }

    private float getSizeFactorPerView() {
        return 2.4f;
    }

    public void addActor(Actor actor) {
        super.addActor(actor);
        if (actor instanceof GridUnitView) {
            unitViewCount++;
            recalcUnitViewBounds();
        }
    }

    public boolean removeActor(Actor actor) {
        boolean result = super.removeActor(actor);

        if (result && actor instanceof GridUnitView) {
            unitViewCount--;
            recalcUnitViewBounds();
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
                actor.setY(perImageOffsetY * ((unitViewCount - 1) - i++));
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

        final int xOffset = (int) (getWidth() / 3);
        final int yOffset = (int) (getHeight() / 3);
        overlays.forEach(view -> {
            Coordinates.DIRECTION direction = view.getDirection();
            int calcXOffset = 0;
            int calcYOffset = 0;
            if (direction == null) {
                calcXOffset += xOffset;
                calcYOffset += yOffset;
            } else {
                switch (direction) {
                    case UP:
                        calcXOffset += xOffset;
                        calcYOffset += yOffset * 2;
                        break;
                    case DOWN:
                        calcXOffset += xOffset;
                        break;
                    case LEFT:
                        calcYOffset += yOffset;
                        break;
                    case RIGHT:
                        calcXOffset += xOffset * 2;
                        calcYOffset += yOffset;
                        break;
                    case UP_LEFT:
                        calcYOffset += yOffset * 2;
                        break;
                    case UP_RIGHT:
                        calcXOffset += xOffset * 2;
                        calcYOffset += yOffset * 2;
                        break;
                    case DOWN_RIGHT:
                        calcXOffset += xOffset * 2;
                        break;
                    case DOWN_LEFT:
                        break;
                }
            }

            view.setBounds(calcXOffset, calcYOffset, xOffset, yOffset);
            addActor(view);
            overlayCount++;
        });
    }

    public void updateGraveyard() {
        graveyard.updateGraveyard();
    }

}

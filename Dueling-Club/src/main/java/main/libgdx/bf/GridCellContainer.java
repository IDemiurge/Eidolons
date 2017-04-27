package main.libgdx.bf;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import main.game.battlefield.Coordinates;

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
        return this;
    }

    public void setObjects(List<BaseView> objects) {
        objects.forEach(this::addActor);
    }

    private void recalcUnitViewBounds() {
        if (unitViewCount == 0) {
            return;
        }
        final int perImageOffsetX = ((int) getWidth()) / 2 / unitViewCount;
        final int perImageOffsetY = ((int) getHeight()) / 2 / unitViewCount;
        final int w = ((int) getWidth()) - perImageOffsetX * (unitViewCount - 1);
        final int h = ((int) getHeight()) - perImageOffsetY * (unitViewCount - 1);
        int i = 0;

        for (Actor actor : getChildren()) {
            if (actor instanceof GridUnitView) {
                actor.setBounds(
                        perImageOffsetX * i,
                        perImageOffsetY * ((unitViewCount - 1) - i),
                        w,
                        h
                );
                i++;
            }
        }

        if (graveyard != null) {
            graveyard.setZIndex(Integer.MAX_VALUE);
        }
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
        final int perImageOffsetX = ((int) getWidth()) / 2 / unitViewCount;
        final int perImageOffsetY = ((int) getHeight()) / 2 / unitViewCount;
        for (Actor actor : getChildren()) {
            if (actor instanceof GridUnitView) {
                actor.setX(perImageOffsetX * i);
                actor.setY(perImageOffsetY * ((unitViewCount - 1) - i++));
            }
        }
    }

    public void popupUnitView(BaseView uv) {
        super.removeActor(uv); //call super for only popup
        super.addActorAt(getChildren().size - overlayCount, uv);
        recalcImagesPos();
        graveyard.setZIndex(Integer.MAX_VALUE);
    }

/*    @Override
    public Actor hit(float x, float y, boolean touchable) {
        Vector2 v = new Vector2(x, y);
        v = getParent().parentToLocalCoordinates(v);
        return super.hitChilds(v.x, v.y, touchable);
    }*/

    public void setOverlays(List<OverlayView> overlays) {
        if (overlays.size() == 0) {
            return;
        }
//        final int xOffset = (int) (getW() *OverlayView.SCALE);
//        final int yOffset = (int) (getH() *OverlayView.SCALE);
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
}

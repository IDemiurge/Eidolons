package main.libgdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.List;

public class GridCellContainer extends GridCell {

    private final int maxW = 96;
    private final int maxH = 99;
    private final int offsetX = 18;
    private final int offsetY = 7;
    private int unitViewCount = 0;

    public GridCellContainer(Texture backTexture, String imagePath, int gridX, int gridY) {
        super(backTexture, imagePath, gridX, gridY);
    }

    public GridCellContainer(GridCell parent) {
        super(parent.backTexture, parent.imagePath, parent.gridX, parent.gridY);
    }

    @Override
    public GridCellContainer init() {
        super.init();
        return this;
    }

    public void setObjects(List<UnitViewOptions> objects) {
        for (UnitViewOptions object : objects) {
            UnitView im = new UnitView(object);
            addActor(im);
        }
    }

    private void recalcUnitViewBounds() {
        if (unitViewCount == 0) return;
        final int perImageOffsetX = ((int) getWidth()) / 2 / unitViewCount;
        final int perImageOffsetY = ((int) getHeight()) / 2 / unitViewCount;
        final int w = ((int) getWidth()) - perImageOffsetX * (unitViewCount - 1);
        final int h = ((int) getHeight()) - perImageOffsetY * (unitViewCount - 1);
        int i = 0;

        for (Actor actor : getChildren()) {
            if (actor instanceof UnitView) {
                actor.setX(perImageOffsetX * i);
                actor.setY(perImageOffsetY * ((unitViewCount - 1) - i));
                actor.setWidth(w);
                actor.setHeight(h);
                i++;
            }
        }
    }

    public void addActor(Actor actor) {
        super.addActor(actor);
        if (actor instanceof UnitView) {
            unitViewCount++;
            recalcUnitViewBounds();
        }
    }

    public boolean removeActor(Actor actor) {
        boolean result = super.removeActor(actor);
        if (actor instanceof UnitView) {
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
            if (actor instanceof UnitView) {
                actor.setX(perImageOffsetX * i);
                actor.setY(perImageOffsetY * ((unitViewCount - 1) - i++));
            }
        }
    }

    public void popupUnitView(UnitView uv){
        this.removeActor(uv);
        this.addActor(uv);
        recalcImagesPos();
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        Vector2 v = new Vector2(x, y);
        v = getParent().parentToLocalCoordinates(v);
        Actor a = super.hitChilds(v.x, v.y, touchable);
        return a != null ? a : null;
    }
}

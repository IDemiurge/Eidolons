package main.libgdx.bf;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import main.entity.obj.DC_HeroObj;
import main.game.DC_Game;
import main.game.battlefield.Coordinates;

import java.util.List;
import java.util.Map;

public class GridCellContainer extends GridCell {

    private final int maxW = 96;
    private final int maxH = 99;
    private final int offsetX = 18;
    private final int offsetY = 7;
    private int unitViewCount = 0;
    private int overlayCount = 0;

    public GridCellContainer(Texture backTexture, int gridX, int gridY) {
        super(backTexture, gridX, gridY);
    }

    public GridCellContainer(GridCell parent) {
        super(parent.backTexture, parent.getGridX(), parent.getGridY());
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

        if (result && actor instanceof UnitView) {
            unitViewCount--;
            recalcUnitViewBounds();
            if (unitViewCount <= 0) {
                ((GridCell) getParent()).addInnerDrawable(null);
            }
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

    public void popupUnitView(UnitView uv) {
        super.removeActor(uv); //call super for only popup
        super.addActorAt(getChildren().size - overlayCount, uv);
        recalcImagesPos();
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        Vector2 v = new Vector2(x, y);
        v = getParent().parentToLocalCoordinates(v);
        Actor a = super.hitChilds(v.x, v.y, touchable);
        return a != null ? a : null;
    }

    public void setOverlays(List<UnitViewOptions> overlays) {
        if (overlays.size() == 0) return;
        final int xOffset = getW() / 3;
        final int yOffset = getH() / 3;
        Map<Coordinates, Map<DC_HeroObj, Coordinates.DIRECTION>> directionMap = DC_Game.game.getDirectionMap();
        Map<DC_HeroObj, Coordinates.DIRECTION> heroObjDIRECTIONMap = directionMap.get(new Coordinates(getGridX(), getGridY()));
        overlays.forEach(unitViewOptions -> {
            Coordinates.DIRECTION direction = null;
            if (heroObjDIRECTIONMap != null) {
                direction = heroObjDIRECTIONMap.get(unitViewOptions.getObj());
            }
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

            OverlayView view = new OverlayView(unitViewOptions.getPortrateTexture());
            view.setBounds(calcXOffset, calcYOffset, xOffset, yOffset);
            view.setScale(.333f, .333f);
            addActor(view);
            overlayCount++;
        });
    }
}

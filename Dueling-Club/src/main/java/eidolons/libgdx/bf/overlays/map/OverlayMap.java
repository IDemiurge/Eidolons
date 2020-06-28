package eidolons.libgdx.bf.overlays.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.google.inject.internal.util.ImmutableList;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.grid.handlers.GridManager;
import eidolons.libgdx.screens.ScreenMaster;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.EventType;
import main.system.GuiEventManager;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.function.Function;

public abstract class OverlayMap extends SuperActor {

    protected ObjectMap<Coordinates, Object> map;
    protected Function<Coordinates, Color
                > colorFunc;
    protected boolean on = true;
    protected Array<Pair<Vector2, TextureRegion>> drawMap;
    protected ObjectMap<Vector2, TextureRegion> drawMapOver;

    private boolean screen;
    private ObjectMap<Coordinates, TextureRegion> drawMapAlt;

    public OverlayMap() {
        bindEvents();
    }

    protected void bindEvents() {
        GuiEventManager.bind(getUpdateEvent(), p -> {
            update((ObjectMap<Coordinates, Object>) p.get());

        });
    }

    public void update(ObjectMap<Coordinates, Object> m) {
        map = m;
        drawMap = null;
        drawMapOver = null;
        drawMapAlt=null;
    }


    public void drawAlt(Batch batch,  Vector2 v, TextureRegion r) {

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!on)
            return;
        if (!screen) {
            // if (isDrawScreen()) {
            //     screen = true;
            //     ((CustomSpriteBatch) batch).setBlending(GenericEnums.BLENDING.SCREEN);
            //     draw(batch, parentAlpha);
            //     screen = false;
            //     ((CustomSpriteBatch) batch).resetBlending();
            // }
            // batch.setColor(new Color(1, 1, 1, 1));
            if (map == null)
                return;
            if (drawMapOver == null) {
                drawMapOver = new ObjectMap<>(map.size * 2);
                for (Coordinates c : map.keys()) {
                    fillOverlayMap(c, drawMapOver);
                }
            }
            if (drawMap == null && drawMapAlt == null) {
                if (isAlt()) {
                    fillMapAlt();
                } else {
                    fillMap();
                }

            }
        }
        if (isCustomDraw()) {
             GridPanel grid = ScreenMaster.getGrid();
            for (int x = grid.drawX1; x < grid.drawX2; x++) {
                for (int y = grid.drawY1; y < grid.drawY2; y++) {
                    Coordinates c = Coordinates.get(x, y);
                    TextureRegion r = drawMapAlt.get(c);
                    if (r == null) {
                        continue;
                    }
                    initColor(c, screen, batch);
                    drawAlt(batch, getV(c), r);

                }

            }
            batch.setColor( GdxColorMaster.WHITE);
            return ;
        }
        for (Pair<Vector2, TextureRegion> pair : drawMap) {
            Vector2 v = pair.getKey();
            batch.draw(pair.getRight(), v.x, v.y);
        }
        for (Vector2 key : drawMapOver.keys()) {
            // if (!checkCoordinateIgnored(key))
            batch.draw(drawMapOver.get(key), key.x, key.y);
        }
    }

    protected boolean isAlt() {
        return false;
    }

    private void fillMapAlt() {
        drawMapAlt = new ObjectMap<>();
        for (Coordinates coordinates : map.keys()) {
            fillDrawMapAlt(drawMapAlt, coordinates, map.get(coordinates));
        }
    }

    private void fillMap() {
        drawMap = new Array<>(map.size * 2);
        for (Coordinates coordinates : map.keys()) {
            List<DIRECTION> list = null;
            if (map.get(coordinates) instanceof DIRECTION) {
                DIRECTION d = (DIRECTION) map.get(coordinates);
                list = ImmutableList.of(d);
            } else {
                list = (List<DIRECTION>) map.get(coordinates);
            }
            Vector2 v = getV(coordinates);
            fillDrawMap(drawMap, coordinates, list, v);
        }
    }

    private Vector2 getV(Coordinates coordinates) {
        Vector2 v = GridMaster.getVectorForCoordinate(coordinates, false, false, true,
                ScreenMaster.getGrid());
        v.set(v.x, v.y - 128);
        return v;
    }

    protected boolean isCustomDraw() {
        return GridManager.isCustomDraw();
    }

    private void initColor(Coordinates c, boolean screen, Batch batch) {
        if (colorFunc == null) {
            return;
        }
        Color color = colorFunc.apply(c);
        //can we store a function of color from time?
        if (color == null) {
            color = GdxColorMaster.WHITE;
        }
        batch.setColor( color);

    }

    public void setColorFunc(Function<Coordinates, Color> colorFunc) {
        this.colorFunc = colorFunc;
    }

    protected boolean isDrawScreen() {
        return false;
    }

    protected void fillOverlayMap(Coordinates c, ObjectMap<Vector2, TextureRegion> drawMapOver) {
    }

    protected abstract void fillDrawMap(Array<Pair<Vector2, TextureRegion>> batch, Coordinates coordinates, List<DIRECTION> list, Vector2 v);

    protected void fillDrawMapAlt(ObjectMap<Coordinates, TextureRegion> draw,
                                  Coordinates coordinates, Object arg) {

    }

    protected EventType getColorUpdateEvent() {
        return null;
    }

    protected abstract EventType getUpdateEvent();


    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    protected boolean checkCoordinateIgnored(Coordinates coordinates) {
        Vector2 v = GridMaster.getVectorForCoordinate(coordinates, false, false, true,
                ScreenMaster.getGrid());
        return checkCoordinateIgnored(v);
    }

    protected boolean checkCoordinateIgnored(Vector2 v) {
        float offsetX = v.x;
        float offsetY = v.y;
        return !ScreenMaster.getScreen().controller.
                isWithinCamera(offsetX, offsetY - 128, 128, 128);
    }

    public ObjectMap<Coordinates, TextureRegion> getDrawMapAlt() {
        return drawMapAlt;
    }

}

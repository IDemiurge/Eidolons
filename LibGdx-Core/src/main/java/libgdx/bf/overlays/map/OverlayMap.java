package libgdx.bf.overlays.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.google.inject.internal.util.ImmutableList;
import eidolons.game.battlecraft.logic.battlefield.vision.colormap.LightConsts;
import eidolons.content.consts.libgdx.GdxColorMaster;
import libgdx.bf.GridMaster;
import libgdx.bf.SuperActor;
import libgdx.bf.grid.GridPanel;
import libgdx.bf.grid.handlers.ColorHandler;
import libgdx.bf.grid.handlers.GridManager;
import libgdx.screens.batch.CustomSpriteBatch;
import libgdx.screens.handlers.ScreenMaster;
import main.content.enums.GenericEnums;
import main.game.bf.Coordinates;
import main.system.EventType;
import main.system.GuiEventManager;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Function;

public abstract class OverlayMap<T> extends SuperActor {

    protected Map<Coordinates, Object> map;
    protected Function<Coordinates, Color> colorFunc;
    protected Map<Coordinates, Color> colorCache = new HashMap<>();
    protected List<Pair<Vector2, TextureRegion>> drawMap;
    protected Map<Vector2, TextureRegion> drawMapOver;

    protected boolean screen;
    protected Map<Coordinates, TextureRegion> drawMapAlt;
    private boolean resetRequired;

    public OverlayMap() {
        bindEvents();
    }

    protected void bindEvents() {
        GuiEventManager.bind(getUpdateEvent(), p -> {
            update((Map<Coordinates, Object>) p.get());

        });
    }

    public void update(Map<Coordinates, Object> m) {
        map = m;
        // colorCache.replaceAll(c-> map.containsKey(c) ? null : colorCache.get(c));
        colorCache.keySet().removeAll(m.keySet());
        resetRequired = true;
    }


    public void drawAlt(Batch batch, Coordinates v, TextureRegion r) {

    }

    public void drawScreen(Batch batch) {
        screen = true;
        ((CustomSpriteBatch) batch).setBlending(GenericEnums.BLENDING.SCREEN);
        draw(batch, 1);
        screen = false;
        ((CustomSpriteBatch) batch).resetBlending();
        // batch.setColor(new Color(1, 1, 1, 1));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!isOn())
            return;
        if (!screen) {
            if (map == null)
                return;
            if (resetRequired) {
                drawMapOver = new HashMap<>();
                for (Coordinates c : map.keySet()) {
                    fillOverlayMap(c, drawMapOver);
                }
                if (isAlt()) {
                    fillMapAlt();
                } else {
                    fillMap();
                }
                resetRequired = false;
            }
        }
        if (drawMap != null)
            for (Pair<Vector2, TextureRegion> pair : drawMap) {
                Vector2 v = pair.getKey();
                batch.draw(pair.getRight(), v.x, v.y);
            }
        if (drawMapOver != null)
            for (Vector2 key : drawMapOver.keySet()) {
                // if (!checkCoordinateIgnored(key))
                batch.draw(drawMapOver.get(key), key.x, key.y);
            }
        if (drawMapAlt != null) {
            GridPanel grid = ScreenMaster.getGrid();
            for (Coordinates c : drawMapAlt.keySet()) {
                if (!grid.isDrawn(c)) {
                    continue;
                }
                TextureRegion r = drawMapAlt.get(c);
                if (initColor(c, screen, batch))
                    drawAlt(batch, c, r);
            }

            batch.setColor(GdxColorMaster.WHITE);

        }
    }

    protected abstract boolean isOn();


    protected boolean isAlt() {
        return false;
    }

    protected void fillMapAlt() {
        drawMapAlt = new LinkedHashMap<>();
        //sort it for Z!

        for (Coordinates coordinates : map.keySet()) {
            Object o = map.get(coordinates);
            if (isUnder(coordinates, o))
                fillDrawMapAlt(drawMapAlt, coordinates, o);
        }
        for (Coordinates coordinates : map.keySet()) {
            Object o = map.get(coordinates);
            if (!isUnder(coordinates, o))
                fillDrawMapAlt(drawMapAlt, coordinates, o);
        }
    }

    protected boolean isUnder(Coordinates coordinates, Object o) {
        return false;
    }

    protected void fillMap() {
        drawMap = new LinkedList<>();
        for (Coordinates coordinates : map.keySet()) {
            List<T> list;
            if (map.get(coordinates) != null) //TODO was supposed to check more..
            {
                T d = (T) map.get(coordinates);
                list = ImmutableList.of(d);
            } else {
                list = (List<T>) map.get(coordinates);
            }
            Vector2 v = getV(coordinates, map.get(coordinates));
            fillDrawMap(drawMap, coordinates, list, v);
        }
    }

    protected Vector2 getV(Coordinates coordinates, Object o) {
        Vector2 v = GridMaster.getVectorForCoordinate(coordinates, false, false, true,
                ScreenMaster.getGrid());
        v.set(v.x, v.y - 128);
        return v;
    }

    protected boolean isCustomDraw() {
        return GridManager.isCustomDraw();
    }

    protected boolean initColor(Coordinates c, boolean screen, Batch batch) {
        if (colorFunc == null) {
            return true;
        }
        Color color = null;
        if (ColorHandler.isStaticColors()) {
            color = colorCache.get(c);
        }
        if (colorCache == null) {
            color = colorFunc.apply(c);
            if (ColorHandler.isStaticColors()) {
                colorCache.put(c, color);
            }
        }

        //can we store a function of color from time?
        if (color == null) {
            color = GdxColorMaster.WHITE;
        } else if (!screen)
            color = new Color(color.r, color.g, color.b, 1);
        else {
            color.a = LightConsts.getScreen(color.a);
            if (color.a <= 0)
                return false;
        }
        batch.setColor(color);
        return true;

    }

    public void setColorFunc(Function<Coordinates, Color> colorFunc) {
        this.colorFunc = colorFunc;
    }

    protected boolean isDrawScreen() {
        return false;
    }

    protected void fillOverlayMap(Coordinates c, Map<Vector2, TextureRegion> drawMapOver) {
    }

    protected abstract void fillDrawMap(List<Pair<Vector2, TextureRegion>> batch, Coordinates coordinates, List<T> list, Vector2 v);

    protected void fillDrawMapAlt(Map<Coordinates, TextureRegion> draw,
                                  Coordinates coordinates, Object arg) {

    }

    protected EventType getColorUpdateEvent() {
        return null;
    }

    protected abstract EventType getUpdateEvent();


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

}

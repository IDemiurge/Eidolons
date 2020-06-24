package eidolons.libgdx.bf.overlays;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.screens.ScreenMaster;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.EventType;
import main.system.GuiEventManager;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public abstract class OverlayMap extends SuperActor {

    protected ObjectMap<Coordinates, List<DIRECTION>> map;
    protected boolean on = true;
    protected Array<Pair<Vector2, TextureRegion>> drawMap;
    protected ObjectMap<Vector2, TextureRegion> drawMapOver;

    public OverlayMap() {
        bindEvents();
    }

    protected void bindEvents() {
        GuiEventManager.bind(getUpdateEvent(), p -> {
            map =
                    (ObjectMap<Coordinates, List<DIRECTION>>) p.get();
            drawMap = null;
            drawMapOver = null;
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!on)
            return;
        batch.setColor(new Color(1, 1, 1, 1));
        if (map == null)
            return;
        if (drawMapOver == null) {
            drawMapOver = new ObjectMap<>(map.size * 2);
            for (Coordinates c : map.keys()) {
                fillOverlayMap(c, drawMapOver);
            }
        }
        if (drawMap == null) {
            drawMap = new Array<>(map.size * 2);
            for (Coordinates coordinates : map.keys()) {
                List<DIRECTION> list = map.get(coordinates);
                Vector2 v = GridMaster.getVectorForCoordinate(coordinates, false, false, true,
                        ScreenMaster.getGrid());
                v.set(v.x, v.y - 128);
                fillDrawMap(batch, coordinates, list, v);
            }
        }
        for (Pair<Vector2, TextureRegion> pair : drawMap) {
            //offset?
            // if (!checkCoordinateIgnored(key))
            Vector2 v = pair.getKey();
            batch.draw(pair.getRight(), v.x, v.y);
        }
        for (Vector2 key : drawMapOver.keys()) {
            // if (!checkCoordinateIgnored(key))
            batch.draw(drawMapOver.get(key), key.x, key.y);
        }

    }

    protected void fillOverlayMap(Coordinates c, ObjectMap<Vector2, TextureRegion> drawMapOver) {
    }

    protected abstract void fillDrawMap(Batch batch, Coordinates coordinates, List<DIRECTION> list, Vector2 v);


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
}

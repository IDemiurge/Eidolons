package eidolons.libgdx.bf.overlays.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.EventType;
import main.system.GuiEventType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class BorderMap extends OverlayMap<DIRECTION> {

    public static boolean on;
    public boolean isOn() {
        return on;
    }
    @Override
    protected void fillDrawMap(List<Pair<Vector2, TextureRegion>> batch, Coordinates coordinates, List<DIRECTION> list, Vector2 v) {
        for (DIRECTION d : list) {
        float x = v.x;
        float y = v.y;
        //for each direction, just draw a tiled line
        //what about joints? Ah, so here it gets a bit more interesting. Second map?

        //ideally, we'd do flip/rotate too
        TextureRegion region=getRegion(d);

        }
    }

    private TextureRegion getRegion(DIRECTION d) {
        // TextureCache.getOrCreateR()
        return null;
    }

    @Override
    protected EventType getUpdateEvent() {
        return GuiEventType.BORDER_MAP_UPDATE;
    }
}

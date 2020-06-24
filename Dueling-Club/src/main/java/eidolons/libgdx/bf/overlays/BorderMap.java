package eidolons.libgdx.bf.overlays;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.EventType;
import main.system.GuiEventType;

import java.util.List;

public class BorderMap extends OverlayMap {

    @Override
    protected void fillDrawMap(Batch batch, Coordinates coordinates, List<DIRECTION> list, Vector2 v) {
        for (DIRECTION d : list) {
        float x = v.x;
        float y = v.y;
        //for each direction, just draw a tiled line
        //what about joints? Ah, so here it gets a bit more interesting. Second map?

        //ideally, we'd do flip/rotate too
        TextureRegion region=getRegion(d);
        batch.draw(region, x, y);

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

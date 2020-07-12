package eidolons.libgdx.bf.overlays.map;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.decor.wall.WallMaster;
import eidolons.libgdx.screens.ScreenMaster;
import eidolons.libgdx.texture.TextureCache;
import main.game.bf.Coordinates;
import main.system.EventType;
import main.system.GuiEventType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

import static eidolons.libgdx.bf.decor.pillar.Pillars.PILLAR;
import static eidolons.libgdx.bf.decor.pillar.Pillars.getOffset;

public class PillarMap extends OverlayMap<PILLAR> {
    private final ObjectMap<TextureRegion, PILLAR> invertCache = new ObjectMap<>();
    //so do we have cases with 2+ pillars per cell? a cell standing alone in the void..
    boolean wall;
    public static boolean on = true;

    public boolean isOn() {
        return on;
    }

    public PillarMap(boolean wall) {
        this.wall = wall;
    }

    @Override
    protected boolean isAlt() {
        return true;
    }

    @Override
    public void drawAlt(Batch batch, Coordinates c, TextureRegion r) {
        Vector2 v = getV(c, map.get(c));
        PILLAR pillar = invertCache.get(r);
        Vector2 v1 = getOffset(pillar);
        v.add(v1);
        batch.draw(r, v.x, v.y);
    }


    protected boolean isUnder(Coordinates coordinates, Object o) {
        return o == PILLAR.SKEWED_CORNER || o == PILLAR.SKEWED_CORNER_LEFT || o == PILLAR.SKEWED_CORNER_UP;
    }

    @Override
    protected void fillDrawMapAlt(Map<Coordinates, TextureRegion> draw,
                                  Coordinates c, Object arg) {
        PILLAR pillar = (PILLAR) arg;
        draw.put(c, getRegion(c, pillar));
    }

    @Override
    protected Vector2 getV(Coordinates coordinates, Object o) {
        Vector2 v = GridMaster.getVectorForCoordinate(coordinates, false, false, true,
                ScreenMaster.getGrid());
        PILLAR d = null;
        if (o instanceof PILLAR) {
            d = (PILLAR) o;
        }
        if (wall && (d != PILLAR.DOWN)) {
            v.set(v.x + WallMap.getOffsetX(), v.y - 128 + WallMap.getOffsetY());
        } else if (d == PILLAR.SINGLE)
            v.set(v.x, v.y - 127);
        else
            v.set(v.x, v.y - 128);
        return v;
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    protected void fillDrawMap(List<Pair<Vector2, TextureRegion>> batch, Coordinates coordinates, List<PILLAR> list, Vector2 v) {

    }


    private TextureRegion getRegion(Coordinates c, PILLAR pillar) {
        String pillarPath = WallMaster.getPillarImage(c, pillar, wall);
        TextureRegion region = TextureCache.getOrCreateR(pillarPath);
        invertCache.put(region, pillar);
        return region;
    }

    @Override
    protected EventType getColorUpdateEvent() {
        return GuiEventType.PILLAR_COLOR_MAP_UPDATE;
    }

    @Override
    protected EventType getUpdateEvent() {
        return wall ? GuiEventType.WALL_PILLAR_MAP_UPDATE : GuiEventType.PILLAR_MAP_UPDATE;
    }
}

package eidolons.libgdx.bf.overlays.map;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.decor.pillar.Pillars;
import eidolons.libgdx.screens.ScreenMaster;
import eidolons.libgdx.texture.TextureCache;
import main.content.enums.DungeonEnums;
import main.data.filesys.PathFinder;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.EventType;
import main.system.GuiEventType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public class PillarMap extends OverlayMap {
    private final ObjectMap<TextureRegion, DIRECTION> invertCache = new ObjectMap<>();
    //so do we have cases with 2+ pillars per cell? a cell standing alone in the void..
    boolean wall;
    public static boolean on=true;
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
        DIRECTION direction = invertCache.get(r);
        checkDrawFix(batch, c);
        Vector2 v1 = Pillars.getOffset(direction);
        v.add(v1);
        batch.draw(r, v.x, v.y);
    }

    private void checkDrawFix(Batch batch, Coordinates key) {
        Vector2 v = getV(key, null);
        if (map.get(key) == Pillars.getCorner(true)) {
            TextureRegion region;
            region = TextureCache.getOrCreateR( //Images.PILLAR_FIX);
                    Pillars.getPillarPath(getType(key), Pillars.getPillarD(Pillars.PILLAR.VERT)));
            if (key.getAdjacentCoordinate(DIRECTION.UP) != null)
                if (map.get(key.getAdjacentCoordinate(DIRECTION.UP)) != null) {
                    float x = 118;
                    float y = Pillars.size;
                    batch.draw(region, v.x + x, v.y + y);
                }
            if (key.getAdjacentCoordinate(DIRECTION.LEFT) != null)
                if (map.get(key.getAdjacentCoordinate(DIRECTION.LEFT)) != null) {
                    float x = 0;
                    float y = -Pillars.size + 3;
                    region = TextureCache.getOrCreateR( //Images.PILLAR_FIX);
                            Pillars.getPillarPath(getType(key), Pillars.getPillarD(Pillars.PILLAR.HOR)));
                    batch.draw(region, v.x + x, v.y + y);
                }
        }
    }

    protected boolean isUnder(Coordinates coordinates, Object o) {
        return o == Pillars.getCorner(true);
    }

    @Override
    protected void fillDrawMapAlt(Map<Coordinates, TextureRegion> draw,
                                  Coordinates c, Object arg) {
        DIRECTION direction = (DIRECTION) arg;
        draw.put(c, getRegion(getType(c), direction));
    }

    private DungeonEnums.PILLAR_TYPE getType(Coordinates c) {
      return Pillars.getType (wall, c);
    }

    @Override
    protected Vector2 getV(Coordinates coordinates, Object o) {
        Vector2 v = GridMaster.getVectorForCoordinate(coordinates, false, false, true,
                ScreenMaster.getGrid());
        DIRECTION d = null;
        if (o instanceof DIRECTION) {
            d = (DIRECTION) o;
        }
        if (wall && (d != DIRECTION.DOWN_RIGHT)) {
            v.set(v.x+ WallMap.getOffsetX(), v.y - 128 + WallMap.getOffsetY());
        } else
            v.set(v.x, v.y - 128);
        return v;
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    protected void fillDrawMap(List<Pair<Vector2, TextureRegion>> batch, Coordinates coordinates, List<DIRECTION> list, Vector2 v) {

    }


    private TextureRegion getRegion(DungeonEnums.PILLAR_TYPE type, DIRECTION direction) {
        String pillarPath = Pillars.getPillarPath(type, direction);
        if (!new FileHandle(PathFinder.getImagePath()+ pillarPath).exists()) {
            pillarPath.trim();
        }
        TextureRegion region = TextureCache.getOrCreateR(pillarPath);
        invertCache.put(region, direction);
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

package eidolons.libgdx.bf.overlays.map;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import eidolons.entity.obj.DC_Cell;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.bf.decor.pillar.Pillars;
import eidolons.libgdx.texture.TextureCache;
import main.content.enums.DungeonEnums;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.EventType;
import main.system.GuiEventType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class PillarMap extends OverlayMap {
    private final ObjectMap<TextureRegion, DIRECTION> invertCache= new ObjectMap<>();
    //so do we have cases with 2+ pillars per cell? a cell standing alone in the void..


    public PillarMap() {
    }

    @Override
    protected boolean isAlt() {
        return true;
    }

    public ObjectMap<TextureRegion, DIRECTION> getInvertCache() {
        return invertCache;
    }

    @Override
    protected void fillDrawMapAlt(ObjectMap<Coordinates, TextureRegion> draw,
                                  Coordinates c, Object arg ) {
        DIRECTION direction = (DIRECTION) arg;
        DC_Cell cell = DC_Game.game.getCellByCoordinate(c);
        TextureRegion region;
        draw.put(c,region= getRegion(
                cell.getCellType(),
                direction));

    }
    @Override
    public void drawAlt(Batch batch,   Vector2 v, TextureRegion r) {
        DIRECTION direction = invertCache.get(r);
        Vector2 v1 = Pillars.getOffset(direction);
        v.add(v1);
        batch.draw(r, v.x, v.y);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    protected void fillDrawMap(Array<Pair<Vector2, TextureRegion>> batch, Coordinates coordinates, List<DIRECTION> list, Vector2 v) {

    }


    private TextureRegion getRegion(DungeonEnums.CELL_IMAGE cellType, DIRECTION direction) {
        TextureRegion region = TextureCache.getOrCreateR(Pillars.getPillarPath(cellType, direction));
        invertCache.put(region, direction);
        return region;
    }

    @Override
    protected EventType getColorUpdateEvent() {
        return GuiEventType.PILLAR_COLOR_MAP_UPDATE;
    }

    @Override
    protected EventType getUpdateEvent() {
        return GuiEventType.PILLAR_MAP_UPDATE;
    }
}

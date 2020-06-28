package eidolons.libgdx.bf.decor.pillar;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ObjectMap;
import eidolons.entity.obj.DC_Cell;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.grid.cell.GridCellContainer;
import eidolons.libgdx.bf.grid.handlers.GridHandler;
import eidolons.libgdx.bf.light.ShadowMap;
import eidolons.libgdx.texture.TextureCache;
import main.content.enums.DungeonEnums;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.*;

import static eidolons.libgdx.bf.decor.pillar.Pillars.*;
import static main.game.bf.directions.DIRECTION.*;

public class PillarManager extends GridHandler {

    ObjectMap<Coordinates, Set<DIRECTION>> voidAdjacency;
    ObjectMap<Coordinates, Set<DIRECTION>> bordersMap;
    ObjectMap<Coordinates, DIRECTION> shardMap; //plain in addition to default shards? which now must be RARE...
    ObjectMap<Coordinates, Object> pillarMap;

    public PillarManager(GridPanel panel) {
        super(panel);
    }

    public Color getColor(Coordinates coord) {
        Color c = getManager().getColor(coord);
        float a = getManager().getLightness(coord);
        DC_Cell cell = DC_Game.game.getCellByCoordinate(coord);
        a = a * a * 5;
        a = Math.max(0.2f, a);
        return new Color(c.r * a, c.g * a, c.b * a, 1).mul( ShadowMap.getLightColor(cell) );

        // float a = 1 - GammaMaster.getGammaForPillar(cell.getGamma());
        // return new Color(c.r - a, c.g - a, c.b - a, 1);
    }

    @Override
    protected void bindEvents() {
        GuiEventManager.bind(GuiEventType.CELL_MAP_RESET, p -> reset());
    }


    public void updateVisuals() {
        grid.getPillars().update(pillarMap);
        // GuiEventManager.trigger(GuiEventType.BORDER_MAP_UPDATE, bordersMap);
    }

    public void reset() {
        firstPass();
        secondPass();
        thirdPass();
        updateVisuals();
        grid.getPillars().setColorFunc(c -> getColor(c));
    }


    private void firstPass() {
        voidAdjacency = new ObjectMap<>();
        for (int x = grid.getX1(); x < grid.getX2(); x++) {
            for (int y = grid.getY1(); y < grid.getY2(); y++) {
                Coordinates c = Coordinates.get(x, y);
                processAdjacency(c);
            }
        }
        return;
    }

    private void secondPass() {
        pillarMap = new ObjectMap<>();
        for (Coordinates c : voidAdjacency.keys()) {
            Set<DIRECTION> set = voidAdjacency.get(c);
            Object d = getPillarForCell(set);
            if (d != NO_PILLAR) {
                pillarMap.put(c, d);
            }

            // shardMap
        }
    }

    private void thirdPass() {
        shardMap = new ObjectMap<>();
    }

    private void processAdjacency(Coordinates c) {
        if (grid.isVoid(c)) {
            return;
        }
        Set<DIRECTION> empty = new LinkedHashSet<>();
        Set<DIRECTION> borders = new LinkedHashSet<>();
        for (DIRECTION direction : clockwise) {
            Coordinates c1 = c.getAdjacentCoordinate(direction);
            if (c1 == null || grid.isVoid(c1.x, c1.y)) {
                empty.add(direction);
                // if (!direction.isDiagonal()) {
                //     borders.add(direction);
                //perhaps we will want some CORNER overlays sometime
                // }
            }
        }
        if (!empty.isEmpty()) {
            voidAdjacency.put(c, empty);
        }
        // bordersMap.put(c, borders);
    }

    public void updateDynamicPillars(Coordinates c1) {
        List<Coordinates> list = new ArrayList<>(Arrays.asList(c1.getAdjacent()));
        list.add(c1);
        for (Coordinates c : list) {
            processAdjacency(c);
        }
        for (Coordinates c : list) {
            Set<DIRECTION> voidAdjacent = voidAdjacency.get(c);
            if (voidAdjacent != null) { //TODO might need to create it!
                GridCellContainer container = grid.getCells()[c.x][c.y];
                Object d = getPillarForCell(voidAdjacent);
                if (d == NO_PILLAR) {
                    container.removePillar();
                    continue;
                }
                DIRECTION direction = (DIRECTION) d;
                DungeonEnums.CELL_IMAGE type = grid.getCell(c.x, c.y).getCellType();
                if (!isPillarType(type)){
                    type = DEFAULT_PILLAR;
                }
                TextureRegion region = TextureCache.getOrCreateR(getPillarPath(type, direction));
                Vector2 pillarV = getOffset(direction);
                FadeImageContainer pillar = new FadeImageContainer(new Image(region));
                pillar.setPosition(pillarV.x, pillarV.y);
                // pillar.setColor(getColor(c)); //will already have done it via Grid_cell?
                container.addPillar(pillar);
            }
        }
    }

    private boolean isPillarType(DungeonEnums.CELL_IMAGE type) {
        if (type== DungeonEnums.CELL_IMAGE.bare) return true;
        if (type== DungeonEnums.CELL_IMAGE.mossy) return true;
        return type == DungeonEnums.CELL_IMAGE.iron;
    }

    public Object getPillarForCell(Set<DIRECTION> voidAdjacent) {
        Set<DIRECTION> inverted = new LinkedHashSet<>();
        for (DIRECTION direction : clockwise) {
            if (!voidAdjacent.contains(direction)) {
                inverted.add(direction);
            }
        }
        return getPillar(inverted);
    }


    private boolean checkSingle(Set<DIRECTION> cellsAdjacent, boolean vert) {

        if (cellsAdjacent.size() == 7) {
            //revamp!
            return !cellsAdjacent.contains((vert ? prefHor : prefVert));
        }
        return false;
    }


    public Object getPillar(Set<DIRECTION> adj) {
        //check straight pillar no skew
        // if (checkSingle(adj, true)) {
        //     return getPillarDIRECTION(false, false );
        // }
        // if (checkSingle(adj, false)) {
        //     return getPillarDIRECTION(false, prefHor != DIRECTION.RIGHT);
        // }
        //CORNER

        if (!adj.contains(prefVert)) {
            if (!adj.contains(prefHor)) {

                //special CORNER cases
                if (adj.contains(UP) && adj.contains(UP_LEFT) && adj.contains(LEFT))
                    if (!adj.contains(UP_RIGHT) && !adj.contains(DOWN_LEFT))
                        return getCorner(false);
                if (!adj.contains(UP)) {
                    if (adj.contains(DOWN_LEFT)) {
                        return getDefault();
                    }
                    if (adj.contains(LEFT)) {
                        return getPillarDIRECTION(false, false);
                    }
                } else {
                    boolean skewed = adj.contains(UP_LEFT) || adj.contains(UP_RIGHT);
                    // if (adj.contains(DIRECTION.LEFT) || adj.contains(DIRECTION.UP)) {
                    //     skewed=false;
                    // }
                    return getCorner(skewed);
                }
            }
        }

        if (!adj.contains(prefVert)) {
            if (adj.contains(RIGHT) && adj.contains(LEFT)) {
                if (!adj.contains(DOWN_LEFT))
                    return getPillarDIRECTION(false, null);
            }
            if (adj.contains(RIGHT)) {
                // if (adj.contains(DIRECTION.DOWN_RIGHT))
                //     return getPillarDIRECTION(false, false);
                return getPillarDIRECTION(false, true);
            }
            if (adj.contains(LEFT)) {
                if (adj.contains(DOWN_LEFT))
                    return getPillarDIRECTION(false, false);
                return getPillarDIRECTION(false, false);
            }
            return getDefault();
        }


        if (!adj.contains(prefHor)) {
            if (adj.contains(UP) && adj.contains(DOWN)) {
                return getPillarDIRECTION(true, null);
            }
            if (adj.contains(UP))
                return getPillarDIRECTION(true, true);
            if (adj.contains(DOWN))
                return getPillarDIRECTION(true, false);

            return getPillarDIRECTION(true, prefHor == RIGHT);
        }
        return NO_PILLAR;
    }

    public ObjectMap<Coordinates, DIRECTION> getShardMap() {
        return shardMap;
    }
}

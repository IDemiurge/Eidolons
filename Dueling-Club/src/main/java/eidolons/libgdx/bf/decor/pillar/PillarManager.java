package eidolons.libgdx.bf.decor.pillar;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ObjectMap;
import eidolons.game.battlecraft.logic.battlefield.vision.colormap.LightHandler;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.grid.cell.GridCellContainer;
import eidolons.libgdx.bf.grid.handlers.GridHandler;
import eidolons.libgdx.texture.TextureCache;
import main.content.enums.DungeonEnums;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;

import java.util.*;
import java.util.stream.Collectors;

import static eidolons.libgdx.bf.decor.pillar.Pillars.*;
import static main.game.bf.directions.DIRECTION.*;

public class PillarManager extends GridHandler {

    Map<Coordinates, Set<DIRECTION>> voidAdjacency;
    Map<Coordinates, Set<DIRECTION>> wallAdjacency;
    Map<Coordinates, Set<DIRECTION>> bordersMap;
    Map<Coordinates, DIRECTION> shardMap; //plain in addition to default shards? which now must be RARE...
    Map<Coordinates, Object> pillarMap;
    Map<Coordinates, Object> wallPillarMap;

    public PillarManager(GridPanel panel) {
        super(panel);
    }
// Light revamp - get average between some adjacent cells ...
    public Color getColor(Coordinates coord, Object o, boolean wall) {
        PILLAR type = Pillars.getPillar(o);
        DIRECTION[] adj = Pillars.getAdjacent(type, wall);
        Set<Color> collect = Arrays.stream(adj).map(d -> getManager().getColor(d==null ? coord : coord.getAdjacentCoordinate(d))).collect(Collectors.toSet());

        Color lerp = GdxColorMaster.getAverage(collect);

        // Color lerp = new Color(c).lerp(c1, wall
        //         ?1- LightConsts.PILLAR_COLOR_LERP
        //         : LightConsts.PILLAR_COLOR_LERP);
        //
        // lerp.a = Math.max(LightConsts.MIN_LIGHTNESS,
        //         wall ? lerp.a * LightConsts.PILLAR_WALL_COEF_LIGHT
        //                 : lerp.a * LightConsts.PILLAR_COEF_LIGHT);
        Color c1 =LightHandler.applyLightnessToColor(lerp.clamp());
        c1.a=1;
        return c1;
    }

    @Override
    protected void bindEvents() {
    }


    public void updateVisuals() {
        grid.getPillars().update(pillarMap);
        grid.getWallPillars().update(wallPillarMap);
        // GuiEventManager.trigger(GuiEventType.BORDER_MAP_UPDATE, bordersMap);
    }

    public void reset() {
        firstPass();
        pillarMap = secondPass(voidAdjacency, false);
        wallPillarMap = secondPass(wallAdjacency, true);
        thirdPass();
        updateVisuals();
        grid.getPillars().setColorFunc(c -> getColor(c, pillarMap.get(c), false));
        grid.getWallPillars().setColorFunc(c -> getColor(c, pillarMap.get(c), true));
    }

    private void firstPass() {
        voidAdjacency = new HashMap<>();
        for (int x = grid.getX1(); x < grid.getX2(); x++) {
            for (int y = grid.getY1(); y < grid.getY2(); y++) {
                Coordinates c = Coordinates.get(x, y);
                processAdjacency(c);
            }
        }
        DC_Game.game.getBattleFieldManager().resetWalls();
        ObjectMap<Coordinates, List<DIRECTION>> wallMap = DC_Game.game.getBattleFieldManager().getWallMap();
        wallAdjacency = new HashMap<>();
        // for (Coordinates key : wallMap.keys()) {
        //     Set<DIRECTION> set = new LinkedHashSet<>(Arrays.asList(clockwise));
        //     set.removeIf(d -> wallMap.containsKey(key.getAdjacentCoordinate(d))); //real void too?
        //     wallAdjacency.put(key, set);
        // }
        Boolean[][] wallCache = DC_Game.game.getGrid().getWallCache();
        for (int x = 0; x < wallCache.length; x++) {
            for (int y = 0; y < wallCache[0].length; y++) {
                if (wallCache[x][y] == Boolean.TRUE) {
                    Coordinates key = Coordinates.get(x, y);
                    if (!wallMap.containsKey(key)) {
                        wallAdjacency.put(key, new LinkedHashSet<>());
                        continue;
                    }
                    Set<DIRECTION> set = new LinkedHashSet<>(Arrays.asList(clockwise));
                    set.removeIf(d -> wallMap.containsKey(key.getAdjacentCoordinate(d))); //real void too?
                    wallAdjacency.put(key, set);
                }

            }
        }

        return;
    }

    private Map<Coordinates, Object> secondPass(Map<Coordinates, Set<DIRECTION>> voidAdjacency, boolean wall) {
        Map pillarMap = new HashMap<>();
        for (Coordinates c : voidAdjacency.keySet()) {
            Set<DIRECTION> set = voidAdjacency.get(c);
            Object d = getPillarForCell(set, wall);
            if (d != NO_PILLAR) {
                pillarMap.put(c, d);
            }
            // Set<DIRECTION> borders = new LinkedHashSet<>();
            // if (!direction.isDiagonal()) {
            //     borders.add(direction);
            //perhaps we will want some CORNER overlays sometime
            // }
            // bordersMap.put(c, borders);
            // shardMap
        }
        return pillarMap;
    }

    private void thirdPass() {
        shardMap = new HashMap<>();

        for (Coordinates coordinates : pillarMap.keySet()) {
            Object o = pillarMap.get(coordinates);
            if (o instanceof DIRECTION) {
                shardMap.put(coordinates, (DIRECTION) o);
            }

        }
    }

    private void processAdjacency(Coordinates c) {
        if (grid.isVoid(c)) {
            return;
        }
        Set<DIRECTION> empty = new LinkedHashSet<>();
        for (DIRECTION direction : clockwise) {
            Coordinates c1 = c.getAdjacentCoordinate(direction);
            if (c1 == null || grid.isVoid(c1.x, c1.y)) {
                empty.add(direction);
            }
        }
        if (!empty.isEmpty()) {
            voidAdjacency.put(c, empty);
        }
    }

    public void updateDynamicPillars(Coordinates c1, boolean wall) {
        List<Coordinates> list = new ArrayList<>(Arrays.asList(c1.getAdjacent()));
        list.add(c1);
        for (Coordinates c : list) {
            processAdjacency(c);
        }
        for (Coordinates c : list) {
            Set<DIRECTION> voidAdjacent = voidAdjacency.get(c);
            if (voidAdjacent != null) { //TODO might need to create it!
                GridCellContainer container = grid.getCells()[c.x][c.y];
                Object d = getPillarForCell(voidAdjacent, wall);
                if (d == NO_PILLAR) {
                    container.removePillar();
                    continue;
                }
                DIRECTION direction = (DIRECTION) d;
                // DungeonEnums.CELL_IMAGE type = grid.getCell(c.x, c.y).getCellType();
                // if (!isPillarType(type)) {
                //     type = DEFAULT_PILLAR;
                // }
                TextureRegion region = TextureCache.getOrCreateR(getPillarPath(getType(false, c), direction));
                Vector2 pillarV = getOffset(direction);
                FadeImageContainer pillar = new FadeImageContainer(new Image(region));
                pillar.setPosition(pillarV.x, pillarV.y);
                // pillar.setColor(getColor(c)); //will already have done it via Grid_cell?
                container.addPillar(pillar);
            }
        }
    }

    private boolean isPillarType(DungeonEnums.CELL_IMAGE type) {
        if (type == DungeonEnums.CELL_IMAGE.bare) return true;
        if (type == DungeonEnums.CELL_IMAGE.mossy) return true;
        return type == DungeonEnums.CELL_IMAGE.iron;
    }

    public Object getPillarForCell(Set<DIRECTION> voidAdjacent, boolean wall) {
        Set<DIRECTION> inverted = new LinkedHashSet<>();
        for (DIRECTION direction : clockwise) {
            if (!voidAdjacent.contains(direction)) {
                inverted.add(direction);
            }
        }
        return getPillar(inverted, wall);
    }


    public Object getPillar(Set<DIRECTION> adj, boolean wall) {
        if (adj.size() == 8) {
            return getDefault(wall);
        }
        //CORNER
        if (!adj.contains(prefVert)) {
            if (!adj.contains(prefHor)) {

                //special CORNER cases
                if (adj.contains(UP) && adj.contains(UP_LEFT) && adj.contains(LEFT))
                    if (!adj.contains(UP_RIGHT) && !adj.contains(DOWN_LEFT))
                        return getCorner(false);
                if (!adj.contains(UP)) {
                    if (adj.contains(DOWN_LEFT)) {
                        return getDefault(wall);
                    }
                    if (wall)
                        return getDefault(true);
                    if (adj.contains(LEFT)) {
                        return getPillarDIRECTION(false, false);
                    }
                } else {
                    boolean skewed = adj.contains(UP_LEFT) || adj.contains(UP_RIGHT) || adj.contains(UP);
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

            return getDefault(wall);
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

    public Map<Coordinates, DIRECTION> getShardMap() {
        return shardMap;
    }
}

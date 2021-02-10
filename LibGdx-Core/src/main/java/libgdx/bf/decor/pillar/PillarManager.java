package libgdx.bf.decor.pillar;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import eidolons.content.consts.VisualEnums;
import eidolons.content.consts.libgdx.WallMaster;
import eidolons.game.battlecraft.logic.battlefield.vision.colormap.LightHandler;
import eidolons.game.core.game.DC_Game;
import libgdx.GdxColorMaster;
import libgdx.bf.decor.wall.WallMaster;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.bf.grid.GridPanel;
import libgdx.bf.grid.cell.GridCellContainer;
import libgdx.bf.grid.handlers.GridHandler;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
    //TODO optimization
    public Color getColor(Coordinates coord, Object o, boolean wall) {
        if (o == null) {
            return GdxColorMaster.get(GdxColorMaster.NULL_COLOR);
        }
        VisualEnums.PILLAR type = (VisualEnums.PILLAR) o;
        DIRECTION[] adj = Pillars.getAdjacent(type, wall);
        Set<Color> collect = Arrays.stream(adj).map(d -> getManager().getColor(d == null
                ? coord
                : coord.getAdjacentCoordinate(d))).collect(Collectors.toCollection(LinkedHashSet::new));
        collect.removeIf(Objects::isNull);
        Color lerp = collect.iterator().next();
        //GdxColorMaster.getAverage(collect);

        // Color lerp = new Color(c).lerp(c1, wall
        //         ?1- LightConsts.PILLAR_COLOR_LERP
        //         : LightConsts.PILLAR_COLOR_LERP);
        //
        // lerp.a = Math.max(LightConsts.MIN_LIGHTNESS,
        //         wall ? lerp.a * LightConsts.PILLAR_WALL_COEF_LIGHT
        //                 : lerp.a * LightConsts.PILLAR_COEF_LIGHT);

        return LightHandler.applyLightnessToColor(lerp.clamp(), false);
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
        grid.getWallPillars().setColorFunc(c -> getColor(c, wallPillarMap.get(c), true));
    }

    private void firstPass() {
        voidAdjacency = new ConcurrentHashMap<>();
        for (int x = grid.getX1(); x < grid.getX2(); x++) {
            for (int y = grid.getY1(); y < grid.getY2(); y++) {
                Coordinates c = Coordinates.get(x, y);
                processAdjacency(c);
            }
        }
        DC_Game.game.getBattleFieldManager().resetWalls();
        Map<Coordinates, List<DIRECTION>> wallMap = DC_Game.game.getBattleFieldManager().getWallMap();
        wallAdjacency = new ConcurrentHashMap<>();
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
        Map pillarMap = new ConcurrentHashMap<>();
        for (Coordinates c : voidAdjacency.keySet()) {
            Set<DIRECTION> set = voidAdjacency.get(c);
            Object d = getPillarForCell(set, wall);
            if (d != null) {
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
        shardMap = new ConcurrentHashMap<>();

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
                if (container.getUserObject().isVOID()) {
                    container.removePillar();
                    continue;
                }
                VisualEnums.PILLAR p = getPillarForCell(voidAdjacent, wall);
                if (p == null) {
                    container.removePillar();
                    continue;
                }
                String path = WallMaster.getPillarImage(c, p, wall);
                Vector2 pillarV = Pillars.getOffset(p);
                FadeImageContainer pillar = container.addPillar(path);
                if (pillar == null) {
                    continue;
                }
                //check pos?
                // MoveToAction move = ActionMaster.addMoveToAction(pillar, pillarV.x, pillarV.y, 1f);
                // move.setInterpolation(Interpolation.pow2Out);
                pillar.setPosition(pillarV.x, pillarV.y);
                pillar.setColor(getColor(c, p, false));
                pillar.getColor().a=0.25f;
                pillar.fadeIn();
            }
        }
    }

    public VisualEnums.PILLAR getPillarForCell(Set<DIRECTION> voidAdjacent, boolean wall) {
        Set<DIRECTION> inverted = new LinkedHashSet<>();
        for (DIRECTION direction : clockwise) {
            if (!voidAdjacent.contains(direction)) {
                inverted.add(direction);
            }
        }
        return getPillar(inverted, wall);
    }

    public VisualEnums.PILLAR getPillar(Set<DIRECTION> adj, boolean wall) {//ToDo-Cleanup
        return getPillar_(adj, wall);
    }

    public VisualEnums.PILLAR getPillar_(Set<DIRECTION> adj, boolean wall) {
        if (adj.size() == 8) {
            return VisualEnums.PILLAR.SINGLE;
        }
        if (adj.size() == 0) {
            return wall ? VisualEnums.PILLAR.SKEWED_CORNER : VisualEnums.PILLAR.SINGLE;
        }

        // if (adj.size() == 0) {
        //     if (adj.iterator().next() == LEFT) {
        //         return PILLAR.SKEWED_CORNER_UP;
        //     }
        // }
        //CORNER
        if (!adj.contains(RIGHT)) {
            if (!adj.contains(DOWN)) {
                if (adj.contains(DOWN_LEFT) && adj.contains(UP_RIGHT)) {
                    return VisualEnums.PILLAR.SKEWED_CORNER;
                }
                if (adj.contains(DOWN_LEFT)) {
                    if (adj.contains(UP))
                        return VisualEnums.PILLAR.SKEWED_CORNER_LEFT;
                    return VisualEnums.PILLAR.SKEWED_CORNER;
                }
                if (adj.contains(UP_RIGHT)) {
                    if (adj.contains(LEFT))
                        return VisualEnums.PILLAR.SKEWED_CORNER_UP;
                    return VisualEnums.PILLAR.SKEWED_CORNER;
                }
                if (adj.contains(UP) && adj.contains(LEFT))
                    return VisualEnums.PILLAR.CORNER;

                // if (adj.contains(LEFT)) {
                //     return PILLAR.SKEWED_CORNER_UP;
                // }
                // if (adj.contains(UP_LEFT)) {
                //     return PILLAR.SKEWED_CORNER_LEFT;
                // }

            }
        }
        if (!adj.contains(Pillars.prefVert)) {
            if (adj.contains(RIGHT) && adj.contains(LEFT)) {
                if (!adj.contains(DOWN_LEFT))
                    return Pillars.getPillarDIRECTION(false, null);
            }
            if (adj.contains(RIGHT)) {
                // if (adj.contains(DIRECTION.DOWN_RIGHT))
                //     return getPillarDIRECTION(false, false);
                return Pillars.getPillarDIRECTION(false, true);
            }
            if (adj.contains(LEFT)) {
                if (adj.contains(DOWN_RIGHT))
                    return VisualEnums.PILLAR.SKEWED_CORNER_UP;
                if (adj.contains(DOWN_LEFT))
                    return Pillars.getPillarDIRECTION(false, false);
                return VisualEnums.PILLAR.SKEWED_CORNER_UP;
            }
            if (adj.contains(UP))
                return VisualEnums.PILLAR.SKEWED_CORNER_LEFT;
            return VisualEnums.PILLAR.SKEWED_CORNER;
        }


        if (!adj.contains(Pillars.prefHor)) {
            if (adj.contains(UP) && adj.contains(DOWN)) {
                if (!adj.contains(UP_RIGHT))
                    return Pillars.getPillarDIRECTION(true, null);
                return VisualEnums.PILLAR.UP;
            }
            if (adj.contains(UP))
                return Pillars.getPillarDIRECTION(true, true);
            if (adj.contains(DOWN))
                return Pillars.getPillarDIRECTION(true, false);

            return Pillars.getPillarDIRECTION(true, Pillars.prefHor == RIGHT);
        }
        return null;
    }

    public Map<Coordinates, DIRECTION> getShardMap() {
        return shardMap;
    }
}
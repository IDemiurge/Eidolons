package eidolons.libgdx.bf.decor;

import com.badlogic.gdx.utils.ObjectMap;
import eidolons.entity.obj.DC_Cell;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.grid.handlers.GridHandler;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class PillarManager extends GridHandler {
    //gridHandler?
    //similar to wallMap - just draw for each direction!
    //can we sync this with shards? Like, when we determine a pillar direction, we also set shard direction in advance..


    DIRECTION prefHor = DIRECTION.RIGHT;
    DIRECTION prefVert = DIRECTION.DOWN;
    boolean startFrom0x;
    boolean startFrom0y;
    ObjectMap<Coordinates, Set<DIRECTION>> emptyAdjacency;
    ObjectMap<Coordinates, Set<DIRECTION>> bordersMap;

    public PillarManager(GridPanel panel) {
        super(panel);
    }

    @Override
    protected void bindEvents() {
         //TODO
    }

    public void updateVisuals() {
        GuiEventManager.trigger(GuiEventType.BORDER_MAP_UPDATE, bordersMap);
        // GuiEventManager.trigger(GuiEventType.BORDER_MAP_UPDATE, pillarsMap);
    }
        public void reset() {

        // gridPanel.getModuleCols()
        firstPass();
        secondPass();
            updateVisuals();
    }


    private void firstPass() {
        emptyAdjacency = new ObjectMap<>();
        for (int x = grid.getX1(); x <   grid.getX2(); x++) {
        for (int y = grid.getY1(); y <   grid.getY2(); y++) {
            Coordinates c = Coordinates.get(x, y);
            Set<DIRECTION> empty = new LinkedHashSet<>();
            Set<DIRECTION> borders = new LinkedHashSet<>();
            for (DIRECTION direction : DIRECTION.clockwise) {
                Coordinates c1 = c.getAdjacentCoordinate(direction);
                if (grid.isVoid(c1.x, c1.y)) {
                    empty.add(direction);
                    if (!direction.isDiagonal()) {
                        borders.add(direction);
                        //perhaps we will want some CORNER overlays sometime
                    }
                }
            }
            emptyAdjacency.put(c, empty);
            bordersMap.put(c, borders);
        }
        }
    }

    private void secondPass() {

    }
    public DIRECTION getPillarDirectionForCell(int x, int y) {

        //if both left/right are available...

        Set<DIRECTION> emptyAdj = new LinkedHashSet<>();
        Map<DIRECTION, DIRECTION> filledAdj = new LinkedHashMap<>();
        Coordinates c = Coordinates.get(x, y);
        for (DIRECTION direction : DIRECTION.clockwise) {
            Coordinates c1 = c.getAdjacentCoordinate(direction);
            DC_Cell cell = grid.getCell(c1.x, c1.y);
            if (cell == null || cell.isVOID()) {
                emptyAdj.add(direction);
            } else if (cell.getPillar() != null) {
                filledAdj.put(direction, cell.getPillar());
            }
        }
        if (emptyAdj.isEmpty() && filledAdj.isEmpty()) {
            return null;
        }
        // first, check if we gotta continue adjacent an pillar
        if (!filledAdj.isEmpty()) {
            for (DIRECTION direction : filledAdj.keySet()) {
                // relative = getRelative(c, c1) figure out if we CAN continue, or if we should?
                //this somehow depends on PREF directons...
                if (!emptyAdj.contains(filledAdj.get(direction))) { //TODO this must check 3 direction-side!
                    //can continue, now figure out if it's a straight or skewed pillar
                   Boolean skewed = checkSkewed(); // perhaps do this on a 'second pass'? After we've assigned basic pillar
                    //directions to a map?
                    if (skewed != null) {
                        return direction.rotate45(skewed);
                    }
                }
            }


        }

        if (emptyAdj.contains(DIRECTION.LEFT) && emptyAdj.contains(DIRECTION.RIGHT)) {

        }
        return null;
    }

    private Boolean checkSkewed() {
        return null;
    }

}

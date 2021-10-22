package libgdx.bf.decor.shard;

import eidolons.content.consts.VisualEnums;
import eidolons.game.core.Core;
import eidolons.game.core.game.DC_Game;
import eidolons.game.exploration.dungeons.struct.LevelStruct;
import eidolons.game.exploration.dungeons.generator.model.AbstractCoordinates;
import libgdx.bf.grid.GridPanel;
import libgdx.bf.grid.cell.GridCellContainer;
import main.content.CONTENT_CONSTS;
import main.data.XLinkedMap;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.secondary.Bools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ShardBuilder {
    final VisualEnums.SHARD_TYPE DEFAULT_TYPE = VisualEnums.SHARD_TYPE.CHAINS;
    final VisualEnums.SHARD_TYPE DEFAULT_TYPE_ALT = VisualEnums.SHARD_TYPE.ROCKS;
    final int BASE_PASS_CHANCE = 33;

    List<Shard> last = new ArrayList<>();
    private final GridPanel grid;
    protected final Map<Coordinates, Shard> map = new XLinkedMap<>();
    private final Function<Coordinates, VisualEnums.SHARD_TYPE> typeFunc;

    public int passed;
    private Object[][] builtShards;

    public ShardBuilder(GridPanel grid) {
        this.grid = grid;
        typeFunc = c ->
        {
            //check pillars? 
            LevelStruct struct = DC_Game.game.getDungeonMaster().getStructMaster().getLowestStruct(c);
            VisualEnums.SHARD_TYPE type = checkAltShard(c) ?
                    struct.getShardTypeAlt() : struct.getShardType();
            if (type == null) {
                type = checkAltShard(c) ? DEFAULT_TYPE_ALT : DEFAULT_TYPE;
            }
            return type;
        };
    }

    private boolean checkAltShard(Coordinates c) {
        return RandomWizard.chance(33);
    }

    public VisualEnums.SHARD_SIZE chooseSize(int x, int y, Object direction) {
        if (!(direction instanceof DIRECTION)) {
            return VisualEnums.SHARD_SIZE.NORMAL;
        }
        //cases? 1. line 2. jumble 3. corners
        //for LARGE - at least 1 free adj. in line; at least 2 cells of void in front
        //plus chance... and rarity
        if (checkLarge(x, y, (DIRECTION) direction)) {
            return VisualEnums.SHARD_SIZE.LARGE;
        }
        if (checkNormal(x, y, (DIRECTION) direction)) {
            return VisualEnums.SHARD_SIZE.NORMAL;
        }
        if (Core.getGame().isBossFight()) {
            return null;
        }
        return VisualEnums.SHARD_SIZE.SMALL;
    }

    private boolean checkNormal(int x, int y, DIRECTION direction) {
        if (direction.isDiagonal()) {
            return true;
        }
        //chance
        //large nearby?
        return RandomWizard.chance(50);
    }

    private boolean checkLarge(int x, int y, DIRECTION direction) {
        if (direction.isDiagonal()) {
            return false;
        }

        Coordinates c = Coordinates.get(true, x, y);

        DIRECTION[] toCheck = {
                direction.rotate90(true),
                direction.rotate90(false),
                direction.rotate180(),
                direction.rotate90(true).rotate45(true),
                direction.rotate90(false).rotate45(false)
        };
        DIRECTION[] toCheckPresent = {
                direction.rotate45(true),
                direction.rotate45(false)
        };
        for (DIRECTION d : toCheck) {
            Coordinates adj = c.getAdjacentCoordinate(d);
            Boolean result = checkAdjacent(adj, grid);
            if (Bools.isFalse(result))
                return false;
        }
        for (DIRECTION d : toCheckPresent) {
            Coordinates adj = c.getAdjacentCoordinate(d);
            Boolean result = checkAdjacent(adj, grid);
            if (Bools.isFalse(result))
                continue;
            return false;
        }

        for (Shard shard : last) {
            if (new AbstractCoordinates(shard.x, shard.y).dst_(c) > 3) {
                continue;
            }
            if (shard.size == VisualEnums.SHARD_SIZE.LARGE) {
                return false;
            }
        }

        for (Coordinates coordinates : c.getAdjacent()) {
            if (checkSize(map, coordinates, VisualEnums.SHARD_SIZE.LARGE))
                return false;
            for (Coordinates c1 : coordinates.getAdjacent()) {
                if (checkSize(map, c1, VisualEnums.SHARD_SIZE.LARGE))
                    return false;
            }
        }

        // not adjacent to large? no more than 3 in line
        int LARGE_SHARD_CHANCE = 85;
        return RandomWizard.chance(LARGE_SHARD_CHANCE);

        //add to map so we know where large ones are
    }

    private boolean checkSize(Map<Coordinates, Shard> map, Coordinates coordinates, VisualEnums.SHARD_SIZE size) {
        if (map.get(coordinates) != null) {
            return map.get(coordinates).getSize() == size;
        }
        return false;
    }

    private Boolean checkAdjacent(Coordinates adj, GridPanel grid) {
        if (adj == null)
            return null;
        if (adj.isInvalid()) {
            return null;
        }
        if (adj.x < 0 || adj.y < 0)
            return null;
        if (adj.x >= grid.getCells().length || adj.y >= grid.getCells()[0].length)
            return null;
        return grid.isVoid(adj);
        //        return grid.getCells()[adj.x][adj.y] == null;
    }

    int getIsleChancePerAdjacent() {
        return 0;
    }

    int getIsleChanceEmpty() {
        return 0;
    }


    public Integer getDirectionForShards(int x, int y) {

        Coordinates c = Coordinates.get(true, x, y);
        List<DIRECTION> adj = new ArrayList<>();
        GridCellContainer gridCell = grid.getGridCell(x, y);
        if (gridCell != null)
        if (gridCell.getUserObject().hasMark(CONTENT_CONSTS.MARK.undecorated)) {
            return -1;
        }
        int n = 0;
        for (DIRECTION d : DIRECTION.clockwise) {
            Coordinates cc = c.getAdjacentCoordinate(d);
            boolean VOID = true;
            GridCellContainer cell = cc == null ? null : grid.getGridCell(cc.x, cc.y);

            if (cell != null) {
                VOID = cell.getUserObject().isVOID();
            }
            if (!VOID) {
                adj.add(d);
                n++;
            } else {
                n = 0; //counting consecutive adjacency
            }
            if (n > 2) {
                List<DIRECTION> diag = new ArrayList<>(adj);
                diag.removeIf(dir -> !dir.isDiagonal());
                if (!diag.isEmpty()) {
                    for (DIRECTION direction : diag) {
                        DIRECTION corner = null;
                        //TODO fix case
                        if (adj.contains(direction.rotate45(true)))
                            if (adj.contains(direction.rotate45(false)))
                                corner = direction; //check corner
                        if (corner == null) {
                            continue; //prevents "diag-orth-diag" adjacency which is rly just a line
                        }
                        return corner.getDegrees();
                    }

                }
            }

        }
        if (adj.size() > 4) {
            if (RandomWizard.chance(adj.size() * getIsleChancePerAdjacent())) {
                return -1;
            }
        } else
            adj.removeIf(DIRECTION::isDiagonal);

        if (adj.isEmpty()) {
            if (RandomWizard.chance(getIsleChanceEmpty())) {
                return -1;
            }
            return null;
        }
        if (RandomWizard.chance(2 * BASE_PASS_CHANCE - passed * 5)) {
            passed++;
            return null;
        }
        Collections.shuffle(adj);
        DIRECTION result = adj.get(0);

        return result.getDegrees();
    }

    public VisualEnums.SHARD_TYPE getType(int x, int y) {
        return typeFunc.apply(new AbstractCoordinates(true, x, y));
    }

    public void init( Map<Coordinates, DIRECTION> map, int x1, int x2, int y1, int y2) {
        builtShards = new Object[x2 - x1 + 2][y2 - y1 + 2];


        for (int x = x1 - 1; x - 1 < x2; x++) {
            for (int y = y1 - 1; y - 1 < y2; y++) {
                if (x >= x1 && y >= y1)
                    if (x < x2 && y < y2) {
                        if (grid.getGridCell(x, y) != null)
                            if (!grid.isVoid(x, y)) {
                                continue;
                            }
                    }
                // Coordinates c = new AbstractCoordinates(true, x, y);
                    x+=1;
                    y+=1;
                // if (!c.isInvalid())
                if (map.containsKey(Coordinates.get(true, x, y))) {
                    //pass all adjacent too?
                    builtShards[x][y] = map.get(Coordinates.get(x, y));
                    continue;
                }
                //ToDo-Cleanup
                // Object direction = null;
                // Integer degrees = getDirectionForShards(x, y);
                // if (degrees == null) {
                //     passed++;
                //     continue;
                // }
                // passed = 0;
                // if (degrees < 0) {
                //     direction = ""; //isle
                // } else {
                //     direction = DirectionMaster.getDirectionByDegree(degrees);
                // }
                // builtShards[x  ][y  ] = direction;
            }
        }
    }

    public Object getBuilt(int x, int y) {
        return builtShards[x + 1][y + 1];
    }
}

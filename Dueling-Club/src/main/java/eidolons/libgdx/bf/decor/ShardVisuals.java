package eidolons.libgdx.bf.decor;

import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.game.module.dungeoncrawl.generator.model.AbstractCoordinates;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.SuperActor.ALPHA_TEMPLATE;
import eidolons.libgdx.bf.grid.GridCellContainer;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.particles.EMITTER_PRESET;
import eidolons.libgdx.particles.EmitterActor;
import eidolons.libgdx.screens.CustomSpriteBatch;
import main.data.XLinkedMap;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.secondary.Bools;

import java.util.*;

/**
 * Created by JustMe on 10/8/2018.
 * <p>
 * fade for fog and light on top of static shards
 * emitters
 * <p>
 * logic for placements and choice of shards
 * <p>
 * separate layer? that would be better than sticking it into shadowmap?
 * <p>
 * placement should be a bit smarter too
 */
public class ShardVisuals extends GroupX {

    List<Shard> last = new ArrayList<>();
    Map<Shard, List<EmitterActor>> emittersMap = new XLinkedMap<>();
    GroupX emitterLayer = new GroupX();
    private GridPanel grid;
    private Map<Coordinates, Shard> map = new XLinkedMap<>();

    public ShardVisuals(GridPanel grid) {
        this.grid = grid;
        init();
        addActor(emitterLayer);
    }

    public static ALPHA_TEMPLATE getTemplateForOverlay(SHARD_OVERLAY overlay) {
        return ALPHA_TEMPLATE.SHARD_OVERLAY;
    }

    public static EMITTER_PRESET[] getEmitters(SHARD_OVERLAY overlay, SHARD_SIZE size) {
        List<EMITTER_PRESET> list = new ArrayList<>(Arrays.asList(getEmittersForOverlay(overlay)));
        int n = 2;
        switch (size) {
            case SMALL:
                n = 1;
                break;
            case LARGE:
                n = 3;
                break;
        }
        EMITTER_PRESET[] array = new EMITTER_PRESET[n];
        for (int i = 0; i < n; i++) {
            array[i] = list.remove(
             RandomWizard.getRandomIndex(list));
        }
        return array;
    }

    private static EMITTER_PRESET[] getEmittersForOverlay(SHARD_OVERLAY overlay) {
        if (overlay == null) {
            overlay = new EnumMaster<SHARD_OVERLAY>().
             getRandomEnumConst(SHARD_OVERLAY.class);
        }
        switch (overlay) {
            case MIST:
                return new EMITTER_PRESET[]{
                 EMITTER_PRESET.MIST_ARCANE,
                 //                 EMITTER_PRESET.MIST_TRUE2,
                 EMITTER_PRESET.MIST_WHITE,
                 EMITTER_PRESET.MIST_WHITE2,
                 EMITTER_PRESET.MIST_WHITE3
                };
            case DARKNESS:
                return new EMITTER_PRESET[]{
                 EMITTER_PRESET.MIST_ARCANE,
                 EMITTER_PRESET.CINDERS3,
                 EMITTER_PRESET.ASH,
                 EMITTER_PRESET.MIST_WHITE,
                 EMITTER_PRESET.MIST_WHITE2,
                 EMITTER_PRESET.MIST_WHITE3
                };
            case NETHER:
                return new EMITTER_PRESET[]{
                 EMITTER_PRESET.CINDERS3,
                 EMITTER_PRESET.WISPS,
                 EMITTER_PRESET.STARS,
                 EMITTER_PRESET.MIST_BLACK,
                 EMITTER_PRESET.MIST_BLACK,
                 EMITTER_PRESET.MIST_WIND,
                 EMITTER_PRESET.THUNDER_CLOUDS_CRACKS,
                 EMITTER_PRESET.MIST_WIND
                };
        }
        return new EMITTER_PRESET[0];
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (batch instanceof CustomSpriteBatch) {
            ((CustomSpriteBatch) batch).resetBlending();
        }
    }

    public void init() {
        setSize(grid.getWidth(), grid.getHeight());

        for (int x = -1; x - 1 < grid.getCols(); x++) {
            for (int y = -1; y - 1 < grid.getRows(); y++) {

                if (x >= 0 && y >= 0)
                    if (x < grid.getCells().length &&
                     y < grid.getCells()[0].length)
                        if (grid.getCells()[x][y] != null)
                            continue;
                Object direction = null;
                Integer degrees = getDirectionForShards(x, y);
                if (degrees == null) {
                    continue;
                }
                if (degrees < 0) {
                    direction = ""; //isle
                } else {
                    direction = DirectionMaster.getDirectionByDegree(degrees);
                }
                AbstractCoordinates c = new AbstractCoordinates(x, y);
                SHARD_SIZE size = chooseSize(x, y, direction);
                SHARD_TYPE type = SHARD_TYPE.ROCKS;
                SHARD_OVERLAY overlay = new EnumMaster<SHARD_OVERLAY>().
                 getRandomEnumConst(SHARD_OVERLAY.class);
                if (!(direction instanceof DIRECTION)) {
                    overlay = null;
                } else if (((DIRECTION) direction).isDiagonal())
                    overlay = null;

                try {
                    Shard shard = new Shard(x, y, type, size, overlay, direction);

                    addActor(shard);
                    float offsetX = -(shard.getWidth() / 2 - 64);
                    float offsetY = -(shard.getHeight() / 2 - 64);

                    if (direction instanceof DIRECTION) {
                        DIRECTION d = ((DIRECTION) direction);

                        if (d.isVertical() || d.isDiagonal()) {
                            if (d.isGrowY()) {
                                offsetY = 0;
                            } else {
                                offsetY *= 2;
                            }
                        }
                        if (!d.isVertical() || d.isDiagonal()) {
                            if (!d.isGrowX()) {
                                offsetX = 0;
                            } else {
                                offsetX *= 2;
                            }
                        }


                    }
                    shard.setPosition(
                     x * GridMaster.CELL_W + offsetX,
                     y * GridMaster.CELL_H + offsetY);
                    last.add(shard);


                    EMITTER_PRESET[] presets = ShardVisuals.getEmitters(overlay, size);
                    for (EMITTER_PRESET preset : presets) {
                        EmitterActor actor = new EmitterActor(preset);
                        MapMaster.addToListMap(emittersMap, shard, actor);
                        emitterLayer.addActor(actor);
                        actor.setPosition(shard.getX() + shard.getWidth() / 2 - 55, shard.getY() + shard.getHeight() / 2 - 55);
                        actor.start();
                    }

                    while (last.size() > 4) {
                        last.remove(0);
                    }
                    map.put(c, shard);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
        }
        //        different parts of the map with dif style?
        //update-able?
        // more than one shard on a single cell?

    }

    private SHARD_SIZE chooseSize(int x, int y, Object direction) {
        if (!(direction instanceof DIRECTION)) {
            return SHARD_SIZE.NORMAL;
        }
        //cases? 1. line 2. jumble 3. corners
        //for LARGE - at least 1 free adj. in line; at least 2 cells of void in front
        //plus chance... and rarity
        if (checkLarge(x, y, (DIRECTION) direction)) {
            return SHARD_SIZE.LARGE;
        }
        if (checkNormal(x, y, (DIRECTION) direction)) {
            return SHARD_SIZE.NORMAL;
        }
        return SHARD_SIZE.SMALL;
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
        if (direction.isVertical()) {
            direction = direction.flip();
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
            Boolean result = checkAdjacent(adj);
            if (Bools.isFalse(result))
                return false;
        }
        for (DIRECTION d : toCheckPresent) {
            Coordinates adj = c.getAdjacentCoordinate(d);
            Boolean result = checkAdjacent(adj);
            if (Bools.isFalse(result))
                continue;
            return false;
        }

        for (Shard shard : last) {
            if (new AbstractCoordinates(shard.x, shard.y).dst_(c) > 3) {
                continue;
            }
            if (shard.size == SHARD_SIZE.LARGE) {
                return false;
            }
        }
        // not adjacent to large? no more than 3 in line
        if (RandomWizard.chance(25))
            return false;

        //add to map so we know where large ones are
        return true;
    }

    private Boolean checkAdjacent(Coordinates adj) {
        if (adj == null)
            return null;
        if (adj.isInvalid()) {
            return null;
        }
        if (adj.x < 0 || adj.y < 0)
            return null;
        if (adj.x >= grid.getCells().length || adj.y >= grid.getCells()[0].length)
            return null;
        if (grid.getCells()[adj.x][adj.y] != null) {
            return false;
        }
        return true;
    }

    private Integer getDirectionForShards(int x, int y) {

        Coordinates c = Coordinates.get(true, x, y);
        List<DIRECTION> adj = new ArrayList<>();
        int n = 0;
        for (DIRECTION d : DIRECTION.clockwise) {
            Coordinates cc = c.getAdjacentCoordinate(d);

            GridCellContainer cell = null;
            try {
                cell = grid.getCells()[cc.x][cc.y];
            } catch (Exception e) {
            }
            if (cell != null) {
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
                        if (adj.contains(direction.rotate45(true)))
                            if (adj.contains(direction.rotate45(false)))
                                corner = direction; //check corner
                        if (corner == null) {
                            continue; //prevents "diag-orth-diag" adjacency which is rly just a line
                        }
                        corner = corner.flipVertically();
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
            adj.removeIf(direction -> direction.isDiagonal());

        if (adj.isEmpty()) {
            if (RandomWizard.chance(getIsleChanceEmpty())) {
                return -1;
            }
            return null;
        }
        Collections.shuffle(adj);
        DIRECTION result = adj.get(0);
        if (result.isVertical())
            result = result.flip();

        return result.getDegrees();
    }

    private int getIsleChancePerAdjacent() {
        return 5;
    }

    private int getIsleChanceEmpty() {
        return 12;
    }

    public enum SHARD_OVERLAY {
        MIST,
        DARKNESS,
        NETHER,

    }

    public enum SHARD_SIZE {
        SMALL,
        NORMAL {
            @Override
            public String toString() {
                return "";
            }
        },
        LARGE,;

        public String toString() {
            return name() + " ";
        }
    }

    public enum SHARD_TYPE {
        ROCKS,
        ROOTS,
        METAL,

    }


}

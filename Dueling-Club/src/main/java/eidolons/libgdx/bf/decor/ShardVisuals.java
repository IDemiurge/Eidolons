package eidolons.libgdx.bf.decor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.LevelStruct;
import eidolons.game.module.generator.model.AbstractCoordinates;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.grid.cell.GridCellContainer;
import eidolons.libgdx.bf.grid.sub.GridElement;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.particles.EmitterActor;
import eidolons.libgdx.screens.CustomSpriteBatch;
import eidolons.libgdx.screens.ScreenMaster;
import eidolons.system.options.GraphicsOptions;
import eidolons.system.options.OptionsMaster;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.ALPHA_TEMPLATE;
import main.data.XLinkedMap;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;
import main.system.ExceptionMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.secondary.Bools;

import java.util.*;
import java.util.function.Function;

/**
 * Created by JustMe on 10/8/2018.
 * <p>
 * fade for fog and light on top of static shards emitters
 * <p>
 * logic for placements and choice of shards
 * <p>
 * separate layer? that would be better than sticking it into shadowmap?
 * <p>
 * placement should be a bit smarter too
 */
public class ShardVisuals extends GroupX implements GridElement {

    private static final SHARD_TYPE DEFAULT_TYPE = SHARD_TYPE.CHAINS;
    private static final SHARD_TYPE DEFAULT_TYPE_ALT = SHARD_TYPE.ROCKS;
    private static final int BASE_PASS_CHANCE = 33;
    private static boolean on=true;
    protected int cols;
    protected int rows;
    private int x1, x2, y1, y2;
    private static final int LARGE_SHARD_CHANCE = 85;
    List<Shard> last = new ArrayList<>();
    Map<Shard, List<EmitterActor>> emittersMap = new XLinkedMap<>();
    GroupX emitterLayer = new GroupX();
    private final GridPanel grid;
    private final Map<Coordinates, Shard> map = new XLinkedMap<>();
    private final Function<Coordinates, SHARD_TYPE> typeFunc;
    private int passed;

    public ShardVisuals(GridPanel grid) {
        this.grid = grid;
        setTouchable(Touchable.disabled);
        addActor(emitterLayer);
        typeFunc = c ->
        {
            LevelStruct struct = DC_Game.game.getDungeonMaster().getStructMaster().getLowestStruct(c);
            SHARD_TYPE type = checkAltShard(c) ?
                    struct.getShardTypeAlt() : struct.getShardType();
            if (type == null) {
                type = checkAltShard(c) ? DEFAULT_TYPE_ALT : DEFAULT_TYPE;
            }
            return type;
        };
    }

    public static void setOn(boolean on) {
        ShardVisuals.on = on;
    }

    public static boolean getOn() {
        return on;
    }

    private boolean checkAltShard(Coordinates c) {
        return RandomWizard.chance(33);
    }

    public static ALPHA_TEMPLATE getTemplateForOverlay(SHARD_OVERLAY overlay) {
        return ALPHA_TEMPLATE.SHARD_OVERLAY;
    }

    public static GenericEnums.VFX[] getEmitters(SHARD_OVERLAY overlay, SHARD_SIZE size) {
        if (!OptionsMaster.getGraphicsOptions().getBooleanValue(GraphicsOptions.GRAPHIC_OPTION.SHARD_VFX)) {
            return new GenericEnums.VFX[0];
        }
        List<GenericEnums.VFX> list = new ArrayList<>(Arrays.asList(getEmittersForOverlay(overlay)));
        int n = 2;
        if (size != null)
            switch (size) {
                case SMALL:
                    n = 1;
                    break;
                case LARGE:
                    n = 3;
                    break;
            }
        n = RandomWizard.getRandomInt(n);
        if (n < 0) {
            return new GenericEnums.VFX[0];
        }
        GenericEnums.VFX[] array = new GenericEnums.VFX[n];
        for (int i = 0; i < n; i++) {
            array[i] = list.remove(
                    RandomWizard.getRandomIndex(list));
        }
        return array;
    }

    private static GenericEnums.VFX[] getEmittersForOverlay(SHARD_OVERLAY overlay) {
        if (overlay == null) {
            overlay = new EnumMaster<SHARD_OVERLAY>().
                    getRandomEnumConst(SHARD_OVERLAY.class);
        }
        switch (overlay) {
            case MIST:
                return new GenericEnums.VFX[]{
                        GenericEnums.VFX.MIST_ARCANE,
                        GenericEnums.VFX.DARK_MIST_LITE,
                        GenericEnums.VFX.THUNDER_CLOUDS_CRACKS,
                        GenericEnums.VFX.THUNDER_CLOUDS_CRACKS,
                        //                 EMITTER_PRESET.MIST_TRUE2,
                        GenericEnums.VFX.MIST_WHITE,
                        GenericEnums.VFX.MIST_WIND,
                        GenericEnums.VFX.MIST_BLACK,
                        GenericEnums.VFX.MIST_WIND,
                        GenericEnums.VFX.STARS,
                        GenericEnums.VFX.ASH,
                        GenericEnums.VFX.MOTHS_TIGHT2,
                        GenericEnums.VFX.WISPS,
                        GenericEnums.VFX.SNOW_TIGHT2,
                        GenericEnums.VFX.SNOW,
                        GenericEnums.VFX.MOTHS_TIGHT2,
                        GenericEnums.VFX.STARS
                };
            case DARKNESS:
                return new GenericEnums.VFX[]{
                        GenericEnums.VFX.DARK_MIST,
                        GenericEnums.VFX.MIST_ARCANE,
                        GenericEnums.VFX.MIST_ARCANE,
                        GenericEnums.VFX.MIST_ARCANE,
                        GenericEnums.VFX.DARK_MIST_LITE,
                        GenericEnums.VFX.MIST_BLACK,
                        GenericEnums.VFX.CINDERS3,
                        GenericEnums.VFX.ASH,
                        GenericEnums.VFX.MIST_WHITE,
                        GenericEnums.VFX.MIST_BLACK,
                        GenericEnums.VFX.MIST_WIND,
                        GenericEnums.VFX.STARS,
                        GenericEnums.VFX.ASH,
                        GenericEnums.VFX.MOTHS_TIGHT2,
                        GenericEnums.VFX.WISPS,
                        GenericEnums.VFX.SNOW_TIGHT2,
                        GenericEnums.VFX.SNOW,
                        GenericEnums.VFX.MOTHS_TIGHT2,
                        GenericEnums.VFX.STARS
                };
            case NETHER:
                return new GenericEnums.VFX[]{
                        GenericEnums.VFX.DARK_MIST,
                        GenericEnums.VFX.DARK_MIST_LITE,
                        GenericEnums.VFX.MIST_ARCANE,
                        GenericEnums.VFX.MIST_ARCANE,
                        GenericEnums.VFX.MIST_ARCANE,
                        GenericEnums.VFX.MIST_BLACK,
                        GenericEnums.VFX.CINDERS3,
                        GenericEnums.VFX.WISPS,
                        GenericEnums.VFX.STARS,
                        GenericEnums.VFX.MIST_BLACK,
                        GenericEnums.VFX.MIST_BLACK,
                        GenericEnums.VFX.MIST_WHITE3,
                        GenericEnums.VFX.MIST_WIND,
                        GenericEnums.VFX.THUNDER_CLOUDS_CRACKS,
                        GenericEnums.VFX.MIST_WIND
                };
        }
        return new GenericEnums.VFX[0];
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!on) {
            return ;
        }
        if (batch instanceof CustomSpriteBatch) {
            ((CustomSpriteBatch) batch).resetBlending();
        }
        super.draw(batch, parentAlpha);
        if (batch instanceof CustomSpriteBatch) {
            ((CustomSpriteBatch) batch).resetBlending();
        }
    }

    @Override
    public void setModule(Module module) {
        x1 = module.getOrigin().x;
        y1 = module.getOrigin().y;
        cols = module.getEffectiveWidth();
        rows = module.getEffectiveHeight();
        x2 = cols + module.getOrigin().x;
        y2 = rows + module.getOrigin().y;
        try {
            init();
        } catch (Exception e) {
            ExceptionMaster.printStackTrace(e);
        }
    }

    public void init() {
        clearChildren();
        setSize(grid.getWidth(), grid.getHeight());
        //buffer by 1
        for (int x = x1 - 1; x - 1 < x2; x++) {
            for (int y = y1 - 1; y - 1 < y2; y++) {

                if (x >= x1 && y >= y1)
                    if (x < x2 &&
                            y < y2) {
                        // Coordinates c = DC_Game.game.getGrid().getModuleCoordinates(x, y);
                        if (grid.getGridCell(x, y) != null)
                            if (!grid.isVoid(x, y)) {
                                continue;
                            }
                    }
                Object direction = null;
                Integer degrees = getDirectionForShards(x, y);
                if (degrees == null) {
                    passed++;
                    continue;
                }
                passed =0;
                if (degrees < 0) {
                    direction = ""; //isle
                } else {
                    direction = DirectionMaster.getDirectionByDegree(degrees);
                }
                AbstractCoordinates c = new AbstractCoordinates(x, y);
                SHARD_SIZE size = chooseSize(x, y, direction);
                if (size == null) {
                    //                just empty    continue;
                }
                SHARD_TYPE type = getType(x, y);

                SHARD_OVERLAY overlay = new EnumMaster<SHARD_OVERLAY>().
                        getRandomEnumConst(SHARD_OVERLAY.class);
                if (!(direction instanceof DIRECTION)) {
                    overlay = null;
                } else if (((DIRECTION) direction).isDiagonal())
                    overlay = null;

                try {
                    Shard shard = new Shard(x, y, type, size, overlay, direction);
                    if (direction instanceof DIRECTION)
                        if (y >= grid.getModuleRows()) {
                            if (x >= grid.getModuleCols()) {
                                shard.setUserObject(grid.getCell(x + 1, y + 1));
                            } else
                                shard.setUserObject(grid.getCell(x - 1, y + 1));
                        } else if (x >= grid.getModuleCols()) {
                            if (y >= grid.getModuleRows()) {
                                shard.setUserObject(grid.getCell(x + 1, y + 1));
                            } else
                                shard.setUserObject(grid.getCell(x + 1, y - 1));
                        } else
                            shard.setUserObject(grid.getCell(x + 1, y + 1));
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
                            grid.getGdxY_ForModule(y) * GridMaster.CELL_H + offsetY);
                    last.add(shard);


                    GenericEnums.VFX[] presets = ShardVisuals.getEmitters(overlay, size);
                    if (isVfxOn())
                        for (GenericEnums.VFX preset : presets) {
                            EmitterActor actor = new EmitterActor(preset) {
                                @Override
                                public void act(float delta) {
                                    if (!ScreenMaster.getScreen().getController().isWithinCamera(getX(), getY(), 400, 400)) {
                                        return;
                                    }
                                    super.act(delta);
                                }

                                @Override
                                public void draw(Batch batch, float parentAlpha) {
                                    if (!ScreenMaster.getScreen().getController().isWithinCamera(getX(), getY(), 400, 400)) {
                                        return;
                                    }
                                    super.draw(batch, parentAlpha);
                                }
                            };
                            MapMaster.addToListMap(emittersMap, shard, actor);
                            emitterLayer.addActor(actor);
                            actor.setPosition(shard.getX() + shard.getWidth() / 2
                                    + 50 - RandomWizard.getRandomInt(100), shard.getY() + shard.getHeight() / 2
                                    + 50 - RandomWizard.getRandomInt(100));
                            actor.start();
                            actor.act(RandomWizard.getRandomFloat());
                        }

                    while (last.size() > 4) {
                        last.remove(0);
                    }
                    map.put(c, shard);
                    LogMaster.log(1, c + " has shard with direction " + direction);
                } catch (Exception e) {
                    ExceptionMaster.printStackTrace(e);
                }
            }
        }
        //        different parts of the map with dif style?
        //update-able?
        // more than one shard on a single cell?

    }

    private SHARD_TYPE getType(int x, int y) {
        return typeFunc.apply(new AbstractCoordinates(true, x, y));
    }

    private boolean isVfxOn() {
        //        return OptionsMaster.getGraphicsOptions().getBooleanValue(GraphicsOptions.GRAPHIC_OPTION.AMBIENCE_VFX);
        return true;
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
        if (Eidolons.getGame().isBossFight()) {
            return null;
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

        if (isFlipVertical() && direction.isVertical()) {
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

        for (Coordinates coordinates : c.getAdjacent()) {
            if (checkSize(coordinates, SHARD_SIZE.LARGE))
                return false;
            for (Coordinates c1 : coordinates.getAdjacent()) {
                if (checkSize(c1, SHARD_SIZE.LARGE))
                    return false;
            }
        }

        // not adjacent to large? no more than 3 in line
        return RandomWizard.chance(LARGE_SHARD_CHANCE);

        //add to map so we know where large ones are
    }

    private boolean isFlipVertical() {
        return false;
    }

    private boolean checkSize(Coordinates coordinates, SHARD_SIZE size) {
        if (map.get(coordinates) != null) {
            return map.get(coordinates).getSize() == size;
        }
        return false;
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
        return grid.isVoid(adj);
        //        return grid.getCells()[adj.x][adj.y] == null;
    }

    private Integer getDirectionForShards(int x, int y) {

        Coordinates c = Coordinates.get(true, x, y);
        List<DIRECTION> adj = new ArrayList<>();
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
                        if (adj.contains(direction.rotate45(true)))
                            if (adj.contains(direction.rotate45(false)))
                                corner = direction; //check corner
                        if (corner == null) {
                            continue; //prevents "diag-orth-diag" adjacency which is rly just a line
                        }
                        if (isFlipVertical()) {
                            corner = corner.flipVertically();
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
            adj.removeIf(direction -> direction.isDiagonal());

        if (adj.isEmpty()) {
            if (RandomWizard.chance(getIsleChanceEmpty())) {
                return -1;
            }
            return null;
        }
        if (RandomWizard.chance(BASE_PASS_CHANCE - passed)){
            passed++;
            return null;
        }
        Collections.shuffle(adj);
        DIRECTION result = adj.get(0);
        if (isFlipVertical() && result.isVertical())
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
        LARGE,
        ;

        public String toString() {
            return name() + " ";
        }
    }

    public enum SHARD_TYPE {
        ROCKS,
        ROOTS,
        CHAINS,

    }


}

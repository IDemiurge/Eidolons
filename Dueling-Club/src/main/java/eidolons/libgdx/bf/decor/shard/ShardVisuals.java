package eidolons.libgdx.bf.decor.shard;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import eidolons.entity.obj.DC_Cell;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.module.generator.model.AbstractCoordinates;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.grid.GridLayer;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.grid.sub.GridElement;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.particles.EmitterActor;
import eidolons.libgdx.screens.ScreenMaster;
import main.content.enums.GenericEnums;
import main.data.XLinkedMap;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.ExceptionMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.LogMaster;

import java.util.List;
import java.util.Map;

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
public class ShardVisuals extends GridLayer implements GridElement {

    public static final boolean TEST_MODE = true;
    private static boolean on = true;
    private final ShardBuilder builder;
    protected int cols;
    protected int rows;
    private int x1, x2, y1, y2;
    Map<Shard, List<EmitterActor>> emittersMap = new XLinkedMap<>();
    GroupX emitterLayer = new GroupX();
    private Shard[][] shards;

    public ShardVisuals(GridPanel grid) {
        super(grid);
        setTouchable(Touchable.disabled);
        addActor(emitterLayer);
        builder = new ShardBuilder(grid);
    }

    public static void setOn(boolean on) {
        ShardVisuals.on = on;
    }

    private boolean isVfxOn() {
        //        return OptionsMaster.getGraphicsOptions().getBooleanValue(GraphicsOptions.GRAPHIC_OPTION.AMBIENCE_VFX);
        return true;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!on || shards==null) {
            return;
        }
        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        if (!on || shards==null ) {
            return;
        }
        super.act(delta);
    }

    @Override
    protected void act(int x, int y, float delta) {
        if (shards[x+1][y+1] != null) {
            shards[x+1][y+1].act(delta);
        }
    }

    @Override
    protected void draw(int x, int y, Batch batch, float parentAlpha) {
        if (shards[x+1][y+1] != null) {
            shards[x+1][y+1].draw(batch,parentAlpha);
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
        shards = new Shard[grid.getModuleCols() + 2][grid.getModuleRows() + 2];


        Map<Coordinates, DIRECTION> map = grid.getGridManager().getPillarManager().getShardMap();
        //buffer by 1
        builder.init(map, x1, x2, y1, y2);
        for (int x = x1 - 1; x - 1 < x2; x++) {
            for (int y = y1 - 1; y - 1 < y2; y++) {
                Object direction = builder.getBuilt(x, y);
                if (direction==null) {
                    continue;
                }
                AbstractCoordinates c = new AbstractCoordinates(x, y);
                ShardEnums.SHARD_SIZE size = builder.chooseSize(x, y, direction);
                if (size == null) {
                    //                just empty    continue;
                }
                ShardEnums.SHARD_TYPE type = builder.getType(x, y);

                ShardEnums.SHARD_OVERLAY overlay = new EnumMaster<ShardEnums.SHARD_OVERLAY>().
                        getRandomEnumConst(ShardEnums.SHARD_OVERLAY.class);
                if (!(direction instanceof DIRECTION)) {
                    overlay = null;
                } else if (((DIRECTION) direction).isDiagonal())
                    overlay = null;

                try {
                    Shard shard = new Shard(x, y, type, size, overlay, direction);
                    if (direction instanceof DIRECTION)
                        shard.setUserObject(getCellForShard(x, y));

                    Coordinates c1 =Coordinates.get(x+1, y+1);
                    shard.setColorFunc( coord -> grid.getGridManager().getColor(c1));
                    shard.setLightnessFunc( coord-> {
                        // return GammaMaster.getGammaForPillar(shard.getUserObject().getGamma());
                        return grid.getGridManager().getLightness(c1);
                    });



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
                    builder.last.add(shard);


                    if (isVfxOn()) {
                        GenericEnums.VFX[] presets = ShardEnums.getEmitters(overlay, size);
                        initShardVfx(shard, presets);
                    }

                    while (builder.last.size() > 4) {
                        builder.last.remove(0);
                    }
                    builder.map.put(c, shard);
                    shards[c.x+1][c.y+1] = shard;
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

    private void initShardVfx(Shard shard, GenericEnums.VFX[] presets) {
        for (GenericEnums.VFX preset : presets) {
            EmitterActor actor = new EmitterActor(preset) {
                @Override
                public void act(float delta) {
                    if (!grid.isCustomDraw())
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
            // actor.act(RandomWizard.getRandomFloat());
        }
    }

    private DC_Cell getCellForShard(int x, int y) {
        if (y >= grid.getModuleRows()) {
            if (x >= grid.getModuleCols()) {
                return (grid.getCell(x + 1, y + 1));
            } else
                return (grid.getCell(x - 1, y + 1));
        } else if (x >= grid.getModuleCols()) {
            if (y >= grid.getModuleRows()) {
                return (grid.getCell(x + 1, y + 1));
            } else
                return (grid.getCell(x + 1, y - 1));
        } else
            return (grid.getCell(x + 1, y + 1));
    }


}

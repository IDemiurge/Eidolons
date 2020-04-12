package eidolons.libgdx.particles.ambi;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.generic.GroupX;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.data.ability.construct.VariableManager;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.NumberUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 9/5/2018.
 */
public class GlobalVfxMap extends GroupX {

    Map<DAY_TIME, Map<LevelBlock, List<EmitterMap>>> caches = new HashMap<>();
    private List<EmitterMap> maps=    new ArrayList<>() ;
    private Integer emitterCountControlCoef;

    public GlobalVfxMap( ) {
        GuiEventManager.bind(GuiEventType.UPDATE_MAIN_HERO, p -> {
            Unit hero = (Unit) p.get();

            maps.forEach(map -> {
               float dst = (float) Coordinates.get(map.getTopLeft().x / 2 + map.getBottomRight().x / 2,
                 map.getTopLeft().y / 2 + map.getBottomRight().y / 2).dst_(hero.getCoordinates());

               map.setBaseAlpha(Math.max(0.05f, 0.5f-dst/100));
                //                map.getTop()
                /*
                default alpha would be 0.5f then, if same block 0.75, if real close - 1f
                speed?

                 */
            });
        });
    }

    public void update(DAY_TIME dayTime) {
        if (maps != null)
            maps.forEach(map -> {
                map.hide();
            });

        Map<LevelBlock, List<EmitterMap>> cache = caches.get(dayTime);
        if (cache == null) {
            cache = new HashMap<>();
        }

        for (LevelBlock block :    DC_Game.game.getDungeonMaster().
                getStructureMaster().getCurrentBlocks()) {
            maps = cache.get(block);
            if (maps == null) {
                maps = new ArrayList<>();
                AmbienceDataSource data = new AmbienceDataSource(block, dayTime);
                for (String path : data.getEmitters()) {
                    Integer chance = data.getShowChance();
                    if (NumberUtils.isInteger(VariableManager.getVar(path))) {
                        chance = NumberUtils.getInteger(VariableManager.getVar(path));
                        path = VariableManager.removeVarPart(path);
                    }

                    EmitterMap emitters = new EmitterMap(path,
                     chance,
                     data.getColorHue()); //TODO
                    emitters.setTopLeft(block.getOrigin());
                    emitters.setBottomRight(block.getOrigin().getOffset(
                     Coordinates.get(block.getWidth(), block.getHeight())
                    ));
                    maps.add(emitters);
                }
                cache.put(block, maps);
                for (EmitterMap map : maps) {
                    addActor(map);
                }
            } else {
                for (EmitterMap map : maps) {
                    map.show();
                }
            }
        }
        caches.put(dayTime, cache);
    }

    @Override
    public void act(float delta) {
        float n = 0;
        if (maps != null)
        for (EmitterMap map : maps) {
            n+=map.getActiveCount();
        }
        n= n/GdxMaster.getFontSizeMod();
        emitterCountControlCoef = (int) (Math.round(200 / Math.sqrt(n+1) - 10 * n));
        super.act(delta);
    }

    public Integer getEmitterCountControlCoef() {
        return emitterCountControlCoef;
    }


}

package eidolons.libgdx.particles.ambi;

import com.badlogic.gdx.graphics.Color;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.LevelStruct;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.bf.grid.cell.GridUnitView;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.particles.ambi.AmbienceDataSource.VFX_TEMPLATE;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.MapMaster;

import java.util.*;

/**
 * Created by JustMe on 9/5/2018.
 */
public class GlobalVfxMap extends GroupX {

    Map<DAY_TIME, Map<EmitterMap, AmbienceDataSource>> caches = new HashMap<>();
    Map<Coordinates, Color> colorMap = new HashMap<>();
    private List<EmitterMap> maps = new ArrayList<>();
    private Integer emitterCountControlCoef;
    private DAY_TIME time;
    float updatePeriod = 3f;
    float updateTimer;

    public GlobalVfxMap(Module module) {
        initModuleVfx(module);
        GuiEventManager.bind(GuiEventType.UNIT_VIEW_MOVED, p -> {
            GridUnitView view = (GridUnitView) p.get();
            if (view.getUserObject() == Eidolons.getMainHero()) {
                Unit hero = (Unit) view.getUserObject();
                for (EmitterMap map : maps) {
                    map.update(hero.getCoordinates());
                }
            }
        });
    }

    public void initModuleVfx(Module module) {
        Map<VFX_TEMPLATE, Set<Coordinates>> templates = new LinkedHashMap<>();
        for (Coordinates c : module.getCoordinatesSet()) {
            LevelStruct struct = DC_Game.game.getDungeonMaster().getStructMaster().findLowestStruct(c);
            MapMaster.addToListMap(templates, struct.getVfx(), c);
            colorMap.put(c, GdxColorMaster.getColorForTheme(struct.getColorTheme()));
        }

        for (VFX_TEMPLATE template : templates.keySet()) {
            EmitterMap emitterMap = null;
            for (DAY_TIME time : DAY_TIME.values()) {
                AmbienceDataSource data = new AmbienceDataSource(template, time);

                for (String path : data.getMap().keySet()) {
                    Integer chance = data.getMap().get(path);
                    if (emitterMap == null) {
                        emitterMap =
                                new EmitterMap(path, templates.get(template), chance, data.colorHue);
                    }
                    Map<EmitterMap, AmbienceDataSource> dataSourceMap = caches.get(time);
                    if (dataSourceMap == null) {
                        dataSourceMap = new HashMap<>();
                    }
                    caches.put(time, dataSourceMap);
                    dataSourceMap.put(emitterMap, data);
                    maps.add(emitterMap);
                    addActor(emitterMap);
                }
            }
        }
    }


    public void update(DAY_TIME time) {
        this.time = time;
        Map<EmitterMap, AmbienceDataSource> dataSourceMap = caches.get(time);
        for (EmitterMap map : dataSourceMap.keySet()) {
            AmbienceDataSource data = dataSourceMap.get(map);
            map.setHue(colorMap.get(map.getCoordinates().iterator().next()));
            map.setShowChance(data.getShowChance(map.getVfxPath()));
        }

    }

    @Override
    public void act(float delta) {
        // float n = 0;
        // if (maps != null)
        //     for (EmitterMap map : maps) {
        //         n += map.getActiveCount();
        //     }
        // n = n / GdxMaster.getFontSizeMod();
        // emitterCountControlCoef = (int) (Math.round(200 / Math.sqrt(n + 1) - 10 * n));
        super.act(delta);
        updateTimer += delta;
        if (updateTimer >= updatePeriod) {
            updateTimer = 0;
            Map<EmitterMap, AmbienceDataSource> dataSourceMap = caches.get(time);
            for (EmitterMap map : dataSourceMap.keySet()) {
                if (RandomWizard.chance(10)) {
                    map.init();
                }
            }
        }
    }

    public Integer getEmitterCountControlCoef() {
        return emitterCountControlCoef;
    }


}

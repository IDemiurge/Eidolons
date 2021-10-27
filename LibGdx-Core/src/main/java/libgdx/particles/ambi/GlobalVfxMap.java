package libgdx.particles.ambi;

import com.badlogic.gdx.graphics.Color;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.core.Core;
import eidolons.game.core.game.DC_Game;
import eidolons.game.exploration.dungeon.struct.LevelStruct;
import eidolons.content.consts.libgdx.GdxColorMaster;
import eidolons.system.libgdx.datasource.AmbienceDataSource;
import libgdx.GdxMaster;
import libgdx.bf.grid.cell.UnitGridView;
import libgdx.gui.generic.GroupX;
import eidolons.content.consts.VisualEnums.VFX_TEMPLATE;
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

    final Map<DAY_TIME, Map<EmitterMap, AmbienceDataSource>> caches = new HashMap<>();
    Map<Coordinates, Color> colorMap = new HashMap<>();
    private final List<EmitterMap> maps = new ArrayList<>();
    private Integer emitterCountControlCoef;
    private DAY_TIME time;
    float updatePeriod = 3f;
    float updateTimer;
    private final VFX_TEMPLATE DEFAULT_TEMPLATE= VisualEnums.VFX_TEMPLATE.DEEP_MIST;

    public GlobalVfxMap(Module module) {
        initModuleVfx(module);
        GuiEventManager.bind(GuiEventType.UNIT_VIEW_MOVED, p -> {
            UnitGridView view = (UnitGridView) p.get();
            if (view.getUserObject() == Core.getMainHero()) {
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
            LevelStruct struct = DC_Game.game.getDungeonMaster().getStructMaster().getLowestStruct(c);
            VFX_TEMPLATE vfx = struct.getVfx();
            if (vfx == null) {
                vfx = DEFAULT_TEMPLATE;
            }
            MapMaster.addToListMap(templates, vfx, c);
            colorMap.put(c, GdxColorMaster.getColorForTheme(struct.getColorTheme()));
        }

        for (VFX_TEMPLATE template : templates.keySet()) {
            EmitterMap emitterMap = null;
            for (DAY_TIME time : DAY_TIME.values()) {
                AmbienceDataSource data = new AmbienceDataSource(template, time);

                for (String path : data.getMap().keys()) {
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
        float n = 0;
        if (maps != null)
            for (EmitterMap map : maps) {
                n += map.getActiveCount();
            }
        n = n / GdxMaster.getFontSizeMod();
        emitterCountControlCoef = (int) (Math.round(200 / Math.sqrt(n + 1) - 10 * n));
        super.act(delta);
        updateTimer += delta;
        if (updateTimer >= updatePeriod) {
            updateTimer = 0;
            Map<EmitterMap, AmbienceDataSource> dataSourceMap = caches.get(time);
            if (dataSourceMap == null) {
                return;
            }
            //gdx revamp  -- wtf is this?
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

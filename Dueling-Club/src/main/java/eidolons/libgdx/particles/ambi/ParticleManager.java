package eidolons.libgdx.particles.ambi;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import eidolons.game.battlecraft.logic.dungeon.universal.Floor;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.particles.EmitterActor;
import eidolons.libgdx.particles.ambi.AmbienceDataSource.AMBIENCE_TEMPLATE;
import eidolons.libgdx.screens.CustomSpriteBatch;
import eidolons.system.options.GraphicsOptions.GRAPHIC_OPTION;
import eidolons.system.options.OptionsMaster;
import main.content.enums.GenericEnums;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.data.ability.construct.VariableManager;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.MapEvent;
import main.system.auxiliary.NumberUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 1/8/2017.
 */
public class ParticleManager extends GroupX {
    private static final GenericEnums.VFX FOG_VFX = GenericEnums.VFX.SMOKE_TEST;
    private static final boolean TEST = true;
    private static boolean ambienceOn = OptionsMaster.getGraphicsOptions().getBooleanValue(
            GRAPHIC_OPTION.AMBIENCE_VFX);
    private static boolean ambienceMoveOn;
    private static Floor floor_;
    public boolean debugMode;
    List<EmitterMap> emitterMaps = new ArrayList<>();
    List<EmitterActor> dynamicVfx = new ArrayList<>();
    Map<String, EmitterMap> cache = new HashMap<>();

    GlobalVfxMap ambienceMap;

    public ParticleManager() {
//        if (OptionsMaster) TODO
//        new AttachEmitterManager();
        GuiEventManager.bind(GuiEventType.GAME_STARTED, p -> {
            addActor(ambienceMap = new GlobalVfxMap());
        });
        GuiEventManager.bind(MapEvent.PREPARE_TIME_CHANGED, p -> {
            if (isAmbienceOn())
                if (ambienceMap != null) {
                    ambienceMap.update((DAY_TIME) p.get());
                    addActor(ambienceMap);
                    return;
                }
            if (isAmbienceOn())
                GuiEventManager.trigger(GuiEventType.INIT_AMBIENCE,
                        new AmbienceDataSource(getTemplate(floor_), (DAY_TIME) p.get()));
        });

        GuiEventManager.bind(GuiEventType.SHOW_CUSTOM_VFX, p -> {
            List args = (List) p.get();
            String preset = (String) args.get(0);
            Vector2 v = (Vector2) args.get(1);
            EmitterActor vfx = new EmitterActor(preset);

            dynamicVfx.add(vfx);
            vfx.setPosition(v.x, v.y);
            addActor(vfx);
            vfx.start();
            vfx.getEffect().allowCompletion();
        });
        GuiEventManager.bind(GuiEventType.SHOW_VFX, p -> {
            List<Object> list = (List<Object>) p.get();
            List<Object> newList = new ArrayList<>();
            GenericEnums.VFX preset = (GenericEnums.VFX) list.get(0);
            newList.add(preset.getPath());
            newList.add(list.get(1));
            GuiEventManager.trigger(GuiEventType.SHOW_CUSTOM_VFX,
                    newList);

        });

        GuiEventManager.bind(GuiEventType.ADD_AMBI_VFX, p -> {
            List l = (List) p.get();
            GenericEnums.VFX vfx = (GenericEnums.VFX) l.get(0);
            Vector2 v = (Vector2) l.get(1);
            Ambience ambi = null;
            addActor(ambi = new Ambience(vfx));
            ambi.setPosition(v.x, v.y);


        });
        GuiEventManager.bind(GuiEventType.INIT_AMBIENCE, p -> {
            if (!isAmbienceOn())
                return;

            AmbienceDataSource dataSource = (AmbienceDataSource) p.get();
            clearChildren();
            emitterMaps.clear();
            for (String sub : dataSource.getEmitters()) {
                int showChance = dataSource.getShowChance();
                if (!VariableManager.getVar(sub).isEmpty()) {
                    showChance = NumberUtils.getInteger(VariableManager.getVar(sub));
                    sub = VariableManager.removeVarPart(sub);
                }
                EmitterMap emitterMap = cache.get(sub);
                if (emitterMap == null) {
                    emitterMap = new EmitterMap(sub, showChance, dataSource.getColorHue());
                    cache.put(sub, emitterMap);
                } else
                    emitterMap.setShowChance(showChance);

                emitterMap.update();
                emitterMaps.add(emitterMap);
                addActor(emitterMap);
            }
        });
        GuiEventManager.bind(GuiEventType.UPDATE_AMBIENCE, p -> {
            if (isAmbienceOn())
                emitterMaps.forEach(emitterMap -> {
                    try {
                        emitterMap.update();
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    }
                });

        });
    }

    @Deprecated
    public static AMBIENCE_TEMPLATE getTemplate(Floor floor_) {
        if (floor_.getDungeonSubtype() != null)
            switch (floor_.getDungeonSubtype()) {
                case CAVE:
                case HIVE:

                case HELL:
                    return AMBIENCE_TEMPLATE.CAVE;
                case DUNGEON:
                case SEWER:
                case TOWER:
                case CASTLE:
                case DEN:
                case HOUSE:
                    return AMBIENCE_TEMPLATE.DUNGEON;
                case CRYPT:
                case BARROW:
                    return AMBIENCE_TEMPLATE.CRYPT;
            }
        return AMBIENCE_TEMPLATE.DEEP_MIST;
    }

    public static boolean isAmbienceOn() {
        if (TEST) {
            return true;
        }
        return ambienceOn;
    }

    public static void setAmbienceOn(boolean ambienceOn) {
        ParticleManager.ambienceOn = ambienceOn;
    }

    public static Ambience addFogOn(Vector2 at, GenericEnums.VFX preset) {
        Ambience fog = new Ambience(preset) {
            @Override
            protected boolean isCullingOn() {
                return false;
            }
        };
        fog.setPosition(
                at.x,
                at.y);
        fog.added();
        fog.setVisible(true);
        fog.getEffect().start();
        return fog;
    }

    public static boolean isAmbienceMoveOn() {
        return ambienceMoveOn;
    }

    public static void setAmbienceMoveOn(boolean ambienceMoveOn) {
        ParticleManager.ambienceMoveOn = ambienceMoveOn;
    }

    public static void init(Floor floor) {
        floor_ = floor;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (batch instanceof CustomSpriteBatch) {
            ((CustomSpriteBatch) batch).resetBlending();
        }
    }

    public Integer getEmitterCountControlCoef() {
        if (ambienceMap != null) {
            return ambienceMap.getEmitterCountControlCoef();
        }
        return 100;
    }
}

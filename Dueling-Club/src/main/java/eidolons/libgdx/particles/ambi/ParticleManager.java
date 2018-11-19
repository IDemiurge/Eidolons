package eidolons.libgdx.particles.ambi;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.particles.ambi.AmbienceDataSource.AMBIENCE_TEMPLATE;
import eidolons.libgdx.particles.VFX;
import eidolons.libgdx.particles.EmitterActor;
import eidolons.libgdx.screens.CustomSpriteBatch;
import eidolons.system.options.GraphicsOptions.GRAPHIC_OPTION;
import eidolons.system.options.OptionsMaster;
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
    private static final VFX FOG_VFX = VFX.SMOKE_TEST;
    private static boolean ambienceOn = OptionsMaster.getGraphicsOptions().getBooleanValue(
     GRAPHIC_OPTION.AMBIENCE);
    private static boolean ambienceMoveOn;
    private static Dungeon dungeon_;
    public boolean debugMode;
    List<EmitterMap> emitterMaps = new ArrayList<>();
    List<EmitterActor> dynamicVfx = new ArrayList<>();
    Map<String, EmitterMap> cache = new HashMap<>();

    SmartAmbienceMap ambienceMap;

    public ParticleManager() {
        new AttachEmitterManager();
        GuiEventManager.bind(GuiEventType.GAME_STARTED, p -> {
            DC_Game game = (DC_Game) p.get();
            DungeonLevel level = game.getDungeonMaster().getDungeonLevel();
            if (level != null) {
                addActor(ambienceMap = new SmartAmbienceMap(level));
            }
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
                 new AmbienceDataSource(getTemplate(dungeon_), (DAY_TIME) p.get()));
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
            VFX preset = (VFX) list.get(0);
            newList.add(preset.getPath());
            newList.add(list.get(1));
            GuiEventManager.trigger(GuiEventType.SHOW_CUSTOM_VFX,
             newList);

        });

        GuiEventManager.bind(GuiEventType.INIT_AMBIENCE, p -> {
            if (!isAmbienceOn())
                return;

            AmbienceDataSource dataSource = (AmbienceDataSource) p.get();
            clearChildren();
            emitterMaps.clear();
            for (String sub : dataSource.getEmitters()) {
                int showChance = dataSource.getShowChance();
                if (!VariableManager.getVarPart(sub).isEmpty()) {
                    sub = VariableManager.removeVarPart(sub);
                    showChance = NumberUtils.getInteger(VariableManager.getVarPart(sub));
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
    public static AMBIENCE_TEMPLATE getTemplate(Dungeon dungeon_) {
        if (dungeon_.getDungeonSubtype() != null)
            switch (dungeon_.getDungeonSubtype()) {
                case CAVE:
                case HIVE:
                    return AMBIENCE_TEMPLATE.CAVE;
                case DUNGEON:
                case SEWER:
                case TOWER:
                case CASTLE:
                case DEN:
                case HOUSE:
                    return AMBIENCE_TEMPLATE.DUNGEON;

                case HELL:
                    return AMBIENCE_TEMPLATE.CAVE;
                case CRYPT:
                case BARROW:
                    return AMBIENCE_TEMPLATE.CRYPT;
            }
        return AMBIENCE_TEMPLATE.DEEP_MIST;
    }

    public static boolean isAmbienceOn() {
        return ambienceOn;
    }

    public static void setAmbienceOn(boolean ambienceOn) {
        ParticleManager.ambienceOn = ambienceOn;
    }

    public static Ambience addFogOn(Vector2 at, VFX preset) {
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

    public static void init(Dungeon dungeon) {
        dungeon_ = dungeon;
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

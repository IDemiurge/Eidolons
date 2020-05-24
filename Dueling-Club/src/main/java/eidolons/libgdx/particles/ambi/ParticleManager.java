package eidolons.libgdx.particles.ambi;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.dungeon.universal.Floor;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.particles.EmitterActor;
import eidolons.libgdx.particles.ambi.AmbienceDataSource.VFX_TEMPLATE;
import eidolons.libgdx.screens.CustomSpriteBatch;
import eidolons.system.options.GraphicsOptions.GRAPHIC_OPTION;
import eidolons.system.options.OptionsMaster;
import main.content.enums.GenericEnums;
import main.content.enums.macro.MACRO_CONTENT_CONSTS;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.MapEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 1/8/2017.
 */
public class ParticleManager extends GroupX {
    private static final boolean TEST = false;
    private static boolean ambienceOn = OptionsMaster.getGraphicsOptions().getBooleanValue(
            GRAPHIC_OPTION.AMBIENCE_VFX);
    private static boolean ambienceMoveOn;

    List<EmitterActor> dynamicVfx = new ArrayList<>();
    Map<Module, GlobalVfxMap> cache = new HashMap<>();

    GlobalVfxMap ambienceMap;

    public void initModule(Module module) {
        //should be part of gridPanel? Under SHadowMap?
        if (ambienceMap != null) {
            ambienceMap.remove();
        } else {
            bindEvents();
        }
        ambienceMap = cache.get(module);
        if (ambienceMap == null) {
            cache.put(module, ambienceMap = new GlobalVfxMap(module));
        }
        addActor(ambienceMap);
    }

    public ParticleManager() {

    }

    private void bindEvents() {
        GuiEventManager.bind(MapEvent.PREPARE_TIME_CHANGED, p -> {
            if (isAmbienceOn())
                ambienceMap.update((MACRO_CONTENT_CONSTS.DAY_TIME) p.get());
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
    }

    @Deprecated
    public static VFX_TEMPLATE getTemplate(Floor floor_) {
        if (floor_.getDungeonSubtype() != null)
            switch (floor_.getDungeonSubtype()) {
                case CAVE:
                case HIVE:

                case HELL:
                    return VFX_TEMPLATE.CAVE;
                case DUNGEON:
                case SEWER:
                case TOWER:
                case CASTLE:
                case DEN:
                case HOUSE:
                    return VFX_TEMPLATE.DUNGEON;
                case CRYPT:
                case BARROW:
                    return VFX_TEMPLATE.CRYPT;
            }
        return VFX_TEMPLATE.DEEP_MIST;
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

package libgdx.particles.ambi;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import eidolons.content.consts.VisualEnums;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.dungeon.location.struct.Floor;
import libgdx.gui.generic.GroupX;
import libgdx.particles.EmitterActor;
import eidolons.content.consts.VisualEnums.VFX_TEMPLATE;
import libgdx.screens.batch.CustomSpriteBatch;
import eidolons.system.options.GraphicsOptions.GRAPHIC_OPTION;
import eidolons.system.options.OptionsMaster;
import main.content.enums.GenericEnums;
import main.content.enums.macro.MACRO_CONTENT_CONSTS;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.MapEvent;

import java.util.ArrayList;
import java.util.List;

import static libgdx.bf.GridMaster.getCenteredPos;

/**
 * Created by JustMe on 1/8/2017.
 */
public class ParticleManager extends GroupX {
    public static final boolean TEST = false;
    private static boolean ambienceOn = OptionsMaster.getGraphicsOptions().getBooleanValue(
            GRAPHIC_OPTION.AMBIENCE_VFX);
    private static boolean ambienceMoveOn;

    List<EmitterActor> dynamicVfx = new ArrayList<>();
    ObjectMap<Module, GlobalVfxMap> cache = new ObjectMap<>(5);

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
            Vector2 v = null;
            if (l.get(1) instanceof Coordinates) {
                v = getCenteredPos(((Coordinates) l.get(1)));
            } else
                v = (Vector2) l.get(1);
            Ambience ambi;
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
                    return VisualEnums.VFX_TEMPLATE.CAVE;
                case DUNGEON:
                case SEWER:
                case TOWER:
                case CASTLE:
                case DEN:
                case HOUSE:
                    return VisualEnums.VFX_TEMPLATE.DUNGEON;
                case CRYPT:
                case BARROW:
                    return VisualEnums.VFX_TEMPLATE.CRYPT;
            }
        return VisualEnums.VFX_TEMPLATE.DEEP_MIST;
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
        if (!isAmbienceOn()) {
            return;
        }
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

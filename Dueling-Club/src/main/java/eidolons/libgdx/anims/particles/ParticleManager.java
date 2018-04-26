package eidolons.libgdx.anims.particles;

import com.badlogic.gdx.math.Vector2;
import eidolons.libgdx.gui.generic.GroupX;
import main.content.CONTENT_CONSTS2.EMITTER_PRESET;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.MapEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 1/8/2017.
 */
public class ParticleManager extends GroupX {
    private static final EMITTER_PRESET FOG_SFX = EMITTER_PRESET.SMOKE_TEST;
    private static boolean ambienceOn = true;
    private static boolean ambienceMoveOn;
    public boolean debugMode;
    List<EmitterMap> emitterMaps = new ArrayList<>();


    public ParticleManager() {
        GuiEventManager.bind(MapEvent.PREPARE_TIME_CHANGED, p -> {
            GuiEventManager.trigger(GuiEventType.INIT_AMBIENCE,
             new AmbienceDataSource((DAY_TIME) p.get()));
        });

        GuiEventManager.bind(GuiEventType.INIT_AMBIENCE, p -> {
            AmbienceDataSource dataSource = (AmbienceDataSource) p.get();
            clearChildren();
            emitterMaps.clear();
            for (String sub : dataSource.getEmitters()) {
                EmitterMap emitterMap = new EmitterMap(sub, dataSource);
                emitterMap.update();
                emitterMaps.add(emitterMap);
                addActor(emitterMap);
            }
        });
        GuiEventManager.bind(GuiEventType.UPDATE_AMBIENCE, p -> {

            emitterMaps.forEach(emitterMap -> {
                try {
                    emitterMap.update();
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            });

        });
    }

    public static boolean isAmbienceOn() {
        return ambienceOn;
    }

    public static void setAmbienceOn(boolean ambienceOn) {
        ParticleManager.ambienceOn = ambienceOn;
    }

    public static Ambience addFogOn(Vector2 at, List<Ambience> fogList) {
        Ambience fog = new Ambience(FOG_SFX) {
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
        if (fogList != null)
            fogList.add(fog);
        return fog;
    }

    public static boolean isAmbienceMoveOn() {
        return ambienceMoveOn;
    }

    public static void setAmbienceMoveOn(boolean ambienceMoveOn) {
        ParticleManager.ambienceMoveOn = ambienceMoveOn;
    }

}

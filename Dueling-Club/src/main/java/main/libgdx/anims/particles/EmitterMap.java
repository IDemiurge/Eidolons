package main.libgdx.anims.particles;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import main.content.CONTENT_CONSTS2.SFX;
import main.content.PARAMS;
import main.entity.obj.unit.Unit;
import main.game.bf.Coordinates;
import main.game.core.game.DC_Game;
import main.libgdx.bf.GridMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.Chronos;
import main.system.math.PositionMaster;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by JustMe on 1/9/2017.
 */
public class EmitterMap {

    private static final int MIN_DISTANCE_FROM_LIGHT = 1;
    private static final int MIN_DISTANCE_BETWEEN_FOG = 2;
    private final Pool<Ambience> ambiencePool = new Pool<Ambience>() {
        @Override
        protected Ambience newObject() {
            return new Ambience(getFogSfx());
        }
    };
    Map<Coordinates, Ambience> fogMap = new LinkedHashMap<>();

    public EmitterMap() {

        GuiEventManager.bind(GuiEventType.UPDATE_AMBIENCE, p -> {
            try {
                Chronos.mark("UPDATE_AMBIENCE");
                update();
                Chronos.logTimeElapsedForMark("UPDATE_AMBIENCE");
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    private SFX getFogSfx() {
        return SFX.SKULL2;
    }

//    public boolean contains(ParticleInterface actor) {
//        return emitters.contains(actor);
//    }

    public void update() {
        if (!isAmbienceOn()) {
            return;
        }

        cc:
        for (Coordinates c : DC_Game.game.getCoordinates()) {
            boolean add = true;
            boolean remove = false;
//         if (!GameScreen.getInstance().getGridPanel().isCoordinateVisible(c))
//             continue ;  если апдейт был бы быстрый, можно было бы не пре-создавать там где не видно

            //нужно оптимизировать!
            for (Unit unit : DC_Game.game.getUnits()) {
                if ((unit.isActiveSelected() ||
                        unit.checkParam(PARAMS.LIGHT_EMISSION)) &&
                        PositionMaster.getDistance(unit.getCoordinates(), c) <
                                MIN_DISTANCE_FROM_LIGHT
                                        + unit.getIntParam(PARAMS.LIGHT_EMISSION) / 25
                        ) {
                    add = false;
                    remove = true;
                }


            }
            for (Coordinates c1 : fogMap.keySet()) {
                if (PositionMaster.getDistance(c1, c) < MIN_DISTANCE_BETWEEN_FOG) {
                    add = false;
                    remove = true;
                }
            }

            if (add) {
                addSmoke(c);
            }
            if (remove) {
                removeSmoke(c);
            }
        }
    }

    private void removeSmoke(Coordinates c) {
        Ambience fog = fogMap.remove(c);
        if (fog == null) {
            return;
        }
        fog.remove();
        fog.setVisible(false);
        ambiencePool.free(fog);
    }

    private void addSmoke(Coordinates c) {
        if (fogMap.containsKey(c)) {
            return;
        }

        Vector2 v = GridMaster.
                getVectorForCoordinateWithOffset(c);
        Ambience fog = ambiencePool.obtain();
        fog.setTarget(c);
        fogMap.put(c, fog);
        fog.setPosition(v.x, v.y);
        //DungeonScreen.getInstance().getAmbienceStage().addActor(fog);
        fog.setVisible(true);
        fog.getEffect().start();
    }


    public boolean isAmbienceOn() {
        return ParticleManager.isAmbienceOn();
    }

}

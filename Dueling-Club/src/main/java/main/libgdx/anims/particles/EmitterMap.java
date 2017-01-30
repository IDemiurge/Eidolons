package main.libgdx.anims.particles;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import main.content.CONTENT_CONSTS2.SFX;
import main.content.PARAMS;
import main.entity.obj.DC_HeroObj;
import main.game.DC_Game;
import main.game.battlefield.Coordinates;
import main.libgdx.GameScreen;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.Chronos;
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
            return new Ambience(getFogSfx() );
        }
    };

    private SFX getFogSfx() {
        return SFX.SKULL2;
    }

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

//    public boolean contains(ParticleInterface actor) {
//        return emitters.contains(actor);
//    }


    public void update() {


        cc:
        for (Coordinates c : DC_Game.game.getCoordinates()) {
            boolean add = true;
            boolean remove = false;
//         if (!GameScreen.getInstance().getGridPanel().isCoordinateVisible(c))
//             continue ;  если апдейт был бы быстрый, можно было бы не пре-создавать там где не видно

            //нужно оптимизировать!
            for (DC_HeroObj unit : DC_Game.game.getUnits()) {
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

            if (add)
                addSmoke(c);
            if (remove)
                removeSmoke(c);
        }
    }

    private void removeSmoke(Coordinates c) {
        Ambience fog = fogMap.remove(c);
        if (fog == null) return;
        fog.remove();
        fog.setVisible(false);
        ambiencePool.free(fog);
    }

    private void addSmoke(Coordinates c) {
        if (fogMap.containsKey(c))
            return;

        Vector2 v = GameScreen.getInstance().getGridPanel().
                getVectorForCoordinateWithOffset(c);
        Ambience fog = ambiencePool.obtain();
        fog.setTarget(c);
        fogMap.put(c, fog);
        fog.setPosition(v.x, v.y);
        GameScreen.getInstance().getAmbienceStage().addActor(fog);
        fog.setVisible(true);
        fog.getEffect().start();
    }


}

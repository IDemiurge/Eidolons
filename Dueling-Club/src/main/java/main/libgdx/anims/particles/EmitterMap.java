package main.libgdx.anims.particles;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Pool;
import main.content.CONTENT_CONSTS2.SFX;
import main.game.bf.Coordinates;
import main.game.core.game.DC_Game;
import main.libgdx.bf.GridMaster;
import main.libgdx.screens.DungeonScreen;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.math.PositionMaster;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by JustMe on 1/9/2017.
 */
public class EmitterMap extends Group{

    private static final int MIN_DISTANCE_FROM_LIGHT = 1;
    private static final boolean HIDE_SMOKE_AROUND_MAIN_HERO =true ;
    private static   float MIN_FOG_DISTANCE_FROM_ALLY = 3;
    private static   float MIN_DISTANCE_BETWEEN_FOG = 3;
    private final Pool<Ambience> ambiencePool = new Pool<Ambience>() {
        @Override
        protected Ambience newObject() {
            return new Ambience(getFogSfx());
        }
    };
    Map<Coordinates, Ambience> fogMap = new LinkedHashMap<>();
    private static Boolean on;

    public EmitterMap( ) {
        GuiEventManager.bind(GuiEventType.UPDATE_AMBIENCE, p -> {
            try {
                update();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    public static void setOn(Boolean on) {
        EmitterMap.on = on;
    }

    public static Boolean getOn() {
        return on;
    }

    private SFX getFogSfx() {
        return SFX.SMOKE_TEST;
    }

//    public boolean contains(ParticleInterface actor) {
//        return emitters.contains(actor);
//    }

    public void initFog() {
        int xDistanceFog = 3;
        int yDistanceFog = 2;
        for (int x = 0; x < DungeonScreen.getInstance().getGridPanel().getRows(); x += xDistanceFog)
            for (int y = 0; y < DungeonScreen.getInstance().getGridPanel().getCols(); y += yDistanceFog) {
                addSmoke(new Coordinates(x, y));

            }

    }

    public void update() {
        if (!isAmbienceOn()) {
            return;
        }
        if (             DC_Game.game.getPlayer(true).getHeroObj()==null ) {
            return;
        }
        if (fogMap.isEmpty())
            initFog();
        for (Coordinates c1 : fogMap.keySet()) {
            Coordinates mainHeroCoordinates =
             DC_Game.game.getPlayer(true).getHeroObj().getCoordinates();
            if (HIDE_SMOKE_AROUND_MAIN_HERO&&
            PositionMaster.getDistance(c1, mainHeroCoordinates)
             < MIN_DISTANCE_BETWEEN_FOG) {
                hideSmoke(c1);
            }
            else addSmoke(c1);
        }

//        cc:
//        for (Coordinates c : DC_Game.game.getCoordinates()) {
//            boolean add = true;
//            boolean remove = false;
//            //TODO IDEA: based on Gamma?
//            //
////         if (!GameScreen.getInstance().getGridPanel().isCoordinateVisible(c))
////             continue ;  если апдейт был бы быстрый, можно было бы не пре-создавать там где не видно
////            for (Unit unit : DC_Game.game.getUnits()) {
////                if ((unit.isActiveSelected() ||
////                 unit.checkParam(PARAMS.LIGHT_EMISSION)) &&
////                 PositionMaster.getDistance(unit.getCoordinates(), c) <
////                  MIN_DISTANCE_FROM_LIGHT
////                   + unit.getIntParam(PARAMS.LIGHT_EMISSION) / 25
////                 ) {
////                    add = false;
////                    remove = true;
////                }
////            }
//            for (Coordinates c1 : fogMap.keySet()) {
//                if (PositionMaster.getDistance(c1, c) < MIN_DISTANCE_BETWEEN_FOG) {
//                    add = false;
//                    remove = true;
//                }
//            }
//
//            if (add) {
//                addSmoke(c);
//            }
//            if (remove) {
//                removeSmoke(c);
//            }
//        }
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

    private void hideSmoke(Coordinates c) {
        Ambience fog = fogMap.get(c);
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
         getCenteredPos(c);
        Ambience fog = ambiencePool.obtain();
        fog.setTarget(c);
        fogMap.put(c, fog);
        fog.setPosition(v.x, v.y);
        fog.added();
        if (! getChildren().contains(fog, true))
         addActor(fog);
        //DungeonScreen.getInstance().getAmbienceStage().addActor(fog);
        fog.setVisible(true);
        fog.getEffect().start();
    }


    public boolean isAmbienceOn() {
        return ParticleManager.isAmbienceOn();
    }

}

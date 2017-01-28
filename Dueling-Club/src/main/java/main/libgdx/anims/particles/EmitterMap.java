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
import main.system.math.PositionMaster;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 1/9/2017.
 */
public class EmitterMap {

    List<EmitterActor> ambientFx=    new LinkedList<>() ;
    private final Pool<Ambience> ambiencePool = new Pool<Ambience>() {
        @Override
        protected Ambience newObject() {
            return new Ambience(SFX.SMOKE_TEST);
        }
    };
    public EmitterMap() {

        GuiEventManager.bind(GuiEventType.UPDATE_AMBIENCE, p -> {
            try {
                update();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

//    public boolean contains(ParticleInterface actor) {
//        return emitters.contains(actor);
//    }


    public void update() {


     cc:   for (Coordinates c: DC_Game.game.getCoordinates() ) {
         boolean add=true;
         if (!GameScreen.getInstance().getGridPanel().isCoordinateVisible(c))
             continue ;
         for (DC_HeroObj unit :  DC_Game.game.getUnits()) {
                if ((unit.isActiveSelected()||
                 unit.checkParam(PARAMS.LIGHT_EMISSION)) &&
                 PositionMaster.getDistance(unit.getCoordinates(), c) < 3) {
                    add = false;continue cc;
                }


         } for (EmitterActor e :  ambientFx) {
             if (
              PositionMaster.getDistance(e.getTarget() , c) < 3)
                 add = false;
         }

         if ( add)
             addSmoke(c);
        }
    }

    private void addSmoke(Coordinates c) {
        Vector2 v=  GameScreen.getInstance().getGridPanel().
         getVectorForCoordinateWithOffset(c);
        Ambience smoke = ambiencePool.obtain();
        smoke.setTarget(c);
        ambientFx.add(smoke);
        smoke.setPosition(v.x, v.y);
        GameScreen.getInstance().getAmbienceStage().addActor(smoke);

        smoke.getEffect().getEmitters().get(0). start();
    }



}

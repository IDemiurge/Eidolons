package main.libgdx.anims.particles;

import com.badlogic.gdx.math.Vector2;
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

    List<ParticleInterface> ambientFx=    new LinkedList<>() ;

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
         for (DC_HeroObj unit :  DC_Game.game.getUnits()) {
                if (unit.checkParam(PARAMS.LIGHT_EMISSION) &&
                 PositionMaster.getDistance(unit.getCoordinates(), c) < 10) {

                } else {
                    addSmoke(GameScreen.getInstance().getGridPanel().
                     getVectorForCoordinateWithOffset(c));
                    continue cc;
                }
            }
        }
    }

    private void addSmoke(Vector2 v) {
        Ambience smoke = new Ambience(SFX.SMOKE_TEST);
        ambientFx.add(smoke);
        smoke.setPosition(v.x, v.y);
        GameScreen.getInstance().getAmbienceStage().addActor(smoke);

        smoke.getEffect().getEmitters().get(0). start();
    }


    public List<ParticleInterface> getAmbientFx() {
        return ambientFx;
    }

}

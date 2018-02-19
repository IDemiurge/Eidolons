package main.libgdx.screens.map.sfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import main.content.CONTENT_CONSTS2.SFX;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.libgdx.anims.particles.EmitterActor;

/**
 * Created by JustMe on 2/17/2018.
 */
public class MapParticles {

    Stage stage;
    public enum MAP_EMITTER_GROUP{
        MIST_WHITE(SFX.SMOKE_TEST, 500, 500, 70, 6),
        ;

        MAP_EMITTER_GROUP(SFX sfx, int x, int y, float distance, int number) {
            this.sfx = sfx;
            this.pos = new Vector2(x, y);
            this.distance = distance;
            this.number = number;
        }

        SFX sfx;
        DAY_TIME[] times;
        float[] intensity;
        Color[] colors;
        Vector2 pos;
        float distance;
        int number;
        float move;
    }

    public MapParticles(Stage stage) {
        this.stage = stage;
    }

    public void update(){
//        DAY_TIME time = getTime();


    }
        public void init(){
        //no displacement on 0?

//            for (String sub: DAY_TIME.values())

        for (MAP_EMITTER_GROUP sub : MAP_EMITTER_GROUP.values()) {
            for (int i = 0; i < sub.number; i++) {
            EmitterActor actor= new EmitterActor(sub.sfx);
                int mod = (i % 2 == 0) ? 1 : -1;
                float offsetX=mod*sub.distance*i;
                  mod = (i%3 == 0) ? 1 : -1;
                float offsetY=mod*sub.distance*i;
                actor.setPosition(sub.pos.x+offsetX, sub.pos.y+offsetY);
                stage.addActor(actor);

                actor.setColor(new Color(1, 0, 0, 1)
//                 sub.colors[i]
                );
                actor.start(); //desync? alpha fluct?

            }

        }


    }

}

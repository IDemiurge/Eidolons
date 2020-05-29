package eidolons.game.module.cinematic.flight;

import com.badlogic.gdx.math.Vector2;
import eidolons.game.module.cinematic.flight.FlyingObj.FlyObjData;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.grid.moving.MoveController;
import main.system.auxiliary.RandomWizard;

import java.util.LinkedHashSet;
import java.util.Set;
/*
sends certain kinds of actors flying in certain way for the FLIGHT CINEMATICS
start    - already in motion?
 */
public class FlyingObjs {

    private final float minDelay;
    private final float maxDelay;
    private final float angle;
    FLY_OBJ_TYPE type;
    FlyObjData data; // how often to appear? how fast to go?
    CinematicPlatform platform; // don't want to collide with it right? Or is it ABOVE?..
    private float timer=0;
    Set<FlyingObj> objects = new LinkedHashSet<>(); //might want to *STOP* them or change their ANGLE

    public FlyingObjs(FLY_OBJ_TYPE type, FlyObjData data, CinematicPlatform platform) {
        this.type = type;
        this.data = data;
        this.platform = platform;
        minDelay =data.getFloatValue(FlyingObj.FLY_OBJ_VALUE.minDelay) ;
        maxDelay =data.getFloatValue(FlyingObj.FLY_OBJ_VALUE.maxDelay) ;
        //concurrent instances, distance, ...
        angle = platform.angle;
    }

    public void act(float delta) {
        if (timer <= 0) {
            timer = RandomWizard.getRandomFloatBetween(minDelay, maxDelay);
            sendObject();
        } else {
            timer-=delta;
        }
        for (FlyingObj object : objects) {
            object.act(delta);
        }
    }

    private void sendObject() {
        FlyingObj obj = create();
       Vector2 v=getPosForNew();
        obj.setPosition(v.x, v.y);
        objects.add(obj);
    }

    private Vector2 getPosForNew() {
        Vector2 v= new Vector2();
        //
        return v;
    }


    public FlyingObj create() { //override?
        // sprites, textures, vfx - generally any actor perhaps
        MoveController controller = new FlyMoveController(data);
        SuperActor actor=createActor();
        return new FlyingObj(actor, controller);
    }

    private SuperActor createActor() {
        switch (type) {
            case cloud:
               // return new FadeImageContainer(Images.)
        }
        return null;
    }

    public enum FLY_OBJ_TYPE {
        cloud,
        isle,
        debris,
        light,
        ;
    }


}

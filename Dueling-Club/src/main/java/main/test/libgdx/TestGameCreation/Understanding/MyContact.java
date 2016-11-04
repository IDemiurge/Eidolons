package main.test.libgdx.TestGameCreation.Understanding;

import com.badlogic.gdx.physics.box2d.*;
import main.test.libgdx.TestGameCreation.Ball;

/**
 * Created by PC on 03.11.2016.
 */
public class MyContact implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Body a = contact.getFixtureA().getBody();
        Body b = contact.getFixtureB().getBody();
       if (a.getUserData() != null && b.getUserData() != null){
           if (a.getUserData().equals("player") && b.getUserData().equals("ball") ||
                   b.getUserData().equals("player") && a.getUserData().equals("ball")){
               GameScore.Score++;
               System.out.println(GameScore.Score);
               if (GameScore.Score == 2){
                   myUnderstandingField.TimeToCreate = true;
               }
           }
       }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }
}

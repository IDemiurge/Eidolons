package eidolons.libgdx;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

public class ActorMaster {
    public static Actor addTop(Group group, Actor actor) {
        group.addActor(actor);
        GDX.top(actor);
        return actor;
    }
}

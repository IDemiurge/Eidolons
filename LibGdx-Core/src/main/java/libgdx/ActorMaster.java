package libgdx;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

public class ActorMaster {
    public static Actor addTop(Group group, Actor actor) {
        group.addActor(actor);
        GDX.top(actor);
        return actor;
    }
    public static Actor addCenter(Group group, Actor actor) {
        group.addActor(actor);
        actor.setY(GDX.centerHeight(actor));
        actor.setX(GDX.centerWidth(actor));
        return actor;
    }
    public static Actor addCenterX(Group group, Actor actor) {
        group.addActor(actor);
        actor.setX(GDX.centerWidth(actor));
        return actor;
    }
    public static Actor addCenterY(Group group, Actor actor) {
        group.addActor(actor);
        actor.setY(GDX.centerHeight(actor));
        return actor;
    }
}

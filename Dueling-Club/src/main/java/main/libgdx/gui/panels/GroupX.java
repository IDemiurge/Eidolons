package main.libgdx.gui.panels;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import main.system.auxiliary.ClassMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 3/2/2018.
 */
public class GroupX extends Group {

    public void addAt(float x, float y, Actor actor) {
        super.addActor(actor);
        actor.setPosition(x, y);
    }

    public   Group getFirstParentOfClass(Class clazz ) {
        Group actor = getParent();
        while (true) {
            actor = actor.getParent();
            if (actor == null) {
                break;
            }
            if (ClassMaster.isInstanceOf(actor, clazz)) {
                return actor;
            }

        }
        return null ;
    }
        public static List<Group> getAncestors(Actor actor) {
        List<Group> list = new ArrayList<>();
        while (true) {
            actor = actor.getParent();
            if (actor == null) {
                break;
            }
            list.add(actor.getParent());
        }
        return list;
    }
    public Array<Action> getAllChildrenActions() {
        return getAllChildrenActions(null);
    }

    public Array<Action> getAllChildrenActions(Class actionClass) {
        Array<Action> list = new Array<>();
        Group group = this;
        addActions(list, group, actionClass);

        return list;
    }

    private void addActions(Array<Action> list, Actor actor, Class actionClass) {

        for (Action sub : actor.getActions()) {
            if (actionClass != null)
                if (!ClassMaster.isInstanceOf(sub, actionClass)) {
                    continue;
                }
            list.add(sub);
        }

        if (actor instanceof Group) {

            for (Actor sub :   ((Group) actor) .getChildren()) {
                    addActions(list,   sub, actionClass);
            }
        }
    }
}

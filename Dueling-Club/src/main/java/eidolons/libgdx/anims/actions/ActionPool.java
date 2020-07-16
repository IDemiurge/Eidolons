package eidolons.libgdx.anims.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by JustMe on 9/17/2017.
 */
public class ActionPool extends Pool<Action> {

    Class<? extends Action> aClass;

    public ActionPool(Class<? extends Action> aClass) {
        setClass(aClass);
    }

    public void setClass(Class<? extends Action> aClass) {
        this.aClass = aClass;
    }

    @Override
    protected Action newObject() {
        try {
            return (Action) aClass.getConstructors()[0].newInstance(null);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return null;
    }

}

package eidolons.libgdx.anims.actions;


import com.badlogic.gdx.scenes.scene2d.Action;

import java.util.function.Supplier;

public class BlockingAction extends Action {

    private   float waitPeriod;
    private  Supplier<Boolean> condition;

    public BlockingAction(Supplier<Boolean> o) {
        condition = o;
    }

    public BlockingAction(float waitPeriod) {
        this.waitPeriod = waitPeriod;
    }

    @Override
    public boolean act(float delta) {
        if (condition != null) {
            return condition.get();
        }
        waitPeriod-=delta;

        return waitPeriod<=0;
    }
}

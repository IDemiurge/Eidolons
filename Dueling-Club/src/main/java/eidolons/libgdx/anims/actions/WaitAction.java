package eidolons.libgdx.anims.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import java.util.function.Predicate;

public class WaitAction extends TemporalAction {
    Predicate<Float> stayFullCondition;

    public WaitAction(Predicate<Float> stayFullCondition) {
        this.stayFullCondition = stayFullCondition;
    }

    public WaitAction(float duration) {
        super(duration);
    }

    public boolean act(float delta) {
        if (stayFullCondition != null) {
            super.act(delta);
            return stayFullCondition.test(getTime());
        }
        return super.act(delta);
    }

    @Override
    protected void update(float percent) {
    }
}

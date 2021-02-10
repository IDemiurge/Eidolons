package libgdx.anims.actions;

import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;

/**
 * Created by JustMe on 3/30/2018.
 */
public class AutoFloatAction extends FloatAction {
    Float float_;

    public AutoFloatAction() {
    }

    @Override
    protected void update(float percent) {
        super.update(percent);
        float_ = getValue();
    }

    public void setFloat_(Float float_) {
        this.float_ = float_;
    }

    public AutoFloatAction(Float float_) {
        this.float_ = float_;
    }
}

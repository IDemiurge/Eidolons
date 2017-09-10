package main.libgdx.anims.actions;

import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;

/**
 * Created by JustMe on 9/10/2017.
 */
public class RotateByActionLimited extends RotateByAction {

    private float initialRotation;

    @Override
    public void setAmount(float rotationAmount) {
        super.setAmount(rotationAmount);
    }

    @Override
    protected void begin() {
        super.begin();
        saveInitialState();
    }

    private boolean checkExceeds() {
        return Math.abs(target.getRotation() -initialRotation)   >= Math.abs(getAmount());
    }

    private void saveInitialState() {
        initialRotation = target.getRotation();
    }

    private void applyLeftover() { if (initialRotation+getAmount()!= target.getRotation())
        target.setRotation(initialRotation+getAmount());
    }

    @Override
    public boolean act(float delta) {
        boolean result = super.act(delta);
        if (result) {
            applyLeftover();
        }
        return result;
    }


    @Override
    protected void updateRelative(float percentDelta) {
        if (checkExceeds())
            return;
        super.updateRelative(percentDelta);
    }

}

package eidolons.libgdx.anims.actions;

import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;
import main.system.auxiliary.log.LogMaster;

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
//        main.system.auxiliary.log.LogMaster.log(1, "rotation; initial = " + initialRotation
//         + " current= " + target.getRotation() + " getAmount= " + getAmount());
    }

    private boolean checkExceeds() {
        return Math.abs(target.getRotation() - initialRotation) >= Math.abs(getAmount());
    }

    private void saveInitialState() {
        initialRotation = target.getRotation();
    }

    private void applyLeftover() {
        if (initialRotation + getAmount() != target.getRotation()) {
            target.setRotation(initialRotation + getAmount());
            LogMaster.log(1, "applyLeftover rotation ");
        } else
            LogMaster.log(1, "rotation; no applyLeftover ");
    }

    @Override
    public boolean act(float delta) {
//        LogMaster.log(1, "rotation; initial = " + initialRotation
//         + " current= " + target.getRotation());
        boolean result = super.act(delta);
        if (result) {
//            applyLeftover();
//            return true;
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

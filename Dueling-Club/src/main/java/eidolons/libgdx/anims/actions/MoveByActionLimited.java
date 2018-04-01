package eidolons.libgdx.anims.actions;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;

/**
 * Created by JustMe on 9/10/2017.
 */
public class MoveByActionLimited extends MoveByAction {
    private Float startPointX;
    private Float startPointY;

    public MoveByActionLimited() {
    }

    public MoveByActionLimited(Vector2 origin) {
        startPointX = origin.x;
        startPointY = origin.y;
    }

    public void setStartPointX(Float startPointX) {
        this.startPointX = startPointX;
    }

    public void setStartPointY(Float startPointY) {
        this.startPointY = startPointY;
    }

    @Override
    protected void begin() {
        super.begin();
        if (startPointX == null)
            startPointX = target.getX();
        if (startPointY == null)
            startPointY = target.getY();
    }

    private void applyLeftover() {
        if (target.getX() == 0) if (target.getY() == 0) return;
        float x = startPointX + getAmountX() - target.getX();
        float y = startPointY + getAmountY() - target.getY();
        if (x != 0 || y != 0) {
//            target.moveBy(x, y);
        }
    }

    @Override
    protected void end() {
        applyLeftover();
    }

    @Override
    protected void updateRelative(float percentDelta) {
        if (getAmountX() != 0)
            if (Math.abs(startPointX - target.getX()) >= Math.abs(getAmountX()))
                return;
        if (getAmountY() != 0) if (Math.abs(startPointY - target.getY()) >= Math.abs(getAmountY()))
            return;
        super.updateRelative(percentDelta);
    }
}

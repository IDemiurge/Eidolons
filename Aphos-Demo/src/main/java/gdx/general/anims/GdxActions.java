package gdx.general.anims;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.anims.actions.AfterActionSmart;
import main.game.bf.directions.DIRECTION;

public class GdxActions {
    public static void addDisplace(Actor actor, DIRECTION d, float intensity) {
        float x = 0;
        float y = 0;
        if (d.isGrowX() != null) {
            x = d.isGrowX() ? intensity : -intensity;
        }
        if (d.isGrowY() != null) {
            y = d.isGrowY() ? intensity : -intensity;
        }
        Vector2 dest = new Vector2(x, y);

        MoveToAction moveToAction = ActionMasterGdx.getMoveToAction(dest.x, dest.y, 0.2f);
        ActionMasterGdx.addAction(actor, moveToAction);

        MoveToAction moveBack = ActionMasterGdx.getCopy(moveToAction, MoveToAction.class);
        moveBack.setPosition(0, 0);
        AfterActionSmart after = new AfterActionSmart();
        after.setAction(moveBack);
        ActionMasterGdx.addAction(actor, after);
        after.setAction(moveBack);
    }
}

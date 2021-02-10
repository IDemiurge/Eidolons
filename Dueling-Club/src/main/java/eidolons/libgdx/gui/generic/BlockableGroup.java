package eidolons.libgdx.gui.generic;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.Array;
import eidolons.libgdx.anims.actions.BlockingAction;

public class BlockableGroup extends GroupX {

    private boolean blocked;

    @Override
    public void act(float delta) {
        if (!isBlocked())
            super.act(delta);
        else {
            Array<Action> actionsOfClass = getActionsOfClass(BlockingAction.class);
            if (actionsOfClass.size > 0) {
                boolean result = true;
                for (Action action : actionsOfClass) {
                    result &= action.act(delta);
                }
                if (result) {
                    setBlocked(false);
                }
            }
        }
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}

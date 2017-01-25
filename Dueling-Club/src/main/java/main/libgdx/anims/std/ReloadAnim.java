package main.libgdx.anims.std;

import com.badlogic.gdx.scenes.scene2d.Action;
import main.entity.Entity;
import main.libgdx.anims.AnimData;

/**
 * Created by JustMe on 1/19/2017.
 */
public class ReloadAnim extends RangedAttackAnim {
    public ReloadAnim(Entity active, AnimData params) {
        super(active );
    }

    @Override
    protected Action getAction() {
        return super.getAction();
    }
}

package main.libgdx.anims.std;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import main.entity.Entity;
import main.libgdx.anims.AnimData;
import main.libgdx.bf.GridConst;

/**
 * Created by JustMe on 1/19/2017.
 */
public class ReloadAnim extends RangedAttackAnim {
    public ReloadAnim(Entity active ) {
        super(active );
        data=new AnimData("missile speed=100;");
    }

    @Override
    protected Action getAction() {
        return getMoveAmmoAction();
    }

    @Override
    public void start() {
        super.start();
    }

    protected MoveByAction getMoveAmmoAction() {
        MoveByAction mainMove = new MoveByAction();
        mainMove.setAmount(destination.x-origin.x, destination.y-origin.y);
        mainMove.setDuration(duration);
        return mainMove;
    }

    @Override
    public void initPosition() {
        super.initPosition();
//offsetX
    initialAngle=0;
        destination.set(destination.x-GridConst.CELL_W/8, destination.y-GridConst.CELL_H/2);

    }


}

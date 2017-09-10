package main.libgdx.anims.weapons;

import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.game.bf.Coordinates;

/**
 * Created by JustMe on 9/9/2017.
 */
public class Ranged3dAnim extends Weapon3dAnim {
    public Ranged3dAnim(DC_ActiveObj active) {
        super(active);
    }

    @Override
    public Coordinates getDestinationCoordinates() {
        return getOriginCoordinates();
    }

    @Override
    public void start(Ref ref) {
        //missile to target, weapon on source
        //it's like there will be "precast" animation really
        super.start(ref);
    }
}

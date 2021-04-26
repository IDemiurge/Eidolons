package libgdx.adapters;

import eidolons.game.core.ActionInput;
import eidolons.system.libgdx.api.GdxAnims;
import libgdx.anims.main.ActionAnimMaster;

public class GdxAnimsImpl implements GdxAnims {
    @Override
    public void actionResolves(ActionInput input) {
        ActionAnimMaster.animate(input);
    }
}

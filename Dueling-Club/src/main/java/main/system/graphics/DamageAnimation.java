package main.system.graphics;

import main.content.CONTENT_CONSTS.DAMAGE_TYPE;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.system.graphics.AnimationManager.ANIM_TYPE;

import java.awt.*;

public class DamageAnimation extends Animation {

    private DAMAGE_TYPE dmg_type;
    private Integer amount;
    private Obj source;
    private Obj target;
    private Integer e_amount;
    private Integer t_amount;

    public DamageAnimation(Ref ref) {
        super(ANIM_TYPE.DAMAGE);
        dmg_type = ref.getDamageType();
        amount = ref.getAmount();
        source = ref.getSourceObj();
        target = ref.getTargetObj();
    }

    @Override
    public Object getArg() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getArgString() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ANIM clone() {
        return null;
    }

    @Override
    protected Image getThumbnailImage() {
        return null;
    }

    @Override
    public boolean draw(Graphics g) {
        // TODO Auto-generated method stub
        return false;
    }

}

package main.entity.tools.active;

import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.Obj;
import main.entity.tools.EntityAnimator;
import main.entity.tools.EntityMaster;
import main.system.graphics.ANIM;
import main.system.graphics.AnimPhase;
import main.system.graphics.AnimPhase.PHASE_TYPE;
import main.system.graphics.AttackAnimation;
import main.system.graphics.PhaseAnimation;
import main.system.threading.WaitMaster;

/**
 * Created by JustMe on 2/23/2017.
 */
public class ActiveAnimator extends EntityAnimator<DC_ActiveObj> {
    private static final String[] ANIMATION_EXCEPTIONS = {"Turn Clockwise", "Move",
     "Turn Anticlockwise",};
    PhaseAnimation anim;
    private boolean costAnimAdded;
    private String animationKey;
    private ANIM animation;

    public ActiveAnimator(DC_ActiveObj entity, EntityMaster<DC_ActiveObj> entityMaster) {
        super(entity, entityMaster);
    }

    public void waitForAnimation() {
        if (anim != null) {
            if (anim.isStarted()) {
                while (!anim.isFinished()) { // TODO limit?
                    WaitMaster.WAIT(80);
                }
            }
        }
    }

    public Object getAnimationKey() {
        if (animationKey != null) {
            return animationKey;
        }
        String id = getName();
        // if (isStandardAttack()) {
        // id = getParentAction().getName();
        // }
        if (getRef().getTargetObj() == null) {
            animationKey = id + " by " + getRef().getSourceObj().getName();
        } else {
            animationKey = id + " on " + getRef().getTargetObj().getName();
        }
        return animationKey;
    }

    public void animate(Obj target) {
        Ref REF = getRef().getCopy();
        REF.setTarget(target.getId());
        animate(REF);
    }

    public void animate(Ref ref) {

       getMaster(). getGame().getAnimationManager().actionResolves(getEntity(), getRef());

        // phases? for generic actions - turn, modes, inventory etc - ?

    }

    public void animate() {
        if (checkAnimationOmitted()) {
            return;
        }

        animate(getRef());
    }


    private boolean checkAnimationOmitted() {

        for (String exception : ANIMATION_EXCEPTIONS) {
            if (getName().equalsIgnoreCase(exception)) {
                return true;
            }
        }
        return false;
    }

    private void addCostAnim() {
        if (getAnimation() != null) {
            if (!getEntity(). isSubActionOnly()) {
                anim.addStaticPhase(new AnimPhase(PHASE_TYPE.COSTS_PAID,getEntity().getCosts()));
            }
        }
        setCostAnimAdded(true);
    }

    private boolean isCostAnimAdded() {
        return costAnimAdded;
    }

    public void setCostAnimAdded(boolean costAnimAdded) {
        this.costAnimAdded = costAnimAdded;
    }

    public void addResolvesPhase() {

        if (animation != null)

        {
            if (!(animation instanceof AttackAnimation)) {
                animation.addPhase(new AnimPhase(PHASE_TYPE.ACTION_RESOLVES, this));
            }
        }

    }


    public void initAnimation() {
        getMaster().getGame().getAnimationManager().newAnimation(anim );
        if (getEntity(). getParentAction() != null) // TODO ?
        {
            getEntity().getParentAction().setAnimation(anim);
        }
    }

    public ANIM getAnimation() {
        return animation;
    }

    @Override
    public ActiveMaster getMaster() {
        return (ActiveMaster) super.getMaster();
    }

}

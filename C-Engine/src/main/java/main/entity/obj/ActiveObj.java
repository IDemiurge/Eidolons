package main.entity.obj;

import main.ability.Abilities;
import main.elements.targeting.Targeting;
import main.entity.OBJ;
import main.entity.group.GroupImpl;
import main.system.graphics.ANIM;

/**
 *
 */
public interface ActiveObj extends Cancellable, Active, OBJ {

    void playCancelSound();

    Obj getOwnerObj();

    boolean isFree();

    boolean isEffectSoundPlayed();

    void setEffectSoundPlayed(boolean effectSoundPlayed);

    Abilities getAbilities();

    Targeting getTargeting();

    boolean isForcePresetTarget();

    void setForcePresetTarget(boolean b);

    boolean canBeActivated();

    boolean isZone();

    boolean isMissile();

    boolean isOffhand();

    ANIM getAnimation();

    void initAnimation();

    boolean isAttackGeneric();

    boolean isBlocked();

    boolean isRanged();

    boolean isMelee();

    boolean isMove();

    boolean isTurn();

    Obj getTargetObj();

    GroupImpl getTargetGroup();

    boolean isConstructed();

    void setConstructed(boolean b);
}

package main.entity.obj;

import main.ability.Abilities;
import main.elements.targeting.Targeting;
import main.entity.OBJ;
import main.system.graphics.ANIM;

/**
 *
 */
public interface ActiveObj extends Cancellable, Active, OBJ {

    void playCancelSound();

    Obj getOwnerObj();

    void setConstructed(boolean b);

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

    boolean isAttack();

    boolean isBlocked();

    boolean isRanged();

    boolean isMelee();

    boolean isMove();

    boolean isTurn();
}

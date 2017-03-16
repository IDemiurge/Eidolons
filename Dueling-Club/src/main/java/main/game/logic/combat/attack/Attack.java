package main.game.logic.combat.attack;

import main.ability.effects.Effect;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.item.DC_WeaponObj;
import main.entity.obj.unit.Unit;
import main.game.logic.combat.damage.Damage;
import main.rules.attack.InstantAttackRule.INSTANT_ATTACK_TYPE;
import main.system.graphics.AttackAnimation;

import java.util.List;

public class Attack {
    public static final Integer DAMAGE_NOT_SET = -1;
    AttackAnimation animation;
    private boolean sneak;
    private boolean offhand;
    private boolean free;
    private boolean counter;
    private boolean attackOfOpportunity;
    private boolean instant;
    private boolean canCounter;
    private boolean ranged;
    private boolean critical;
    private boolean dodged;
    private boolean lethal;
    private Effect onHit;
    private Effect onKill;
    private Effect onAttack;
    private Ref ref;
    private DC_ActiveObj action;
    private Unit attacker;
    private Unit attacked;
    private boolean countered;
    private Integer damage = DAMAGE_NOT_SET;
    private Integer remainingDamage;
    private DAMAGE_TYPE dmg_type;
    private boolean triggered;
    private List<Damage> rawDamage;
    private int damageDealt;
    private INSTANT_ATTACK_TYPE instantAttackType;
    private DC_WeaponObj weapon;
    private boolean doubleStrike;

    public Attack(Ref ref, boolean offhand, boolean counter, boolean canCounter, boolean free,
                  Effect onHit, Effect onKill) {
        this.canCounter = canCounter;

        this.free = free;
        this.offhand = offhand;
        this.onHit = onHit;
        this.onKill = onKill;

        this.ref = ref;
        try {
            attacker = (Unit) ref.getSourceObj();
            attacked = (Unit) ref.getTargetObj();
            action = (DC_ActiveObj) ref.getActive();
            // sneak = DC_AttackMaster.checkSneak(ref); elsewhere
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!offhand) {
            if (action != null) {
                offhand = action.isOffhand();
            }
        }
        if (!counter) {
            counter = action.isCounterMode();
        }
        this.counter = counter;
        instant = action.isInstantMode();
        attackOfOpportunity = action.isAttackOfOpportunityMode();

        if (this.canCounter) {
            if (instant || attackOfOpportunity || counter) {
                this.canCounter = false;
            }
        }
    }

    public static Object getAnimationKey(DC_ActiveObj action) {
        return "Attack anim for "
                + action.getAnimator().getAnimationKey();
    }

    @Override
    public String toString() {
        return "Attack by" + attacker.getNameAndCoordinate() + " on "
                + attacked.getNameAndCoordinate();
    }

    public Integer getPrecalculatedDamageAmount() {
        return new AttackCalculator(this, true).calculateFinalDamage();
    }

    public DAMAGE_TYPE getDamageType() {
        DAMAGE_TYPE dmg_type = action.getDamageType();
        if (dmg_type == null) {
            DC_WeaponObj weapon = getWeapon();
            if (weapon == null) {
                dmg_type = attacker.getDamageType();
            } else {
                dmg_type = weapon.getDamageType();
            }
        }
        return dmg_type;
    }

    public void setDamageType(DAMAGE_TYPE dmg_type) {
        this.dmg_type = dmg_type;
    }

    public DC_WeaponObj getWeapon() {
        if (weapon == null) {
            if (offhand) {
                weapon = (DC_WeaponObj) ref.getObj(KEYS.OFFHAND);
            } else {
                weapon = (DC_WeaponObj) ref.getObj(KEYS.WEAPON);
            }
        }
        return weapon;
    }

    public boolean isSneak() {
        return sneak;
    }

    public void setSneak(boolean sneak) {
        this.sneak = sneak;
    }

    public boolean isOffhand() {
        return offhand;
    }

    public void setOffhand(boolean offhand) {
        this.offhand = offhand;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public boolean isDisengagement() {
        return getInstantAttackType() == INSTANT_ATTACK_TYPE.DISENGAGEMENT;
    }

    public INSTANT_ATTACK_TYPE getInstantAttackType() {
        return instantAttackType;
    }

    public void setInstantAttackType(INSTANT_ATTACK_TYPE type) {
        instantAttackType = type;
    }

    public boolean isCounter() {
        return (counter);
        // if (counter)
        // return true;
        // return (action.isCounterMode() || action.isInstantMode() || action
        // .isAttackOfOpportunityMode());
    }

    public void setCounter(boolean counter) {
        this.counter = counter;
    }

    public boolean isCanCounter() {
        return canCounter;
    }

    public void setCanCounter(boolean canCounter) {
        this.canCounter = canCounter;
    }

    public boolean isRanged() {
        return ranged;
    }

    public void setRanged(boolean ranged) {
        this.ranged = ranged;
    }

    public boolean isCritical() {
        return critical;
    }

    public void setCritical(boolean critical) {
        this.critical = critical;
    }

    public boolean isDodged() {
        return dodged;
    }

    public void setDodged(boolean dodged) {
        this.dodged = dodged;
    }

    public Effect getOnHit() {
        return onHit;
    }

    public void setOnHit(Effect onHit) {
        this.onHit = onHit;
    }

    public Effect getOnKill() {
        return onKill;
    }

    public void setOnKill(Effect onKill) {
        this.onKill = onKill;
    }

    public Effect getOnAttack() {
        return onAttack;
    }

    public void setOnAttack(Effect onAttack) {
        this.onAttack = onAttack;
    }

    public Ref getRef() {
        return ref;
    }

    public void setRef(Ref ref) {
        this.ref = ref;
    }

    public DC_ActiveObj getAction() {
        return action;
    }

    public void setAction(DC_ActiveObj action) {
        this.action = action;
    }

    public Unit getAttacker() {
        return attacker;
    }

    public void setAttacker(Unit attacker) {
        this.attacker = attacker;
    }

    public Unit getAttacked() {
        return attacked;
    }

    public void setAttacked(Unit attacked) {
        this.attacked = attacked;
    }

    public boolean isAttackOfOpportunity() {
        return attackOfOpportunity;
    }

    public void setAttackOfOpportunity(boolean attackOfOpportunity) {
        this.attackOfOpportunity = attackOfOpportunity;
    }

    public boolean isInstant() {
        return instant;
    }

    public void setInstant(boolean instant) {
        this.instant = instant;
    }

    public boolean isCountered() {
        return countered;
    }

    public void setCountered(boolean countered) {
        this.countered = countered;
    }

    public Integer getRemainingDamage() {
        remainingDamage = damage - damageDealt;
        return remainingDamage;
    }

    public AttackAnimation getAnimation() {
        return animation;
    }

    public void setAnimation(AttackAnimation animation) {
        this.animation = animation;
        action.setAnimation(animation);
        animation.setAttack(this);
    }

    public DC_WeaponObj getShield() {
        DC_WeaponObj weapon = attacked.getWeapon(true);
        if (weapon != null) {
            if (weapon.isShield()) {
                return weapon;
            }
        }
        return null;
    }

    public boolean isExtra() {
        return attackOfOpportunity || counter || instant;
    }

    public List<Damage> getRawDamage() {
        return rawDamage;
    }

    public void setRawDamage(List<Damage> rawDamage) {

        this.rawDamage = rawDamage;

    }

    public boolean isLethal() {
        return lethal;
    }

    public void setLethal(boolean lethal) {
        this.lethal = lethal;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(Integer damage) {
        this.damage = damage;
    }

    public void damageDealt(int damageDealt) {
        this.damageDealt += damageDealt;
    }

    public int getDamageDealt() {
        return damageDealt;
    }

    public boolean isTriggered() {
        return triggered;
    }

    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }

    public boolean isDoubleStrike() {
        return doubleStrike;
    }

    public void reset() {
        critical = false;
        dodged = false;
        countered = false;

    }

    public void setDoubleStrike(boolean doubleStrike) {
        this.doubleStrike = doubleStrike;
    }
}

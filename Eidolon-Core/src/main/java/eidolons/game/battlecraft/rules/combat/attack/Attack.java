package eidolons.game.battlecraft.rules.combat.attack;

import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.item.WeaponItem;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.rules.combat.damage.Damage;
import main.ability.effects.Effect;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.NewRpgEnums;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.system.ExceptionMaster;

import java.util.List;

import static main.content.enums.entity.NewRpgEnums.HitType.hit;

public class Attack {
    public static final Integer DAMAGE_NOT_SET = -1;
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

    private boolean parried;
    private boolean lethal;
    private Effect onHit;
    private Effect onKill;
    private Effect onAttack;
    private Ref ref;
    private ActiveObj action;
    private Unit attacker;
    private BattleFieldObject attacked;
    private boolean countered;
    private Integer damage = DAMAGE_NOT_SET;
    private boolean triggered;
    private List<Damage> rawDamage;
    private int damageDealt;
    private WeaponItem weapon;
    private boolean doubleStrike;
    private NewRpgEnums.HitType hitType;
    private int accuracyRate;

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
            attacked = (BattleFieldObject) ref.getTargetObj();
            action = (ActiveObj) ref.getActive();
            // sneak = DC_AttackMaster.checkSneak(ref); elsewhere
        } catch (Exception e) {
            ExceptionMaster.printStackTrace(e);
        }
        if (!offhand) {
            if (action != null) {
                offhand = action.isOffhand();
            }
        }
        if (action != null) {
        if (!counter) {
            counter = action.isCounterMode();
        }
        setCounter(counter);
        setInstant(action.isInstantMode());
        setAttackOfOpportunity(action.isAttackOfOpportunityMode());

        }
        if (this.canCounter) {
            if (instant || attackOfOpportunity || counter) {
                this.canCounter = false;
            }
        }
    }

    @Override
    public String toString() {
        return "Attack by " + attacker.getNameAndCoordinate() + " on "
         + attacked.getNameAndCoordinate();
    }

    public String toLogString() {
        return getAction().getName() + " by " + attacker.getNameIfKnown() + " on "
         + attacked.getNameIfKnown();
    }

    public Integer getPrecalculatedDamageAmount() {
        return new AttackCalculator(this, true).calculateFinalDamage();
    }

    public DAMAGE_TYPE getDamageType() {
        DAMAGE_TYPE dmg_type = action.getDamageType();
        if (dmg_type == null) {
            WeaponItem weapon = getWeapon();
            if (weapon == null) {
                dmg_type = attacker.getDamageType();
            } else {
                dmg_type = weapon.getDamageType();
            }
        }
        return dmg_type;
    }

    public void setDamageType(DAMAGE_TYPE dmg_type) {
    }

    public WeaponItem getWeapon() {
        if (weapon == null) {
            if (offhand) {
                weapon = (WeaponItem) ref.getObj(KEYS.OFFHAND);
            } else {
                weapon = (WeaponItem) ref.getObj(KEYS.WEAPON);
            }
        }
        if (weapon == null) {
            return attacker.getActiveWeapon(offhand);
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

    public ActiveObj getAction() {
        return action;
    }

    public void setAction(ActiveObj action) {
        this.action = action;
    }

    public Unit getAttacker() {
        return attacker;
    }

    public void setAttacker(Unit attacker) {
        this.attacker = attacker;
    }

    public BattleFieldObject getAttackedUnit() {
        if (attacked instanceof Unit)
            return attacked;
        return null;
    }

    public BattleFieldObject getAttacked() {
        return attacked;
    }

    public void setAttacked(BattleFieldObject attacked) {
        this.attacked = attacked;
        ref.setTarget(attacked.getId());
        action.setTargetObj(attacked);
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
        Integer remainingDamage = damage - damageDealt;
        return remainingDamage;
    }

    public WeaponItem getShield() {
        if (attacked instanceof Unit) {
            WeaponItem weapon = ((Unit) attacked).getWeapon(true);
            if (weapon != null) {
                if (weapon.isShield()) {
                    return weapon;
                }
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

    public void setDoubleStrike(boolean doubleStrike) {
        this.doubleStrike = doubleStrike;
    }

    public void reset() {
        critical = false;
        dodged = false;
        countered = false;

    }

    public boolean isParried() {
        return parried;
    }

    public void setParried(boolean parried) {
        this.parried = parried;
    }

    public void setHitType(NewRpgEnums.HitType hitType) {
        this.hitType = hitType;
    }

    public NewRpgEnums.HitType getHitType() {
        if (hitType==null ){
            hitType = hit;
        }
        return hitType;
    }

    public int getAccuracyRate() {
        return accuracyRate;
    }

    public void setAccuracyRate(int accuracyRate) {
        this.accuracyRate = accuracyRate;
    }
}

package main.game.logic.combat.damage;

import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;

public class Damage {
    public boolean shield;
    public boolean spell;
    public boolean canCritOrBlock;
    public boolean average;
    public Integer amount;
    public Unit attacked;
    public Unit attacker;
    public boolean offhand;
    public DAMAGE_TYPE dmg_type;
    public DC_ActiveObj action;
    public Ref ref;

    public Damage() {

    }

    public Damage(DAMAGE_TYPE damageType, int amount, Unit attacker, Unit attacked) {
        this.attacker = attacker;
        this.attacked = attacked;
        this.dmg_type = damageType;
        this.amount = amount;
    }

    public Damage(DAMAGE_TYPE damage_type, Ref ref, int amount) {
        this.dmg_type=damage_type;
        this.amount = amount;
        this.ref = ref;
    }

    public boolean isShield() {
        return shield;
    }

    public boolean isSpell() {
        return spell;
    }

    public boolean isCanCritOrBlock() {
        return canCritOrBlock;
    }

    public boolean isAverage() {
        return average;
    }

    public Integer getAmount() {
        return amount;
    }

    public Unit getAttacked() {
        return attacked;
    }

    public Unit getAttacker() {
        return attacker;
    }

    public boolean isOffhand() {
        return offhand;
    }

    public DAMAGE_TYPE getDmg_type() {
        return dmg_type;
    }

    public DC_ActiveObj getAction() {
        return action;
    }

    public Ref getRef() {
        return ref;
    }

    public void setRef(Ref ref) {
        this.ref = ref;
    }
}
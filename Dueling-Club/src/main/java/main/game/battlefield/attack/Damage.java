package main.game.battlefield.attack;

import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;

public class Damage {
    public boolean shield;
    public boolean spell;
    public boolean canCritOrBlock;
    public boolean average;
    public Integer damage;
    public Unit attacked;
    public Unit attacker;
    public boolean offhand;
    public DAMAGE_TYPE dmg_type;
    public DC_ActiveObj action;

    public Damage() {

    }

    public Damage(DAMAGE_TYPE damageType, int amount, Unit attacker, Unit attacked) {
        this.attacker = attacker;
        this.attacked = attacked;
        this.dmg_type = damageType;
        this.damage = amount;
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

    public Integer getDamage() {
        return damage;
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
}
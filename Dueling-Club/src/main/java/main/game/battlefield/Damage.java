package main.game.battlefield;

import main.content.CONTENT_CONSTS.DAMAGE_TYPE;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.top.DC_ActiveObj;

public class Damage {
    public boolean shield;
    public boolean spell;
    public boolean canCritOrBlock;
    public boolean average;
    public Integer damage;
    public DC_HeroObj attacked;
    public DC_HeroObj attacker;
    public boolean offhand;
    public DAMAGE_TYPE dmg_type;
    public DC_ActiveObj action;

    public Damage() {

    }

    public Damage(DAMAGE_TYPE damageType, int amount, DC_HeroObj attacker, DC_HeroObj attacked) {
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

    public DC_HeroObj getAttacked() {
        return attacked;
    }

    public DC_HeroObj getAttacker() {
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
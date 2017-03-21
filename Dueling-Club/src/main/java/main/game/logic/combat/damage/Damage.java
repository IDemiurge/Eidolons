package main.game.logic.combat.damage;

import main.content.enums.GenericEnums.DAMAGE_MODIFIER;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;

public class Damage {
    protected boolean spell;
    protected boolean attack;
    protected boolean average;
    protected Integer amount;
    protected Unit attacked;
    protected Unit attacker;
    protected boolean offhand;
    protected DAMAGE_TYPE dmg_type;
    protected DAMAGE_MODIFIER[] modifiers;
    protected DC_ActiveObj action;
    protected Ref ref;


    protected Damage() {

    }

//    public Damage(DAMAGE_TYPE damageType, int amount, Unit attacker, Unit attacked) {
//        this.attacker = attacker;
//        this.attacked = attacked;
//        this.dmg_type = damageType;
//        this.amount = amount;
//    }
//
//    public Damage(DAMAGE_TYPE damage_type, Ref ref, int amount) {
//        this.dmg_type=damage_type;
//        this.amount = amount;
//        this.ref = ref;
//    }

    public void setRef(Ref ref) {
        this.ref = ref;
        attacked= (Unit) ref.getTargetObj();
        attacked= (Unit) ref.getSourceObj();
    }

    public boolean canCritOrBlock() {
return true;// TODO
    }
    public boolean isSpell() {
        return spell;
    }

    public void setSpell(boolean spell) {
        this.spell = spell;
    }

    public boolean isAttack() {
        return attack;
    }

    public void setAttack(boolean attack) {
        this.attack = attack;
    }

    public DAMAGE_MODIFIER[] getModifiers() {
        return modifiers;
    }

    public void setModifiers(DAMAGE_MODIFIER[] modifiers) {
        this.modifiers = modifiers;
    }

    public void setAverage(boolean average) {
        this.average = average;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public void setAttacked(Unit attacked) {
        this.attacked = attacked;
    }

    public void setAttacker(Unit attacker) {
        this.attacker = attacker;
    }

    public void setOffhand(boolean offhand) {
        this.offhand = offhand;
    }

    public void setDmgType(DAMAGE_TYPE dmg_type) {
        this.dmg_type = dmg_type;
    }

    public void setAction(DC_ActiveObj action) {
        this.action = action;
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

    public DAMAGE_TYPE getDmgType() {
        return dmg_type;
    }

    public DC_ActiveObj getAction() {
        return action;
    }

    public Ref getRef() {
        return ref;
    }


}
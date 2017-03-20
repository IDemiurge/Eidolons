package main.game.logic.combat.damage;

import main.content.enums.GenericEnums.DAMAGE_MODIFIER;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;

public class Damage {
    private boolean spell;
    private boolean attack;
    private boolean average;
    private Integer amount;
    private Unit attacked;
    private Unit attacker;
    private boolean offhand;
    private DAMAGE_TYPE dmg_type;
    private DAMAGE_MODIFIER[] modifiers;
    private DC_ActiveObj action;
    private Ref ref;


    private Damage() {

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

    public void setDmg_type(DAMAGE_TYPE dmg_type) {
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
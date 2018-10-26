package eidolons.game.battlecraft.rules.combat.damage;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import main.content.enums.GenericEnums.DAMAGE_MODIFIER;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.system.auxiliary.ContainerUtils;

import java.util.Arrays;

public class Damage {
    protected boolean spell;
    protected boolean attack;
    protected Integer amount;
    protected BattleFieldObject target;
    protected BattleFieldObject source;
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

    @Deprecated
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
        if (ref != null) {
            ref.setValue(KEYS.DAMAGE_MODS, ContainerUtils
             .constructStringContainer(Arrays.asList(modifiers)));
        }
    }

    public boolean isAverage() {
        return DamageCalculator.isArmorAveraged(getRef());
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public BattleFieldObject getTarget() {
        return target;
    }

    public void setTarget(BattleFieldObject target) {
        this.target = target;
    }

    public BattleFieldObject getSource() {
        return source;
    }

    public void setSource(Unit source) {
        this.source = source;
    }

    public boolean isOffhand() {
        return offhand;
    }

    public void setOffhand(boolean offhand) {
        this.offhand = offhand;
    }

    public DAMAGE_TYPE getDmgType() {
        return dmg_type;
    }

    public void setDmgType(DAMAGE_TYPE dmg_type) {
        this.dmg_type = dmg_type;
    }

    public DC_ActiveObj getAction() {
        return action;
    }

    public void setAction(DC_ActiveObj action) {
        this.action = action;
    }

    public Ref getRef() {
        return ref;
    }

    public void setRef(Ref ref) {
        this.ref = ref;
        target = (BattleFieldObject) ref.getTargetObj();
        source = (BattleFieldObject) ref.getSourceObj();
    }


}
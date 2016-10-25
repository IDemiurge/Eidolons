package main.ability.effects;

import main.ability.effects.oneshot.MicroEffect;
import main.content.CONTENT_CONSTS.ACTION_TAGS;
import main.content.properties.G_PROPS;
import main.data.ability.AE_ConstrArgs;
import main.data.ability.OmittedConstructor;
import main.entity.Ref.KEYS;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_WeaponObj;
import main.entity.obj.top.DC_ActiveObj;
import main.game.DC_Game;
import main.game.battlefield.attack.Attack;
import main.system.auxiliary.StringMaster;
import main.system.math.PositionMaster;

public class AttackEffect extends MicroEffect {
    // effects on-death; effects on-hit; conditional effects;
    // specials: X damage modifier,
    /**    */
    protected boolean free = false;
    protected boolean canCounter = true;
    protected boolean counter = false;
    protected Effect onHit;
    protected Effect onKill;
    protected boolean offhand;
    protected DC_WeaponObj weapon;
    protected Attack attack;

    // on deal > than x%?

    // for default attack action
    public AttackEffect() {

    }

    @AE_ConstrArgs(argNames = {"off hand"})
    public AttackEffect(Boolean offhand) {
        this.offhand = offhand;
    }

    @AE_ConstrArgs(argNames = {"free", "is Counter"})
    public AttackEffect(Boolean free, Boolean c) {
        this.free = free;
        this.counter = c;
    }

    @AE_ConstrArgs(argNames = {"free", "is Counter", "can Counter"})
    public AttackEffect(Boolean free, Boolean c, Boolean cc) {
        this.free = free;
        this.counter = c;
        this.canCounter = cc;
    }

    @AE_ConstrArgs(argNames = {"free", "is Counter", "can Counter", "off hand"})
    public AttackEffect(Boolean free, Boolean c, Boolean cc, Boolean offhand) {
        this.free = free;
        this.counter = c;
        this.canCounter = cc;
        this.offhand = offhand;
    }

    @AE_ConstrArgs(argNames = {"free", "can Counter", "onHit effects"})
    public AttackEffect(Boolean free, Boolean c, Effect onHit) {
        this.free = free;
        this.canCounter = c;
        this.onHit = onHit;
    }

    @OmittedConstructor
    public AttackEffect(DC_WeaponObj weapon) {
        this(false, false, false);
        this.weapon = weapon;
    }

    @Override
    public boolean applyThis() {
        // new Attack(target, source).execute(effects); => to State's history!
        attack = null;
        DC_HeroObj attacker = (DC_HeroObj) ref.getSourceObj();
        DC_ActiveObj activeObj = (DC_ActiveObj) getActiveObj();
        if (!activeObj.isExtraAttackMode()) {
            if (!activeObj.isThrow())
                if (!activeObj.isRanged()) {
                    main.system.auxiliary.LogMaster.log(1, "*** MELEE ATTACK BY "
                            + activeObj.getOwnerObj().getNameAndCoordinate() + " on "
                            + ref.getTargetObj().getNameAndCoordinate());
                    if (PositionMaster.getDistance(activeObj.getOwnerObj(), ref.getTargetObj()) > 1) {
                        main.system.auxiliary.LogMaster.log(1, "*** RANGE BUG ");
                        // AI_Manager.logFullInfo();
                    }
                }
        }
        if (!offhand) {
            if (StringMaster.compare(ref.getObj(KEYS.ACTIVE).getProperty(G_PROPS.ACTION_TAGS),
                    ACTION_TAGS.OFF_HAND + "")) {
                offhand = true;
            }

        }

        if (weapon != null)
            ref.setID(KEYS.WEAPON, weapon.getId());
        else if (ref.getSourceObj() instanceof DC_HeroObj) {
            Integer id = null;
            try {
                id = (!offhand) ? attacker.getMainWeapon().getId() : attacker.getSecondWeapon()
                        .getId();
            } catch (Exception e) {
            }
            if (id != null)
                ref.setValue(KEYS.WEAPON, "" + id);
        }

        try {
            return ((DC_Game) getGame()).getAttackMaster().attack(getAttack(), ref, free,
                    canCounter, onHit, onKill, offhand, counter, attacker.hasDoubleStrike()); // UNIT_TAKES_DAMAGE
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        // there
    }

    public Attack getAttack() {
        if (attack == null)
            attack = initAttack();
        return attack;
    }

    public void setAttack(Attack attack) {
        this.attack = attack;
    }

    public Attack initAttack() {
        return new Attack(ref, offhand, counter, canCounter, free, onHit, onKill);
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public boolean isCanCounter() {
        return canCounter;
    }

    public void setCanCounter(boolean canCounter) {
        this.canCounter = canCounter;
    }

    public boolean isCounter() {
        return counter;
    }

    public void setCounter(boolean counter) {
        this.counter = counter;
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

    public boolean isOffhand() {
        return offhand;
    }

    public void setOffhand(boolean offhand) {
        this.offhand = offhand;
    }

    public DC_WeaponObj getWeapon() {
        return weapon;
    }

    public void setWeapon(DC_WeaponObj weapon) {
        this.weapon = weapon;
    }

}

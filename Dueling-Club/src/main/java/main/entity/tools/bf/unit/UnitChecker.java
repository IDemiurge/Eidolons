package main.entity.tools.bf.unit;

import main.content.DC_TYPE;
import main.content.PARAMS;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.CLASSIFICATIONS;
import main.content.enums.entity.UnitEnums.IMMUNITIES;
import main.content.enums.entity.UnitEnums.STANDARD_PASSIVES;
import main.content.enums.entity.UnitEnums.STATUS;
import main.content.values.properties.G_PROPS;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.DC_UnitModel;
import main.entity.obj.unit.Unit;
import main.entity.tools.EntityChecker;
import main.entity.tools.EntityMaster;
import main.game.core.game.DC_Game;
import main.game.logic.battle.player.Player;

/**
 * Created by JustMe on 2/26/2017.
 */
public class UnitChecker extends EntityChecker<Unit> {
    public UnitChecker(Unit entity, EntityMaster<Unit> entityMaster) {
        super(entity, entityMaster);
    }

    @Override
    public UnitCalculator getCalculator() {
        return (UnitCalculator) super.getCalculator();
    }

    @Override
    public UnitInitializer getInitializer() {
        return (UnitInitializer) super.getInitializer();
    }


    @Override
    public UnitResetter getResetter() {
        return (UnitResetter) super.getResetter();
    }

    public boolean canUseItems() {
        return canUseWeapons() || canUseArmor();
    }

    public boolean canUseArmor() {
        return checkContainerProp(G_PROPS.CLASSIFICATIONS, UnitEnums.CLASSIFICATIONS.HUMANOID.toString());
    }


    public boolean canUseWeapons() {
        return isHero(); //TODO ?
        // return checkContainerProp(G_PROPS.CLASSIFICATIONS,
        // CLASSIFICATIONS.HUMANOID
        // .toString());
    }

    public boolean checkDualWielding() {
        if (getEntity().getSecondWeapon() == null || getEntity().getMainWeapon() == null) {
            return false;
        }
        if (getEntity().getMainWeapon().isRanged() || getEntity().getMainWeapon().isMagical()) {
            return false;
        }
        if (getEntity().getSecondWeapon().isRanged() || getEntity().getSecondWeapon().isMagical()) {
            return false;
        }
        return (getEntity().getSecondWeapon().isWeapon());

    }

    public boolean checkInSight() {
        return getEntity().checkInSight();
    }

    public boolean checkInSightForUnit(Unit unit) {
        return getEntity().checkInSightForUnit(unit);
    }

    public boolean checkClassification(CLASSIFICATIONS PROP) {
        return getEntity().checkClassification(PROP);
    }

    public boolean checkPassive(STANDARD_PASSIVES PROP) {
        return getEntity().checkPassive(PROP);
    }

    public boolean checkSelectHighlighted() {
        return getEntity().checkSelectHighlighted();
    }

    public boolean checkVisible() {
        return getEntity().checkVisible();
    }

    public boolean checkStatus(STATUS STATUS) {
        return getEntity().checkStatus(STATUS);
    }

    public boolean isImmortalityOn() {
        if (isMine()) {
            if (equals(getEntity().getOwner().getHeroObj())) {
                return game.isDummyMode();
            }
        }
        if (getGame().getTestMaster().isImmortal() != null) {
            return
             getGame().getTestMaster().isImmortal();
        }
        return getGame().isDummyPlus();
    }

    public boolean isMine() {
        return getEntity().getOwner().isMe();
    }

    public Boolean isLandscape() {
        return getEntity().isLandscape();
    }

    public boolean isWall() {
        return getEntity().isWall();
    }

    public boolean isObstructing(Obj obj) {
        return getEntity().isObstructing(obj);
    }

    public boolean isObstructing(Obj obj, DC_Obj target) {
        return getEntity().isObstructing(obj, target);
    }

    public boolean isSmall() {
        return getEntity().isSmall();
    }

    public boolean isShort() {
        return getEntity().isShort();
    }

    public boolean isTransparent() {
        if (getEntity(). isDead()) {
        return true;
    }
        if (checkPassive(UnitEnums.STANDARD_PASSIVES.IMMATERIAL)) {
            return true;
        }
        return checkPassive(UnitEnums.STANDARD_PASSIVES.TRANSPARENT);
    }

    public boolean isTall() {
        return getEntity().isTall();
    }

    public boolean isBfObj() {
        return getEntity().getOBJ_TYPE_ENUM()== DC_TYPE.BF_OBJ;
    }

    public boolean isHero() {
        return getEntity().getOBJ_TYPE_ENUM()== DC_TYPE.CHARS;
    }

    public boolean isDone() {
        return getEntity().isDone();
    }

    public boolean isUnmoved() {
        return getEntity().isUnmoved();
    }

    public boolean isFull() {
        return getEntity().isFull();
    }

    public boolean isHuge() {
        return getEntity().isHuge();
    }

    public boolean isTurnable() {
        return getEntity().isTurnable();
    }


    public boolean canCounter() {
        if (isDisabled()) {
            return false;
        }
        if (checkModeDisablesCounters()) {
            return false;
        }
        if (checkStatusDisablesCounters()) {
            return false;
        }
        // TODO getMinimumAttackCost
        // if ( checkAlertCounter())
        // return false;
        // alternative cost
        // if (getIntParam(PARAMS.C_N_OF_COUNTERS) <= 0) {
        // }
        return true;
    }

    public boolean isDisabled() {
        if (isUnconscious()) {
            return true;
        }
        return getEntity().isDead();
    }

    public boolean checkStatusDisablesCounters() {
        if (checkStatus(UnitEnums.STATUS.IMMOBILE)) {
            return true;
        }
        if (checkStatus(UnitEnums.STATUS.CHARMED)) {
            return true;
        }
        if (checkStatus(UnitEnums.STATUS.ENSNARED)) {
            return true;
        }
        if (checkStatus(UnitEnums.STATUS.PRONE)) {
            return true;
        }
        return checkStatus(UnitEnums.STATUS.EXHAUSTED);

    }

    public boolean isUnconscious() {
        return checkStatus(UnitEnums.STATUS.UNCONSCIOUS);
    }

    public boolean canAttack() {
        return getEntity(). getAttack().canBeActivated(getRef(), true);
    }

    // melee/ranged separate!
    public boolean canAttack(DC_UnitModel attacked) {
        if (!canAttack()) {
            return false;
        }
        // ConditionMaster.getAdjacent().preCheck(ref);
        int range = getIntParam(PARAMS.RANGE);
        if (range == 1) {
            return getGame().getMovementManager().isAdjacent(getEntity(), attacked);
        }
        return (range >= getGame().getMovementManager().getDistance(getEntity(), attacked));

    }

    public boolean canMove() {
        if (isBfObj()) {
            return false;
        }
        // if (isstructure)
        return canActNow();
    }

    public boolean canCounter(DC_ActiveObj active) {
        return canCounter(active, false);
    }

    public boolean canCounter(DC_ActiveObj active, boolean sneak) {
        if (!canCounter()) {
            return false;
        }
        if (active.checkPassive(UnitEnums.STANDARD_PASSIVES.NO_RETALIATION)) {
            return false;
        }
        // if (!attacked.checkPassive(STANDARD_PASSIVES.VIGILANCE))
        if (active.getOwnerObj().checkPassive(UnitEnums.STANDARD_PASSIVES.NO_RETALIATION)) {
            return false;
        }
        // may still fail to activate any particular Attack Action!
        return true;
    }

    public boolean canAct() {
        if (getEntity().getOwner() == Player.NEUTRAL) {
            return false;
        }
        if (checkStatusPreventsActions()) {
            return false;
        }
        return !isImmobilized();
    }

    public boolean canActNow() {
        if (getGame().isDummyPlus()) {
            if (!isMine()) {
                return false;
            }
        }
        if (getEntity().getOwner() == Player.NEUTRAL) {
            return false;
        }
        if (checkStatusPreventsActions()) {
            return false;
        }

        // if (checkStatus(STATUS.DISCOMBOBULATED))
        // return false;

        if (checkStatus(UnitEnums.STATUS.ON_ALERT)) {
            return false;
        }
        if (checkStatus(UnitEnums.STATUS.WAITING)) {
            return false;
        }
        // if (checkStatus(STATUS.LATE))
        // return false;
        if (getIntParam(PARAMS.C_N_OF_ACTIONS) <= 0) {
            return false;
        }
        return !isImmobilized();

    }

    public boolean checkUncontrollable() {
        // if (checkBuffStatusPreventsActions())
        // return true;
        return getEntity().getMode().isBehavior();

    }

    public boolean checkStatusPreventsActions() {
        if (checkStatus(UnitEnums.STATUS.DEAD)) {
            return true;
        }
        if (checkStatus(UnitEnums.STATUS.EXHAUSTED)) {
            return true;
        }
        if (checkStatus(UnitEnums.STATUS.ASLEEP)) {
            return true;
        }
        if (checkStatus(UnitEnums.STATUS.FROZEN)) {
            return true;
        }
        return checkStatus(UnitEnums.STATUS.UNCONSCIOUS);
    }

    public boolean isIncapacitated() {
        if (checkStatus(UnitEnums.STATUS.IMMOBILE)) {
            return true;
        }
        if (checkStatus(UnitEnums.STATUS.CHARMED)) {
            return true;
        }
        return checkStatusPreventsActions();
    }

    public boolean isImmobilized() {

        if (checkStatus(UnitEnums.STATUS.IMMOBILE)) {
            return true;
        }

        if (checkStatus(UnitEnums.STATUS.CHARMED)) {
            return true;
        }

        return checkModeDisablesActions();

    }

    public boolean checkModeDisablesCounters() {
        if (getEntity(). getBehaviorMode() != null) {
            return getEntity().getBehaviorMode().isDisableCounters();
        }

        return getEntity().getMode().isDisableCounter();
    }

    public boolean checkModeDisablesActions() {
        return getEntity().getMode().isDisableActions();

    }




    public boolean isLiving() {
        if (checkClassification(UnitEnums.CLASSIFICATIONS.UNDEAD)) {
            return false;
        }
        if (checkClassification(UnitEnums.CLASSIFICATIONS.WRAITH)) {
            return false;
        }
        if (checkClassification(UnitEnums.CLASSIFICATIONS.ELEMENTAL)) {
            return false;
        }
        if (checkClassification(UnitEnums.CLASSIFICATIONS.CONSTRUCT)) {
            return false;
        }
        if (checkClassification(UnitEnums.CLASSIFICATIONS.STRUCTURE)) {
            return false;
        }
        return !checkClassification(UnitEnums.CLASSIFICATIONS.MECHANICAL);
    }

    public boolean isHidden() {
        return getEntity().isHidden();
    }

    @Override
    public DC_Game getGame() {
        return (DC_Game) super.getGame();
    }

    public boolean hasDoubleStrike() {
        return
         checkPassive(UnitEnums.STANDARD_PASSIVES.DOUBLE_STRIKE);
    }
    public boolean checkImmunity(IMMUNITIES type) {
        return checkProperty(G_PROPS.IMMUNITIES, type.toString());
    }

}

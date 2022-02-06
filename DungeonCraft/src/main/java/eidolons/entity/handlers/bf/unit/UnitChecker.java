package eidolons.entity.handlers.bf.unit;

import eidolons.content.PROPS;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.ai.AI_Manager;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.core.game.DC_Game;
import main.content.CONTENT_CONSTS2;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.enums.entity.UnitEnums.CLASSIFICATIONS;
import main.content.enums.entity.UnitEnums.IMMUNITIES;
import main.content.enums.entity.UnitEnums.STANDARD_PASSIVES;
import main.content.enums.entity.UnitEnums.STATUS;
import main.content.enums.system.AiEnums;
import main.content.mode.STD_MODES;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.entity.handlers.EntityChecker;
import main.entity.handlers.EntityMaster;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.ContainerUtils;
import main.system.launch.Flags;

/**
 * Created by JustMe on 2/26/2017.
 */
public class UnitChecker extends EntityChecker<Unit> {
    public UnitChecker(Unit entity, EntityMaster<Unit> entityMaster) {
        super(entity, entityMaster);
    }

    public static boolean isUnarmedFighter(Unit unit) {
        //check mastery or monster
        return false;
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
        return checkContainerProp(G_PROPS.CLASSIFICATIONS,
                CLASSIFICATIONS.HUMANOID.toString());
    }


    public boolean canUseWeapons() {
        return isHero(); //TODO ?
        // return checkContainerProp(G_PROPS.CLASSIFICATIONS,
        // CLASSIFICATIONS.HUMANOID
        // .toString());
    }

    public boolean checkDualWielding() {
        if (getEntity().getOffhandWeapon() == null || getEntity().getMainWeapon() == null) {
            return false;
        }
        if (getEntity().getMainWeapon().isRanged() || getEntity().getMainWeapon().isMagical()) {
            return false;
        }
        if (getEntity().getOffhandWeapon().isRanged() || getEntity().getOffhandWeapon().isMagical()) {
            return false;
        }
        return (getEntity().getOffhandWeapon().isWeapon());

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

        if (getGame().isDebugMode())
            return false;
        if (Flags.isLiteLaunch()) {
            if (Flags.isActiveTestMode()) {
                return true;
            }
        }
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
        if (getEntity().isDead()) {
            return true;
        }
        return checkPassive(STANDARD_PASSIVES.IMMATERIAL);//checkPassive(UnitEnums.STANDARD_PASSIVES.TRANSPARENT);
    }

    public boolean isTall() {
        return getEntity().isTall();
    }

    public boolean isBfObj() {
        return getEntity().getOBJ_TYPE_ENUM() == DC_TYPE.BF_OBJ;
    }

    public boolean isHero() {
        return getEntity().getOBJ_TYPE_ENUM() == DC_TYPE.CHARS;
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
        if (checkStatus(STATUS.DISCOMBOBULATED)) {
            return false;
        }
        if (checkStatus(STATUS.ENSNARED)) {
            return false;
        }
        if (checkStatus(STATUS.CHARMED)) {
            return false;
        }
        if (checkStatus(STATUS.EXHAUSTED)) {
            return false;
        }
        if (checkStatus(STATUS.ASLEEP)) {
            return false;
        }
        return !checkStatus(STATUS.FROZEN);
        // TODO getMinimumAttackCost
        // if ( checkAlertCounter())
        // return false;
        // alternative cost
        // if (getIntParam(PARAMS.C_N_OF_COUNTERS) <= 0) {
        // }
    }

    public boolean isDisabled() {
        if (isUnconscious()) {
            return true;
        }
        return getEntity().isDead();
    }

    public boolean checkStatusDisablesCounters() {
        if (checkStatus(STATUS.IMMOBILE)) {
            return true;
        }
        if (checkStatus(STATUS.CHARMED)) {
            return true;
        }
        if (checkStatus(STATUS.ENSNARED)) {
            return true;
        }
        if (checkStatus(STATUS.PRONE)) {
            return true;
        }
        return checkStatus(STATUS.EXHAUSTED);

    }

    public boolean isUnconscious() {
        if (checkStatus(STATUS.UNCONSCIOUS))
            return true;
        return getEntity().getBuff("Unconscious") != null;
    }

    public boolean canAttack() {
        return getEntity().getAttack().canBeActivated(getRef(), true);
    }

    public boolean canCounter(ActiveObj active) {
        return canCounter(active, false);
    }

    public boolean canCounter(ActiveObj active, boolean sneak) {
        if (!canCounter()) {
            return false;
        }
        if (getEntity().checkPassive(STANDARD_PASSIVES.VIGILANCE))
            return true;
        if (sneak)
            return false;
        if (active.checkPassive(STANDARD_PASSIVES.NO_RETALIATION)) {
            return false;
        }
        return !active.getOwnerUnit().checkPassive(STANDARD_PASSIVES.NO_RETALIATION);
    }

    public boolean canAct() {
        return canActNow(); // TODO difference in Mode?
    }

    public boolean canActNow() {
        if (getEntity().isDead()) {
            return false;
        }

        if (getEntity().getOwner() == Player.NEUTRAL) {
            return false;
        }
        if (checkStatusPreventsActions()) {
            return false;
        }
        if (isUnconscious()) {
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
        if (checkStatus(STATUS.DEAD)) {
            return true;
        }
        if (checkStatus(STATUS.DISABLED)) {
            return true;
        }
        if (checkPassive(STANDARD_PASSIVES.IMMOBILE)) {
            return true;
        }
        if (checkStatus(STATUS.EXHAUSTED)) {
            return true;
        }
        if (checkStatus(STATUS.ASLEEP)) {
            return true;
        }
        return checkStatus(STATUS.FROZEN);
    }

    public boolean isIncapacitated() {
        if (checkStatus(STATUS.IMMOBILE)) {
            return true;
        }
        if (checkStatus(STATUS.CHARMED)) {
            return true;
        }
        return checkStatusPreventsActions();
    }

    public boolean isImmobilized() {

        if (checkStatus(STATUS.IMMOBILE)) {
            return true;
        }

        if (checkStatus(STATUS.CHARMED)) {
            return true;
        }

        return checkModeDisablesActions();

    }

    public boolean checkModeDisablesCounters() {
        if (getEntity().getBehaviorMode() != null) {
            return getEntity().getBehaviorMode().isDisableCounters();
        }

        return getEntity().getMode().isDisableCounter();
    }

    public boolean checkModeDisablesActions() {
        return getEntity().getMode().isDisableActions();

    }


    public boolean isLiving() {
        if (checkClassification(CLASSIFICATIONS.UNDEAD)) {
            return false;
        }
        if (checkClassification(CLASSIFICATIONS.WRAITH)) {
            return false;
        }
        if (checkClassification(CLASSIFICATIONS.ELEMENTAL)) {
            return false;
        }
        if (checkClassification(CLASSIFICATIONS.CONSTRUCT)) {
            return false;
        }
        if (checkClassification(CLASSIFICATIONS.STRUCTURE)) {
            return false;
        }
        return !checkClassification(CLASSIFICATIONS.MECHANICAL);
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
                checkPassive(STANDARD_PASSIVES.DOUBLE_STRIKE);
    }

    public boolean checkImmunity(IMMUNITIES type) {
        return checkProperty(G_PROPS.IMMUNITIES, type.toString());
    }


    /**
     * mastery group (spell/skill),
     *
     * @param potential    has or can have
     * @param TYPE
     * @param dividingProp spellgroup/mastery group/...
     * @param prop         spellbook/verbatim/skills/etc
     * @return
     */

    public boolean checkItemGroup(PROPERTY prop, PROPERTY dividingProp, String name,
                                  boolean potential, OBJ_TYPE TYPE) {
        // at least one item with NAME as PROP

        for (String item : ContainerUtils.open(getProperty(prop))) {

            ObjType type = DataManager.getType(item, TYPE);
            if (type == null) {
                continue;
            }
            if (!potential) {
                return type.checkSingleProp(dividingProp, name);
            }

            return game.getRequirementsManager().check(getEntity(), type) == null;

        }

        return false;

    }

    public boolean canInstantAttack() {
        if (!getEntity().canCounter()) {
            return false;
        }
        if (getEntity().checkPassive(STANDARD_PASSIVES.VIGILANCE)) {
            return true;
        }
        return getEntity().getMode() == STD_MODES.ALERT;
    }

    public boolean checkAiMod(CONTENT_CONSTS2.AI_MODIFIERS aiMod) {
        if (AI_Manager.BRUTE_AI_MODE) {
            if (aiMod == CONTENT_CONSTS2.AI_MODIFIERS.TRUE_BRUTE) {
                UnitAI ai = getEntity().getUnitAI();
                if (ai == null) {
                    return true;
                }
                if (ai.getType() != AiEnums.AI_TYPE.SNEAK) {
                    if (ai.getType() != AiEnums.AI_TYPE.CASTER) {
                        if (ai.getType() != AiEnums.AI_TYPE.ARCHER) {
                            if (getEntity().getSpells().isEmpty()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return checkProperty(PROPS.AI_MODIFIERS, aiMod.toString());
    }
}

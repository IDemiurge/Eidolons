package main.entity.handlers.bf.unit;

import main.content.ContentManager;
import main.content.DC_ContentManager;
import main.content.DC_ContentManager.ATTRIBUTE;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.enums.entity.UnitEnums;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.entity.Ref.KEYS;
import main.entity.active.DC_SpellObj;
import main.entity.handlers.EntityMaster;
import main.entity.handlers.EntityResetter;
import main.entity.item.DC_HeroItemObj;
import main.entity.item.DC_QuickItemObj;
import main.entity.obj.ActiveObj;
import main.entity.obj.attach.DC_FeatObj;
import main.entity.obj.unit.DC_UnitModel;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.tools.ParamAnalyzer;
import main.game.battlecraft.logic.battlefield.FacingMaster;
import main.game.battlecraft.rules.RuleMaster;
import main.game.battlecraft.rules.RuleMaster.RULE;
import main.game.battlecraft.rules.action.EngagedRule;
import main.game.battlecraft.rules.combat.damage.ResistMaster;
import main.game.battlecraft.rules.rpg.IntegrityRule;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.game.core.game.DC_Game;
import main.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.system.DC_Constants;
import main.system.DC_Formulas;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.Chronos;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;
import main.system.test.TestMasterContent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 2/26/2017.
 */
public class UnitResetter extends EntityResetter<Unit> {
    public UnitResetter(Unit entity, EntityMaster<Unit> entityMaster) {
        super(entity, entityMaster);
    }

    @Override
    public UnitCalculator getCalculator() {
        return (UnitCalculator) super.getCalculator();
    }

    //addDefault
    @Override
    public UnitInitializer getInitializer() {
        return (UnitInitializer) super.getInitializer();
    }

    @Override
    public UnitChecker getChecker() {
        return (UnitChecker) super.getChecker();
    }

    @Override
    public DC_Game getGame() {
        return (DC_Game) super.getGame();
    }

    @Override
    public void reset() {
        getGame().getStateManager().reset(getEntity());

    }

    public void resetFacing() {
        FACING_DIRECTION facing = null;
        if (facing != null) {
            setProperty(PROPS.FACING_DIRECTION, facing.getName());
        } else {
            String name = getProperty(PROPS.FACING_DIRECTION);
            facing = (new EnumMaster<FACING_DIRECTION>().retrieveEnumConst(FACING_DIRECTION.class,
             name));
            if (facing == null) {
                if (getEntity().getDirection() != null) {
                    FacingMaster.getFacingFromDirection(getEntity().getDirection());
                } else if (getRef().getObj(KEYS.SUMMONER) != null) {
                    facing = ((DC_UnitModel) getRef().getObj(KEYS.SUMMONER)).getFacing();
                } else {
                    facing = FacingMaster.getRandomFacing();
                }
            }

        }
        getEntity().setFacing(facing);
    }

    @Override
    public void toBase() {
//        getEntity().setMode(STD_MODES.NORMAL); ??
        // Chronos.mark(toString() + "to base (values)");
        getEntity().setMode(null);
        if (getEntity().getSpecialEffects() != null) {
            getEntity().getSpecialEffects().clear();
        }
        if (getEntity().getBonusDamage() != null) {
            getEntity().getBonusDamage().clear();
        }
        super.toBase();

//        if (game.isSimulation()) {
//            return;
//        }
        if (getEntity().isMine()) {
            if (CoreEngine.isLogicTest())
                TestMasterContent.addTestGroupSpells(getEntity());

            if (CoreEngine.isAnimationTestMode()) {
                TestMasterContent.addANIM_TEST_Spells(getEntity());
            } else if (CoreEngine.isGuiTestMode()) {
                TestMasterContent.addGRAPHICS_TEST_Spells(getEntity());
            }
        }

        if (getChecker().checkClassification(UnitEnums.CLASSIFICATIONS.TALL)) {
            getInitializer().addProperty(G_PROPS.STANDARD_PASSIVES, "" + UnitEnums.CLASSIFICATIONS.TALL, true);
        }
        if (getChecker().checkClassification(UnitEnums.CLASSIFICATIONS.SHORT)) {
            getInitializer().addProperty(G_PROPS.STANDARD_PASSIVES, "" + UnitEnums.CLASSIFICATIONS.TALL, true);
        }

        // Chronos.logTimeElapsedForMark(toString() + "to base (values)");

        // Chronos.mark(toString() + "to base (init objects)");
        getInitializer().initHeroObjects();
        // Chronos.logTimeElapsedForMark(toString() + "to base (init objects)");
        // if (mainHero)
        if (!CoreEngine.isArcaneVault()) {
            if (game.isSimulation()) {
                resetObjects();
                resetQuickSlotsNumber();
                String value = "";
                for (DC_SpellObj s : getEntity().getSpells()) {
                    if (!s.getProperty(PROPS.SPELL_UPGRADES).isEmpty()) {
                        value += s.getName()
                         + StringMaster.wrapInParenthesis(s
                         .getProperty(PROPS.SPELL_UPGRADES).replace(";", ",")) + ";";
                    }
                }
                if (!value.isEmpty()) {
                    setProperty(PROPS.SPELL_UPGRADES, value, true);
                }
            }
        }

        if (!getChecker().isBfObj()) {
            if (!getChecker().isNeutral()) {
                if (getChecker().isImmortalityOn()) {
                    getEntity().addPassive(UnitEnums.STANDARD_PASSIVES.INDESTRUCTIBLE);
                }
                if (game.isDummyMode()) {
                    if (getGame().isDummyPlus()) {
                        getEntity().resetDynamicParam(PARAMS.C_N_OF_COUNTERS);
                        resetParam(PARAMS.C_STAMINA);
                        resetParam(PARAMS.C_FOCUS);
                        resetParam(PARAMS.C_ESSENCE);
                        resetParam(PARAMS.C_FOCUS);
                    }
                    if (!getEntity().getOwner().isMe()) {
                        setParam(PARAMS.INITIATIVE_MODIFIER, 1);
                    }


                }
            }
        }
    }


    public void applyMods() {
        // if (getMainWeapon() != null) //
        // getMainWeapon().applyMods();
        // if (getArmor() != null)
        // getArmor().applyMods();
        // if (getSecondWeapon() != null)
        // getSecondWeapon().applyMods();

        if (getEntity().getEngagementTarget() != null) {
            EngagedRule.applyMods(getEntity());
        } else {
            getEntity().removeStatus(UnitEnums.STATUS.ENGAGED);
        }
        int mod = getIntParam(PARAMS.ATTACK_MOD);
        getEntity().multiplyParamByPercent(PARAMS.ATTACK, mod, false);
        mod = getIntParam(PARAMS.OFFHAND_ATTACK_MOD);
        getEntity().multiplyParamByPercent(PARAMS.OFF_HAND_ATTACK, mod, false);
        mod = getIntParam(PARAMS.DEFENSE_MOD);
        getEntity().multiplyParamByPercent(PARAMS.DEFENSE, mod, false);

        ResistMaster.initUnitResistances(getEntity());
    }

    public void resetSpells() {
        if (getEntity().getSpells() != null) {
            for (DC_SpellObj spell : getEntity().getSpells()) {
                spell.toBase();
            }
        }
    }

    public void resetQuickItemActives() {
        for (DC_QuickItemObj q : getEntity().getQuickItems()) {
            q.afterEffects();
        }
    }

    public void resetQuickSlotsNumber() {
        int size = 0;
        if (getEntity().getQuickItems() != null) {
            size = getEntity().getQuickItems().size();
        }
        int slotsRemaining = getIntParam(PARAMS.QUICK_SLOTS) - size;
        setParam(PARAMS.C_QUICK_SLOTS, slotsRemaining);
    }

    public void resetObjectContainers(boolean fromValues) {
        if (fromValues) {
            getEntity().setItemsInitialized(false);
        }
    }

    public void resetObjects() {
        if (ExplorationMaster.isExplorationOn()) {
            if (!getEntity().isDirty()) {
                return;
            }
        }

        getEntity().setBeingReset(true);
        Chronos.mark(toString() + " OBJECTS APPLY");

        applyBackground();
        resetAttributes();
        resetMasteryScores();
        if (getEntity().getSkills() != null) {

            resetRanks(getEntity().getSkills(), PROPS.SKILLS);
            for (DC_FeatObj feat : getEntity().getSkills()) {
                feat.apply();
            }
        }
        if (getEntity().getClasses() != null) {
            resetRanks(getEntity().getClasses(), PROPS.CLASSES);
            for (DC_FeatObj feat : getEntity().getClasses()) {
                feat.apply();
            }

        }
        if (getEntity().getMainWeapon() != null) {
            getEntity().getMainWeapon().apply();
        } else if (getEntity().getNaturalWeapon() != null) {
            getEntity().getNaturalWeapon().apply();
        }

        if (getEntity().getSecondWeapon() != null) {
            getEntity().getSecondWeapon().apply();
            // if (checkDualWielding())
            // DC_Formulas.MAIN_HAND_DUAL_ATTACK_MOD
        } else if (getEntity().getOffhandNaturalWeapon() != null) {
            getEntity().getOffhandNaturalWeapon().apply();
        }

        if (getEntity().getArmor() != null) {
            getEntity().getArmor().apply();
        }
        resetQuickSlotsNumber();
        for (DC_HeroItemObj item : getEntity().getQuickItems()) {
            item.apply();
        }
        for (DC_HeroItemObj item : getEntity().getJewelry()) {
            item.apply();
        }
        // Chronos.logTimeElapsedForMark(toString() + " OBJECTS APPLY");

//        Chronos.mark(toString() + " activate PASSIVES");
        getInitializer().initSpells(game.isSimulation());
        getEntity().activatePassives();

        // Chronos.logTimeElapsedForMark(toString() + " activate PASSIVES");

        getEntity().setDirty(false);
        getEntity().setBeingReset(false);
        if (game.isSimulation() || getType().isModel()) {
            // initSpellbook(); //in afterEffect()
            return;
        }
//        Chronos.mark(toString() + " init ACTIVES");
        getInitializer().initActives();
        // Chronos.logTimeElapsedForMark(toString() + " init ACTIVES");


    }

    public void resetRanks(DequeImpl<DC_FeatObj> container, PROPERTY property) {
        List<DC_FeatObj> list = new ArrayList<>(container);
        for (String feat : StringMaster.open(getProperty(property))) {
            Integer rank = StringMaster.getInteger(VariableManager.getVarPart(feat));
            if (rank == 0) {
                continue;
            }
            feat = (VariableManager.removeVarPart(feat));
            for (DC_FeatObj featObj : container) {
                if (!featObj.getName().equals(feat)) {
                    continue;
                }
                featObj.setParam(PARAMS.RANK, rank);
                list.remove(featObj);
            }
        }
        for (DC_FeatObj featObj : list) {
            featObj.setParam(PARAMS.RANK, 0);
        }
    }

    public void resetActives() {
        for (ActiveObj active : getEntity().getActives()) {
            active.setRef(getRef());
            active.toBase();
        }
    }

    public void resetActions() {
        if (getChecker().checkPassive(UnitEnums.STANDARD_PASSIVES.AUTOMATA)) {
            return;
        }
        if (getChecker().checkStatus(UnitEnums.STATUS.IMMOBILE)) {
            setParam(PARAMS.C_N_OF_ACTIONS, 0);
            return;
        }
        float carryOverFactor = DC_Constants.CARRY_OVER_FACTOR;
        if (getIntParam(PARAMS.C_N_OF_ACTIONS) < 0) {
            carryOverFactor = DC_Constants.CARRY_OVER_FACTOR_NEGATIVE;
        }
        if (getGame().getState().getRound() == 0)
            carryOverFactor = 0;
        int actions = (int) (getIntParam(PARAMS.N_OF_ACTIONS) + getIntParam(PARAMS.C_N_OF_ACTIONS)
         * carryOverFactor);

        setParam(PARAMS.C_N_OF_ACTIONS, actions);

    }

    public void resetAttacksAndMovement() {
        if (getChecker().checkStatus(UnitEnums.STATUS.IMMOBILE)) {
            setParam(PARAMS.C_N_OF_ACTIONS, 0);
            return;
        }
        setParam(PARAMS.C_N_OF_COUNTERS, getIntParam(PARAMS.N_OF_COUNTERS));

    }

    public void resetIntegrity() {
        IntegrityRule.resetIntegrity(getEntity());

    }

    public void applyIntegrity() {
        IntegrityRule.applyIntegrity(getEntity());

    }

    public void applyBackground() {
        if (getEntity().getBackgroundType() == null) {
            getEntity().setBackgroundType(DataManager.getType(getProperty(G_PROPS.BACKGROUND_TYPE),
             getEntity().getOBJ_TYPE_ENUM()));
            if (getEntity().getBackgroundType() == null) {
                getEntity().setBackgroundType(DataManager.getType(getProperty(G_PROPS.BASE_TYPE),
                 getEntity().getOBJ_TYPE_ENUM()));
            }
        }
        if (getEntity().getBackgroundType() == null) {
            return;
        }
        for (PARAMETER param : DC_ContentManager.getBackgroundDynamicParams()) {
            Integer amount = getEntity().getBackgroundType().getIntParam(param);
            getEntity().modifyParameter(param, amount);
        }

    }


    public void resetMasteryScores() {
        for (PARAMS mastery : DC_ContentManager.getMasteryParams()) {
            PARAMETER score = ContentManager.getMasteryScore(mastery);
            getEntity().getType().setParam(score, getIntParam(mastery));
            getEntity().setParam(score, getIntParam(mastery));
        }
    }

    public void resetAttributes() {
        for (ATTRIBUTE attr : DC_ContentManager.getAttributeEnums()) {
            resetAttr(attr);
        }

    }

    public void resetDefaultAttr(ATTRIBUTE attr) {
        getEntity().getType().setParam(DC_ContentManager.getDefaultAttr(attr.getParameter()),
         getIntParam(DC_ContentManager.getBaseAttr(attr.getParameter())));
    }

    /**
     * only from Arcane Vault!
     */
    public void resetDefaultAttrs() {
        for (ATTRIBUTE attr : DC_ContentManager.getAttributeEnums()) {
            resetDefaultAttr(attr);
        }
    }

    public void resetAttr(ATTRIBUTE attr) {
        PARAMETER baseAttr = DC_ContentManager.getBaseAttr(attr);
        getEntity().getType().setParam(attr.getParameter(), getIntParam(baseAttr));
        getEntity().setParam(attr.getParameter(), getIntParam(baseAttr));

    }

    public void resetHeroValues() {
        if (getEntity().isHero()) {
            resetIntegrity();
            applyIntegrity();
        }
        getEntity().getMasteries().apply();
        getEntity().getAttrs().apply();
    }

    public void resetMorale() {
        if (ParamAnalyzer.isMoraleIgnore(getEntity())) {
            return;
        }
        if (getIntParam(PARAMS.BATTLE_SPIRIT) == 0) {
            if (getRef().getObj(KEYS.PARTY) == null) {
                getEntity().setParam(PARAMS.BATTLE_SPIRIT, 100);
            }
        }
        getEntity().setParam(PARAMS.MORALE, getIntParam(PARAMS.SPIRIT) * DC_Formulas.MORALE_PER_SPIRIT
         * getIntParam(PARAMS.BATTLE_SPIRIT) / 100);
        // the C_ value cannot be changed, but the PERCENTAGE
        getEntity().setParam(PARAMS.C_MORALE, getIntParam(PARAMS.C_MORALE), true);

    }

    public void regenerateToughness() {
        Integer amount = getIntParam(PARAMS.TOUGHNESS_RECOVERY) * getIntParam(PARAMS.TOUGHNESS)
         / 100;
        // setParam(PARAMS.C_TOUGHNESS, amount);
        if (amount > 0) {
            getEntity().modifyParameter(PARAMS.C_TOUGHNESS, amount, getIntParam(PARAMS.TOUGHNESS));
        }
    }

    public void afterEffectsApplied() {
        getEntity().setBeingReset(true);
        resetHeroValues();
        if (game.isSimulation()) {
            getInitializer().initSpellbook();
        }

        resetMorale();
        if (!getInitializer().dynamicValuesReady && !game.isSimulation()) {
            getInitializer().addDynamicValues();
            getInitializer().dynamicValuesReady = true;
            getEntity().resetPercentages();
        }

        getCalculator().calculatePower();
        getCalculator().calculateWeight();
        getCalculator().calculateRemainingMemory();

        if (!game.isSimulation()) { // TODO perhaps I should apply and display
            // them!
            if (!getGame().getRules().getStaminaRule().apply(getEntity())) {
                getEntity().setInfiniteValue(PARAMS.STAMINA, 0.2f);
            }
            if (!getGame().getRules().getFocusBuffRule().apply(getEntity())) {
                getEntity().setInfiniteValue(PARAMS.FOCUS, 1);
            }
            if (!getGame().getRules().getMoraleBuffRule().apply(getEntity())) {
                getEntity().setInfiniteValue(PARAMS.MORALE, 0.5f);
            }
            if (!getGame().getRules().getWeightRule().apply(getEntity())) {
                getEntity().setInfiniteValue(PARAMS.CARRYING_CAPACITY, 2);
            }
            if (RuleMaster.isRuleOn(RULE.WATCH))
                getGame().getRules().getWatchRule().updateWatchStatus(getEntity());
            getGame().getRules().getWoundsRule().apply(getEntity());


//            recalculateInitiative();
        } else {
            afterBuffRuleEffects();
        }

        if (game.isSimulation()) {
            resetSpells();
            return;
        }
        if (getGame().getInventoryTransactionManager() != null) {
            if (getGame().getInventoryTransactionManager().isActive()) {
                return;
            }
        }
        resetSpells();
        resetQuickItemActives();
        resetActives();
        getEntity().setBeingReset(false);
    }

    public void afterBuffRuleEffects() {
        if (getEntity().getSecondWeapon() != null) {
            setParam(PARAMS.OFF_HAND_ATTACK, getIntParam(PARAMS.ATTACK));
            getEntity().getSecondWeapon().applyMasteryBonus();

        } else if (getEntity().getNaturalWeapon(true) != null) {
            setParam(PARAMS.OFF_HAND_ATTACK, getIntParam(PARAMS.ATTACK));
            getEntity().getNaturalWeapon(true).applyUnarmedMasteryBonus();
        }
        getCalculator().calculateAndSetDamage(true);
        if (getEntity().getMainWeapon() != null) {
            getEntity().getMainWeapon().applyMasteryBonus();
        } else if (getEntity().getNaturalWeapon(false) != null) {
            getEntity().getNaturalWeapon(false).applyUnarmedMasteryBonus();
        }
        getCalculator().calculateAndSetDamage(false);
        applyMods();
    }
}
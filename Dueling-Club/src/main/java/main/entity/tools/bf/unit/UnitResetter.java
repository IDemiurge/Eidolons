package main.entity.tools.bf.unit;

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
import main.entity.item.DC_HeroItemObj;
import main.entity.item.DC_QuickItemObj;
import main.entity.obj.ActiveObj;
import main.entity.obj.attach.DC_FeatObj;
import main.entity.obj.unit.Unit;
import main.entity.tools.EntityMaster;
import main.entity.tools.EntityResetter;
import main.game.ai.tools.ParamAnalyzer;
import main.game.battlefield.attack.ResistMaster;
import main.game.core.game.DC_Game;
import main.rules.action.EngagedRule;
import main.rules.rpg.IntegrityRule;
import main.system.DC_Constants;
import main.system.DC_Formulas;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.Chronos;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;
import main.system.test.TestMasterContent;
import main.test.debug.GameLauncher;

import java.util.LinkedList;
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
    public void toBase() {
//        getEntity().setMode(STD_MODES.NORMAL); ??
        // Chronos.mark(toString() + "to base (values)");
        getEntity().setMode(null);
        if (getEntity().getSpecialEffects() != null) {
            getEntity().getSpecialEffects().clear();
        }
        super.toBase();

        if (game.isSimulation()) {
            return;
        }
        if (getEntity().isMine()) {
            if (CoreEngine.isAnimationTestMode()) {
                TestMasterContent.addANIM_TEST_Spells(getEntity());
            } else if (CoreEngine.isGraphicTestMode()) {
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
        if (!GameLauncher.getInstance().SUPER_FAST_MODE) {
            getInitializer().initHeroObjects();
        }
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
                        getEntity().resetParam(PARAMS.C_N_OF_COUNTERS);
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

        Chronos.mark(toString() + " activate PASSIVES");
        getInitializer().initSpells(game.isSimulation());
        getEntity().activatePassives();

        // Chronos.logTimeElapsedForMark(toString() + " activate PASSIVES");

        getEntity().setDirty(false);
        if (game.isSimulation() || getType().isModel()) {
            // initSpellbook(); //in afterEffect()
            return;
        }
        Chronos.mark(toString() + " init ACTIVES");
        getInitializer().initActives();
        // Chronos.logTimeElapsedForMark(toString() + " init ACTIVES");

        getEntity().resetFacing();

    }

    public void resetRanks(DequeImpl<DC_FeatObj> container, PROPERTY property) {
        List<DC_FeatObj> list = new LinkedList<>(container);
        for (String feat : StringMaster.openContainer(getProperty(property))) {
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
        int carryOverFactor = DC_Constants.CARRY_OVER_FACTOR;
        if (getIntParam(PARAMS.C_N_OF_ACTIONS) < 0) {
            carryOverFactor = DC_Constants.CARRY_OVER_FACTOR_NEGATIVE;
        }

        int actions = getIntParam(PARAMS.N_OF_ACTIONS) + getIntParam(PARAMS.C_N_OF_ACTIONS)
         / carryOverFactor;

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

    public void resetToughness() {
        Integer amount = getIntParam(PARAMS.TOUGHNESS_RECOVERY) * getIntParam(PARAMS.TOUGHNESS)
         / 100;
        // setParam(PARAMS.C_TOUGHNESS, amount);
        if (amount > 0) {
            getEntity().modifyParameter(PARAMS.C_TOUGHNESS, amount, getIntParam(PARAMS.TOUGHNESS));
        }
    }
}

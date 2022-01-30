package eidolons.entity.handlers.bf.unit;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.DC_ContentValsManager.ATTRIBUTE;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.active.Spell;
import eidolons.entity.item.HeroItem;
import eidolons.entity.item.QuickItem;
import eidolons.entity.unit.attach.DC_PassiveObj;
import eidolons.entity.unit.UnitModel;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.rules.combat.damage.ResistMaster;
import eidolons.game.core.game.DC_Game;
import eidolons.game.exploration.handlers.ExplorationMaster;
import main.content.ContentValsManager;
import main.content.enums.entity.UnitEnums;
import main.content.mode.STD_MODES;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.Ref.KEYS;
import main.entity.handlers.EntityMaster;
import main.entity.handlers.EntityResetter;
import main.entity.obj.ActiveObj;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.GuiEventManager;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.log.Chronos;
import main.system.launch.CoreEngine;

import static main.system.GuiEventType.SHOW_MODE_ICON;

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

    @Override
    public void toBase() {
//        getEntity().setMode(STD_MODES.NORMAL); ??
        // Chronos.mark(toString() + "to base (values)");
        getEntity().setMode(null);
        if (getEntity().isUnconscious()) {
            GuiEventManager.triggerWithParams(SHOW_MODE_ICON, getEntity(), STD_MODES.UNCONSCIOUS.getImagePath());
            // return; will break construct() !
        }
        if (getEntity().getSpecialEffects() != null) {
            getEntity().getSpecialEffects().clear();
        }
        if (getEntity().getBonusDamage() != null) {
            getEntity().getBonusDamage().clear();
        }
        super.toBase();

        if (CoreEngine.isLevelEditor()) {
            return;
        }


        if (getChecker().checkClassification(UnitEnums.CLASSIFICATIONS.TALL)) {
            getInitializer().addProperty(G_PROPS.STANDARD_PASSIVES, "" + UnitEnums.CLASSIFICATIONS.TALL, true);
        }
        if (getChecker().checkClassification(UnitEnums.CLASSIFICATIONS.SHORT)) {
            getInitializer().addProperty(G_PROPS.STANDARD_PASSIVES, "" + UnitEnums.CLASSIFICATIONS.SHORT, true);
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
            }
        } else {
            resetAttributes();
            resetObjects();
        }

        if (!getChecker().isBfObj()) {
            if (!getChecker().isNeutral()) {
                if (getChecker().isImmortalityOn()) {
                    getEntity().addPassive(UnitEnums.STANDARD_PASSIVES.INDESTRUCTIBLE);
                } else {
                    getEntity().removeProperty(G_PROPS.STD_BOOLS,
                            UnitEnums.STANDARD_PASSIVES.INDESTRUCTIBLE.getName());
                    getEntity().removeProperty(G_PROPS.STANDARD_PASSIVES,
                            UnitEnums.STANDARD_PASSIVES.INDESTRUCTIBLE.getName());
                }
                if (game.isDummyMode()) {
                    if (getGame().isDummyPlus()) {
                        resetParam(PARAMS.C_FOCUS);
                        resetParam(PARAMS.C_ESSENCE);
                        resetParam(PARAMS.C_FOCUS);
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
        // if (getOffhandWeapon() != null)
        // getOffhandWeapon().applyMods();

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
            for (Spell spell : getEntity().getSpells()) {
                spell.toBase();
            }
        }
    }

    public void resetQuickItemActives() {
        for (QuickItem q : getEntity().getQuickItems()) {
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


    public void resetObjects() {
        if (!CoreEngine.isArcaneVault())
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
        if (getEntity().getPerks() != null) {
            for (DC_PassiveObj feat : getEntity().getPerks()) {
                feat.apply();
            }
        }
        if (getEntity().getSkills() != null) {
            for (DC_PassiveObj feat : getEntity().getSkills()) {
                feat.apply();
            }
        }
        if (getEntity().getClasses() != null) {
            for (DC_PassiveObj feat : getEntity().getClasses()) {
                feat.apply();
            }

        }
        if (getEntity().getMainWeapon() != null) {
            getEntity().getMainWeapon().apply();
        } else if (getEntity().getNaturalWeapon() != null) {
            getEntity().getNaturalWeapon().apply();
        }

        if (getEntity().getOffhandWeapon() != null) {
            getEntity().getOffhandWeapon().apply();
            // if (checkDualWielding())
            // DC_Formulas.MAIN_HAND_DUAL_ATTACK_MOD
        } else if (getEntity().getOffhandNaturalWeapon() != null) {
            getEntity().getOffhandNaturalWeapon().apply();
        }

        if (getEntity().getArmor() != null) {
            getEntity().getArmor().apply();
        }
        resetQuickSlotsNumber();
        for (HeroItem item : getEntity().getQuickItems()) {
            item.apply();
        }
        for (HeroItem item : getEntity().getJewelry()) {
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


    public void resetActives() {
        for (ActiveObj active : getEntity().getActives()) {
            active.setRef(getRef());
            active.toBase();
        }
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

    }


    public void resetMasteryScores() {
        for (PARAMS mastery : DC_ContentValsManager.getMasteryParams()) {
            PARAMETER score = ContentValsManager.getMasteryScore(mastery);
            getEntity().getType().setParam(score, getIntParam(mastery));
            getEntity().setParam(score, getIntParam(mastery));
        }
    }

    public void resetAttributes() {
        for (ATTRIBUTE attr : DC_ContentValsManager.getAttributeEnums()) {
            resetAttr(attr);
        }

    }

    public void resetDefaultAttr(ATTRIBUTE attr) {
        getEntity().getType().setParam(DC_ContentValsManager.getDefaultAttr(attr.getParameter()),
                getIntParam(DC_ContentValsManager.getBaseAttr(attr.getParameter())));
    }

    /**
     * only from Arcane Vault!
     */
    public void resetDefaultAttrs() {
        for (ATTRIBUTE attr : DC_ContentValsManager.getAttributeEnums()) {
            resetDefaultAttr(attr);
        }
    }

    public void resetAttr(ATTRIBUTE attr) {
        PARAMETER baseAttr = DC_ContentValsManager.getBaseAttr(attr);
        getEntity().getType().setParam(attr.getParameter(), getIntParam(baseAttr));
        getEntity().setParam(attr.getParameter(), getIntParam(baseAttr));

    }

    public void resetHeroValues() {
        getEntity().getMasteries().apply();
        getEntity().getAttrs().apply();
    }

    public void regenerateToughness(float delta) {
        if (getEntity().isFull(PARAMS.TOUGHNESS))
            return;
        Integer amount = Math.round(getIntParam(PARAMS.TOUGHNESS_RECOVERY)
                * getIntParam(PARAMS.TOUGHNESS)
                / 100 * delta);
        // setParam(PARAMS.C_TOUGHNESS, amount);
        if (amount > 0) {
            getEntity().modifyParameter(PARAMS.C_TOUGHNESS, amount, getIntParam(PARAMS.TOUGHNESS));
        }
    }

    public void afterEffectsApplied() {
        getEntity().setBeingReset(true);
        resetHeroValues();

        if (!getInitializer().dynamicValuesReady && !game.isSimulation()) {
            getInitializer().addDynamicValues();
            getInitializer().dynamicValuesReady = true;
            getEntity().resetPercentages();
        }

        getCalculator().calculateWeight();
        getCalculator().calculateRemainingMemory();

        if (!game.isSimulation()) { // TODO perhaps I should apply and display
            // them!
            applyBuffRules();


//            recalculateInitiative();
        } else {
            afterBuffRuleEffects();
        }

        if (game.isSimulation()) {
            resetSpells();
            getEntity().setBeingReset(false);
            return;
        }
        if (getGame().getInventoryTransactionManager() != null) {
            if (getGame().getInventoryTransactionManager().isActive()) {
                getEntity().setBeingReset(false);
                return;
            }
        }
        resetSpells();
        resetQuickItemActives();
        resetActives();
        getEntity().setBeingReset(false);
    }

    public void applyBuffRules() {
        //Param revamp - toughness rule, and new essence rule
        if (!getGame().getRules().getFocusBuffRule().apply(getEntity())) {
            getEntity().setInfiniteValue(PARAMS.FOCUS, 1);
        }
        if (!getGame().getRules().getWeightRule().apply(getEntity())) {
            getEntity().setInfiniteValue(PARAMS.CARRYING_CAPACITY, 2);
        }
        getGame().getRules().getWoundsRule().apply(getEntity());
    }

    public void afterBuffRuleEffects() {
        if (getEntity().getOffhandWeapon() != null) {
            setParam(PARAMS.OFF_HAND_ATTACK, getIntParam(PARAMS.ATTACK));
            getEntity().getOffhandWeapon().applyMasteryBonus();

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

        finalizeReset();
    }

    private void finalizeReset() {
        getGame().getRules().getDynamicBuffRules().checkBuffs(getEntity());

    }

}

package main.ability;

import main.client.cc.HeroManager;
import main.client.cc.logic.spells.LibraryManager;
import main.content.DC_TYPE;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.ValuePages;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.XLinkedMap;
import main.data.ability.construct.VariableManager;
import main.elements.conditions.RequirementsManager;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.battlecraft.rules.UnitAnalyzer;
import main.game.core.game.DC_Game;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.secondary.InfoMaster;
import main.system.auxiliary.secondary.WorkspaceMaster;

import java.util.List;
import java.util.Map;

public class UnitLibrary {
    private static Map<ObjType, Integer> spellPool;
    private static Unit unit;
    private static Integer xpPercentageToSpend;
    private static HeroManager heroManager;

    public static void learnSpellsForUnit(Unit learner) {
        unit = learner;
        // a better approach: iterate thru new/memo/verb until <...>
        // at each iteration, learn 1 new spell if possible, memorize max and
        // getOrCreate the rest max en verbatim
        if (!unit.checkProperty(PROPS.SPELL_PLAN) || getPlan().contains(StringMaster.BASE_CHAR)) {
            if (UnitAnalyzer.checkIsCaster(unit)) {
                generateSpellPlan();
            } else {
                return;
            }
        }
        boolean result = true;
        while (result) {
            result = learnSpells(LEARN_CASE.NEW) || learnSpells(LEARN_CASE.MEMORIZE)
                    || learnSpells(LEARN_CASE.EN_VERBATIM);
        }

        // learnSpells(LEARN_CASE.NEW); // randomized how? same as skills - if
        // // there are
        // // multiple options, randomize!
        // // but the property... could do with weights, really :)
        // learnSpellUpgradesForUnit(); // Same here!
        // learnSpells(LEARN_CASE.MEMORIZE); // fill memory first, naturally...
        // I
        // could
        // randomize X times and choose the best
        // fit!
        // learnSpells(LEARN_CASE.EN_VERBATIM); // same here with the remaining
        // xp!

    }

    private static void generateSpellPlan() {
        String property = getPlan();
        String plan = property.replace(StringMaster.BASE_CHAR, "");
        if (!plan.isEmpty()) {
            if (!plan.endsWith(";")) {
                plan += ";"; // ++ syntax for cancelling [group] spells...
            }
        }
        for (PARAMETER mastery : ValuePages.MASTERIES_MAGIC_SCHOOLS) {
            Integer score = unit.getIntParam(mastery);
            if (score <= 0) {
                continue;
            }
            List<ObjType> types = DataManager.getTypesGroup(DC_TYPE.SPELLS, mastery.getName());
            for (ObjType t : types) {
                if (plan.contains(t.getName())) {
                    continue;
                }
                int weight = Math.max(1, score - t.getIntParam(PARAMS.SPELL_DIFFICULTY));
                plan += t.getName() + StringMaster.wrapInParenthesis("" + weight)
                        + StringMaster.CONTAINER_SEPARATOR;
            }
        }
        unit.setProperty(PROPS.SPELL_PLAN, plan, true);
    }

    private static String getPlan() {
        return unit.getProperty(PROPS.SPELL_PLAN);
    }

    private static boolean learnSpells(LEARN_CASE lc) {
        if (lc == LEARN_CASE.UPGRADE) {
            // ???
        }
        boolean result = false;
        xpPercentageToSpend = 100;
        if (lc == LEARN_CASE.NEW) {
            xpPercentageToSpend = 50;
        }
        // balancing between learning new and learning en verbatim...
        initPool(lc);
        Loop.startLoop(75);
        while (!Loop.loopEnded() && !spellPool.isEmpty()) {
            ObjType spellType = new RandomWizard<ObjType>().getObjectByWeight(spellPool);
            if (checkCanLearnSpell(spellType, lc)) {
                if (!learnSpell(spellType, lc)) {
                    return false;
                }
                // spellPool.remove(spellType);
                if (lc == LEARN_CASE.NEW || lc == LEARN_CASE.UPGRADE) {
                    return true; // TODO *one at a time, right?*
                }
                result = true;
            }
            initPool(lc);
        }
        unit.initSpells(true);
        return result;
    }

    private static void initPool(LEARN_CASE lc) {
        LibraryManager.initSpellbook(unit);
        spellPool = new XLinkedMap<>();
        for (String substring : StringMaster.openContainer(unit.getProperty(getSourceProp(lc)))) {
            ObjType type = DataManager.getType(VariableManager.removeVarPart(substring),
                    DC_TYPE.SPELLS);
            if (checkCanLearnSpell(type, lc)) {
                Integer weight = StringMaster.getWeight(substring);
                if (weight <= 0) {
                    weight = 1;
                }
                spellPool.put(type, weight);
            }
        }
    }

    private static boolean checkCanLearnSpell(ObjType type, LEARN_CASE lc) {
        if (type == null) {
            return false;
        }

        if (!WorkspaceMaster.checkTypeIsReadyForUse(type)) {
            return false;
        }

        if (lc == LEARN_CASE.NEW || lc == LEARN_CASE.UPGRADE) {
            if (LibraryManager.checkKnown(unit, type)) {
                return false; // already in reqs?
            }
        }
        if (lc == LEARN_CASE.EN_VERBATIM || lc == LEARN_CASE.MEMORIZE) {
            if (!LibraryManager.checkKnown(unit, type)) {
                return false;
            }
        }

        if (lc == LEARN_CASE.EN_VERBATIM || lc == LEARN_CASE.MEMORIZE) {
            if (!LibraryManager.checkKnown(unit, type)) {
                return false;
            }
        }

        if (unit.checkProperty(getTargetProp(lc), type.getName())) {
            return false;
        }
        String reason = unit.getGame().getRequirementsManager().check(unit, type, getMode(lc));
        if (reason == null) {
            return true;
        } else {// TODO there could be more than one reason, right? =)
            if (reason.equals(InfoMaster.SPELL_KNOWN)) {
                if (lc == LEARN_CASE.EN_VERBATIM) {
                    return true;
                }
            }
            if (reason.contains(PARAMS.XP.getName())) {
                if (unit.checkParam(PARAMS.XP, type.getIntParam(PARAMS.XP_COST) + "*100/"
                        + xpPercentageToSpend)) // TODO discount
                {
                    return true;
                }
            }
        }
        return false;
    }

    private static int getMode(LEARN_CASE lc) {
        if (lc == LEARN_CASE.EN_VERBATIM) {
            return RequirementsManager.VERBATIM_MODE;
        }
        return (lc == LEARN_CASE.MEMORIZE) ? RequirementsManager.ALT_MODE
                : RequirementsManager.NORMAL_MODE;
    }

    private static void learnSpellUpgradesForUnit() {
        // TODO
        // if (empty) autoupgrade = true; => find and add!

        // DataManager.getTypesGroup(TYPE, group)

    }

    private static boolean learnSpell(ObjType spellType, LEARN_CASE lc) {
        boolean result;
        if (lc == LEARN_CASE.MEMORIZE) {
            result = getHeroManager().addMemorizedSpell(unit, spellType);
        } else {
            result = getHeroManager().addItem(unit, spellType, DC_TYPE.SPELLS, getTargetProp(lc),
                    false, false);
        }
        if (!result) {
            return false;
        }
        LogMaster.log(1, "SPELL TRAINING: " + unit.getName() + " learns "
                + spellType.getName() + " (" + lc.toString() + "), remaining xp: "
                + unit.getIntParam(PARAMS.XP));

        getHeroManager().update(unit);
        return true;
    }

    private static HeroManager getHeroManager() {
        if (heroManager == null) {
            heroManager = new HeroManager(DC_Game.game);
            heroManager.setTrainer(true);
        }
        return heroManager;
    }

    private static PROPERTY getTargetProp(LEARN_CASE lc) {
        switch (lc) {
            case EN_VERBATIM:
                return PROPS.VERBATIM_SPELLS;
            case MEMORIZE:
                return PROPS.MEMORIZED_SPELLS;
            case NEW:
                return PROPS.LEARNED_SPELLS;
            case UPGRADE:
                return PROPS.LEARNED_SPELLS;
        }
        return null;
    }

    private static PROPERTY getSourceProp(LEARN_CASE lc) {
        switch (lc) {
            case EN_VERBATIM:
                return PROPS.SPELLBOOK;
            case MEMORIZE:
                return PROPS.SPELLBOOK;
            case NEW:
                return PROPS.SPELL_PLAN;
            case UPGRADE:
                return PROPS.SPELL_UPGRADES_PLAN;
        }
        return null;
    }

    public enum LEARN_CASE {
        NEW, EN_VERBATIM, MEMORIZE, UPGRADE
    }
}

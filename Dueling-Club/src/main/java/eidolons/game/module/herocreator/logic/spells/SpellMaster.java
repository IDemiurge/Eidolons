package eidolons.game.module.herocreator.logic.spells;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.active.Spell;
import eidolons.entity.obj.unit.Unit;
import eidolons.system.DC_Formulas;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.elements.conditions.Condition;
import main.elements.conditions.NumericCondition;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.List;

public class SpellMaster {

    private static final String SD = "{SPELL_SPELL_DIFFICULTY}";
    private static final String STANDARD = "STANDARD";

    public static Condition getSpellRequirements(Spell spell) {
        // SD: Mastery or Knowledge
        String spellGroup = spell.getProperty(G_PROPS.SPELL_GROUP);
        return new NumericCondition("max({SOURCE_KNOWLEDGE}/2-5," + "{SOURCE_" + spellGroup
                + "MASTERY})", SD);
    }

    public static String initSpellbook(Unit hero) {
        List<ObjType> list = new ArrayList<>();
        if (hero.getGame().isSimulation()) {
            checkNewAutoVerbatim(hero);
            for (ObjType type : DataManager.getTypes(DC_TYPE.SPELLS)) {
                if (checkKnown(hero, type)) {
                    // if (!hero.checkProperty(PROPS.LEARNED_SPELLS,
                    // type.getName()))
                    if (!isUpgraded(hero, type)) {
                        list.add(type);
                    }
                }
            }
            StringBuilder known = new StringBuilder();
            for (ObjType type : list) {
                known.append(type.getName()).append(ContainerUtils.getContainerSeparator());
            }
            hero.setProperty(PROPS.KNOWN_SPELLS, known.toString(), true);

        } else {
            String prop = hero.getProperty(PROPS.KNOWN_SPELLS);
            list = DataManager.toTypeList(prop, DC_TYPE.SPELLS);
        }
        StringBuilder spellbookBuilder = new StringBuilder();
        for (ObjType type : list) {
            if (!hero.checkProperty(PROPS.MEMORIZED_SPELLS, type.getName())) {
                if (!hero.checkProperty(PROPS.VERBATIM_SPELLS, type.getName())) {
                    spellbookBuilder.append(type.getName()).append(ContainerUtils.getContainerSeparator());
                }
            }
        }
        String spellbook = spellbookBuilder.toString();
        hero.setProperty(PROPS.SPELLBOOK, spellbook, true);
        return spellbook;
    }

    private static boolean isUpgraded(Unit hero, ObjType type) {
        return hero.checkProperty(PROPS.UPGRADED_SPELLS, type.getName());
    }

    public static boolean checkHeroHasSpell(Unit hero, ObjType type) {
        if (hero.checkProperty(PROPS.MEMORIZED_SPELLS, type.getName())) {
            return true;
        }
        if (hero.checkProperty(PROPS.DIVINED_SPELLS, type.getName())) {
            return true;
        }
        return hero.checkProperty(PROPS.VERBATIM_SPELLS, type.getName());
    }

    public static Spell getSpellFromHero(Unit hero, String name) {
        for (Spell s : hero.getSpells()) {
            if (s.getName().equalsIgnoreCase(name)) {
                return s;
            }
        }

        return null;
    }

    public static void checkNewAutoVerbatim(Unit hero) {
        for (String spell : ContainerUtils.open(hero.getProperty(PROPS.LEARNED_SPELLS))) {
            boolean result = false;
            ObjType type = DataManager.getType(spell, DC_TYPE.SPELLS);
            if (type == null) {
                main.system.auxiliary.log.LogMaster.log(1, "LibraryManager::checkNewAutoVerbatim - No such spell " + spell);
                continue;
            }
            if (checkStandardSpell(type)) {
                if (checkDoubleKnowledge(hero, type)) {
                    result = true;
                }

                if (checkKnowledge(hero, type)) // optimize-merge!
                {
                    if (checkMastery(hero, type)) {
                        result = true;
                    }
                }
            }

            if (result) {
                if (hero.checkProperty(PROPS.MEMORIZED_SPELLS, type.getName())) {
                    hero.removeProperty(PROPS.MEMORIZED_SPELLS, type.getName(), true);
                }
                addVerbatimSpell(hero, type);
            }
        }
    }

    public static boolean checkKnown(Unit hero, ObjType type) {
        if (isLearned(hero, type)) {
            return true;
        }
        if (checkStandardSpell(type)) {
            if (checkDoubleKnowledge(hero, type)) {
                return true;
            }
            if (checkKnowledge(hero, type)) // Intelligence ?
            {
                return checkMastery(hero, type);
            }
        }
        return false;
    }

    public static boolean isLearned(Unit hero, ObjType type) {
        return hero.checkProperty(PROPS.LEARNED_SPELLS, type.getName());
    }

    public static boolean isKnown(Unit hero, Entity type) {
        return hero.checkProperty(PROPS.KNOWN_SPELLS, type.getName());
    }

    public static boolean checkKnowledge(Unit hero, ObjType type) {
        if (type.getIntParam(PARAMS.SPELL_DIFFICULTY) == 0) {
            return false;
        }
        return hero.checkParam(PARAMS.KNOWLEDGE, type.getParam(PARAMS.SPELL_DIFFICULTY));
    }

    // 2X KNOWLEDGE FOR ANY SPELL
    // IF MASTERY IS >1
    public static boolean checkDoubleKnowledge(Unit hero, ObjType type) {
        return (hero.checkParam(PARAMS.KNOWLEDGE, ""
                + Math.round(type.getIntParam(PARAMS.SPELL_DIFFICULTY)
                * DC_Formulas.KNOWLEDGE_ANY_SPELL_FACTOR)))
                && hero.checkParam(ContentValsManager
                .findMastery(type.getProperty(G_PROPS.SPELL_GROUP)));
    }

    public static boolean checkMastery(Unit hero, ObjType type) {

        return hero.checkParam(ContentValsManager.findMastery(type.getProperty(G_PROPS.SPELL_GROUP)),
                type.getParam(PARAMS.SPELL_DIFFICULTY));
    }

    public static boolean checkStandardSpell(ObjType type) {
        return type.checkSingleProp(G_PROPS.GROUP, STANDARD);
    }

    public static boolean replaceSpellVersion(Unit hero, Entity type, PROPERTY poolProp) {
        return hasSpellVersion(hero, type, poolProp, true);
    }

    public static Spell getVerbatimSpellVersion(Unit hero, Entity type) {
        hero.initSpells(true);
        boolean upgrade = type.isUpgrade();
        String baseName = (upgrade) ? type.getProperty(G_PROPS.BASE_TYPE) : type.getName();

        for (Spell s : hero.getSpells()) {
            // TODO refactor into checkUpgrade(spell, type)
            if (StringMaster.compare(s.getSpellPool() + "", PROPS.VERBATIM_SPELLS.getName(), false)) {
                if (s.isUpgrade()) {
                    if (s.getProperty(G_PROPS.BASE_TYPE).equalsIgnoreCase(baseName)) {
                        return s;
                    }
                } else if (s.getName().equalsIgnoreCase(baseName)) {
                    return s;
                }

            }
        }
        return null;
    }

    public static boolean hasSpellVersion(Unit hero, Entity type, PROPERTY poolProp,
                                          boolean replace) {
        hero.initSpells(true);
        boolean upgrade = type.isUpgrade();
        String baseName = (upgrade) ? type.getProperty(G_PROPS.BASE_TYPE) : type.getName();
        for (Spell spell : hero.getSpells()) {
            if (StringMaster.compare(spell.getSpellPool() + "", poolProp.getName(), false)) {
                if (spell.isUpgrade()) {
                    if (spell.getProperty(G_PROPS.BASE_TYPE).equalsIgnoreCase(baseName)) {
                        if (replace) {
                            replaceSpell(hero, type, poolProp, spell);
                        }
                        return true;
                    }
                } else if (spell.getName().equalsIgnoreCase(baseName)) {
                    if (replace) {
                        replaceSpell(hero, type, poolProp, spell);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean replaceSpell(Unit hero, Entity type, PROPERTY poolProp,
                                        Spell spell) {
        if (poolProp == PROPS.VERBATIM_SPELLS) {
            hero.addProperty(PROPS.UPGRADED_SPELLS, spell.getName(), true);
        }
        return hero.getType().replaceContainerPropItem(poolProp, type.getName(), spell.getName());
    }

    public static boolean hasSpellVersion(Unit hero, Entity type, PROPERTY pool) {
        return hasSpellVersion(hero, type, pool, false);
    }

    public static void addVerbatimSpell(Unit hero, ObjType type) {
        hero.getType().addProperty(PROPS.VERBATIM_SPELLS, type.getName(), true);
        hero.getType().addProperty(PROPS.LEARNED_SPELLS, type.getName(), true);
        hero.addProperty(PROPS.VERBATIM_SPELLS, type.getName(), true);
        hero.addProperty(PROPS.LEARNED_SPELLS, type.getName(), true);
        hero.initSpells(true);
    }

    public static void removeSpell(Unit unit, ObjType type) {
        removeSpell(unit, type, true);
//                !CoreEngine.isIDE());
    }

    public static void removeSpell(Unit unit, ObjType type, boolean forever) {
        unit.removeProperty(forever, PROPS.VERBATIM_SPELLS, type.getName());
        unit.removeProperty(forever, PROPS.MEMORIZED_SPELLS, type.getName());
        unit.initSpellbook();
        unit.initSpells(true);
    }

    public static void removeSpells(Unit unit) {
        unit.removeProperty(PROPS.VERBATIM_SPELLS);
        unit.removeProperty(PROPS.MEMORIZED_SPELLS);
        unit.getType().removeProperty(PROPS.VERBATIM_SPELLS);
        unit.getType().removeProperty(PROPS.MEMORIZED_SPELLS);
        unit.initSpellbook();
        unit.initSpells(true);
    }
}

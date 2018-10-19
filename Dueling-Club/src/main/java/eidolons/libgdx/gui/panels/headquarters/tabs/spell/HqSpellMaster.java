package eidolons.libgdx.gui.panels.headquarters.tabs.spell;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.active.DC_SpellObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.module.herocreator.logic.spells.LibraryManager;
import eidolons.libgdx.gui.panels.headquarters.tabs.spell.SpellActor.SPELL_OVERLAY;
import main.content.enums.entity.SpellEnums.SPELL_POOL;
import main.elements.conditions.RequirementsManager;

/**
 * Created by JustMe on 4/17/2018.
 */
public class HqSpellMaster {
    public static String getOverlay(DC_SpellObj sub
    ) {
        if (sub.getSpellPool() != null)
            switch (sub.getSpellPool()) {
                case MEMORIZED:
                    return SPELL_OVERLAY.MEMORIZED.imagePath;
                case DIVINED:
                    return SPELL_OVERLAY.DIVINED.imagePath;
                case VERBATIM:
                    return SPELL_OVERLAY.VERBATIM.imagePath;
            }

        return null;
    }

    public static void learnSpellEnVerbatim(Unit hero, DC_SpellObj spell) {
        //        LibraryManager.addVerbatimSpell(hero, spell.getType());
        hero.addProperty(true, PROPS.VERBATIM_SPELLS, spell.getName());
        spellsChanged(hero);

    }

    public static void learnSpell(Unit hero, DC_SpellObj spell) {
        hero.addProperty(true, PROPS.LEARNED_SPELLS, spell.getName());
        spellsChanged(hero);
    }

    public static void memorizeSpell(Unit hero, DC_SpellObj spell) {
        hero.addProperty(true, PROPS.MEMORIZED_SPELLS, spell.getName());
        spellsChanged(hero);
    }

    private static void spellsChanged(Unit hero) {
        hero.initSpells(true);
    }

    public static void unmemorizeSpell(Unit hero, DC_SpellObj spell) {
        hero.removeProperty(true, PROPS.MEMORIZED_SPELLS, spell.getName());
        spellsChanged(hero);
    }

    public static boolean canMemorize(DC_SpellObj spell) {
        if (spell.getSpellPool() != SPELL_POOL.SPELLBOOK)
            return false;
        if (spell.getOwnerUnit().calculateRemainingMemory() < spell.getIntParam(PARAMS.SPELL_DIFFICULTY))
            return false;
        if (spell.isUpgrade()) {
            if (LibraryManager.hasSpellVersion(spell.getOwnerUnit(), spell, PROPS.MEMORIZED_SPELLS)) {
                return false;
            }
        }
        return spell.getGame().getRequirementsManager().check(spell.getOwnerUnit(), spell
         , RequirementsManager.ALT_MODE) == null;
    }

    public static boolean canLearn(DC_SpellObj spell) {
        return spell.getGame().getRequirementsManager().check(spell.getOwnerUnit(), spell) == null;
    }

    public static boolean canLearnEnVerbatim(DC_SpellObj spell) {
        return spell.getGame().getRequirementsManager().check(spell.getOwnerUnit(), spell,
         RequirementsManager.VERBATIM_MODE) == null;
    }

}

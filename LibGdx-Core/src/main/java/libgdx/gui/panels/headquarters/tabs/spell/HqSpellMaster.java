package libgdx.gui.panels.headquarters.tabs.spell;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.active.Spell;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import eidolons.game.module.herocreator.HeroManager;
import eidolons.game.module.herocreator.logic.spells.SpellMaster;
import libgdx.gui.panels.headquarters.tabs.spell.SpellActor.SPELL_OVERLAY;
import main.content.enums.entity.SpellEnums.SPELL_POOL;
import main.elements.conditions.RequirementsManager;

/**
 * Created by JustMe on 4/17/2018.
 */
public class HqSpellMaster {
    public static String getOverlay(Spell sub
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
    //DC Review - any global changes to spell sys?

    public static void learnSpellEnVerbatim(Unit hero, Spell spell) {
        //        LibraryManager.addVerbatimSpell(hero, spell.getType());
        Integer cost = Integer.valueOf(HeroManager.getCost(spell, hero));
        hero.modifyParameter(PARAMS.SPELL_POINTS_UNSPENT, -cost);
        hero.addProperty(true, PROPS.VERBATIM_SPELLS, spell.getName());
        spellsChanged(hero);

    }

    public static void learnSpell(Unit hero, Spell spell) {
        Integer cost = Integer.valueOf(HeroManager.getCost(spell, hero));
        hero.modifyParameter(PARAMS.SPELL_POINTS_UNSPENT, -cost);
        hero.addProperty(true, PROPS.LEARNED_SPELLS, spell.getName());
        spellsChanged(hero);
    }

    public static void memorizeSpell(Unit hero, Spell spell) {
        hero.addProperty(true, PROPS.MEMORIZED_SPELLS, spell.getName());
        spellsChanged(hero);
    }

    private static void spellsChanged(Unit hero) {

        hero.initSpells(true);
    }

    public static void unmemorizeSpell(Unit hero, Spell spell) {
        hero.removeProperty(true, PROPS.MEMORIZED_SPELLS, spell.getName());
        spellsChanged(hero);
    }

    public static boolean canMemorize(Spell spell) {
        if (spell.getSpellPool() != SPELL_POOL.SPELLBOOK)
            return false;
        if (spell.getOwnerUnit().calculateRemainingMemory() < spell.getIntParam(PARAMS.SPELL_DIFFICULTY))
            return false;
        if (spell.isUpgrade()) {
            if (SpellMaster.hasSpellVersion(spell.getOwnerUnit(), spell, PROPS.MEMORIZED_SPELLS)) {
                return false;
            }
        }
        return spell.getGame().getRequirementsManager().check(spell.getOwnerUnit(), spell
                , RequirementsManager.ALT_MODE) == null;
    }

    public static boolean canLearn(Spell spell) {
        return spell.getGame().getRequirementsManager().check(Eidolons.getMainHero(), spell) == null;
    }

    public static boolean canLearnEnVerbatim(Spell spell) {
        return spell.getGame().getRequirementsManager().check(spell.getOwnerUnit(), spell,
                RequirementsManager.VERBATIM_MODE) == null;
    }

}

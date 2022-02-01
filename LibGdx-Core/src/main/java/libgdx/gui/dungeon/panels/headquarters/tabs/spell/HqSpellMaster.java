package libgdx.gui.dungeon.panels.headquarters.tabs.spell;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.active.Spell;
import eidolons.entity.unit.Unit;
import eidolons.game.core.Core;
import eidolons.netherflame.eidolon.heromake.handlers.HeroManager;
import libgdx.gui.dungeon.panels.headquarters.tabs.spell.SpellActor.SPELL_OVERLAY;
import main.content.enums.entity.SpellEnums.SPELL_POOL;
import main.elements.conditions.RequirementsManager;

/**
 * Created by JustMe on 4/17/2018.
 */
public class HqSpellMaster {
    @Deprecated
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

    public static void learnSpell(Unit hero, Spell spell) {
        Integer cost = Integer.valueOf(HeroManager.getCost(spell, hero));
        hero.modifyParameter(PARAMS.SPELL_POINTS_UNSPENT, -cost);
        hero.addProperty(true, PROPS.LEARNED_SPELLS, spell.getName());
        spellsChanged(hero);
    }

    private static void spellsChanged(Unit hero) {

    }

    public static boolean canLearn(Spell spell) {
        return spell.getGame().getRequirementsManager().check(Core.getMainHero(), spell) == null;
    }

}

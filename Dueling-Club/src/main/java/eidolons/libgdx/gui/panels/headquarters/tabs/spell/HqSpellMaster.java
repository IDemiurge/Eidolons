package eidolons.libgdx.gui.panels.headquarters.tabs.spell;

import eidolons.entity.active.DC_SpellObj;
import eidolons.libgdx.gui.panels.headquarters.tabs.spell.SpellActor.SPELL_OVERLAY;

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

    public static void rightClick(DC_SpellObj spellObj) {
        if (spellObj.isMemorized()) {

        }
    }
}

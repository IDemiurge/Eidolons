package eidolons.libgdx.screens.map.town.library.repertoire;

import eidolons.libgdx.gui.panels.TabbedPanel;
import eidolons.libgdx.screens.map.town.library.hero.SpellSlotsPanel;

/**
 * Created by JustMe on 11/21/2018.
 */
public class SpellRepertoirePanel extends TabbedPanel{

    public enum LIBRARY_VIEW{
        GRIMOIRES("Study the grimoires available here"),
        CURRICULUM("Review your progress in the Magical Arts"),
        SHOP("Browse the library's wares - books, scrolls and more"),
        SPELLCRAFT("Use the materials and equipment to construct spells of your own"),
        ;
        String tooltip;

        LIBRARY_VIEW(String tooltip) {
            this.tooltip = tooltip;
        }
    }

    CurriculumPanel curriculumPanel;

    SpellSlotsPanel repertoirePanel;
}

package eidolons.game.module.adventure.town;

import eidolons.game.module.adventure.MacroGame;
import main.entity.Ref;
import main.entity.type.ObjType;

public class Library extends TownPlace {
    /*
    * similar to Shop
    * additional functions
    * Hire Tutor
    * Buy scrolls (learn while camping)
    */
    public Library(MacroGame game, ObjType type, Ref ref) {
        super(game, type, ref);
    }

    public void initSpells() {
        // s
        /*
         * perhaps it should work like 'rent spellbook, learn the spell, return' (gold back)
		 */

    }

    public enum LIBRARY_MODIFIER {
        HERETIC, SHADOW, ARCANE,
        // PROPS.ASPECT_PREFS
    }

    public enum LIBRARY_TYPE {

    }

    public class Spellbook {
        // cost, spell assortment, difficulty, quality, danger, weight,

    }

}
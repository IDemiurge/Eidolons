package eidolons.macro.entity.library;

import eidolons.entity.active.Spell;
import eidolons.macro.MacroGame;
import eidolons.macro.entity.shop.Shop;
import eidolons.macro.entity.shop.ShopItemManager;
import eidolons.macro.entity.town.TownPlace;
import main.content.enums.entity.SkillEnums.MASTERY;
import main.entity.Ref;
import main.entity.type.ObjType;

import java.util.List;

public class Library extends TownPlace {
    /*
    * Hire Tutor
    * Buy scrolls (learn while camping)
    *
    * available upgrades
    * spell templates
    *
    * generate custom spells?
     *
     * what are Grimoires?
     *
     * "transcribe spell" - so it enters the curriculum?
     *
    */
    List<Spell> spells;

    List<MASTERY> schools;
    List<MASTERY> mentoredMasteries;

    Shop shop; //use it to handle money's? ;)
    ShopItemManager itemManager;


    public Library(MacroGame game, ObjType type, Ref ref) {
        super(game, type, ref);
        LibraryManager manager = new LibraryManager();
    }

    public void initSpells() {

    }

    public enum LIBRARY_MODIFIER {
        HERETIC, OCCULT, SECRET,
        MAGE_GUILD, DEVOUT,
        // PROPS.ASPECT_PREFS
    }

    public enum LIBRARY_TYPE {
        ARCHIVE, HERMIT,
        STUDY, PLACE_OF_POWER,
    }

    public class Spellbook {
        // cost, spell assortment, difficulty, quality, danger, weight,

    }

}

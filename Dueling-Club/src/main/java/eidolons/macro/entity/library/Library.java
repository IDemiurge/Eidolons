package eidolons.macro.entity.library;

import eidolons.content.PARAMS;
import eidolons.entity.active.Spell;
import eidolons.game.core.Eidolons;
import eidolons.macro.MacroGame;
import eidolons.macro.entity.shop.Shop;
import eidolons.macro.entity.shop.ShopItemManager;
import eidolons.macro.entity.town.TownPlace;
import main.content.enums.entity.SkillEnums.MASTERY;
import main.content.enums.entity.SpellEnums.SPELL_GROUP;
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

    LIBRARY_TYPE libType;
    LIBRARY_MODIFIER libMod;

    Shop shop; //use it to handle money's? ;)
    ShopItemManager itemManager;
    private int baseGold;


    public Library(MacroGame game, ObjType type, Ref ref) {
        super(game, type, ref);
        LibraryManager manager = new LibraryManager();
    }

    public void initCustomSpells() {

    }
    public void initStandardSpells() {
        SPELL_GROUP group = null;
//        int minSd = getMinSd();
//        int maxSd = getMaxSd();
//        Set<ObjType> pool = filterSpellPool(group, minSd, maxSd);
//        WeightMap<SPELL_GROUP> groupMap= createGroupWeightMap();
//        while(canAcquireStdSpell()){
//            Spell spell = chooseSpell(pool);
//            acquireSpell(spell);
//        }
    }

    private int getMaxSd() {
        int power = Eidolons.getMainHero().getPower()
         + getPower();

        return 0;
    }

    private int getPower() {
//        getIntParam(MACRO_PARAMS.LIBRARY_POWER)
        int power = getIntParam(PARAMS.POWER);
//        power += power*libMod.getPowerMod();
//        power += power*libType.getPowerMod();
        return power;
    }

    private boolean canAcquireStdSpell() {
        //has gold, check N
        if (shop.getGold()> baseGold*2) {
            return true;
        }
        return true;
    }

    public void initRepertoire( ){

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

    public static class Spellbook {
        // cost, spell assortment, difficulty, quality, danger, weight,

    }

}

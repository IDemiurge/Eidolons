package eidolons.game.battlecraft.logic.meta.universal;

import eidolons.content.PARAMS;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.item.DC_HeroSlotItem;
import eidolons.entity.obj.unit.Unit;
import main.content.C_OBJ_TYPE;
import main.content.values.parameters.PARAMETER;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;
import main.system.math.MathMaster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 7/22/2017.
 */
public class LootMaster<E extends MetaGame> extends MetaGameHandler<E> {

    Map<Unit, String> prefMap;
    int lootValue;

    public LootMaster(MetaGameMaster master) {
        super(master);
    }

    public int getChanceForOwnedItemToDrop(Unit unit, DC_HeroItemObj item){
        // depends on difficulty?!
        if (item instanceof DC_HeroSlotItem) {
            int minChance=25;
            int val=10+ item.getIntParam(PARAMS.C_DURABILITY) * item.getIntParam(PARAMS.GOLD_COST) / 500;
            int maxChance=75;

            //n of items on this unit?
            // is enemy?
            return MathMaster.getMinMax(val, minChance, maxChance);

        }
        return 100;
    }

    public void awardLoot() {
        lootValue = 0;
        prefMap = new HashMap<>();
        String lootData = generateLootData();
        splitLoot(lootData);
        /*
        per enemy?
        so the idea was that forcing ppl to pick things up during combat is ridiculous :)
        spread over heroes?
        claim on items?

        generate loot data object - item_name(quantity);...
        dataUnit? hardly, but formatted
        split loot between heroes
         */

    }

    private void splitLoot(String lootData) {

        List<ObjType> itemList = DataManager.toTypeList(lootData, C_OBJ_TYPE.ITEMS);
        for (Unit hero : getPartyManager().getParty().getMembers()) {
            for (ObjType item : itemList) {
                LOOT_PREFERENCE preference = getPreference(hero, item);
                Boolean result = applyPreference(hero, preference, item, true);
                if (result == null)
                    break;
                if (result) {
                    awardItem(hero, item);
                }
            }
        }
    }

    private void awardItem(Unit hero, ObjType item) {
    }


    //null if hero no longer needs items
    private Boolean applyPreference(Unit hero, LOOT_PREFERENCE preference,
                                    ObjType item, boolean first) {
        int lootShare = evaluateLootShare(hero);
        if (lootShare <= 0)
            return null;
        if (preference == LOOT_PREFERENCE.WONT_HAVE)
            return false;
        if (checkRequest(preference, hero, item))
            return makeRequest(preference, hero, item);
        else
            return autoresolveLoot(preference, hero, item);
//        if (first){
//            return false;
//        }

//        if (preference == LOOT_PREFERENCE.WANT_HAVE)
//            return true;
//        return false;
    }

    private Boolean autoresolveLoot(LOOT_PREFERENCE preference, Unit hero, ObjType item) {
        return null;
    }

    private Boolean makeRequest(LOOT_PREFERENCE preference, Unit hero, ObjType item) {
        //guiEvent
        Boolean result = null;// WaitMaster.waitForInput();
        return result;
    }

    private boolean checkRequest(LOOT_PREFERENCE preference, Unit hero, ObjType item) {
//        if (isRequestsDisabled())
//            return false;
        return preference == LOOT_PREFERENCE.MUST_HAVE;
    }

    private int evaluateLootShare(Unit hero) {
//        int share = lootValue / getPartyManager().getParty().getMembers().size();
//        int mod = 100 + hero.getIntParam(PARAMS.LOOT_SHARE_BONUS);
//        return share * mod / 100;
        return 100;
    }

    private LOOT_PREFERENCE getPreference(Unit hero, ObjType item) {
        //preferences defined as item groups/classes? plus specials
        String prefs =
         getPrefs(hero);

        for (LOOT_PREFERENCE preference : LOOT_PREFERENCE.values()) {
            switch (preference) {
                case MUST_HAVE:


                    break;
                case WANT_HAVE:
                    break;
                case INDIFFERENT:
                    break;
                case WONT_HAVE:
                    break;
            }
        }
        return null;
    }

    private String getPrefs(Unit hero) {
        String prefs = prefMap.get(hero);
        if (prefs == null) {
            prefs = generatePrefs(hero)
// +hero.getProperty(PROPS.LOOT_PREFERENCES)
            ;
            prefMap.put(hero, prefs);
        }
        return prefs;
    }

    private String generatePrefs(Unit hero) {
        String prefs = "";
        //getVar highest masteries?
        List<PARAMETER> highest = hero.getMasteries().getHighest(10);
        for (PARAMETER mastery : highest) {

        }

        return prefs;
    }

    private String generateLootData() {
        final StringBuilder builder = new StringBuilder();
        getMaster().getBattleMaster().getStatManager().
         getStats().getSlainEnemyUnits().forEach(enemy -> {
            builder.append(getLootFromEnemy(enemy) + StringMaster.SEPARATOR);
            lootValue += evaluateLootValue(enemy);
        });
        return null;
    }

    private int evaluateLootValue(Unit enemy) {
        return enemy.calculatePower();
    }

    private String getLootFromEnemy(Unit enemy) {
        return null;
    }

    public enum LOOT_PREFERENCE {
        MUST_HAVE,
        WANT_HAVE,
        INDIFFERENT,
        WONT_HAVE,


    }
}

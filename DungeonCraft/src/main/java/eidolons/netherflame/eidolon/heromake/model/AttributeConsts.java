package eidolons.netherflame.eidolon.heromake.model;

import com.badlogic.gdx.utils.ObjectMap;
import eidolons.content.ATTRIBUTE;
import eidolons.content.PARAMS;
import eidolons.entity.unit.Unit;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.List;

public class AttributeConsts {

    public static final float SAVE_BONUS = 0.1f;
    public static final float SAVE_BONUS_2X= 0.2f;

    public static final float STR_TOU_BONUS = 5.0f;
    public static final float STR_END_BONUS = 5.0f;
    public static final float STR_MIGHT_BONUS = SAVE_BONUS_2X;
    public static final float B25_STR_CARRY_CAPACITY = 2.0f;
    public static final float A25_STR_PENALTY_REDUCTION = 1.0f;

    public static final float VIT_TOU_BONUS = 5f;
    public static final float VIT_END_BONUS = 25.0f;
    public static final float VIT_MIGHT_BONUS = SAVE_BONUS_2X;
    public static final float A25_VIT_END_REGEN = 1.0f;
    public static final float B25_VIT_ELEMENTAL_RESIST = 1.0f;


    public static final float AGI_ATK_BONUS = 1;
    public static final float AGI_REFLEX_BONUS = SAVE_BONUS_2X;
    public static final float B25_AGI_ARMOR_PENETRATION = 1;
    public static final float A25_AGI_INITIATIVE = 0.2f;

    public static final float DEX_DEF_BONUS = 1;
    public static final float DEX_REFLEX_BONUS = SAVE_BONUS_2X;
    public static final float B25_DEX_INITIATIVE =  0.2f;
    public static final float A25_DEX_STEALTH = 1;

    public static final float WIL_RES_BONUS = 1;
    public static final float WIL_GRIT_BONUS = SAVE_BONUS;
    public static final float WIL_SPIRIT_BONUS = SAVE_BONUS;
    public static final float B25_WIL_FOC_BONUS = 1;

    public static final float SP_GRIT_BONUS =SAVE_BONUS ;
    public static final float B25_SP_PENETRATION = 1;

    public static final float INT_WIT_BONUS = SAVE_BONUS;
    public static final float B25_INT_PENETRATION = 1;
    public static final float A25_INT_MASTERY_MOD = 1;

    public static final float KN_WIT_BONUS = SAVE_BONUS;
    public static final float B25_KN_ASTRAL_RES = 1;
    public static final float A25_KN_PENETRATION =1 ;

    public static final float WIS_ESSENCE_BONUS = 15;
    public static final float WIS_PERCEPTION_BONUS = 1;
    public static final float WIS_SPIRIT_BONUS = SAVE_BONUS;
    public static final float WIS_LUCK_BONUS = SAVE_BONUS;
    public static final float B25_WIS_ELEMENTAL_RES = 1;
    public static final float A25_WIS_DETECTION = 1;

    public static final float CHA_SPIRIT_BONUS = SAVE_BONUS;
    public static final float CHA_LUCK_BONUS = SAVE_BONUS;
    public static final float CHA_DEITY_BONUS = 1;
    public static final float B25_CHA_DISCOUNT = 1;
    public static final float A25_CHA_ASTRAL_RES = 1;

    public static List<String> getAttributeBonusInfoStrings(ATTRIBUTE attr, Unit hero) {
        List<String> list = new ArrayList<>();
        Object key  = StringMaster.format(attr.name());
        for (PARAMS p : attr.getParams()) {
            ObjectMap<String, Double> map = hero.getModifierMaps().get(p);
            if (map == null) {
                continue;
            }
            Double amount = map.get((String) key);
            if (amount == null) {
                continue;
            }
            String string = "+";
            if (amount < 0) {
                string = "-";
            }
            string += StringMaster.wrapInBrackets("" + amount) + " " + p.getName();

            list.add(string);
        }
        return list;
    }
}

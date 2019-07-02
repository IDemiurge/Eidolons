package eidolons.game.module.herocreator.logic.items;

import eidolons.content.DC_CONSTS.ITEM_LEVEL;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.game.battlecraft.ai.tools.priority.ParamPriorityAnalyzer;
import eidolons.game.module.herocreator.logic.items.ItemTraits.ITEM_TRAIT;
import eidolons.game.module.herocreator.logic.items.ItemTraits.TRAIT_EFFECT_SCALE;
import eidolons.game.module.herocreator.logic.items.ItemTraits.TRAIT_EFFECT_TEMPLATE;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.VALUE;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.ability.construct.VariableManager;
import main.entity.Ref.KEYS;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;

import java.util.Locale;
import java.util.Set;

/**
 * Created by JustMe on 12/7/2018.
 */
public class ItemTraitParser {

    private static final String VALUE_SEPARATOR = ": ";

    public static void applyTraits(ObjType newType, Set<ItemTrait> traits) {
        for (ItemTrait trait : traits) {
            applyTrait(newType, trait);
        }
    }

    public static void applyTraits(ObjType newType, ItemTrait... traits) {
        for (ItemTrait trait : traits) {
            applyTrait(newType, trait);
        }
    }

    public static void applyTrait(ObjType newType, ItemTrait trait) {
        ITEM_TRAIT t = trait.getTemplate();
        ITEM_LEVEL lvl = trait.getLevel();

        for (String s : ContainerUtils.open(t.args)) {
            String parsed =
             parseMnemonic(s, lvl, (DC_TYPE) newType.getOBJ_TYPE_ENUM());
            VALUE v = ContentValsManager.getValue(parsed.split(VALUE_SEPARATOR)[0]);
            String var = (parsed.split(VALUE_SEPARATOR)[1]);
            if (v instanceof PARAMETER) {
                newType.addParam((PARAMETER) v, var, false);
            } else {
                if (v instanceof PROPERTY) {
                    newType.addProperty((PROPERTY) v, var, false);
                }
            }
        }
        // TODO add up boost params !

        //        for (String s : ContainerUtils.open(trait.arg)) {
        //            String var = VariableManager.getVar(s);
        //            s = VariableManager.removeVarPart(s);
        //            VALUE v = ContentValsManager.getValue(s);
        //            if (v instanceof PARAMETER) {
        //                newType.addParam((PARAMETER) v, var, false);
        //            } else {
        //                if (v instanceof PROPERTY) {
        //                    newType.addProperty((PROPERTY) v, var, false);
        //                }
        //            }
        //        }
    }

    public static String parseMnemonic(String data, ITEM_LEVEL lvl, DC_TYPE forObjType) {
        String mnemonic = VariableManager.removeVarPart(data);
        float n = TRAIT_EFFECT_SCALE.values()[lvl.getLevel()].coef;
        //default

        //+square coef?

        TRAIT_EFFECT_TEMPLATE template = new EnumMaster<TRAIT_EFFECT_TEMPLATE>().
         retrieveEnumConst(TRAIT_EFFECT_TEMPLATE.class, mnemonic);

        //support scale level-appending
        String last = mnemonic.substring(
         mnemonic.length() - 1, mnemonic.length());
        if (NumberUtils.isInteger(last)) {
            n = TRAIT_EFFECT_SCALE.values()[NumberUtils.getInteger(last)].coef;
            mnemonic = mnemonic.substring(0,
             mnemonic.length() - 1);
            template = new EnumMaster<TRAIT_EFFECT_TEMPLATE>().
             retrieveEnumConst(TRAIT_EFFECT_TEMPLATE.class, mnemonic);
        }
        if (template == null)
            template = TRAIT_EFFECT_TEMPLATE.boost;

        n = n * lvl.getPower() / 100;

        String arg = VariableManager.getVar(data);
        if (arg.isEmpty()) {
            arg = mnemonic;
        }
        MNEMONIC_TYPE type = getTemplateType(template);
        arg = parseShortcuts(arg);

        switch (type) {
            case SINGLE:
                return parseSingle(template, arg, n);
            case DOUBLE:
                return parseDoubleParamTemplate(template, arg, n);
            case DMG_TYPE:
                return parseDamageTemplate(forObjType, template, arg, n);
            case CUSTOM:
                break;
            case PROP:
                break;
            case WRAPPER:
                break;
        }


        return data;
    }

    private static String parseShortcuts(String arg) {
        for (PARAM_SHORTCUT shortcut : PARAM_SHORTCUT.values()) {
            arg = arg.replace(shortcut.name().toLowerCase(), shortcut.full.toLowerCase().trim());
        }
        return arg;
    }

    private static String parseSingle(TRAIT_EFFECT_TEMPLATE template, String arg, float n) {
        PARAMETER p1 = ContentValsManager.getPARAM(arg);
        switch (template) {
            case boost:
                return parseBoost(p1, n);
        }
        return parseBoost(p1, n);
    }

    private static String parseDoubleParamTemplate(TRAIT_EFFECT_TEMPLATE template,
                                                   String arg, float n) {

        String data = "";
        PARAMETER p1 = ContentValsManager.getPARAM(arg.split(",")[0]);
        PARAMETER p2 = ContentValsManager.getPARAM(arg.split(",")[1]);
        switch (template) {
            case bind: //reverse bind if <
                return parseBind(p1, p2, n);
            case depend:
            case greater:
            case lesser:
                //dynamic condition?
                //custom description...
        }

        return data;
    }

    private static String parseDamageTemplate(DC_TYPE itemType, TRAIT_EFFECT_TEMPLATE template, String arg, float n) {
        DAMAGE_TYPE type = new EnumMaster<DAMAGE_TYPE>().retrieveEnumConst(DAMAGE_TYPE.class, arg);
        //why parse?
        float coef = 1;
        String abil = "dmg";
        switch (itemType) {
            case WEAPONS:
                abil = "onAttack";
                break;
            case ARMOR:
                abil = "onHit";
                break;
            case JEWELRY:

        }
        String amount = getDamageAmount(coef, n, itemType, template);
        String data = G_PROPS.PASSIVES.getName() + VALUE_SEPARATOR + abil;
        switch (template) {
            case dmg:
                return data + StringMaster.wrapInParenthesis(type + "," + amount);
            case dmgWeapon:
            case dmgFrom:
            case res:

            case spRes:
                //need item info
            case armor:
        }

        return data;
    }

    private static String getDamageAmount(float coef, float n, DC_TYPE itemType,
                                          TRAIT_EFFECT_TEMPLATE template) {

        String amount = "";
        float baseMod = 1f; //1% to all dmg?
        switch (itemType) {
            case WEAPONS:
                baseMod = 5f;
                break;
            case ARMOR:
                baseMod = 3.5f;
                break;
            case JEWELRY:
                baseMod=1;
                break;
        }
      return   NumberUtils.formatFloat(2, n * coef * baseMod);
    }

    private static MNEMONIC_TYPE getTemplateType(TRAIT_EFFECT_TEMPLATE template) {
        switch (template) {

            case sp:
            case aspect:
                return MNEMONIC_TYPE.PROP;
            case dmg:
            case dmgWeapon:
            case dmgFrom:
            case res:
            case spRes:
            case armor:
                return MNEMONIC_TYPE.DMG_TYPE;
            case bind: //reverse bind if <
            case depend:
            case greater:
            case lesser:
                return MNEMONIC_TYPE.DOUBLE;
            case sharp:
            case stack:
                break;
            case random:
            case chaotic:
            case hidden:
                return MNEMONIC_TYPE.WRAPPER;
            case conditional:
            case discount:
            case state:
                return MNEMONIC_TYPE.CUSTOM;
            case activated:
                break;
            case abil:
            case stdPassive:
                return MNEMONIC_TYPE.PROP;
        }
        return MNEMONIC_TYPE.SINGLE;
    }

    private static float getCoef(String scale) {
        TRAIT_EFFECT_SCALE t = new EnumMaster<TRAIT_EFFECT_SCALE>().retrieveEnumConst(TRAIT_EFFECT_SCALE.class, scale);
        if (t == null)
            t = TRAIT_EFFECT_SCALE.medium;
        return t.coef;
    }

    private static String parseBoost(PARAMETER p1, float n) {
        return PROPS.PARAMETER_BONUSES.getName() +VALUE_SEPARATOR + VariableManager.getStringWithVariable(p1.toString(),
         getBoostCoef(p1, n));

    }

    private static String parseBind(PARAMETER p1, PARAMETER p2, float n) {
        String s = PROPS.PARAMETER_BONUSES.getName() +VALUE_SEPARATOR +
         p1.getName() + StringMaster.wrapInParenthesis(
         StringMaster.getValueRef(KEYS.SOURCE, p2) + "*" + getBindCoef(p1, p2, n));

        return s;
    }

    private static String getBoostCoef(PARAMETER p1, float n) {
        float paramCoef = ParamPriorityAnalyzer.getParamNumericPriority((PARAMS) p1);
        float defaultBoostCoef = 100f;
        return
         String.format(Locale.US, "%.1f", defaultBoostCoef * n / paramCoef);
    }

    private static String getBindCoef(PARAMETER p1, PARAMETER p2, float n) {
        float paramCoef = ParamPriorityAnalyzer.getParamNumericPriority((PARAMS) p1);
        float paramCoef2 = ParamPriorityAnalyzer.getParamNumericPriority((PARAMS) p2);
        /*
        suppose we have STR=>ATK
        STR coef is 10, ATK is 3...
        so the end-coef must be multiplied by 3/10
         */
        float defaultBindCoef = 0.1f;
        return
         String.format(Locale.US, "%.1f", defaultBindCoef * n * paramCoef2 / paramCoef);
    }


    public enum MNEMONIC_TYPE {
        //by argument
        SINGLE,
        DOUBLE,
        DMG_TYPE,
        CUSTOM,
        PROP, WRAPPER,

    }

    public enum PARAM_SHORTCUT {

        atk("attack"),
        def("defense"),
        atkMod("attack Mod"),
        defMod("defense Mod"),
        str("Strength"),
        vit("Vitality "),
        agi("Agility "),
        dex("Dexterity "),
        wil("Willpower "),
        INT("Intelligence "),
        sp("Spellpower "),
        kno("knowledge "),
        wis("Wisdom "),
        cha("Charisma  "),


        tou("Toughness "),
        end("Endurance  "),



/*
tou Toughness
end Endurance

 */;

        public String full;

        PARAM_SHORTCUT(String full) {
            this.full = full;
        }
    }


}

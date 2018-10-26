package eidolons.game.module.herocreator.logic.items;

import eidolons.content.DC_CONSTS.MAGICAL_ITEM_LEVEL;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.VALUE;
import main.content.enums.entity.ItemEnums.ITEM_SHOP_CATEGORY;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.Localization;
import main.system.auxiliary.data.ArrayMaster;
import main.system.datatypes.WeightMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static eidolons.game.module.herocreator.logic.items.ItemTraits.RPG_STYLE.*;

/**
 * Created by JustMe on 10/21/2018.
 * <p>
 * based on perks?
 * <p>
 * <p>
 * Usage:
 * Loot
 * Equip enemies
 * Add to shops
 * <p>
 * data from ...
 * <p>
 * Trait groups by "style"?
 */
public class ItemTraits {

    private static final String VAR = "$var";
    static Map<ITEM_TRAIT_TYPE, List<String>> commonTraits;
    static List<ITEM_TRAIT> traits = new ArrayList<>(Arrays.asList(ITEM_TRAIT.values()));

    public ObjType generateRandomItemWithTraits(MAGICAL_ITEM_LEVEL level, DC_TYPE TYPE, Object... args) {
        ITEM_TRAIT_RARITY rarity = chooseRarity(level);
        RPG_STYLE style = chooseStyle(level);
        String group = null;
        if (args[0] instanceof ITEM_SHOP_CATEGORY) {
            //                group=chooseItemGroup(TYPE, level);
        } else {
            //loot?
        }
        return generateItemWithTraits(TYPE, style, rarity, level, group);
    }

    private RPG_STYLE chooseStyle(MAGICAL_ITEM_LEVEL level) {
        return DARK;
    }

    private ITEM_TRAIT_RARITY chooseRarity(MAGICAL_ITEM_LEVEL level) {
        return ITEM_TRAIT_RARITY.UNCOMMON;
    }

    private ObjType generateItemWithTraits(DC_TYPE TYPE, RPG_STYLE style,
                                           ITEM_TRAIT_RARITY rarity,
                                           MAGICAL_ITEM_LEVEL level,
                                           String group) {
        ObjType type = chooseType(TYPE, group, level);
        ObjType newType = new ObjType(type);

        int n = getTraitNumber(level);
        level = adjustLevelForTraitNumber(n, level);

        List<ItemTrait> traits = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            ITEM_TRAIT template = chooseTemplate(TYPE, style, rarity);
            ItemTrait trait = new ItemTrait(template, level);
            applyTrait(newType, trait);
            traits.add(trait);
        }

             String   name = getName(type, traits);
        return newType;
    }

    //TODO LOCALIZATION?!
    private String getName(ObjType type, List<ItemTrait> traits) {
        String base = type.getProperty(G_PROPS.BASE_TYPE);
        String prefix = "";
        String suffix= "";
        //traits are sorted by power... first is the most powerful
        switch (traits.size()) {
            case 1:
                if (isValid(traits.get(0).template.nouns[0]))
                    suffix= Localization.of(traits.get(0).template.nouns[0]);
                else
                    prefix= (traits.get(0).template.adjectives[0]);
                break;
            case 2:
                //suf + pre
                break;
            case 3:
                // pref for suf!
                break;
        }
        return (prefix + " " + base + " " + suffix).trim();
    }

    private boolean isValid(String noun) {
        if (noun.isEmpty()) {
            return false;
        }
        return !noun.equals(".");
    }

    private ObjType chooseType(DC_TYPE type, String group, MAGICAL_ITEM_LEVEL level) {
        return DataManager.getRandomType(type, group);
    }

    private void applyTrait(ObjType newType, ItemTrait trait) {
        switch (trait.template.type) {
            case AURA:
                //wrap in abil
        }
        for (String s : ContainerUtils.open(trait.arg)) {
            String var = VariableManager.getVar(s);
            s = VariableManager.removeVarPart(s);
            VALUE v = ContentValsManager.getValue(s);
            if (v instanceof PARAMETER) {
                newType.addParam((PARAMETER) v, var, false);
            } else {
                if (v instanceof PROPERTY) {
                    newType.addProperty((PROPERTY) v, var, false);
                }
            }
        }
    }

    private ITEM_TRAIT chooseTemplate(DC_TYPE type, RPG_STYLE style, ITEM_TRAIT_RARITY rarity) {
        ITEM_TRAIT pick = traits.stream().filter(
         trait -> {

             if (trait.rarity == rarity)
                 if (ArrayMaster.contains_(trait.types, type)) {
                     RPG_STYLE pickedStyle = trait.styleMap.getRandomByWeight();
                     if (pickedStyle == style)
                         return true;
                 }
             return false;
         }
        ).findAny().orElse(null);

        //while ...
        return pick;
    }

    private MAGICAL_ITEM_LEVEL adjustLevelForTraitNumber(int n, MAGICAL_ITEM_LEVEL level) {
        return level;
    }

    private int getTraitNumber(MAGICAL_ITEM_LEVEL level) {
        switch (level) {

        }
        return 1;
    }

    /*
    ++ generate special named items from trait combinations

    DEVOURING
    HUNGERING
    VOICELESS
    GUILE
    VILE
    DOOMRIDDEN
    GRIM
    Ill-Fated
    Crawling
    Perdition
    Enthralling
    Wanton
    Ornery
    Weightsome
    Grave
    Furor
    Waning
    Ire

    Eclipse
    Veiled

    Mournful


    FEL,
    SEARING,
    FRIGHTFUL,
    AGONIZING,
    TORMENTING,
    DEMENTIA,
    FEROCIOUS,
    OBLIVION,
    DEATHLESS,

    BLEEDING,
    PENETRATING,
    OMNISCIOUS,

    WEARY,

    BEREAVEMENT,
    UNWAKING,
    FRIGID,
    WHISPERING,

    PRIMEVAL,

    ILL,
    FETID,
    SCORNFUL,
    CRIMSON,

    BAT,
    WOLF,
    RAVEN,
    OWL,
    EAGLE,
    LION,

    BLAZING,
    SOARING,

    BINDING,
    HAZE,
    GHOSTLY,
    SPECTRAL,





     */

    //how many levels should they have?
    public enum ITEM_TRAIT {
        VICIOUS("", "", ITEM_TRAIT_TYPE.VALUE, ITEM_TRAIT_RARITY.COMMON,
         map().chain(BLOOD, 10).chain(CHAOS, 6).chain(FLAME, 4).chain(EVIL, 2),
         "Attack mod($var/4);ATTACK_AP_PENALTY($var/-3)", DC_TYPE.WEAPONS),
        ROTTEN("", "", ITEM_TRAIT_TYPE.VALUE, ITEM_TRAIT_RARITY.COMMON,
         map().chain(BLOOD, 10).chain(CHAOS, 6).chain(FLAME, 4).chain(EVIL, 2),
         "Attack mod($var/4);ATTACK_AP_PENALTY($var/-3)", DC_TYPE.WEAPONS),;

        /*
        specifically good for gameplay
Illumination("Glimmer;Shining;.;Radiance", ".;.;Luminous",

        total must have for the setting

Consternation
Oblivion
Deceit
Damnation

Void(Hollow;
Requiem(Passing

Dissolution
Ascension
Tranquility

Retribution
Sombra
Sepulchre
Ancient





I don't know Eidolons gameplay anymore... I've spent so long in the shadows of the Code...
perhaps I need a feature map indeed...
or just a new look, free mind - and then bind it into mechanics...


         */

          final String args;
          final DC_TYPE[] types;
          final int levels;
        ITEM_TRAIT_TYPE type;
        ITEM_TRAIT_RARITY rarity;
        WeightMap<RPG_STYLE> styleMap;
        String[] nouns;
        String[] adjectives;

        ITEM_TRAIT( String nouns, String adjectives, ITEM_TRAIT_TYPE type, ITEM_TRAIT_RARITY rarity,
                   WeightMap<RPG_STYLE> styleMap, String args,
                   DC_TYPE... types) {
            this.type = type;
            this.styleMap = styleMap;
            this.rarity = rarity;
            this.args = args;
            this.types = types;
            this.nouns = ContainerUtils.open(nouns);
            this.adjectives = ContainerUtils.open(adjectives);
            levels =  ContainerUtils.openContainer(nouns).size();
        }

        private static WeightMap<RPG_STYLE> map() {
            return new WeightMap<>();
        }

        private ObjType chooseType(DC_TYPE weapons, String group, MAGICAL_ITEM_LEVEL level) {
            return DataManager.getRandomType(weapons, group);
        }
    }

    public enum ITEM_TRAIT_RARITY {
        COMMON,
        UNCOMMON,
        RARE,
        EXCEPTIONAL,
    }

    public enum ITEM_TRAIT_TYPE {
        VALUE,

        ON_HIT,
        ON_ATTACK,
        ON_DEATH,
        ON_KILL,
        AURA,
        AURA_ALLIES,
        AURA_ENEMIES,
        AURA_RECURRING,

        ACTIVE_CHARGES,//adds special action to weapon's actives
        ACTIVE,//adds special action to weapon's actives

    }


    public enum RPG_STYLE {
        EVIL,
        DARK,
        MOON,
        CHAOS,
        FLAME,
        LAVA,
        BLOOD,
        VENOM,
        ICE,
        WOOD,
        STONE,
        METAL,
        COSMOS,
        WIND,
        THUNDER,
        CRYSTAL,
        SUN,
    }

    public class ItemTrait {
        ITEM_TRAIT template;
        MAGICAL_ITEM_LEVEL level;

        String arg;

        public ItemTrait(ITEM_TRAIT template, MAGICAL_ITEM_LEVEL level) {
            this.template = template;
            this.level = level;
            String value = level.getPower() + "";
            arg = template.args.replace(VAR, value);
        }

        public int getNamingPriority() {
            return 0;
        }

        //some templates are put before level!
        @Override
        public String toString() {
            return level + " " + template;
        }
    }
}

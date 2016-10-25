package main.client.cc.logic.items;

import main.ability.effects.common.EnchantItemEffect;
import main.content.OBJ_TYPES;
import main.content.PARAMS;
import main.content.PROPS;
import main.entity.obj.DC_SpellObj;
import main.entity.type.ObjType;

import java.util.List;

public class Enchanter {
    private static final Integer ENERGY_PER_SD = 2;

    public static int calculateSpellEnergyCost(DC_SpellObj spell) {
        return spell.getIntParam(PARAMS.SPELL_DIFFICULTY) * ENERGY_PER_SD;
    }

    public List<ObjType> getAvailableSpellTypes() {
        return null; // or should it only be a matter of gold?
    }

    public void initEnchantment(ObjType item) {
//        item.getProperty(PROPS.ENCHANTMENT_SPELL);
        // energy?

//        new EnchantItemEffect(energy, weapon, spell);

    }

    public ObjType getEnchantedType(ObjType type, ObjType spellType) {
        // add some standard passive with variables perhaps
        // or perhaps I could initiative the enchantment in-game,
        // and let the type only have Enchantment = SpellName
        switch ((OBJ_TYPES) type.getOBJ_TYPE_ENUM()) {
            case ARMOR:
                // add on hit
                break;
            case ITEMS:
                break;
            case JEWELRY:
                break;
            case WEAPONS:
                // add on attack
                break;
            default:
                break;
        }
        return type;
    }

}

package main.ability.effects.special;

import main.ability.effects.MicroEffect;
import main.content.enums.entity.UnitEnums;
import main.entity.obj.Obj;

@Deprecated
public class ManipulateSpellEffect extends MicroEffect {

    private SPELL_MANIPULATION type;
    private String spell_type;

    public ManipulateSpellEffect(SPELL_MANIPULATION type) {
        this.type = type;
    }

    public ManipulateSpellEffect(String spell_type) {
        this.type = SPELL_MANIPULATION.ADD;
        this.spell_type = spell_type;
    }

    @Override
    public boolean applyThis() {
        Obj spell = ref.getTargetObj();
        switch (type) {
            case ADD:
                // menojeur!!!
                break;
            case PREPARE:
                spell.addStatus(UnitEnums.STATUS.PREPARED.name());

                break;

            case BLOCK:
                spell.addStatus(UnitEnums.STATUS.BLOCKED.name());
                break;
            case OBLIVIATE:
                break;

            case STEAL:
                // change owner and that's it!
                break;
            case UNBLOCK:
                spell.removeStatus(UnitEnums.STATUS.BLOCKED.name());
                break;
            default:
                break;

        }
        return true;
    }

}

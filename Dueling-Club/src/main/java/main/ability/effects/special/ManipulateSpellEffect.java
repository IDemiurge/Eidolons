package main.ability.effects.special;

import main.ability.effects.oneshot.MicroEffect;
import main.content.CONTENT_CONSTS.STATUS;
import main.entity.obj.Obj;

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
                spell.addStatus(STATUS.PREPARED.name());

                break;

            case BLOCK:
                spell.addStatus(STATUS.BLOCKED.name());
                break;
            case OBLIVIATE:
                break;

            case STEAL:
                // change owner and that's it!
                break;
            case UNBLOCK:
                spell.removeStatus(STATUS.BLOCKED.name());
                break;
            default:
                break;

        }
        return true;
    }

}

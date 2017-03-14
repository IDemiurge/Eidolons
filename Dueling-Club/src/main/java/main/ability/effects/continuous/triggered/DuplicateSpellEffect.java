package main.ability.effects.continuous.triggered;

import main.ability.effects.TriggeredEffect;
import main.ability.effects.MicroEffect;
import main.ability.effects.oneshot.activation.CastNewSpellEffect;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.game.logic.event.Event.EVENT_TYPE;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;

public class DuplicateSpellEffect extends MicroEffect  implements TriggeredEffect {
    public static EVENT_TYPE EVENT_TYPE = STANDARD_EVENT_TYPE.SPELL_RESOLVED;
    private boolean chooseTarget = false;
    private boolean group = false;
    private boolean event = false;
    private String target_key;

    public DuplicateSpellEffect(String target_key, Boolean choosetarget, Boolean event) {
        this.target_key = target_key;
        this.chooseTarget = choosetarget;
        this.event = event;
    }

    public DuplicateSpellEffect(String target_key, Boolean choosetarget) {
        this(target_key, choosetarget, false);
    }

    public DuplicateSpellEffect() {
        this.group = true;
        chooseTarget = false;
    }

    @Override
    public boolean applyThis() {
        // ref getspell?

        String spelltype;
        Ref REF;
        if (group) {
            spelltype = ref.getEvent().getRef().getObj(KEYS.SPELL).getName();
            REF = ref.getEvent().getRef().getObj(KEYS.SPELL).getRef();
            CastNewSpellEffect effect = new CastNewSpellEffect(spelltype);
            REF.setGroup(ref.getGroup());
            return effect.apply(REF);
        }
        if (!event) {
            spelltype = ref.getTargetObj().getName();
            REF = ref.getTargetObj().getRef();

        } else {
            spelltype = ref.getEvent().getRef().getObj(KEYS.SPELL).getName();
            REF = ref.getEvent().getRef().getObj(KEYS.SPELL).getRef();
            REF.setTarget(ref.getTarget());
        }
        // spelltype = ref.getObj(KEYS.SPELL).getName();
        // REF = ref.getObj(KEYS.SPELL).getRef();
        return new CastNewSpellEffect(spelltype, true, chooseTarget, target_key)
                .apply(REF);
    }
}

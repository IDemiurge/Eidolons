package eidolons.ability.effects.oneshot.activation;

import eidolons.entity.active.DC_SpellObj;
import main.ability.effects.MicroEffect;
import main.ability.effects.OneshotEffect;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.system.auxiliary.StringMaster;

public class CastLastSpellEffect extends MicroEffect implements OneshotEffect {

    private String obj_ref;
    private Boolean free;
    private Boolean chooseTarget;
    private String target_key;

    public CastLastSpellEffect(Boolean free, Boolean chooseTarget,
                               String target_key) {
        this.free = free;
        this.chooseTarget = chooseTarget;
        this.target_key = target_key;

    }

    @Override
    public boolean applyThis() {

        Obj obj = ref.getTargetObj().getRef().getObj(KEYS.SPELL);
        DC_SpellObj spell = (DC_SpellObj) obj;
        spell.setFree(free);

        Ref REF = Ref.getCopy(spell.getRef());
        REF.setSource(ref.getSource());
        if (chooseTarget) {
            spell.getTargeting().select(REF);
        } else {
            if (StringMaster.isEmpty(target_key)) {
                REF.setTarget(ref.getTarget());
            }

            REF.setTarget(ref.getId(target_key));
        }
        boolean res = spell.activatedOn(REF);
        spell.setFree(false);
        return res;
    }

}

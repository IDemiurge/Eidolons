package main.ability.effects.oneshot.special;

import main.ability.effects.oneshot.MicroEffect;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_SpellObj;
import main.entity.obj.Obj;
import main.system.auxiliary.StringMaster;

public class CastLastSpellEffect extends MicroEffect {

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

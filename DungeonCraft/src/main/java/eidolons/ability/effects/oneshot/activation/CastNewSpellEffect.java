package eidolons.ability.effects.oneshot.activation;

import eidolons.entity.active.Spell;
import main.ability.effects.MicroEffect;
import main.ability.effects.OneshotEffect;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.data.ability.AE_ConstrArgs;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.entity.type.ObjType;

public class CastNewSpellEffect extends MicroEffect implements OneshotEffect {

    private String spelltype;
    private Boolean free;
    private Boolean chooseTarget;
    private String target_key;
    private boolean group = false;

    public CastNewSpellEffect(String spelltype) {
        this(spelltype, true, false, null);
        this.group = true;
        this.chooseTarget = false;
    }

    public CastNewSpellEffect(String spelltype, Boolean free) {
        this.spelltype = spelltype;
        this.free = free;
        this.chooseTarget = true;
    }

    @AE_ConstrArgs(argNames = {"spelltype", "free", "chooseTarget",
     "target_key"})
    public CastNewSpellEffect(String spelltype, Boolean free,
                              Boolean chooseTarget, String target_key) {
        this.spelltype = spelltype;
        this.free = free;
        this.chooseTarget = chooseTarget;
        this.target_key = target_key;

    }

    // TODO spellpower/mastery mods!!!

    @Override
    public boolean applyThis() {
        ObjType type = DataManager.getType(spelltype, DC_TYPE.SPELLS);
        Ref REF = new Ref(ref.getGame(), ref.getSource());

        Obj obj = game.createSpell(type, ref.getSourceObj().getOwner(), REF);
        Spell spell = (Spell) obj;
        spell.setFree(free);
        spell.setQuietMode(true);
        if (group) {
            REF.setGroup(ref.getGroup());

            return spell.activatedOn(REF);
        }
        if (chooseTarget) {
            spell.getTargeting().select(REF);
        } else {
            REF.setTarget(ref.getId(target_key));
        }
        return spell.activatedOn(REF);
    }
}

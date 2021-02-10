package eidolons.ability.effects.oneshot.activation;

import eidolons.ability.effects.DC_Effect;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import main.ability.effects.OneshotEffect;
import main.entity.Ref;
import main.entity.Ref.KEYS;

public class ActivateEffect extends DC_Effect implements OneshotEffect {
    private KEYS key;
    private boolean spell;
    private String name;
    private boolean free;
    private KEYS sourceKey = KEYS.SOURCE;

    public ActivateEffect(String name, KEYS key, boolean spell) {
        this.name = name;
        this.key = key;
        this.spell = spell;
    }

    public ActivateEffect(String name, boolean free) {
        this(name, KEYS.TARGET, false);
        this.free = free;

    }

    public ActivateEffect(String name) {
        this(name, KEYS.TARGET, false);
    }

    @Override
    public boolean applyThis() {
        Unit source = (Unit) ref.getObj(sourceKey);
        DC_ActiveObj active;

        if (!spell) {
            active = source.getAction(name);
        } else {
            active = source.getSpell(name);
        }
        if (free) {
            active.setFree(free);
        }
        try {
            active.activatedOn(Ref.getSelfTargetingRefCopy(ref.getObj(key)));
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            active.setFree(false);

        }
        return true;
    }

}

package main.ability.effects.oneshot.activation;

import main.ability.effects.DC_Effect;
import main.ability.effects.OneshotEffect;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;

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
        Unit target = (Unit) ref.getObj(key);
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
            active.activatedOn(Ref.getSelfTargetingRefCopy(target));
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            active.setFree(false);

        }
        return true;
    }

}

package eidolons.ability.effects.oneshot.rule;

import eidolons.ability.effects.attachment.AddBuffEffect;
import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.content.PARAMS;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.content.mode.STD_MODES;

import java.util.List;

public class WatchBuffEffect extends AddBuffEffect {

    public static final String BUFF_TYPE_NAME = "Watching";
    private final List<DC_Obj> list;

    public WatchBuffEffect(Unit watcher, List<DC_Obj> list) {
        super(BUFF_TYPE_NAME, generateEffects(watcher, list), true);
        this.list = list;
    }

    private static String getBuffName(List<DC_Obj> list) {
        if (list.size() == 1) {
            return BUFF_TYPE_NAME + " " + list.get(0).getNameIfKnown();
        }
        return BUFF_TYPE_NAME + " Multiple";
    }

    private static Effect generateEffects(Unit watcher, List<DC_Obj> list) {
        // some triggers? for the list...
        // preCheck specials per watcher!
        Effects e = new Effects();
        // e.setForceStaticParse(false);
        // e.setForcedLayer(Effect.BUFF_RULE);
        // atk/def VERSUS the list? perhaps only via special preCheck in
        // AttackMaster...

        // TODO so this is re-applied after each reset() ? 'cause formulas may
        // be dynamic, depend on
        // number/kind of watched units...

        if (!watcher.getMode().equals(STD_MODES.ALERT)) {
            e.add(new ModifyValueEffect(PARAMS.DEFENSE_MOD, MOD.MODIFY_BY_CONST, WatchRule
             .getDefenseModVsOthers(watcher, list)));
            e.add(new ModifyValueEffect(PARAMS.ATTACK_MOD, MOD.MODIFY_BY_CONST, WatchRule
             .getAttackModVsOthers(watcher, list)));
            e.add(new ModifyValueEffect(PARAMS.ATB_COST_MOD, MOD.MODIFY_BY_CONST, WatchRule
             .getApPenaltyMod(watcher, list)));

        } else {

        }

        // detection
        //
        return e;
    }

    @Override
    public boolean applyThis() {
        boolean result = super.applyThis();
        if (result) {
            getBuff().setName(getBuffName(list));
        }
        setIrresistible(true);
        return result;
    }

}

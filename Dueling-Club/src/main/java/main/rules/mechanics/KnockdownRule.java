package main.rules.mechanics;

import main.ability.effects.AddBuffEffect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effect.MOD_PROP_TYPE;
import main.ability.effects.Effects;
import main.ability.effects.oneshot.common.ModifyPropertyEffect;
import main.ability.effects.oneshot.common.ModifyValueEffect;
import main.content.CONTENT_CONSTS.STATUS;
import main.content.PARAMS;
import main.content.properties.G_PROPS;
import main.entity.Ref;
import main.entity.obj.DC_HeroObj;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;

public class KnockdownRule {

    private static final String DEFENSE_FORMULA = "-50";
    String STA_COST = "4*weight-Strength*3";
    String AP_COST = "4*weight-Strength*3 *dex";

    // add trigger
    /*
	 * check on UnitTurn()
	 * 
	 * NextRoundAP parameter!
	 * 
	 * is activating Getting Up...
	 */

    public static void checkApplyProneEffect(DC_HeroObj unit) {
        if (!unit.isBfObj())
            if (unit.checkStatus(STATUS.PRONE))
                new ModifyValueEffect(PARAMS.DEFENSE_MOD, MOD.MODIFY_BY_PERCENT, DEFENSE_FORMULA)
                        .apply(Ref.getSelfTargetingRefCopy(unit));
    }

    public static void knockdown(DC_HeroObj target) {
        Effects e = new Effects();
        target.getGame().getLogManager().newLogEntryNode(ENTRY_TYPE.KNOCKDOWN, target);
        e.add(new ModifyValueEffect(PARAMS.C_N_OF_ACTIONS, MOD.MODIFY_BY_CONST, "-3"));
        e.add(new ModifyValueEffect(PARAMS.C_FOCUS, MOD.MODIFY_BY_CONST, "-15"));
        (e).apply(Ref.getSelfTargetingRefCopy(target));

        e = new Effects();
        e.add(new ModifyPropertyEffect(G_PROPS.STATUS, MOD_PROP_TYPE.SET, STATUS.PRONE.toString()));
        // e.add(new ModifyValueEffect(PARAMS.C_FOCUS, MOD.MODIFY_BY_CONST, ));
        e.add(new ModifyValueEffect(PARAMS.DEFENSE_MOD, MOD.MODIFY_BY_CONST, "-50"));
        new AddBuffEffect("Knocked Down", e, 1).apply(Ref.getSelfTargetingRefCopy(target));

        target.getGame().getLogManager().doneLogEntryNode(ENTRY_TYPE.KNOCKDOWN, target);
    }

}

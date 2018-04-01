package eidolons.game.battlecraft.rules.combat.misc;

import main.ability.effects.Effect.MOD;
import main.ability.effects.Effect.MOD_PROP_TYPE;
import main.ability.effects.Effects;
import eidolons.ability.effects.attachment.AddBuffEffect;
import eidolons.ability.effects.common.ModifyPropertyEffect;
import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.content.PARAMS;
import main.content.enums.entity.UnitEnums;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import eidolons.entity.obj.unit.Unit;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;

public class KnockdownRule {

    private static final String DEFENSE_FORMULA = "-50";
    //to get up
    String STA_COST = "4*weight-Strength*3";
    String AP_COST = "4*weight-Strength*3 *dex";

    // add trigger
    /*
     * preCheck on UnitTurn()
	 * 
	 * NextRoundAP parameter!
	 * 
	 * is activating Getting Up...
	 */

    public static void checkApplyProneEffect(Unit unit) {
        if (!unit.isBfObj()) {
            if (unit.checkStatus(UnitEnums.STATUS.PRONE)) {
                new ModifyValueEffect(PARAMS.DEFENSE_MOD, MOD.MODIFY_BY_PERCENT, DEFENSE_FORMULA)
                 .apply(Ref.getSelfTargetingRefCopy(unit));
            }
        }
    }

    public static void knockdown(Unit target) {
        Effects e = new Effects();
        target.getGame().getLogManager().newLogEntryNode(ENTRY_TYPE.KNOCKDOWN, target);
        e.add(new ModifyValueEffect(PARAMS.C_N_OF_ACTIONS, MOD.MODIFY_BY_CONST, "-3"));
        e.add(new ModifyValueEffect(PARAMS.C_FOCUS, MOD.MODIFY_BY_CONST, "-15"));
        (e).apply(Ref.getSelfTargetingRefCopy(target));

        e = new Effects();
        e.add(new ModifyPropertyEffect(G_PROPS.STATUS, MOD_PROP_TYPE.SET, UnitEnums.STATUS.PRONE.toString()));
        // e.add(new ModifyValueEffect(PARAMS.C_FOCUS, MOD.MODIFY_BY_CONST, ));
        e.add(new ModifyValueEffect(PARAMS.DEFENSE_MOD, MOD.MODIFY_BY_CONST, "-50"));
        new AddBuffEffect("Knocked Down", e, 1).apply(Ref.getSelfTargetingRefCopy(target));

        target.getGame().getLogManager().doneLogEntryNode(ENTRY_TYPE.KNOCKDOWN, target);
    }

}

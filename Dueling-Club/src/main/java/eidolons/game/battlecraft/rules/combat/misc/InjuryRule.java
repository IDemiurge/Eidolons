package eidolons.game.battlecraft.rules.combat.misc;

import eidolons.ability.effects.oneshot.rule.InjuryEffect;
import eidolons.content.PROPS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import main.ability.effects.Effects;
import main.content.CONTENT_CONSTS2.INJURY;
import main.content.CONTENT_CONSTS2.INJURY_TYPE;
import main.entity.Ref;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

public class InjuryRule {
    // dynamic reset to support cure?
    public static void applyInjuryRule(DC_ActiveObj action) {
        INJURY_TYPE type = null;
        new InjuryEffect(type);
        // action.getDamageType()

    }

    public static void checkInjuryApplies() {

    }

    public static void applyOldWounds(Unit hero) {
        Effects effects = new Effects();
        for (String substring : StringMaster.open(hero.getProperty(PROPS.INJURIES))) {
            INJURY template = new EnumMaster<INJURY>().retrieveEnumConst(INJURY.class, substring);
            effects.add(new InjuryEffect(template, true));
        }
        effects.apply(Ref.getSelfTargetingRefCopy(hero));
    }
}

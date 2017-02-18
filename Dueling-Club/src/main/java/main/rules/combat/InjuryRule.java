package main.rules.combat;

import main.ability.effects.Effects;
import main.ability.effects.common.InjuryEffect;
import main.content.CONTENT_CONSTS2.INJURY;
import main.content.CONTENT_CONSTS2.INJURY_TYPE;
import main.content.PROPS;
import main.entity.Ref;
import main.entity.obj.unit.Unit;
import main.entity.active.DC_ActiveObj;
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
        for (String substring : StringMaster.openContainer(hero.getProperty(PROPS.INJURIES))) {
            INJURY template = new EnumMaster<INJURY>().retrieveEnumConst(INJURY.class, substring);
            effects.add(new InjuryEffect(template, true));
        }
        effects.apply(Ref.getSelfTargetingRefCopy(hero));
    }
}

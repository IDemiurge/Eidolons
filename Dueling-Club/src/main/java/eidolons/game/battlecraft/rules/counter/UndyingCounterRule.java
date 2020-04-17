package eidolons.game.battlecraft.rules.counter;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.rules.TriggerRule;
import eidolons.game.core.EUtils;
import eidolons.game.netherflame.igg.soul.EidolonLord;
import main.content.enums.entity.UnitEnums;
import main.game.logic.event.Event;
import main.system.auxiliary.NumberUtils;
import main.system.entity.CounterMaster;

public class UndyingCounterRule extends TriggerRule {
    public static boolean check(Event event) {
        if (!(event.getRef().getTargetObj() instanceof Unit)) {
            return false;
        }
        Unit unit = (Unit) event.getRef().getTargetObj();
        int n = NumberUtils.getInteger(unit.getCustomParamMap().get(CounterMaster.findCounter("undying")));

//                event.getRef().getSourceObj().getCounter(CounterMaster.findCounter(UnitEnums.COUNTER.Undying.getName()));
        if (n == 0) {
            return false;
        }
        unit.modifyCounter(UnitEnums.COUNTER.Undying, -1);
        if (EidolonsGame.BRIDGE)
            if (unit.isPlayerCharacter()) {
                EidolonLord.lord.soulforceLost(10);
            }
        unit.preventDeath();
//     TODO shouldn't be necessary?
//      unit.addProperty(G_PROPS.STANDARD_PASSIVES, UnitEnums.STANDARD_PASSIVES.INDESTRUCTIBLE.getName());
        String msg = unit.getName() + " will not die yet... [" +
                (n - 1) +
                "]";
//        DC_SoundMaster.playStandardSound();
        EUtils.showInfoText(true, msg);
        new Event(Event.STANDARD_EVENT_TYPE.UNDYING_RULE, unit.getRef()).fire();
        return true;
    }

    @Override
    public void initTrigger() {

    }
}

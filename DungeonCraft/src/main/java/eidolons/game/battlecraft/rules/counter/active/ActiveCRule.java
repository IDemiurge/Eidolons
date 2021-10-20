package eidolons.game.battlecraft.rules.counter.active;

import eidolons.ability.EventAndCondition;
import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.counter.anew.ExertionRule;
import eidolons.game.battlecraft.rules.counter.generic.CounterRule_10Up;
import eidolons.game.battlecraft.rules.saves.SavesMaster;
import eidolons.game.core.game.DC_Game;
import main.ability.effects.Effect;
import main.content.enums.entity.UnitEnums;
import main.content.values.parameters.PARAMETER;

public abstract class ActiveCRule extends CounterRule_10Up {

    public ActiveCRule(DC_Game game) {
        super(game);
    }

    protected abstract PARAMETER getRetainmentParam();

    public void activated(Unit source) {
        remove(source, 1);
        int bonus = 0;
        source.addParam(PARAMS.C_ATB, 25 + bonus);
        ExertionRule.activated(getCounter(), source);
        // always by 1? Maybe... it'd be kinda more obvious if we could click as many times as we want to gain ATB
        // over 100? Then just prohibit using other actions and activating other counters. Could be better!
        // then 25 atb per counter?
    }

    protected void remove(Unit source, int i) {
        source.modifyCounter(getCounter(), i);
    }

    public boolean canDoExtraAttack(Unit source) {
        //at least N counters? Factors that turn on/off this? EA type - or always same?
        //maybe 1-2-3 for q/std/power?
        Integer n = getNumberOfCounters(source);
        return n > 0;
    }

    public void extraAttackDone(Unit source) {
        // remove(source, getEA_Cost(source));
    }

    public int getCounterNumberReductionPerTurn(BattleFieldObject unit) {
        Integer retained = unit.getIntParam(getRetainmentParam());
        Integer n = getNumberOfCounters(unit);
        return Math.max(0, (n / 2) - retained);
    }



}

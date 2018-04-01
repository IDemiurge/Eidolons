package main.ability.effects.periodic;

import main.ability.effects.AttachmentEffect;
import main.ability.effects.Effect;
import main.ability.effects.MicroEffect;
import main.elements.conditions.Condition;

/**
 * Created by JustMe on 3/26/2018.
 */
public class PeriodicEffect extends MicroEffect implements AttachmentEffect {

    private final Effect effects;
    private final double period;
    private double periodTimer;
    private double timeInGame;

    public PeriodicEffect(String period, Effect effect) {
        this.period = Double.valueOf(period);
        this.effects = effect;
    }

    @Override
    public boolean applyThis() {
        main.system.auxiliary.log.LogMaster.log(1, this + " applies " + effects);
        return effects.apply(getRef());
    }

    public double getPeriod() {
        return period;
    }

    public Effect getEffects() {
        return effects;
    }

    public double getPeriodTimer() {
        return periodTimer;
    }

    public double getTimeInGame() {
        return timeInGame;
    }

    public void timeElapsed(double time) {

        this.periodTimer += time;
        this.timeInGame += time;
        main.system.auxiliary.log.LogMaster.log(1, this + " periodTimer = " + periodTimer);
        main.system.auxiliary.log.LogMaster.log(1, this + " timeInGame = " + timeInGame);
        main.system.auxiliary.log.LogMaster.log(1, this + " period = " + getPeriod());
        if (periodTimer >= getPeriod()) {
            periodTimer = periodTimer - period;
            applyThis();
        }
    }

    @Override
    public void setRetainCondition(Condition c) {

    }
}

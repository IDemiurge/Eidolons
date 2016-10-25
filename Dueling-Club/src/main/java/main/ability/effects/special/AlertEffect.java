package main.ability.effects.special;

import main.content.CONTENT_CONSTS.STD_BUFF_NAMES;
import main.elements.conditions.Condition;

public class AlertEffect extends WaitEffect {
    public AlertEffect() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean applyThis() {

        return super.applyThis();
    }

    @Override
    protected String getBuffName() {
        return STD_BUFF_NAMES.On_Alert.getName();
    }

    @Override
    protected Condition getConditions() {
        // TODO adjacent?
        return super.getConditions();
    }

    @Override
    protected Condition getRetainConditions() {
        // TODO ??? WaitRule wakes up manually...

        return super.getRetainConditions();
    }

    @Override
    protected String getStatus() {
        // TODO Auto-generated method stub
        return super.getStatus();
    }
}

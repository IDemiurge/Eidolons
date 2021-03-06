package eidolons.ability.ignored.oneshot.rule;

import main.content.enums.system.MetaEnums;
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
        return MetaEnums.STD_BUFF_NAME.On_Alert.getName();
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

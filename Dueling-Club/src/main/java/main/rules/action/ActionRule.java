package main.rules.action;

import main.entity.obj.ActiveObj;
import main.entity.obj.unit.Unit;

public interface ActionRule {

    public void actionComplete(ActiveObj activeObj);

    public boolean unitBecomesActive(Unit unit);

}

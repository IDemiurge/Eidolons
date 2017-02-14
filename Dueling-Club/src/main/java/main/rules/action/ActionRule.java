package main.rules.action;

import main.entity.obj.ActiveObj;
import main.entity.obj.unit.DC_HeroObj;

public interface ActionRule {

    public void actionComplete(ActiveObj activeObj);

    public boolean unitBecomesActive(DC_HeroObj unit);

}

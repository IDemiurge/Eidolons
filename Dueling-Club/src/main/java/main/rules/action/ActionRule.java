package main.rules.action;

import main.entity.obj.ActiveObj;
import main.entity.obj.DC_HeroObj;

public interface ActionRule {

    public void actionComplete(ActiveObj activeObj);

    public boolean unitBecomesActive(DC_HeroObj unit);

}
